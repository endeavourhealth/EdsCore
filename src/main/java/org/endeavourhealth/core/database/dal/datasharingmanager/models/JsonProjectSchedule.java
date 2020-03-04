package org.endeavourhealth.core.database.dal.datasharingmanager.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonProjectSchedule {

    private String uuid;
    private String cronExpression;
    private String cronDescription;
    private String cronSettings;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getCronDescription() {
        return cronDescription;
    }

    public void setCronDescription(String cronDescription) {
        this.cronDescription = cronDescription;
    }

    public String getCronSettings() {
        return cronSettings;
    }

    public void setCronSettings(String cronSettings) {
        this.cronSettings = cronSettings;
    }
}
