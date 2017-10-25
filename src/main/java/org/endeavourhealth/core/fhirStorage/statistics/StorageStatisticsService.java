package org.endeavourhealth.core.fhirStorage.statistics;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.ehr.ResourceDalI;
import org.endeavourhealth.core.database.dal.ehr.ResourceMetadataIterator;
import org.endeavourhealth.core.fhirStorage.metadata.PatientMetadata;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorageStatisticsService {

    private final ResourceDalI repository;

    public StorageStatisticsService() {
        this.repository = DalProvider.factoryResourceDal();
    }

    public PatientStatistics getPatientStatistics(UUID serviceId, UUID systemId) throws Exception {
        return createPatientStatistics(serviceId, systemId);
    }

    public List<ResourceStatistics> getResourceStatistics(UUID serviceId, UUID systemId, List<String> resourceTypes) throws Exception {
        List<ResourceStatistics> results = new ArrayList<>();

        for (String resourceType :resourceTypes) {
            results.add(createResourceStatistics(serviceId, systemId, resourceType));
        }

        return results;
    }

    private PatientStatistics createPatientStatistics(UUID serviceId, UUID systemId) throws Exception {
        long totalCount = 0;
        long activeCount = 0;
        long deceasedCount = 0;

        ResourceMetadataIterator<PatientMetadata> patientMetadataIterator = repository.getMetadataByService(serviceId,
                systemId,
                ResourceType.Patient.toString(),
                PatientMetadata.class);

        while(patientMetadataIterator.hasNext()) {
            PatientMetadata patientMetadata = patientMetadataIterator.next();

            if (patientMetadata != null) {
                if (patientMetadata.isDeceased()) {
                    deceasedCount++;
                }
                else if (patientMetadata.isActive()) {
                    activeCount++;
                }
                totalCount++;
            }

        }

        PatientStatistics statistics = new PatientStatistics();
        statistics.setTotalCount(totalCount);
        statistics.setActiveCount(activeCount);
        statistics.setDeceasedCount(deceasedCount);
        return statistics;
    }

    private ResourceStatistics createResourceStatistics(UUID serviceId, UUID systemId, String resourceType) throws Exception {
        ResourceStatistics statistics = new ResourceStatistics(resourceType);
        statistics.setTotalCount(repository.getResourceCountByService(serviceId, systemId, resourceType));
        return statistics;
    }
}
