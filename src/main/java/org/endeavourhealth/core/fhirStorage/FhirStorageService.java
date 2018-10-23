package org.endeavourhealth.core.fhirStorage;

import org.endeavourhealth.core.database.dal.DalProvider;
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
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class FhirStorageService {
    private static final Logger LOG = LoggerFactory.getLogger(FhirStorageService.class);
    //private static final String SCHEMA_VERSION = "0.1";

    private static final ResourceDalI resourceRepository = DalProvider.factoryResourceDal();
    private static final PatientLinkDalI patientLinkDal = DalProvider.factoryPatientLinkDal();
    private static final PatientSearchDalI patientSearchDal = DalProvider.factoryPatientSearchDal();

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


    public List<ResourceWrapper> saveResources(UUID exchangeId, Map<Resource, UUID> resourcesAndBatchIds, Set<Resource> definitelyNewResources) throws Exception {

        List<ResourceWrapper> wrappers = new ArrayList<>();
        for (Resource resource: resourcesAndBatchIds.keySet()) {
            Validate.resourceId(resource);

            UUID batchId = resourcesAndBatchIds.get(resource);
            ResourceWrapper entry = createResourceEntry(resource, exchangeId, batchId);

            //if we're updating a resource but there's no change, don't commit the save
            //this is because Emis send us up to thousands of duplicated resources each day
            boolean isDefinitelyNewResource = definitelyNewResources != null && definitelyNewResources.contains(resource);
            if (shouldSaveResource(entry, isDefinitelyNewResource)) {
                wrappers.add(entry);
            }
        }

        if (wrappers.isEmpty()) {
            return wrappers;
        }

        resourceRepository.save(wrappers);

        //call out to our patient search and person matching services
        for (Resource resource: resourcesAndBatchIds.keySet()) {
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

        return wrappers;
    }


    public List<ResourceWrapper> deleteResources(UUID exchangeId, Map<Resource, UUID> resourcesAndBatchIds, Set<Resource> definitelyNewResources) throws Exception {

        List<ResourceWrapper> wrappers = new ArrayList<>();
        for (Resource resource: resourcesAndBatchIds.keySet()) {
            Validate.resourceId(resource);

            UUID batchId = resourcesAndBatchIds.get(resource);
            ResourceWrapper entry = createResourceEntry(resource, exchangeId, batchId);

            //if we're updating a resource but there's no change, don't commit the save
            //this is because Emis send us up to thousands of duplicated resources each day
            boolean isDefinitelyNewResource = definitelyNewResources != null && definitelyNewResources.contains(resource);
            if (shouldDeleteResource(entry, isDefinitelyNewResource)) {
                wrappers.add(entry);
            }
        }

        if (wrappers.isEmpty()) {
            return wrappers;
        }

        resourceRepository.delete(wrappers);

        //if we're deleting the patient, then delete the row from the patient_search table
        //only doing this for Patient deletes, not Episodes, since a deleted Episode shoudn't remove the patient from the search
        for (Resource resource: resourcesAndBatchIds.keySet()) {
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
        }

        return wrappers;
    }

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
    }


    public ResourceWrapper exchangeBatchDelete(UUID exchangeId, UUID batchId, Resource resource) throws Exception {
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
    }


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
