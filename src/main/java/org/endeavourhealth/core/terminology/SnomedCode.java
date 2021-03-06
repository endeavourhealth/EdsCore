package org.endeavourhealth.core.terminology;

import org.endeavourhealth.common.fhir.FhirCodeUri;
import org.hl7.fhir.instance.model.Coding;

public class SnomedCode {

    private String conceptCode = null;
    private String term = null;

    public SnomedCode(String conceptCode, String term) {
        this.conceptCode = conceptCode;
        this.term = term;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public String getTerm() {
        return term;
    }

    public String getSystem() {
        return FhirCodeUri.CODE_SYSTEM_SNOMED_CT;
    }

    public Coding toCoding() {
        return new Coding()
                .setSystem(this.getSystem())
                .setDisplay(this.getTerm())
                .setCode(this.getConceptCode());
    }

    @Override
    public String toString() {
        return term + " (" + conceptCode + ")";
    }
}
