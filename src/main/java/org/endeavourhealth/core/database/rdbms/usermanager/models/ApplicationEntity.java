package org.endeavourhealth.core.database.rdbms.usermanager.models;


import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "application", schema = "user_manager")
public class ApplicationEntity {
    private String id;
    private String name;
    private String applicationTree;
    private Byte isDeleted;
    private String description;

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
    @Column(name = "application_tree")
    public String getApplicationTree() {
        return applicationTree;
    }

    public void setApplicationTree(String applicationTree) {
        this.applicationTree = applicationTree;
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
        ApplicationEntity that = (ApplicationEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(applicationTree, that.applicationTree) &&
                Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, applicationTree, isDeleted);
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
