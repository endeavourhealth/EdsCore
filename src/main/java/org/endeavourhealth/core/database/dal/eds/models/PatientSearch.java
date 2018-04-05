package org.endeavourhealth.core.database.dal.eds.models;

import org.endeavourhealth.core.database.rdbms.eds.models.RdbmsPatientSearch;
import org.endeavourhealth.core.database.rdbms.eds.models.RdbmsPatientSearchEpisode;

import java.util.Date;
import java.util.UUID;

public class PatientSearch {

    //fields from patient_search table
    private UUID serviceId = null;
    private UUID patientId = null;
    private String nhsNumber = null;
    private String forenames = null;
    private String surname = null;
    private Date dateOfBirth = null;
    private Date dateOfDeath = null;
    private String addressLine1 = null;
    private String addressLine2 = null;
    private String addressLine3 = null;
    private String city = null;
    private String district = null;
    private String postcode = null;
    private String gender = null;
    private String registeredPracticeOdsCode = null;
    //fields from patient_search_episode_table
    private UUID episodeId = null;
    private Date registrationStart = null;
    private Date registrationEnd = null;
    private String careManager = null;
    private String organisationName = null;
    private String organisationTypeCode = null;
    private String registrationTypeCode = null;

    public PatientSearch() {}



    public PatientSearch(RdbmsPatientSearch patientProxy, RdbmsPatientSearchEpisode episodeProxy) {
        this.serviceId = UUID.fromString(patientProxy.getServiceId());
        this.patientId = UUID.fromString(patientProxy.getPatientId());
        this.nhsNumber = patientProxy.getNhsNumber();
        this.forenames = patientProxy.getForenames();
        this.surname = patientProxy.getSurname();
        this.dateOfBirth = patientProxy.getDateOfBirth();
        this.dateOfDeath = patientProxy.getDateOfDeath();
        this.addressLine1 = patientProxy.getAddressLine1();
        this.addressLine2 = patientProxy.getAddressLine2();
        this.addressLine3 = patientProxy.getAddressLine3();
        this.city = patientProxy.getCity();
        this.district = patientProxy.getDistrict();
        this.postcode = patientProxy.getPostcode();
        this.gender = patientProxy.getGender();
        this.registeredPracticeOdsCode = patientProxy.getRegisteredPracticeOdsCode();

        this.episodeId = UUID.fromString(episodeProxy.getEpisodeId());
        this.registrationStart = episodeProxy.getRegistrationStart();
        this.registrationEnd = episodeProxy.getRegistrationEnd();
        this.careManager = episodeProxy.getCareManager();
        this.organisationName = episodeProxy.getOrganisationName();
        this.organisationTypeCode = episodeProxy.getOrganisationTypeCode();
        this.registrationTypeCode = episodeProxy.getRegistrationTypeCode();
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
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

    public String getRegisteredPracticeOdsCode() {
        return registeredPracticeOdsCode;
    }

    public void setRegisteredPracticeOdsCode(String registeredPracticeOdsCode) {
        this.registeredPracticeOdsCode = registeredPracticeOdsCode;
    }

    public UUID getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(UUID episodeId) {
        this.episodeId = episodeId;
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

    public String getCareManager() {
        return careManager;
    }

    public void setCareManager(String careManager) {
        this.careManager = careManager;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
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
