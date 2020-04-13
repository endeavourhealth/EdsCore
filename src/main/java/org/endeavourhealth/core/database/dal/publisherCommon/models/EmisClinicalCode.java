package org.endeavourhealth.core.database.dal.publisherCommon.models;

public class EmisClinicalCode {

    private long codeId;
    private String codeType;
    private String readTerm;
    private String readCode;
    private Long snomedConceptId;
    private Long snomedDescriptionId;
    private String snomedTerm;
    private String nationalCode;
    private String nationalCodeCategory;
    private String nationalCodeDescription;
    private Long parentCode;
    private String adjustedCode;
    private boolean isEmisCode;

    public EmisClinicalCode() {
    }

    public long getCodeId() {
        return codeId;
    }

    public void setCodeId(long codeId) {
        this.codeId = codeId;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
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

    public Long getSnomedDescriptionId() {
        return snomedDescriptionId;
    }

    public void setSnomedDescriptionId(Long snomedDescriptionId) {
        this.snomedDescriptionId = snomedDescriptionId;
    }

    public String getSnomedTerm() {
        return snomedTerm;
    }

    public void setSnomedTerm(String snomedTerm) {
        this.snomedTerm = snomedTerm;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getNationalCodeCategory() {
        return nationalCodeCategory;
    }

    public void setNationalCodeCategory(String nationalCodeCategory) {
        this.nationalCodeCategory = nationalCodeCategory;
    }

    public String getNationalCodeDescription() {
        return nationalCodeDescription;
    }

    public void setNationalCodeDescription(String nationalCodeDescription) {
        this.nationalCodeDescription = nationalCodeDescription;
    }

    public Long getParentCode() {
        return parentCode;
    }

    public void setParentCode(Long parentCode) {
        this.parentCode = parentCode;
    }

    public String getAdjustedCode() {
        return adjustedCode;
    }

    public void setAdjustedCode(String adjustedCode) {
        this.adjustedCode = adjustedCode;
    }

    public boolean isEmisCode() {
        return isEmisCode;
    }

    public void setEmisCode(boolean emisCode) {
        isEmisCode = emisCode;
    }
}
