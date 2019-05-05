
package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "subscriber_pseudo_id_map")
public class RdbmsSubscriberPseudoId implements Serializable {

    private String patientId;
    private long subscriberPatientId;
    private String saltKeyName;
    private String pseudoId;


    public RdbmsSubscriberPseudoId() {}

    @Id
    @Column(name = "patient_id", nullable = false)
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Id
    @Column(name = "subscriber_patient_id", nullable = false)
    public long getSubscriberPatientId() {
        return subscriberPatientId;
    }

    public void setSubscriberPatientId(long subscriberPatientId) {
        this.subscriberPatientId = subscriberPatientId;
    }

    @Id
    @Column(name = "salt_key_name", nullable = false)
    public String getSaltKeyName() {
        return saltKeyName;
    }

    public void setSaltKeyName(String saltKeyName) {
        this.saltKeyName = saltKeyName;
    }

    @Column(name = "pseudo_id", nullable = false)
    public String getPseudoId() {
        return pseudoId;
    }

    public void setPseudoId(String pseudoId) {
        this.pseudoId = pseudoId;
    }
}

