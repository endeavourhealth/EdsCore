package org.endeavourhealth.core.fhirStorage;

import org.endeavourhealth.common.utility.ThreadPool;
import org.endeavourhealth.common.utility.ThreadPoolError;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.admin.models.Service;
import org.endeavourhealth.core.database.dal.audit.ExchangeBatchDalI;
import org.endeavourhealth.core.database.dal.audit.ExchangeDalI;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeBatch;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeEvent;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeTransformAudit;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeTransformErrorState;
import org.endeavourhealth.core.database.dal.ehr.ResourceDalI;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class FhirDeletionService {
    private static final Logger LOG = LoggerFactory.getLogger(FhirDeletionService.class);

    private final ExchangeBatchDalI exchangeBatchRepository = DalProvider.factoryExchangeBatchDal();
    private final ExchangeDalI auditRepository = DalProvider.factoryExchangeDal();
    private final ResourceDalI resourceRepository = DalProvider.factoryResourceDal();
    private final Service service;
    private final UUID systemId;

    private String progress = null;
    private boolean isComplete = false;
    private List<UUID> exchangeIdsToDelete = null;
    private int countBatchesToDelete = 0;
    private int countBatchesDeleted = 0;
    private ThreadPool threadPool = null;

    public FhirDeletionService(Service service, UUID systemId) {
        this.service = service;
        this.systemId = systemId;
        this.progress = "Starting";
    }

    public void deleteData() throws Exception {
        LOG.info("Deleting data for service " + service.getName() + " " + service.getId());

        retrieveExchangeIds();

        if (exchangeIdsToDelete.isEmpty()) {
            LOG.trace("No exchanges to delete");
            this.progress = "Aborted - nothing to delete";
            this.isComplete = true;
            return;
        }

        //count exactly how many exchanges there are over all the systems
        int countExchanges = exchangeIdsToDelete.size();
        LOG.trace("Found " + countExchanges + " exchanges with " + countBatchesToDelete + " batches to delete");

        this.threadPool = new ThreadPool(5, 1000);

        //start looping through our exchange IDs, backwards, so we delete data in reverse order
        for (int i=exchangeIdsToDelete.size()-1; i>=0; i--) {
            UUID exchangeId = exchangeIdsToDelete.get(i);
            deleteExchange(exchangeId);
        }

        List<ThreadPoolError> errors = threadPool.waitAndStop();
        handleErrors(errors);

        //then tidy remove any remaining cassandra and mark the audits as deleted
        this.progress = "Resources deleted - finishing up";

        //delete the error state object for the service and system
        ExchangeTransformErrorState summary = auditRepository.getErrorState(service.getId(), systemId);
        if (summary != null) {
            auditRepository.delete(summary);
        }

        this.isComplete = true;
    }

    private void retrieveExchangeIds() throws Exception {

        ExchangeDalI exchangeDal = DalProvider.factoryExchangeDal();
        this.exchangeIdsToDelete = exchangeDal.getExchangeIdsForService(service.getId(), systemId);

        this.countBatchesToDelete = 0;
        for (UUID exchangeId: exchangeIdsToDelete) {
            List<ExchangeTransformAudit> audits = exchangeDal.getAllExchangeTransformAudits(service.getId(), systemId, exchangeId);
            for (ExchangeTransformAudit audit: audits) {
                Integer batchesCreated = audit.getNumberBatchesCreated();

                if (batchesCreated != null) {
                    countBatchesToDelete += batchesCreated.intValue();
                }
            }
        }
    }

    /*private void retrieveExchangeIds() throws Exception {

        countBatchesToDelete = 0;
        HashSet<UUID> hsExchangeIds = new HashSet<>();
        List<UUID> exchangeIds = new ArrayList<>();

        ExchangeDalI exchangeDal = DalProvider.factoryExchangeDal();

        List<ExchangeTransformAudit> audits = exchangeDal.getAllExchangeTransformAuditsForService(service.getId(), systemId);
        for (ExchangeTransformAudit audit: audits) {

            UUID exchangeId = audit.getExchangeId();
            Integer batchesCreated = audit.getNumberBatchesCreated();

            if (batchesCreated != null) {
                countBatchesToDelete += batchesCreated.intValue();
            }

            if (!hsExchangeIds.contains(exchangeId)) {
                hsExchangeIds.add(exchangeId);
                exchangeIds.add(exchangeId);
            }
        }

        //the exchange IDs will come off the DB in reverse order (i.e. most recent first, so reverse them)
        this.exchangeIdsToDelete = new ArrayList<>();
        for (int i=exchangeIds.size()-1; i>=0; i--) {
            this.exchangeIdsToDelete.add(exchangeIds.get(i));
        }
    }*/



    private void deleteExchange(UUID exchangeId) throws Exception {

        //get all batches received for each exchange
        List<UUID> batchIds = getBatchIds(exchangeId);
        LOG.trace("Deleting data for exchangeId " + exchangeId + " with " + batchIds.size() + " batches");

        for (UUID batchId : batchIds) {

            countBatchesDeleted ++;
            progress = new DecimalFormat("###.##").format(((double)countBatchesDeleted / (double)countBatchesToDelete) * 100d) + "%";

            UUID serviceId = service.getId();
            List<ResourceWrapper> resourceByExchangeBatchList = resourceRepository.getResourcesForBatch(serviceId, batchId);
            //LOG.trace("Deleting cassandra for BatchId " + batchId + " " + progress + " (" + resourceByExchangeBatchList.size() + " resources)");

            for (ResourceWrapper resource : resourceByExchangeBatchList) {

                //bump the actual delete from the DB into the threadpool
                DeleteResourceTask callable = new DeleteResourceTask(resource);
                List<ThreadPoolError> errors = threadPool.submit(callable);
                handleErrors(errors);
            }
        }

        //mark any subscriberTransform audits as deleted
        List<ExchangeTransformAudit> transformAudits = auditRepository.getAllExchangeTransformAudits(service.getId(), systemId, exchangeId);
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
        exchangeEvent.setEventDesc("All cassandra deleted from repository");
        auditRepository.save(exchangeEvent);
    }



    private void handleErrors(List<ThreadPoolError> errors) throws Exception {
        if (errors == null || errors.isEmpty()) {
            return;
        }

        //if we've had multiple exceptions, this will only log the first, but the first exception is the one that's interesting
        for (ThreadPoolError error: errors) {
            Throwable cause = error.getException();
            //the cause may be an Exception or Error so we need to explicitly
            //cast to the right type to throw it without changing the method signature
            if (cause instanceof Exception) {
                throw (Exception)cause;
            } else if (cause instanceof Error) {
                throw (Error)cause;
            }
        }
    }



    private List<UUID> getBatchIds(UUID exchangeId) throws Exception {

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
    /*private List<UUID> getSystemsIds() throws Exception {

        List<UUID> ret = new ArrayList<>();

        List<JsonServiceInterfaceEndpoint> endpoints = ObjectMapperPool.getInstance().readValue(service.getEndpoints(), new TypeReference<List<JsonServiceInterfaceEndpoint>>() {});
        for (JsonServiceInterfaceEndpoint endpoint: endpoints) {

            UUID endpointSystemId = endpoint.getSystemUuid();
            ret.add(endpointSystemId);
        }
        return ret;
    }*/

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

        private ResourceWrapper resourceEntry = null;

        public DeleteResourceTask(ResourceWrapper resourceEntry) {
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
