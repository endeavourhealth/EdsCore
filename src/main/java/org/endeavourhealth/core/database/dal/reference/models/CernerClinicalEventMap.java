package org.endeavourhealth.core.database.dal.reference.models;

public class CernerClinicalEventMap {

    private String cernerCvrefCode;
    private String cernerCvrefTerm;
    private String snomedConceptId;
    private String snomedPreferredTerm;
    private String snomedDescriptionId;
    private String snomedDescriptionTerm;
    private String matchAlgorithm;

    public String getCernerCvrefCode() {
        return cernerCvrefCode;
    }

    public void setCernerCvrefCode(String cernerCvrefCode) {
        this.cernerCvrefCode = cernerCvrefCode;
    }

    public String getCernerCvrefTerm() {
        return cernerCvrefTerm;
    }

    public void setCernerCvrefTerm(String cernerCvrefTerm) {
        this.cernerCvrefTerm = cernerCvrefTerm;
    }

    public String getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(String snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }

    public String getSnomedPreferredTerm() {
        return snomedPreferredTerm;
    }

    public void setSnomedPreferredTerm(String snomedPreferredTerm) {
        this.snomedPreferredTerm = snomedPreferredTerm;
    }

    public String getSnomedDescriptionId() {
        return snomedDescriptionId;
    }

    public void setSnomedDescriptionId(String snomedDescriptionId) {
        this.snomedDescriptionId = snomedDescriptionId;
    }

    public String getSnomedDescriptionTerm() {
        return snomedDescriptionTerm;
    }

    public void setSnomedDescriptionTerm(String snomedDescriptionTerm) {
        this.snomedDescriptionTerm = snomedDescriptionTerm;
    }

    public String getMatchAlgorithm() {
        return matchAlgorithm;
    }

    public void setMatchAlgorithm(String matchAlgorithm) {
        this.matchAlgorithm = matchAlgorithm;
    }
}
