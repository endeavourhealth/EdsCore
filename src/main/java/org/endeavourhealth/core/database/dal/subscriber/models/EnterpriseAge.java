package org.endeavourhealth.core.database.dal.subscriber.models;

import org.endeavourhealth.core.database.rdbms.subscriber.models.RdbmsEnterpriseAge;

import java.util.Date;

public class EnterpriseAge {

    public static final int UNIT_YEARS = 0;
    public static final int UNIT_MONTHS = 1;
    public static final int UNIT_WEEKS = 2;


    private long enterprisePatientId;
    private Date dateOfBirth = null;
    private Date dateNextChange = null;

    public EnterpriseAge() {}

    public EnterpriseAge(RdbmsEnterpriseAge proxy) {
        this.enterprisePatientId = proxy.getEnterprisePatientId();
        this.dateOfBirth = proxy.getDateOfBirth();
        this.dateNextChange = proxy.getDateNextChange();
    }

    public long getEnterprisePatientId() {
        return enterprisePatientId;
    }

    public void setEnterprisePatientId(long enterprisePatientId) {
        this.enterprisePatientId = enterprisePatientId;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateNextChange() {
        return dateNextChange;
    }

    public void setDateNextChange(Date dateNextChange) {
        this.dateNextChange = dateNextChange;
    }
}
