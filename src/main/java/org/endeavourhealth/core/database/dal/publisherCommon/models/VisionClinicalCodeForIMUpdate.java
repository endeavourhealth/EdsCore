package org.endeavourhealth.core.database.dal.publisherCommon.models;


public class VisionClinicalCodeForIMUpdate {

    private String readTerm;
    private String readCode;
    private Long snomedConceptId;
    private boolean isVisionCode;

    public VisionClinicalCodeForIMUpdate() {
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

    public boolean getIsVisionCode() {
        return isVisionCode;
    }

    public void setIsVisionCode(boolean visionCode) {
        isVisionCode = visionCode;
    }

}
