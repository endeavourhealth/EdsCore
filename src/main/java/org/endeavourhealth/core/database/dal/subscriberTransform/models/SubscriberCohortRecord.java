package org.endeavourhealth.core.database.dal.subscriberTransform.models;

import java.util.Date;
import java.util.UUID;

public class SubscriberCohortRecord {

    private String subscriberConfigName;
    private UUID patientId;
    private UUID serviceId;
    private boolean inCohort;
    private String reason;
    private Date dtUpdated;
    private UUID batchIdUpdated;

    public SubscriberCohortRecord(String subscriberConfigName, UUID serviceId, UUID batchIdUpdated, Date dtUpdated, UUID patientId) {
        this.subscriberConfigName = subscriberConfigName;
        this.serviceId = serviceId;
        this.batchIdUpdated = batchIdUpdated;
        this.dtUpdated = dtUpdated;
        this.patientId = patientId;
    }

    public String getSubscriberConfigName() {
        return subscriberConfigName;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public Date getDtUpdated() {
        return dtUpdated;
    }

    public UUID getBatchIdUpdated() {
        return batchIdUpdated;
    }


    public boolean isInCohort() {
        return inCohort;
    }

    public void setInCohort(boolean inCohort) {
        this.inCohort = inCohort;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("subscriberConfigName = [" + subscriberConfigName + "], ");
        sb.append("patientId = [" + patientId + "], ");
        sb.append("serviceId = [" + serviceId + "], ");
        sb.append("inCohort = [" + inCohort + "], ");
        sb.append("reason = [" + reason + "], ");
        sb.append("dtUpdated = [" + dtUpdated + "], ");
        return sb.toString();
    }

}
