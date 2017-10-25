package org.endeavourhealth.core.database.rdbms.keycloak.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by Leigh Ellwood on 30/05/2017.
 */
public class GroupRoleMappingEntityPK implements Serializable {
    private String roleId;
    private String groupId;

    @Column(name = "ROLE_ID", nullable = false, length = 36)
    @Id
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Column(name = "GROUP_ID", nullable = false, length = 36)
    @Id
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupRoleMappingEntityPK that = (GroupRoleMappingEntityPK) o;

        if (roleId != null ? !roleId.equals(that.roleId) : that.roleId != null) return false;
        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = roleId != null ? roleId.hashCode() : 0;
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        return result;
    }
}
