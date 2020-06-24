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

    public SubscriberCohortRecord(String subscriberConfigName, UUID patientId) {
        this.subscriberConfigName = subscriberConfigName;
        this.patientId = patientId;
        this.dtUpdated = new Date();
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

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
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

    public Date getDtUpdated() {
        return dtUpdated;
    }

    public void setDtUpdated(Date dtUpdated) {
        this.dtUpdated = dtUpdated;
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
