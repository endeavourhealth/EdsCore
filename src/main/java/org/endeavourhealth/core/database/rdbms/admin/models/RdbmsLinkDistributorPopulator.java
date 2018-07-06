package org.endeavourhealth.core.database.rdbms.admin.models;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "link_distributor_populator", schema = "admin", catalog = "")
public class RdbmsLinkDistributorPopulator {
    private String patientId;
    private String nhsNumber;
    private Date dateOfBirth;
    private Byte done;

    public RdbmsLinkDistributorPopulator() {
    }

    public RdbmsLinkDistributorPopulator(String patientId, String nhsNumber, Date dateOfBirth, Byte done) {
        this.patientId = patientId;
        this.nhsNumber = nhsNumber;
        this.dateOfBirth = dateOfBirth;
        this.done = done;
    }

    @Id
    @Column(name = "patient_id")
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Basic
    @Column(name = "nhs_number")
    public String getNhsNumber() {
        return nhsNumber;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }

    @Basic
    @Column(name = "date_of_birth")
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Basic
    @Column(name = "done")
    public Byte getDone() {
        return done;
    }

    public void setDone(Byte done) {
        this.done = done;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsLinkDistributorPopulator that = (RdbmsLinkDistributorPopulator) o;
        return Objects.equals(patientId, that.patientId) &&
                Objects.equals(nhsNumber, that.nhsNumber) &&
                Objects.equals(dateOfBirth, that.dateOfBirth) &&
                Objects.equals(done, that.done);
    }

    @Override
    public int hashCode() {

        return Objects.hash(patientId, nhsNumber, dateOfBirth, done);
    }
}
