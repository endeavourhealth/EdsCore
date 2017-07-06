package org.endeavourhealth.core.fhirStorage;

import com.datastax.driver.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.common.cassandra.CassandraConnector;
import org.endeavourhealth.common.utility.ThreadPool;
import org.endeavourhealth.common.utility.ThreadPoolError;
import org.endeavourhealth.core.data.admin.models.Service;
import org.endeavourhealth.core.data.audit.AuditRepository;
import org.endeavourhealth.core.data.audit.models.ExchangeEvent;
import org.endeavourhealth.core.data.audit.models.ExchangeTransformAudit;
import org.endeavourhealth.core.data.audit.models.ExchangeTransformErrorState;
import org.endeavourhealth.core.data.ehr.ExchangeBatchRepository;
import org.endeavourhealth.core.data.ehr.ResourceRepository;
import org.endeavourhealth.core.data.ehr.models.ExchangeBatch;
import org.endeavourhealth.core.data.ehr.models.ResourceByExchangeBatch;
import org.endeavourhealth.core.data.ehr.models.ResourceEntry;
import org.endeavourhealth.core.rdbms.eds.PatientSearchHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class FhirDeletionService {
    private static final Logger LOG = LoggerFactory.getLogger(FhirDeletionService.class);

    private final ExchangeBatchRepository exchangeBatchRepository = new ExchangeBatchRepository();
    private final AuditRepository auditRepository = new AuditRepository();
    private final ResourceRepository resourceRepository = new ResourceRepository();
    private final Service service;

    private String progress = null;
    private boolean isComplete = false;
    private Map<UUID, List<UUID>> exchangeIdsToDeleteBySystem = null;
    private int countBatchesToDelete = 0;
    private int countBatchesDeleted = 0;
    private ThreadPool threadPool = null;

    public FhirDeletionService(Service service) {
        this.service = service;
        this.progress = "Starting";
    }

    public void deleteData() throws Exception {
        LOG.info("Deleting data for service " + service.getId());

        retrieveExchangeIds();

        //count exactly how many exchanges there are over all the systems
        int countExchanges = 0;
        for (UUID systemId: exchangeIdsToDeleteBySystem.keySet()) {
            List<UUID> exchangeIds = exchangeIdsToDeleteBySystem.get(systemId);
            countExchanges += exchangeIds.size();
        }
        LOG.trace("Found " + countExchanges + " exchanges with " + countBatchesToDelete + " batches to delete");

        threadPool = new ThreadPool(5, 1000);

        //for each system...
        for (UUID systemId: exchangeIdsToDeleteBySystem.keySet()) {

            //start looping through our exchange IDs
            List<UUID> exchangeIds = exchangeIdsToDeleteBySystem.get(systemId);
            for (UUID exchangeId: exchangeIds) {
                deleteExchange(systemId, exchangeId);
            }
        }

        List<ThreadPoolError> errors = threadPool.waitAndStop();
        handleErrors(errors);

        //then tidy remove any remaining data and mark the audits as deleted
        this.progress = "Resources deleted - finishing up";
        for (UUID systemId: exchangeIdsToDeleteBySystem.keySet()) {
            LOG.trace("Deleting remaining data for service ID {} and system ID {}", service.getId(), systemId);

            //delete any transform summary, since all errors are now gone
            ExchangeTransformErrorState summary = auditRepository.getErrorState(service.getId(), systemId);
            if (summary != null) {
                auditRepository.delete(summary);
            }

            //get rid of the patient identity record too (patient_identifier_by_local_id)
            PatientSearchHelper.deleteForService(service.getId(), systemId);
        }

        this.isComplete = true;
    }

    private void retrieveExchangeIds() throws Exception {

        exchangeIdsToDeleteBySystem = new HashMap<>();
        countBatchesToDelete = 0;

        HashSet<UUID> hsExchangeIds = new HashSet<>();

        Session session = CassandraConnector.getInstance().getSession();

        for (UUID systemId: getSystemsIds()) {

            List<UUID> exchangeIds = new ArrayList<>();

            Statement stmt = new SimpleStatement("SELECT exchange_id, number_batches_created FROM audit.exchange_transform_audit WHERE service_id = " + service.getId() + " AND system_id = " + systemId);
            stmt.setFetchSize(100);

            ResultSet rs = session.execute(stmt);
            while (!rs.isExhausted()) {
                Row row = rs.one();
                UUID exchangeId = row.get(0, UUID.class);
                Integer batchesCreated = row.get(1, Integer.class);
                LOG.trace("Exchange " + exchangeId + " has " + batchesCreated + " batches");

                if (batchesCreated != null) {
                    countBatchesToDelete += batchesCreated;
                }

                if (!hsExchangeIds.contains(exchangeId)) {
                    hsExchangeIds.add(exchangeId);
                    exchangeIds.add(exchangeId);
                }
            }

            //the exchange IDs will come off the DB in reverse order (i.e. most recent first, so reverse them)
            List reversed = new ArrayList<>();
            for (int i=exchangeIds.size()-1; i>=0; i--) {
                reversed.add(exchangeIds.get(i));
            }

            exchangeIdsToDeleteBySystem.put(systemId, reversed);
        }
    }


    private void deleteExchange(UUID systemId, UUID exchangeId) throws Exception {

        //get all batches received for each exchange
        List<UUID> batchIds = getBatchIds(exchangeId);
        LOG.trace("Deleting data for exchangeId " + exchangeId + " with " + batchIds.size() + " batches");

        for (UUID batchId : batchIds) {

            countBatchesDeleted ++;
            progress = new DecimalFormat("###.##").format(((double)countBatchesDeleted / (double)countBatchesToDelete) * 100d) + "%";

            List<ResourceByExchangeBatch> resourceByExchangeBatchList = resourceRepository.getResourcesForBatch(batchId);
            //LOG.trace("Deleting data for BatchId " + batchId + " " + progress + " (" + resourceByExchangeBatchList.size() + " resources)");

            for (ResourceByExchangeBatch resource : resourceByExchangeBatchList) {

                //populate the resource entry util class with the keys we'll need to delete the resource
                ResourceEntry resourceEntry = new ResourceEntry();
                resourceEntry.setServiceId(service.getId());
                resourceEntry.setSystemId(systemId);
                resourceEntry.setResourceType(resource.getResourceType());
                resourceEntry.setResourceId(resource.getResourceId());
                resourceEntry.setVersion(resource.getVersion());
                resourceEntry.setBatchId(batchId);

                //bump the actual delete from the DB into the threadpool
                DeleteResourceTask callable = new DeleteResourceTask(resourceEntry);
                List<ThreadPoolError> errors = threadPool.submit(callable);
                handleErrors(errors);
            }
        }

        //mark any transform audits as deleted
        List<ExchangeTransformAudit> transformAudits = auditRepository.getAllExchangeTransform(service.getId(), systemId, exchangeId);
        for (ExchangeTransformAudit transformAudit: transformAudits) {
            if (transformAudit.getDeleted() == null) {
                transformAudit.setDeleted(new Date());
                auditRepository.save(transformAudit);
            }
        }

        //add an event to the exchange to say what we did
        ExchangeEvent exchangeEvent = new ExchangeEvent();
        exchangeEvent.setExchangeId(exchangeId);
        exchangeEvent.setTimestamp(new Date());
        exchangeEvent.setEventDesc("All data deleted from repository");
        auditRepository.save(exchangeEvent);
    }

    /*public void deleteData() throws Exception {
        LOG.info("Deleting data for service " + service.getId());

        //get all the transform audits and sum up the number of batches ever created, so we know what we're aiming to delete
        List<ExchangeTransformAudit> transformAudits = getTransformAudits();
        int countBatches = 0;
        for (ExchangeTransformAudit exchangeAudit: transformAudits)
            if (exchangeAudit.getNumberBatchesCreated() != null)
                countBatches += exchangeAudit.getNumberBatchesCreated();

        LOG.trace("Found " + transformAudits.size() + " transform audits with " + countBatches + " batches to delete");

        //first, get rid of all the FHIR resource data
        int countBatchesDone = 0;
        ThreadPool threadPool = new ThreadPool(5, 1000);
        HashSet<UUID> exchangeIdsDone = new HashSet<>();

        for (ExchangeTransformAudit exchangeAudit: transformAudits) {

            UUID exchangeId = exchangeAudit.getExchangeId();

            //although we're processing by exchange audits, we can only get the batch IDs for an exchange
            //as a whole, so if we have two exchange audits for the same exchange (i.e. it was re-processed)
            //then only do the deletes for the batch IDs the first time
            if (!exchangeIdsDone.contains(exchangeId)) {
                exchangeIdsDone.add(exchangeId);

                //get all batches received for each exchange
                List<UUID> batchIds = getBatchIds(exchangeAudit.getExchangeId());
                for (UUID batchId : batchIds) {

                    countBatchesDone++;
                    progress = new DecimalFormat("###.##").format(((double)countBatchesDone / (double)countBatches) * 100d) + "%";

                    List<ResourceByExchangeBatch> resourceByExchangeBatchList = resourceRepository.getResourcesForBatch(batchId);
                    LOG.trace("Deleting data for BatchId " + batchId + " " + progress + " (" + resourceByExchangeBatchList.size() + " resources)");

                    for (ResourceByExchangeBatch resource : resourceByExchangeBatchList) {

                        //populate the resource entry util class with the keys we'll need to delete the resource
                        ResourceEntry resourceEntry = new ResourceEntry();
                        resourceEntry.setServiceId(service.getId());
                        resourceEntry.setSystemId(exchangeAudit.getSystemId());
                        resourceEntry.setResourceType(resource.getResourceType());
                        resourceEntry.setResourceId(resource.getResourceId());
                        resourceEntry.setVersion(resource.getVersion());
                        resourceEntry.setBatchId(batchId);

                        //bump the actual delete from the DB into the threadpool
                        DeleteResourceTask callable = new DeleteResourceTask(resourceEntry);
                        List<ThreadPoolError> errors = threadPool.submit(callable);
                        handleErrors(errors);
                    }
                }

                //add an event to the exchange to say what we did
                ExchangeEvent exchangeEvent = new ExchangeEvent();
                exchangeEvent.setExchangeId(exchangeAudit.getExchangeId());
                exchangeEvent.setTimestamp(new Date());
                exchangeEvent.setEventDesc("All data deleted from repository");
                auditRepository.save(exchangeEvent);
            }

            //mark the transform audit as deleted
            exchangeAudit.setDeleted(new Date());
            auditRepository.save(exchangeAudit);
        }

        List<ThreadPoolError> errors = threadPool.waitAndStop();
        handleErrors(errors);

        //then tidy remove any remaining data and mark the audits as deleted
        this.progress = "Resources deleted - finishing up";
        for (UUID systemId: getSystemsIds()) {
            LOG.trace("Deleting remaining data for service ID {} and system ID {}", service.getId(), systemId);

            //delete any transform summary, since all errors are now gone
            ExchangeTransformErrorState summary = auditRepository.getErrorState(service.getId(), systemId);
            if (summary != null) {
                auditRepository.delete(summary);
            }

            //get rid of the patient identity record too (patient_identifier_by_local_id)
            PatientSearchHelper.delete(service.getId(), systemId);
            //patientIdentifierRepository.hardDeleteForService(service.getId(), systemId);
        }

        this.isComplete = true;
    }*/


    private void handleErrors(List<ThreadPoolError> errors) throws Exception {
        if (errors == null || errors.isEmpty()) {
            return;
        }

        //if we've had multiple exceptions, this will only log the first, but the first exception is the one that's interesting
        for (ThreadPoolError error: errors) {
            Exception exception = error.getException();
            throw exception;
        }
    }


    private List<ExchangeTransformAudit> getTransformAudits() throws Exception {

        List<ExchangeTransformAudit> ret = new ArrayList<>();
        for (UUID systemId: getSystemsIds()) {

            List<ExchangeTransformAudit> exchangeAudits = getExchangeTransformAudits(systemId);
            ret.addAll(exchangeAudits);
        }
        return ret;
    }

    private List<UUID> getBatchIds(UUID exchangeId) {

        List<ExchangeBatch> batches = exchangeBatchRepository.retrieveForExchangeId(exchangeId);

        return batches
                .stream()
                .map(t -> t.getBatchId())
                .collect(Collectors.toList());
    }

    private List<ExchangeTransformAudit> getExchangeTransformAudits(UUID systemId) throws Exception {

        List<ExchangeTransformAudit> transformAudits = auditRepository.getAllExchangeTransformAudits(service.getId(), systemId);

        //sort the transforms so we delete in DESCENDING order of received exchange
        //and remove any that are already deleted
        return transformAudits
                .stream()
                .sorted((auditOne, auditTwo) -> auditTwo.getStarted().compareTo(auditOne.getStarted()))
                //.filter(t -> t.getDeleted() == null) //include deleted ones, since we want to make sure EVERYTHING is deleted
                .collect(Collectors.toList());
    }

    /**
     * returns the UUIDs of the systems linked to our service
     */
    private List<UUID> getSystemsIds() throws Exception {

        List<UUID> ret = new ArrayList<>();

        List<JsonServiceInterfaceEndpoint> endpoints = ObjectMapperPool.getInstance().readValue(service.getEndpoints(), new TypeReference<List<JsonServiceInterfaceEndpoint>>() {});
        for (JsonServiceInterfaceEndpoint endpoint: endpoints) {

            UUID endpointSystemId = endpoint.getSystemUuid();
            ret.add(endpointSystemId);
        }
        return ret;
    }

    public String getProgress() {
        return progress;
    }

    public boolean isComplete() {
        return isComplete;
    }


    /**
     * thread pool runnable to actually perform the delete, so we can do them in parallel
     */
    class DeleteResourceTask implements Callable {

        private ResourceEntry resourceEntry = null;

        public DeleteResourceTask(ResourceEntry resourceEntry) {
            this.resourceEntry = resourceEntry;
        }

        @Override
        public Object call() throws Exception {
            try {
                resourceRepository.hardDelete(resourceEntry);
            } catch (Exception ex) {
                throw new Exception("Exception deleting " + resourceEntry.getResourceType() + " " + resourceEntry.getResourceId(), ex);
            }

            return null;
        }
    }
}
