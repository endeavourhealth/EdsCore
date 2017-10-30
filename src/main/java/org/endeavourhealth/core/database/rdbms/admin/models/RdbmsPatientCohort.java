package org.endeavourhealth.core.database.rdbms.admin.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "patient_cohort")
public class RdbmsPatientCohort implements Serializable {

    private String protocolId = null;
    private String serviceId = null;
    private String nhsNumber = null;
    private Date inserted = null;
    private boolean inCohort = false;

    public RdbmsPatientCohort() {}

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
    public Date getInserted() {
        return inserted;
    }

    public void setInserted(Date inserted) {
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
