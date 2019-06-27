package org.endeavourhealth.core.fhirStorage.metadata;

import org.hl7.fhir.instance.model.QuestionnaireResponse;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.UUID;

public class QuestionnaireResponseMetadata extends AbstractResourceMetadata implements PatientCompartment {
    private UUID patientId;

    @Override
    public UUID getPatientId() {
        return patientId;
    }

    public QuestionnaireResponseMetadata(QuestionnaireResponse resource) {
        super(resource);
        populateMetadataFromResource(resource);
    }

    private void populateMetadataFromResource(QuestionnaireResponse resource) {
        patientId = UUID.fromString(ReferenceHelper.getReferenceId(resource.getSubject(), ResourceType.Patient));
    }
}
