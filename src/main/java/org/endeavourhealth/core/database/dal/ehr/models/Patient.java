package org.endeavourhealth.core.database.dal.ehr.models;

import java.util.Date;

public class Patient {

    private int id;
    private int organizationId;
    private int personId;
    private String title;
    private String firstNames;
    private String lastName;
    private int genderTypeId;
    private String nhsNumber;
    private Date dateOfBirth;
    private Date dateOfDeath;
    private int currentAddressId;
    private int ethnicCodeTypeId;
    private int registeredPracticeOrganizationId;
    private String mothersNHSNumber;

    public Patient() {
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

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstNames() {
        return firstNames;
    }

    public void setFirstNames(String firstNames) {
        this.firstNames = firstNames;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getGenderTypeId() {
        return genderTypeId;
    }

    public void setGenderTypeId(int genderTypeId) {
        this.genderTypeId = genderTypeId;
    }

    public String getNhsNumber() {
        return nhsNumber;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
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

    public int getCurrentAddressId() {
        return currentAddressId;
    }

    public void setCurrentAddressId(int currentAddressId) {
        this.currentAddressId = currentAddressId;
    }

    public int getEthnicCodeTypeId() {
        return ethnicCodeTypeId;
    }

    public void setEthnicCodeTypeId(int ethnicCodeTypeId) {
        this.ethnicCodeTypeId = ethnicCodeTypeId;
    }

    public int getRegisteredPracticeOrganizationId() {
        return registeredPracticeOrganizationId;
    }

    public void setRegisteredPracticeOrganizationId(int registeredPracticeOrganizationId) {
        this.registeredPracticeOrganizationId = registeredPracticeOrganizationId;
    }

    public String getMothersNHSNumber() {
        return mothersNHSNumber;
    }

    public void setMothersNHSNumber(String mothersNHSNumber) {
        this.mothersNHSNumber = mothersNHSNumber;
    }
}