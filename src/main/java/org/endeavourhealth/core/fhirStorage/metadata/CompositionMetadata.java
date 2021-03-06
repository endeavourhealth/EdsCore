package org.endeavourhealth.core.fhirStorage.metadata;

import org.hl7.fhir.instance.model.Composition;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.UUID;

public class CompositionMetadata extends AbstractResourceMetadata implements PatientCompartment {
    private UUID patientId;

    @Override
    public UUID getPatientId() {
        return patientId;
    }

    public CompositionMetadata(Composition resource) {
        super(resource);
        populateMetadataFromResource(resource);
    }

    private void populateMetadataFromResource(Composition resource) {
        patientId = UUID.fromString(ReferenceHelper.getReferenceId(resource.getSubject(), ResourceType.Patient));
    }
}
