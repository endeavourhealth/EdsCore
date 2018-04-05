package org.endeavourhealth.core.database.rdbms.eds.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "patient_search_2")
public class RdbmsPatientSearch implements Serializable {

    private String serviceId = null;
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
    private String patientId = null;
    private Date lastUpdated = null;
    private String registeredPracticeOdsCode = null;

    public RdbmsPatientSearch() {}

    /*public RdbmsPatientSearch(PatientSearch proxy) {
        this.serviceId = proxy.getServiceId().toString();
        this.nhsNumber = proxy.getNhsNumber();
        this.forenames = proxy.getForenames();
        this.surname = proxy.getSurname();
        this.dateOfBirth = proxy.getDateOfBirth();
        this.dateOfDeath = proxy.getDateOfDeath();
        this.postcode = proxy.getPostcode();
        this.gender = proxy.getGender();
        this.registrationStart = proxy.getRegistrationStart();
        this.registrationEnd = proxy.getRegistrationEnd();
        this.patientId = proxy.getPatientId().toString();
        this.lastUpdated = proxy.getLastUpdated();
        this.organisationTypeCode = proxy.getOrganisationTypeCode();
        this.registrationTypeCode = proxy.getRegistrationTypeCode();
    }*/



    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "nhs_number")
    public String getNhsNumber() {
        return nhsNumber;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }

    @Column(name = "forenames")
    public String getForenames() {
        return forenames;
    }

    public void setForenames(String forenames) {
        this.forenames = forenames;
    }

    @Column(name = "surname")
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Column(name = "date_of_birth")
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Column(name = "date_of_death")
    public Date getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(Date dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    @Column(name = "address_line_1")
    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    @Column(name = "address_line_2")
    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @Column(name = "address_line_3")
    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    @Column(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "district")
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Column(name = "postcode")
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Column(name = "gender")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Id
    @Column(name = "patient_id", nullable = false)
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Column(name = "last_updated", nullable = false)
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Column(name = "registered_practice_ods_code", nullable = false)
    public String getRegisteredPracticeOdsCode() {
        return registeredPracticeOdsCode;
    }

    public void setRegisteredPracticeOdsCode(String registeredPracticeOdsCode) {
        this.registeredPracticeOdsCode = registeredPracticeOdsCode;
    }
}
