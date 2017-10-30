package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import org.endeavourhealth.core.database.dal.subscriberTransform.models.EnterpriseAge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "enterprise_age")
public class RdbmsEnterpriseAge implements Serializable {

    private long enterprisePatientId;
    private Date dateOfBirth = null;
    private Date dateNextChange = null;
    //private String enterpriseConfigName = null;

    public RdbmsEnterpriseAge() {}

    public RdbmsEnterpriseAge(EnterpriseAge proxy) {
        this.enterprisePatientId = proxy.getEnterprisePatientId();
        this.dateOfBirth = proxy.getDateOfBirth();
        this.dateNextChange = proxy.getDateNextChange();
    }

    @Id
    @Column(name = "enterprise_patient_id", nullable = false)
    public long getEnterprisePatientId() {
        return enterprisePatientId;
    }

    public void setEnterprisePatientId(long enterprisePatientId) {
        this.enterprisePatientId = enterprisePatientId;
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

    /*@Column(name = "enterprise_config_name", nullable = false)
    public String getEnterpriseConfigName() {
        return enterpriseConfigName;
    }

    public void setEnterpriseConfigName(String enterpriseConfigName) {
        this.enterpriseConfigName = enterpriseConfigName;
    }*/
}
