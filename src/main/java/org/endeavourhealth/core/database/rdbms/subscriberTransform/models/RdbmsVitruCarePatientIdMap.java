package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "vitru_care_patient_id_map")
public class RdbmsVitruCarePatientIdMap implements Serializable {

    private String edsPatientId = null;
    private String serviceId = null;
    private String systemId = null;
    private DateTime createdAt = null;
    private String vitruCareId = null;

    @Id
    @Column(name = "eds_patient_id", nullable = false)
    public String getEdsPatientId() {
        return edsPatientId;
    }

    public void setEdsPatientId(String edsPatientId) {
        this.edsPatientId = edsPatientId;
    }

    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "system_id", nullable = false)
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Column(name = "created_at", nullable = false)
    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "vitrucare_id", nullable = false)
    public String getVitruCareId() {
        return vitruCareId;
    }

    public void setVitruCareId(String vitruCareId) {
        this.vitruCareId = vitruCareId;
    }
}
