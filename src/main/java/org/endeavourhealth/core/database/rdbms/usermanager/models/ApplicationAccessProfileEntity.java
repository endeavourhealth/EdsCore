package org.endeavourhealth.core.database.rdbms.usermanager.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "application_access_profile", schema = "user_manager")
public class ApplicationAccessProfileEntity {

    private String id;
    private String name;
    private String description;
    private String applicationId;
    private Byte isDeleted;
    private byte superUser;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "application_id")
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @Basic
    @Column(name = "is_deleted")
    public Byte getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Basic
    @Column(name = "super_user")
    public byte getSuperUser() {
        return superUser;
    }

    public void setSuperUser(byte superUser) {
        this.superUser = superUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationAccessProfileEntity that = (ApplicationAccessProfileEntity) o;
        return superUser == that.superUser &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(applicationId, that.applicationId) &&
                Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, description, applicationId, isDeleted, superUser);
    }
}
