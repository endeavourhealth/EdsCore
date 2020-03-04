package org.endeavourhealth.core.database.rdbms.usermanager.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "application_policy_attribute", schema = "user_manager")
public class ApplicationPolicyAttributeEntity {

    private String id;
    private String applicationPolicyId;
    private String applicationAccessProfileId;
    private Byte isDeleted;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "application_policy_id")
    public String getApplicationPolicyId() {
        return applicationPolicyId;
    }

    public void setApplicationPolicyId(String applicationPolicyId) {
        this.applicationPolicyId = applicationPolicyId;
    }

    @Basic
    @Column(name = "application_access_profile_id")
    public String getApplicationAccessProfileId() {
        return applicationAccessProfileId;
    }

    public void setApplicationAccessProfileId(String applicationAccessProfileId) {
        this.applicationAccessProfileId = applicationAccessProfileId;
    }

    @Basic
    @Column(name = "is_deleted")
    public Byte getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationPolicyAttributeEntity that = (ApplicationPolicyAttributeEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(applicationPolicyId, that.applicationPolicyId) &&
                Objects.equals(applicationAccessProfileId, that.applicationAccessProfileId) &&
                Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, applicationPolicyId, applicationAccessProfileId, isDeleted);
    }
}
