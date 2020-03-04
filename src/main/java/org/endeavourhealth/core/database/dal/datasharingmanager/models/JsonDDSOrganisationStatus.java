package org.endeavourhealth.core.database.dal.datasharingmanager.models;

public class JsonDDSOrganisationStatus {
    private String practiceName = null;
    private String odsCode = null;
    private String ccg = null;
    private String referenceAgreement = null;
    private String lastReceived;
    private Boolean inError = false;
    private Short systemSupplierType = null;
    private String systemSupplierReference = null;
    private Byte sharingActivated = null;

    public JsonDDSOrganisationStatus() {
    }

    public String getOdsCode() {
        return odsCode;
    }

    public void setOdsCode(String odsCode) {
        this.odsCode = odsCode;
    }

    public String getLastReceived() {
        return lastReceived;
    }

    public void setLastReceived(String lastReceived) {
        this.lastReceived = lastReceived;
    }

    public Boolean getInError() {
        return inError;
    }

    public void setInError(Boolean inError) {
        this.inError = inError;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public void setPracticeName(String practiceName) {
        this.practiceName = practiceName;
    }

    public String getCcg() {
        return ccg;
    }

    public void setCcg(String ccg) {
        this.ccg = ccg;
    }

    public String getReferenceAgreement() {
        return referenceAgreement;
    }

    public void setReferenceAgreement(String referenceAgreement) {
        this.referenceAgreement = referenceAgreement;
    }

    public Short getSystemSupplierType() {
        return systemSupplierType;
    }

    public void setSystemSupplierType(Short systemSupplierType) {
        this.systemSupplierType = systemSupplierType;
    }

    public String getSystemSupplierReference() {
        return systemSupplierReference;
    }

    public void setSystemSupplierReference(String systemSupplierReference) {
        this.systemSupplierReference = systemSupplierReference;
    }

    public Byte getSharingActivated() {
        return sharingActivated;
    }

    public void setSharingActivated(Byte sharingActivated) {
        this.sharingActivated = sharingActivated;
    }
}
