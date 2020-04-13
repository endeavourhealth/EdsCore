package org.endeavourhealth.core.database.dal.publisherCommon.models;

public class EmisDrugCode {

    private long codeId;
    private Long dmdConceptId; //may be null
    private String dmdTerm;

    public EmisDrugCode() {
    }

    public long getCodeId() {
        return codeId;
    }

    public void setCodeId(long codeId) {
        this.codeId = codeId;
    }

    public Long getDmdConceptId() {
        return dmdConceptId;
    }

    public void setDmdConceptId(Long dmdConceptId) {
        this.dmdConceptId = dmdConceptId;
    }

    public String getDmdTerm() {
        return dmdTerm;
    }

    public void setDmdTerm(String dmdTerm) {
        this.dmdTerm = dmdTerm;
    }

    @Override
    public String toString() {
        return "CodeId = " + codeId + " DM+D ID = " + dmdConceptId + " Term = [" + dmdTerm + "]";
    }
}
