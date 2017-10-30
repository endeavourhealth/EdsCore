package org.endeavourhealth.core.fhirStorage;

import com.datastax.driver.core.utils.UUIDs;
import org.endeavourhealth.common.utility.JsonSerializer;
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

import java.util.Date;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class FhirStorageService {
    private static final Logger LOG = LoggerFactory.getLogger(FhirStorageService.class);
    private static final String SCHEMA_VERSION = "0.1";

    private static final ResourceDalI resourceRepository = DalProvider.factoryResourceDal();
    private static final PatientLinkDalI patientLinkDal = DalProvider.factoryPatientLinkDal();
    //private final PatientIdentifierRepository identifierRepository;
    private static final PatientSearchDalI patientSearchDal = DalProvider.factoryPatientSearchDal();

    private final UUID serviceId;
    private final UUID systemId;

    public FhirStorageService(UUID serviceId, UUID systemId) {
        this.serviceId = serviceId;
        this.systemId = systemId;
    }

    public FhirResponse exchangeBatchUpdate(UUID exchangeId, UUID batchId, Resource resource, boolean isNewResource) throws Exception {
        store(resource, exchangeId, batchId, isNewResource);
        return new FhirResponse(resource);
    }

    /*public FhirResponse versionSpecificUpdate(UUID versionkey, Resource resource) throws Exception {
        Validate.resourceId(resource);
        Validate.hasVersion(versionkey);

        UUID resourceId = UUID.fromString(resource.getId());
        String resourceType = resource.getResourceType().toString();

        ResourceHistory current =  repository.getCurrentVersion(resourceType, resourceId);
        if (current == null) {
            throw new UnprocessableEntityException(String.format("Resource not found: ResourceType='%s', ResourceId='%s'", resourceType,resourceId.toString()));
        }

        Validate.isSameVersion(current.getVersion(), versionkey);

        store(resource, null, null);

        return new FhirResponse(resource);
    }*/

    public FhirResponse delete(Resource resource) throws Exception {
        delete(resource, null, null);

        return new FhirResponse(resource);
    }

    public FhirResponse exchangeBatchDelete(UUID exchangeId, UUID batchId, Resource resource) throws Exception {
        delete(resource, exchangeId, batchId);
        return new FhirResponse(resource);
    }


    private void store(Resource resource, UUID exchangeId, UUID batchId, boolean isNewResource) throws Exception {
        Validate.resourceId(resource);

        ResourceWrapper entry = createResourceEntry(resource, exchangeId, batchId);

        //if we're updating a resource but there's no change, don't commit the save
        //this is because Emis send us up to thousands of duplicated resources each day
        if (!shouldSaveResource(entry, isNewResource)) {
            return;
        }

        FhirResourceHelper.updateMetaTags(resource, entry.getVersion(), entry.getCreatedAt());

        resourceRepository.save(entry);

        //call out to our patient search and person matching services
        if (resource instanceof Patient) {

            //LOG.info("Updating PATIENT_LINK with PATIENT resource " + resource.getId());

            patientLinkDal.updatePersonId((Patient)resource);

            //LOG.info("Updating PATIENT_SEARCH with PATIENT resource " + resource.getId());

            patientSearchDal.update(serviceId, systemId, (Patient)resource);

        } else if (resource instanceof EpisodeOfCare) {

            //LOG.info("Updating PATIENT_SEARCH with EPISODEOFCARE resource " + resource.getId());

            patientSearchDal.update(serviceId, systemId, (EpisodeOfCare)resource);
        }

        /*if (resource instanceof Patient) {
            identifierRepository.savePatientIdentity((Patient)resource, serviceId, systemId);
        }*/
    }

    private boolean shouldSaveResource(ResourceWrapper entry, boolean isNewResource) throws Exception {

        //if it's a brand new resource, we always want to save it
        if (isNewResource) {
            return true;
        }

        //check the checksum first, so we only do a very small read from the DB
        Long previousChecksum = resourceRepository.getResourceChecksum(entry.getResourceType(), entry.getResourceId());
        if (previousChecksum == null
                || previousChecksum.longValue() != entry.getResourceChecksum()) {
            //if we don't have a previous checksum (which can happen if we keep re-running transforms
            //that fail, because it thinks it's not a new resource when it actually is) or the checksum differs,
            //then we want to save this resource
            return true;
        }

        //if the checksum is the same, we need to do a full compare
        ResourceWrapper previousVersion = resourceRepository.getCurrentVersion(entry.getResourceType(), entry.getResourceId());

        //if it was previously deleted, or for some reason we didn't
        if (previousVersion == null
            || previousVersion.isDeleted()) {
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

    private void delete(Resource resource, UUID exchangeId, UUID batchId) throws Exception {
        Validate.resourceId(resource);

        ResourceWrapper entry = createResourceEntry(resource, exchangeId, batchId);
        resourceRepository.delete(entry);

        //if we're deleting the patient, then delete the row from the patient_search table
        //only doing this for Patient deletes, not Episodes, since a deleted Episode shoudn't remove the patient from the search
        if (resource instanceof Patient) {
            patientSearchDal.deletePatient(serviceId, systemId, (Patient)resource);
        }
    }

    private ResourceWrapper createResourceEntry(Resource resource, UUID exchangeId, UUID batchId) throws UnprocessableEntityException, SerializationException {
        ResourceMetadata metadata = MetadataFactory.createMetadata(resource);
        String resourceJson = FhirSerializationHelper.serializeResource(resource);

        ResourceWrapper entry = new ResourceWrapper();
        entry.setResourceId(FhirResourceHelper.getResourceId(resource));
        entry.setResourceType(FhirResourceHelper.getResourceType(resource));
        entry.setVersion(createTimeBasedVersion());
        entry.setCreatedAt(new Date());
        entry.setServiceId(serviceId);
        entry.setSystemId(systemId);
        //entry.setSchemaVersion(SCHEMA_VERSION);
        entry.setResourceMetadata(JsonSerializer.serialize(metadata));
        entry.setResourceData(resourceJson);
        entry.setResourceChecksum(generateChecksum(resourceJson));
        entry.setExchangeId(exchangeId);
        entry.setExchangeBatchId(batchId);

        if (metadata instanceof PatientCompartment) {
            entry.setPatientId(((PatientCompartment) metadata).getPatientId());
        }

        return entry;
    }

    private UUID createTimeBasedVersion() {
        return UUIDs.timeBased();
    }

    public static long generateChecksum(String data) {
        byte[] bytes = data.getBytes();
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }

}
