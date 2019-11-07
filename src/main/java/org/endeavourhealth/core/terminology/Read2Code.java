package org.endeavourhealth.core.terminology;

import org.endeavourhealth.common.fhir.FhirCodeUri;
import org.hl7.fhir.instance.model.Coding;

public class Read2Code {
    private String code;
    private String preferredTerm;

    public Read2Code(String code, String preferredTerm) {
        this.code = code;
        this.preferredTerm = preferredTerm;
    }

    public String getCode() {
        return code;
    }

    public String getPreferredTerm() {
        return preferredTerm;
    }

    public String getSystem() {
        return FhirCodeUri.CODE_SYSTEM_READ2;
    }

    public Coding toCoding() {
        return new Coding()
                .setSystem(this.getSystem())
                .setDisplay(this.getPreferredTerm())
                .setCode(this.getCode());
    }

    @Override
    public String toString() {
        return preferredTerm + " (" + code + ")";
    }
}
