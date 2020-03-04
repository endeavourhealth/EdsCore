package org.endeavourhealth.core.database.rdbms.usermanager.models;


import org.endeavourhealth.core.database.dal.usermanager.models.JsonUserApplicationPolicy;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_application_policy", schema = "user_manager")
public class UserApplicationPolicyEntity {
    private String userId;
    private String applicationPolicyId;

    @Id
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        UserApplicationPolicyEntity that = (UserApplicationPolicyEntity) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(applicationPolicyId, that.applicationPolicyId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, applicationPolicyId);
    }

    public UserApplicationPolicyEntity() {
    }

    public UserApplicationPolicyEntity(JsonUserApplicationPolicy jsonUserApplicationPolicy) {
        this.userId = jsonUserApplicationPolicy.getUserId();
        this.applicationPolicyId = jsonUserApplicationPolicy.getApplicationPolicyId();
    }
}
