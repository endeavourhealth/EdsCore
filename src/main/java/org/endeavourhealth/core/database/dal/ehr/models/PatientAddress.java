package org.endeavourhealth.core.database.dal.ehr.models;

import java.util.Date;

public class PatientAddress {

    private int id;
    private int organizationId;
    private int patientId;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String city;
    private String postCode;
    private int useTypeId;
    private Date startDate;
    private Date endDate;
    private String lsoa2001Code;
    private String lsoa2011Code;
    private String msoa2001Code;
    private String msoa2011Code;
    private String wardCode;
    private String localAutorityCode;

    public PatientAddress() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }


    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getAddressLine4() {
        return addressLine4;
    }

    public void setAddressLine4(String addressLine4) {
        this.addressLine4 = addressLine4;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public int getUseTypeId() {
        return useTypeId;
    }

    public void setUseTypeId(int useTypeId) {
        this.useTypeId = useTypeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLsoa2001Code() {
        return lsoa2001Code;
    }

    public void setLsoa2001Code(String lsoa2001Code) {
        this.lsoa2001Code = lsoa2001Code;
    }

    public String getLsoa2011Code() {
        return lsoa2011Code;
    }

    public void setLsoa2011Code(String lsoa2011Code) {
        this.lsoa2011Code = lsoa2011Code;
    }

    public String getMsoa2001Code() {
        return msoa2001Code;
    }

    public void setMsoa2001Code(String msoa2001Code) {
        this.msoa2001Code = msoa2001Code;
    }

    public String getMsoa2011Code() {
        return msoa2011Code;
    }

    public void setMsoa2011Code(String msoa2011Code) {
        this.msoa2011Code = msoa2011Code;
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    public String getLocalAutorityCode() {
        return localAutorityCode;
    }

    public void setLocalAutorityCode(String localAutorityCode) {
        this.localAutorityCode = localAutorityCode;
    }
}