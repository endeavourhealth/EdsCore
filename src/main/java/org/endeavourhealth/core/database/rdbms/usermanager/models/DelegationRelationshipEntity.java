package org.endeavourhealth.core.database.rdbms.usermanager.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "delegation_relationship", schema = "user_manager")
public class DelegationRelationshipEntity {
    private String uuid;
    private String delegation;
    private String parentUuid;
    private short parentType;
    private String childUuid;
    private short childType;
    private byte includeAllChildren;
    private byte createSuperUsers;
    private byte createUsers;
    private Byte isDeleted;

    @Id
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "delegation")
    public String getDelegation() {
        return delegation;
    }

    public void setDelegation(String delegation) {
        this.delegation = delegation;
    }

    @Basic
    @Column(name = "parent_uuid")
    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    @Basic
    @Column(name = "parent_type")
    public short getParentType() {
        return parentType;
    }

    public void setParentType(short parentType) {
        this.parentType = parentType;
    }

    @Basic
    @Column(name = "child_uuid")
    public String getChildUuid() {
        return childUuid;
    }

    public void setChildUuid(String childUuid) {
        this.childUuid = childUuid;
    }

    @Basic
    @Column(name = "child_type")
    public short getChildType() {
        return childType;
    }

    public void setChildType(short childType) {
        this.childType = childType;
    }

    @Basic
    @Column(name = "include_all_children")
    public byte getIncludeAllChildren() {
        return includeAllChildren;
    }

    public void setIncludeAllChildren(byte includeAllChildren) {
        this.includeAllChildren = includeAllChildren;
    }

    @Basic
    @Column(name = "create_super_users")
    public byte getCreateSuperUsers() {
        return createSuperUsers;
    }

    public void setCreateSuperUsers(byte createSuperUsers) {
        this.createSuperUsers = createSuperUsers;
    }

    @Basic
    @Column(name = "create_users")
    public byte getCreateUsers() {
        return createUsers;
    }

    public void setCreateUsers(byte createUsers) {
        this.createUsers = createUsers;
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
        DelegationRelationshipEntity that = (DelegationRelationshipEntity) o;
        return parentType == that.parentType &&
                childType == that.childType &&
                includeAllChildren == that.includeAllChildren &&
                createSuperUsers == that.createSuperUsers &&
                createUsers == that.createUsers &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(delegation, that.delegation) &&
                Objects.equals(parentUuid, that.parentUuid) &&
                Objects.equals(childUuid, that.childUuid) &&
                Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, delegation, parentUuid, parentType, childUuid, childType, includeAllChildren, createSuperUsers, createUsers, isDeleted);
    }
}
