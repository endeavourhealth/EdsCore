package org.endeavourhealth.core.fhirStorage;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.audit.ExchangeBatchDalI;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeBatch;
import org.endeavourhealth.core.database.dal.eds.PatientLinkDalI;
import org.endeavourhealth.core.database.dal.eds.PatientSearchDalI;
import org.endeavourhealth.core.database.dal.ehr.ResourceDalI;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.fhirStorage.exceptions.SerializationException;
import org.endeavourhealth.core.fhirStorage.exceptions.UnprocessableEntityException;
import org.endeavourhealth.core.fhirStorage.metadata.MetadataFactory;
import org.endeavourhealth.core.fhirStorage.metadata.PatientCompartment;
import org.endeavourhealth.core.fhirStorage.metadata.ResourceMetadata;
import org.hl7.fhir.instance.model.EpisodeOfCare;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class FhirStorageService {
    private static final Logger LOG = LoggerFactory.getLogger(FhirStorageService.class);
    //private static final String SCHEMA_VERSION = "0.1";

    private static final ResourceDalI resourceRepository = DalProvider.factoryResourceDal();
    private static final PatientLinkDalI patientLinkDal = DalProvider.factoryPatientLinkDal();
    private static final PatientSearchDalI patientSearchDal = DalProvider.factoryPatientSearchDal();
    private static final ExchangeBatchDalI exchangeBatchRepository = DalProvider.factoryExchangeBatchDal();
    private static final ReentrantLock exchangeBatchLock = new ReentrantLock();

    private final UUID serviceId;
    private final UUID systemId;

    //just for getting some metrics out
    /*private ReentrantLock countLock = new ReentrantLock();
    private long millisCutoff = -1;
    private long count = 0;*/

    public FhirStorageService(UUID serviceId, UUID systemId) {
        this.serviceId = serviceId;
        this.systemId = systemId;
    }


    public List<ResourceWrapper> saveResources(UUID exchangeId, Map<Resource, ExchangeBatch> resourcesAndBatches, Set<Resource> definitelyNewResources) throws Exception {

        List<ResourceWrapper> wrappersToSave = new ArrayList<>();
        List<ExchangeBatch> exchangeBatchesToSave = new ArrayList<>();

        for (Resource resource: resourcesAndBatches.keySet()) {
            Validate.resourceId(resource);

            ExchangeBatch exchangeBatch = resourcesAndBatches.get(resource);
            UUID batchId = exchangeBatch.getBatchId();
            ResourceWrapper resourceWrapper = createResourceEntry(resource, exchangeId, batchId);

            //if we're updating a resource but there's no change, don't commit the save
            //this is because Emis send us up to thousands of duplicated resources each day
            boolean isDefinitelyNewResource = definitelyNewResources != null && definitelyNewResources.contains(resource);

            /*if (resource.getResourceType() == ResourceType.Patient) {
                LOG.trace("Saving " + resourceWrapper + " definitely new = " + isDefinitelyNewResource);
            }*/

            if (shouldSaveResource(resourceWrapper, isDefinitelyNewResource)) {
                wrappersToSave.add(resourceWrapper);

                //save the batch if necessary
                if (exchangeBatch.isNeedsSaving()) {
                    exchangeBatchesToSave.add(exchangeBatch);
                }

                /*if (resource.getResourceType() == ResourceType.Patient) {
                    LOG.trace("Will save resource, batch needs saving = " + exchangeBatch.isNeedsSaving());
                }*/

            } else {

                /*if (resource.getResourceType() == ResourceType.Patient) {
                    LOG.trace("Will not save resource");
                }*/
            }
        }

        if (wrappersToSave.isEmpty()) {
            return wrappersToSave;
        }

        //we must save any exchange batches for the resources themselves
        saveExchangeBatches(exchangeBatchesToSave);

        //call out to our patient search and person matching services
        //moved this to happen BEFORE we commit the resource to the DB, so killing the app at the wrong time won't leave patient_search behind
        for (Resource resource: resourcesAndBatches.keySet()) {
            if (resource instanceof Patient) {
                //LOG.info("Updating PATIENT_LINK with PATIENT resource " + resource.getId());
                try {
                    patientLinkDal.updatePersonId(serviceId, (Patient) resource);
                } catch (Throwable t) {
                    LOG.error("Exception updating patient link table for " + resource.getResourceType() + " " + resource.getId());
                    throw t;
                }

                try {
                    patientSearchDal.update(serviceId, (Patient) resource);
                } catch (Throwable t) {
                    LOG.error("Exception updating patient search table for " + resource.getResourceType() + " " + resource.getId());
                    throw t;
                }

            } else if (resource instanceof EpisodeOfCare) {
                //LOG.info("Updating PATIENT_SEARCH with EPISODEOFCARE resource " + resource.getId());

                try {
                    patientSearchDal.update(serviceId, (EpisodeOfCare) resource);
                } catch (Throwable t) {
                    LOG.error("Exception updating patient search table for " + resource.getResourceType() + " " + resource.getId());
                    throw t;
                }
            }
        }

        //then save the resources
        //LOG.trace("Saving " + wrappersToSave.size() + " resources");
        resourceRepository.save(wrappersToSave);

        return wrappersToSave;
    }

    private void saveExchangeBatches(List<ExchangeBatch> exchangeBatchesToSave) throws Exception {

        //the storage service is invoked from multiple threads, so we need to lock to ensure
        //all batches are saved before any resources
        try {
            exchangeBatchLock.lock();

            //now we're locked, we should double-check that batches haven't been saved yet
            for (int i=exchangeBatchesToSave.size()-1; i>=0; i--) {
                ExchangeBatch exchangeBatch = exchangeBatchesToSave.get(i);
                if (!exchangeBatch.isNeedsSaving()) {
                    exchangeBatchesToSave.remove(i);
                }
            }

            if (exchangeBatchesToSave.isEmpty()) {
                return;
            }

            exchangeBatchRepository.save(exchangeBatchesToSave);

            //mark as saved
            for (ExchangeBatch exchangeBatch: exchangeBatchesToSave) {
                exchangeBatch.setNeedsSaving(false);
            }

        } finally {
            exchangeBatchLock.unlock();
        }
    }

    /*public ResourceWrapper exchangeBatchUpdate(UUID exchangeId, ExchangeBatch exchangeBatch, Resource resource) throws Exception {
        return exchangeBatchUpdate(exchangeId, exchangeBatch, resource, false);
    }

    public ResourceWrapper exchangeBatchUpdate(UUID exchangeId, ExchangeBatch exchangeBatch, Resource resource, boolean isDefinitelyNewResource) throws Exception {

        Map<Resource, ExchangeBatch> resourcesAndBatches = new HashMap<>();
        resourcesAndBatches.put(resource, exchangeBatch);

        Set<Resource> definitelyNewResources = new HashSet<>();
        if (isDefinitelyNewResource) {
            definitelyNewResources.add(resource);
        }

        List<ResourceWrapper> wrappers = saveResources(exchangeId, resourcesAndBatches, definitelyNewResources);
        if (wrappers.isEmpty()) {
            return null;

        } else if (wrappers.size() > 1) {
            throw new Exception("Got " + wrappers.size() + " back from saving resource " + resource.getResourceType() + " " + resource.getId());

        } else {
            return wrappers.get(0);
        }
    }*/

    /*
    public ResourceWrapper exchangeBatchUpdate(UUID exchangeId, UUID batchId, Resource resource) throws Exception {
        return exchangeBatchUpdate(exchangeId, batchId, resource, false);
    }

    public ResourceWrapper exchangeBatchUpdate(UUID exchangeId, UUID batchId, Resource resource, boolean isDefinitelyNewResource) throws Exception {
        Validate.resourceId(resource);

        ResourceWrapper entry = createResourceEntry(resource, exchangeId, batchId);

        //if we're updating a resource but there's no change, don't commit the save
        //this is because Emis send us up to thousands of duplicated resources each day
        if (!shouldSaveResource(entry, isDefinitelyNewResource)) {
            return null;
        }

        //FhirResourceHelper.updateMetaTags(resource, entry.getVersion(), entry.getCreatedAt());

        resourceRepository.save(entry);

        //call out to our patient search and person matching services
        if (resource instanceof Patient) {
            //LOG.info("Updating PATIENT_LINK with PATIENT resource " + resource.getId());
            try {
                patientLinkDal.updatePersonId(serviceId, (Patient)resource);
            } catch (Throwable t) {
                LOG.error("Exception updating patient link table for " + resource.getResourceType() + " " + resource.getId());
                throw t;
            }

            try {
                patientSearchDal.update(serviceId, (Patient)resource);
            } catch (Throwable t) {
                LOG.error("Exception updating patient search table for " + resource.getResourceType() + " " + resource.getId());
                throw t;
            }

        } else if (resource instanceof EpisodeOfCare) {
            //LOG.info("Updating PATIENT_SEARCH with EPISODEOFCARE resource " + resource.getId());

            try {
                patientSearchDal.update(serviceId, (EpisodeOfCare)resource);
            } catch (Throwable t) {
                LOG.error("Exception updating patient search table for " + resource.getResourceType() + " " + resource.getId());
                throw t;
            }
        }

        return entry;
    }*/

    public List<ResourceWrapper> deleteResources(UUID exchangeId, Map<Resource, ExchangeBatch> resourcesAndBatches, Set<Resource> definitelyNewResources) throws Exception {

        List<ResourceWrapper> wrappersToDelete = new ArrayList<>();
        List<ExchangeBatch> exchangeBatchesToSave = new ArrayList<>();

        for (Resource resource: resourcesAndBatches.keySet()) {
            Validate.resourceId(resource);

            ExchangeBatch exchangeBatch = resourcesAndBatches.get(resource);
            UUID batchId = exchangeBatch.getBatchId();
            ResourceWrapper resourceWrapper = createResourceEntry(resource, exchangeId, batchId);

            //if we're updating a resource but there's no change, don't commit the save
            //this is because Emis send us up to thousands of duplicated resources each day
            boolean isDefinitelyNewResource = definitelyNewResources != null && definitelyNewResources.contains(resource);

            /*if (resource.getResourceType() == ResourceType.Patient) {
                LOG.trace("Deleting " + resourceWrapper + " definitely new = " + isDefinitelyNewResource);
            }*/

            if (shouldDeleteResource(resourceWrapper, isDefinitelyNewResource)) {
                wrappersToDelete.add(resourceWrapper);

                //save the batch if necessary
                if (exchangeBatch.isNeedsSaving()) {
                    exchangeBatchesToSave.add(exchangeBatch);
                }

                /*if (resource.getResourceType() == ResourceType.Patient) {
                    LOG.trace("Will delete resource, batch needs saving = " + exchangeBatch.isNeedsSaving());
                }*/

            } else {
                /*if (resource.getResourceType() == ResourceType.Patient) {
                    LOG.trace("Will not delete resource");
                }*/
            }
        }

        if (wrappersToDelete.isEmpty()) {
            return wrappersToDelete;
        }

        //we must save any exchange batches for the resources themselves
        saveExchangeBatches(exchangeBatchesToSave);

        //if we're deleting the patient, then delete the row from the patient_search table
        //only doing this for Patient deletes, not Episodes, since a deleted Episode shoudn't remove the patient from the search
        //moved this to happen BEFORE we commit the resource to the DB, so killing the app at the wrong time won't leave patient_search behind
        for (Resource resource: resourcesAndBatches.keySet()) {
            if (resource instanceof Patient) {
                patientSearchDal.deletePatient(serviceId, (Patient) resource);

            } else if (resource instanceof EpisodeOfCare) {
                patientSearchDal.deleteEpisode(serviceId, (EpisodeOfCare) resource);
            }
        }

        //now delete the resources
        //LOG.trace("Deleting " + wrappersToDelete.size() + " resources");
        resourceRepository.delete(wrappersToDelete);

        return wrappersToDelete;
    }


    /*public ResourceWrapper exchangeBatchDelete(UUID exchangeId, ExchangeBatch exchangeBatch, Resource resource) throws Exception {
        return exchangeBatchDelete(exchangeId, exchangeBatch, resource, false);
    }

    public ResourceWrapper exchangeBatchDelete(UUID exchangeId, ExchangeBatch exchangeBatch, Resource resource, boolean isDefinitelyNewResource) throws Exception {

        Map<Resource, ExchangeBatch> resourcesAndBatches = new HashMap<>();
        resourcesAndBatches.put(resource, exchangeBatch);

        Set<Resource> definitelyNewResources = new HashSet<>();
        if (isDefinitelyNewResource) {
            definitelyNewResources.add(resource);
        }

        List<ResourceWrapper> wrappers = deleteResources(exchangeId, resourcesAndBatches, definitelyNewResources);
        if (wrappers.isEmpty()) {
            return null;

        } else if (wrappers.size() > 1) {
            throw new Exception("Got " + wrappers.size() + " back from saving resource " + resource.getResourceType() + " " + resource.getId());

        } else {
            return wrappers.get(0);
        }
    }*/

    /*public ResourceWrapper exchangeBatchDelete(UUID exchangeId, UUID batchId, Resource resource) throws Exception {
        return exchangeBatchDelete(exchangeId, batchId, resource, false);
    }

    public ResourceWrapper exchangeBatchDelete(UUID exchangeId, UUID batchId, Resource resource, boolean isDefinitelyNewResource) throws Exception {
        Validate.resourceId(resource);

        ResourceWrapper entry = createResourceEntry(resource, exchangeId, batchId);

        //if we're updating a resource but there's no change, don't commit the save
        //this is because Emis send us up to thousands of duplicated resources each day
        if (!shouldDeleteResource(entry, isDefinitelyNewResource)) {
            return null;
        }

        resourceRepository.delete(entry);

        //if we're deleting the patient, then delete the row from the patient_search table
        //only doing this for Patient deletes, not Episodes, since a deleted Episode shoudn't remove the patient from the search
        if (resource instanceof Patient) {
            patientSearchDal.deletePatient(serviceId, (Patient) resource);

        } else if (resource instanceof EpisodeOfCare) {
            patientSearchDal.deleteEpisode(serviceId, (EpisodeOfCare) resource);

            try {
                patientSearchDal.update(serviceId, (EpisodeOfCare) resource);
            } catch (Throwable t) {
                LOG.error("Exception updating patient search table for " + resource.getResourceType() + " " + resource.getId());
                throw t;
            }
        }


        return entry;
    }*/


    private boolean shouldDeleteResource(ResourceWrapper entry, boolean isDefinitelyNewResource) throws Exception {

        //if this is the first time we've heard of this resource, then
        //there's no point running a delete for it
        if (isDefinitelyNewResource) {
            return false;
        }

        //check the checksum first, so we only do a very small read from the DB
        Long previousChecksum = resourceRepository.getResourceChecksum(serviceId, entry.getResourceType(), entry.getResourceId());
        if (previousChecksum == null) {
            //if the previous checksum is null then we've already deleted it, so we don't need to delete again
            return false;
        }

        return true;
    }

    private boolean shouldSaveResource(ResourceWrapper entry, boolean isDefinitelyNewResource) throws Exception {

        //if it's a brand new resource, we always want to save it
        if (isDefinitelyNewResource) {
            return true;
        }

        //check the checksum first, so we only do a very small read from the DB
        Long previousChecksum = resourceRepository.getResourceChecksum(serviceId, entry.getResourceType(), entry.getResourceId());
        if (previousChecksum == null
                || previousChecksum.longValue() != entry.getResourceChecksum()) {
            //if we don't have a previous checksum (which can happen if we keep re-running transforms
            //that fail, because it thinks it's not a new resource when it actually is) or the checksum differs,
            //then we want to save this resource
            return true;
        }

        //if the checksum is the same, we need to do a full compare
        ResourceWrapper previousVersion = resourceRepository.getCurrentVersion(serviceId, entry.getResourceType(), entry.getResourceId());

        //if it was previously deleted, or for some reason we didn't know our resource was definitely new, then save it
        if (previousVersion == null) {
            return true;
        }

        String previousData = previousVersion.getResourceData();
        if (previousData == null
                || entry.getResourceData()== null
                || !previousData.equals(entry.getResourceData())) {
            return true;
        }

        //if we get here, then the resource we're trying to save is completely identical to the last instance
        //of that same resource we previously saved to the DB, so don't save it again
        return false;
    }


    private ResourceWrapper createResourceEntry(Resource resource, UUID exchangeId, UUID batchId) throws UnprocessableEntityException, SerializationException {
        ResourceMetadata metadata = MetadataFactory.createMetadata(resource);
        String resourceJson = FhirSerializationHelper.serializeResource(resource);

        ResourceWrapper entry = new ResourceWrapper();
        entry.setResourceId(FhirResourceHelper.getResourceId(resource));
        entry.setResourceType(FhirResourceHelper.getResourceType(resource));
        entry.setVersion(UUID.randomUUID());
        entry.setCreatedAt(new Date());
        entry.setServiceId(serviceId);
        entry.setSystemId(systemId);
        //entry.setSchemaVersion(SCHEMA_VERSION);
        //entry.setResourceMetadata(JsonSerializer.serialize(metadata));
        entry.setResourceMetadata(""); //we never use the metadata so don't save to the DB
        entry.setResourceData(resourceJson);
        entry.setResourceChecksum(generateChecksum(resourceJson));
        entry.setExchangeId(exchangeId);
        entry.setExchangeBatchId(batchId);

        if (metadata instanceof PatientCompartment) {
            entry.setPatientId(((PatientCompartment) metadata).getPatientId());
        }

        return entry;
    }

    public static long generateChecksum(String data) {
        byte[] bytes = data.getBytes();
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }

}
