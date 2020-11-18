package org.endeavourhealth.core.database.dal.publisherCommon.models;

public class TppClinicalCodeForIMUpdate {

    private String ctv3Term;
    private String ctv3Code;
    private Long snomedConceptId;

    public TppClinicalCodeForIMUpdate() {
    }

    public String getCtv3Term() {
        return ctv3Term;
    }

    public void setCtv3Term(String ctv3Term) {
        this.ctv3Term = ctv3Term;
    }

    public String getCtv3Code() {
        return ctv3Code;
    }

    public void setCtv3Code(String ctv3Code) {
        this.ctv3Code = ctv3Code;
    }

    public Long getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(Long snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }

}
