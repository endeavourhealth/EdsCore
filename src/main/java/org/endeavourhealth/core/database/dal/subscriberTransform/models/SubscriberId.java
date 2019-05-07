package org.endeavourhealth.core.database.dal.subscriberTransform.models;

import java.io.Serializable;
import java.util.Date;

public class SubscriberId implements Serializable {

    private byte subscriberTable;
    private long subscriberId;
    private String sourceId;
    private Date dtUpdatedPreviouslySent;

    public SubscriberId(byte subscriberTable, long subscriberId, String sourceId, Date dtUpdatedPreviouslySent) {
        this.subscriberTable = subscriberTable;
        this.subscriberId = subscriberId;
        this.sourceId = sourceId;
        this.dtUpdatedPreviouslySent = dtUpdatedPreviouslySent;
    }

    public byte getSubscriberTable() {
        return subscriberTable;
    }

    public String getSourceId() {
        return sourceId;
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

    @Override
    public String toString() {
        return "SubscriberID " + subscriberId + ", SubscriberTable " + subscriberTable + ", SourceId " + sourceId + ", DtUpdated " + dtUpdatedPreviouslySent;
    }
}
