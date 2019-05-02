package org.endeavourhealth.core.database.dal.subscriberTransform.models;

import java.util.Date;

public class SubscriberId {

    private long subscriberId;
    private Date dtUpdatedPreviouslySent;

    public SubscriberId(long subscriberId, Date dtUpdatedPreviouslySent) {
        this.subscriberId = subscriberId;
        this.dtUpdatedPreviouslySent = dtUpdatedPreviouslySent;
    }

    public long getSubscriberId() {
        return subscriberId;
    }

    public Date getDtUpdatedPreviouslySent() {
        return dtUpdatedPreviouslySent;
    }

    public void setDtUpdatedPreviouslySent(Date dtUpdatedPreviouslySent) {
        this.dtUpdatedPreviouslySent = dtUpdatedPreviouslySent;
    }
}
