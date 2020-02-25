package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;

import javax.persistence.*;

@Entity
@Table(name = "project_schedule", schema = "data_sharing_manager")
public class ProjectScheduleEntity {

    private String uuid;
    private String cronExpression;
    private String cronDescription;
    private String cronSettings;

    @Id
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "cron_expression")
    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Basic
    @Column(name = "cron_description")
    public String getCronDescription() {
        return cronDescription;
    }

    public void setCronDescription(String cronDescription) {
        this.cronDescription = cronDescription;
    }

    @Basic
    @Column(name = "cron_settings")
    public String getCronSettings() {
        return cronSettings;
    }

    public void setCronSettings(String cronSettings) {
        this.cronSettings = cronSettings;
    }
}
