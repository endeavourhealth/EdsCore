package org.endeavourhealth.core.database.rdbms.eds.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "patient_link_person")
public class RdbmsPatientLinkPerson implements Serializable {

    private String personId = null;
    private String nhsNumber = null;

    @Id
    @Column(name = "person_id", nullable = false)
    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @Column(name = "nhs_number", nullable = false)
    public String getNhsNumber() {
        return nhsNumber;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }

}
