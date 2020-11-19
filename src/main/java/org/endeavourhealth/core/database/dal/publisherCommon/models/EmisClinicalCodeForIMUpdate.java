package org.endeavourhealth.core.database.dal.publisherCommon.models;

import java.sql.Timestamp;

public class EmisClinicalCodeForIMUpdate {

    private String readTerm;
    private String readCode;
    private Long snomedConceptId;
    private boolean isEmisCode;
    private Timestamp dateLastUpdated;

    public EmisClinicalCodeForIMUpdate() {
    }

    public String getReadTerm() {
        return readTerm;
    }

    public void setReadTerm(String readTerm) {
        this.readTerm = readTerm;
    }

    public String getReadCode() {
        return readCode;
    }

    public void setReadCode(String readCode) {
        this.readCode = readCode;
    }

    public Long getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(Long snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }

    public boolean getIsEmisCode() {
        return isEmisCode;
    }

    public void setIsEmisCode(boolean emisCode) {
        isEmisCode = emisCode;
    }

    public void setDateLastUpdated(Timestamp dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public Timestamp getDateLastUpdated() {
        return dateLastUpdated;
    }
}
