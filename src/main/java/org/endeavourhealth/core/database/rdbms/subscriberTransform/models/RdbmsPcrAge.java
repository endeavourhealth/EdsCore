package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import org.endeavourhealth.core.database.dal.subscriberTransform.models.PcrAge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "pcr_age")
public class RdbmsPcrAge implements Serializable {

    private long pcrPatientId;
    private Date dateOfBirth = null;
    private Date dateNextChange = null;
    //private String pcrConfigName = null;

    public RdbmsPcrAge() {}

    public RdbmsPcrAge(PcrAge proxy) {
        this.pcrPatientId = proxy.getPcrPatientId();
        this.dateOfBirth = proxy.getDateOfBirth();
        this.dateNextChange = proxy.getDateNextChange();
    }

    @Id
    @Column(name = "pcr_patient_id", nullable = false)
    public long getPcrPatientId() {
        return pcrPatientId;
    }

    public void setPcrPatientId(long pcrPatientId) {
        this.pcrPatientId = pcrPatientId;
    }

    @Column(name = "date_of_birth", nullable = false)
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Column(name = "date_next_change", nullable = false)
    public Date getDateNextChange() {
        return dateNextChange;
    }

    public void setDateNextChange(Date dateNextChange) {
        this.dateNextChange = dateNextChange;
    }

    /*@Column(name = "pcr_config_name", nullable = false)
    public String getPcrConfigName() {
        return pcrConfigName;
    }

    public void setPcrConfigName(String pcrConfigName) {
        this.pcrConfigName = pcrConfigName;
    }*/
}
