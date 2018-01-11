package org.endeavourhealth.core.database.dal.eds.models;

import org.endeavourhealth.core.database.rdbms.eds.models.RdbmsPatientSearch;

import java.util.Date;
import java.util.UUID;

public class PatientSearch {

    private UUID serviceId = null;
    private UUID systemId = null;
    private String nhsNumber = null;
    private String forenames = null;
    private String surname = null;
    private Date dateOfBirth = null;
    private Date dateOfDeath = null;
    private String postcode = null;
    private String gender = null;
    private Date registrationStart = null;
    private Date registrationEnd = null;
    private UUID patientId = null;
    private Date lastUpdated = null;
    private String organisationTypeCode = null;
    private String registrationTypeCode = null;
    
    public PatientSearch() {}
    
    public PatientSearch(RdbmsPatientSearch proxy) {
        this.serviceId = UUID.fromString(proxy.getServiceId());
        this.nhsNumber = proxy.getNhsNumber();
        this.forenames = proxy.getForenames();
        this.surname = proxy.getSurname();
        this.dateOfBirth = proxy.getDateOfBirth();
        this.dateOfDeath = proxy.getDateOfDeath();
        this.postcode = proxy.getPostcode();
        this.gender = proxy.getGender();
        this.registrationStart = proxy.getRegistrationStart();
        this.registrationEnd = proxy.getRegistrationEnd();
        this.patientId = UUID.fromString(proxy.getPatientId());
        this.lastUpdated = proxy.getLastUpdated();
        this.organisationTypeCode = proxy.getOrganisationTypeCode();
        this.registrationTypeCode = proxy.getRegistrationTypeCode();
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getSystemId() {
        return systemId;
    }

    public void setSystemId(UUID systemId) {
        this.systemId = systemId;
    }

    public String getNhsNumber() {
        return nhsNumber;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }

    public String getForenames() {
        return forenames;
    }

    public void setForenames(String forenames) {
        this.forenames = forenames;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(Date dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(Date registrationStart) {
        this.registrationStart = registrationStart;
    }

    public Date getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(Date registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getOrganisationTypeCode() {
        return organisationTypeCode;
    }

    public void setOrganisationTypeCode(String organisationTypeCode) {
        this.organisationTypeCode = organisationTypeCode;
    }

    public String getRegistrationTypeCode() {
        return registrationTypeCode;
    }

    public void setRegistrationTypeCode(String registrationTypeCode) {
        this.registrationTypeCode = registrationTypeCode;
    }
}
