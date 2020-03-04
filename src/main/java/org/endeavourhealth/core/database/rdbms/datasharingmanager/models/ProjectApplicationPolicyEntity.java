package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;


import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonProjectApplicationPolicy;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "project_application_policy", schema = "data_sharing_manager")
public class ProjectApplicationPolicyEntity {
    private String projectUuid;
    private String applicationPolicyId;

    @Id
    @Column(name = "project_uuid")
    public String getProjectUuid() {
        return projectUuid;
    }

    public void setProjectUuid(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    @Basic
    @Column(name = "application_policy_id")
    public String getApplicationPolicyId() {
        return applicationPolicyId;
    }

    public void setApplicationPolicyId(String applicationPolicyId) {
        this.applicationPolicyId = applicationPolicyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectApplicationPolicyEntity that = (ProjectApplicationPolicyEntity) o;
        return Objects.equals(projectUuid, that.projectUuid) &&
                Objects.equals(applicationPolicyId, that.applicationPolicyId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(projectUuid, applicationPolicyId);
    }

    public ProjectApplicationPolicyEntity() {
    }

    public ProjectApplicationPolicyEntity(JsonProjectApplicationPolicy jsonProjectApplicationPolicy) {
        this.projectUuid = jsonProjectApplicationPolicy.getProjectUuid();
        this.applicationPolicyId = jsonProjectApplicationPolicy.getApplicationPolicyId();
    }
}
