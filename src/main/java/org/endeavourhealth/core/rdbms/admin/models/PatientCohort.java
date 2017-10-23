package org.endeavourhealth.core.rdbms.admin.models;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "patient_cohort", schema = "public", catalog = "admin")
public class PatientCohort {

    private String protocolId = null;
    private String serviceId = null;
    private String nhsNumber = null;
    private DateTime inserted = null;
    private boolean inCohort = false;

    @Id
    @Column(name = "protocol_id", nullable = false)
    public String getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(String protocolId) {
        this.protocolId = protocolId;
    }

    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "nhs_number", nullable = false)
    public String getNhsNumber() {
        return nhsNumber;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }

    @Id
    @Column(name = "inserted", nullable = false)
    public DateTime getInserted() {
        return inserted;
    }

    public void setInserted(DateTime inserted) {
        this.inserted = inserted;
    }

    @Column(name = "in_cohort", nullable = false)
    public boolean isInCohort() {
        return inCohort;
    }

    public void setInCohort(boolean inCohort) {
        this.inCohort = inCohort;
    }
}
