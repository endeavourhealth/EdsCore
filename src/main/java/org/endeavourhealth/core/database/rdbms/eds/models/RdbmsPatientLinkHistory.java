package org.endeavourhealth.core.database.rdbms.eds.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "patient_link_history")
public class RdbmsPatientLinkHistory implements Serializable {

    private String patientId = null;
    private String serviceId = null;
    private Date updated = null;
    private String newPersonId = null;
    private String previousPersonId = null;

    @Id
    @Column(name = "patient_id", nullable = false)
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    @Id
    @Column(name = "updated", nullable = false)
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Column(name = "new_person_id", nullable = false)
    public String getNewPersonId() {
        return newPersonId;
    }

    public void setNewPersonId(String newPersonId) {
        this.newPersonId = newPersonId;
    }

    @Column(name = "previous_person_id")
    public String getPreviousPersonId() {
        return previousPersonId;
    }

    public void setPreviousPersonId(String previousPersonId) {
        this.previousPersonId = previousPersonId;
    }
}
