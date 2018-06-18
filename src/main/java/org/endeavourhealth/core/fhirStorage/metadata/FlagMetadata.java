package org.endeavourhealth.core.fhirStorage.metadata;

import org.hl7.fhir.instance.model.Flag;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.UUID;

public class FlagMetadata extends AbstractResourceMetadata implements PatientCompartment {
    private UUID patientId;

    @Override
    public UUID getPatientId() {
        return patientId;
    }

    public FlagMetadata(Flag resource) {
        super(resource);
        populateMetadataFromResource(resource);
    }

    private void populateMetadataFromResource(Flag resource) {
        patientId = UUID.fromString(ReferenceHelper.getReferenceId(resource.getSubject(), ResourceType.Patient));
    }
}
