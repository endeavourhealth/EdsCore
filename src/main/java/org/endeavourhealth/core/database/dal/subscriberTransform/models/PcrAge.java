package org.endeavourhealth.core.database.dal.subscriberTransform.models;

import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsPcrAge;

import java.util.Date;

public class PcrAge {

    public static final int UNIT_YEARS = 0;
    public static final int UNIT_MONTHS = 1;
    public static final int UNIT_WEEKS = 2;


    private long pcrPatientId;
    private Date dateOfBirth = null;
    private Date dateNextChange = null;

    public PcrAge() {}

    public PcrAge(RdbmsPcrAge proxy) {
        this.pcrPatientId = proxy.getPcrPatientId();
        this.dateOfBirth = proxy.getDateOfBirth();
        this.dateNextChange = proxy.getDateNextChange();
    }

    public long getPcrPatientId() {
        return pcrPatientId;
    }

    public void setPcrPatientId(long pcrPatientId) {
        this.pcrPatientId = pcrPatientId;
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
