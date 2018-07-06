package org.endeavourhealth.core.database.dal.admin.models;

import java.util.Date;

public class LinkDistributorPopulator {
    private String patientId;
    private String nhsNumber;
    private Date dateOfBirth;
    private Byte done;

    public LinkDistributorPopulator() {
    }

    public LinkDistributorPopulator(String patientId, String nhsNumber, Date dateOfBirth, Byte done) {
        this.patientId = patientId;
        this.nhsNumber = nhsNumber;
        this.dateOfBirth = dateOfBirth;
        this.done = done;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
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

    public Byte getDone() {
        return done;
    }

    public void setDone(Byte done) {
        this.done = done;
    }
}
