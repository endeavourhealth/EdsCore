package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "value_sets", schema = "data_sharing_manager")
public class ValueSetsEntity {

    private String uuid;
    private String name;

    @Id
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String valueSetsName) {
        this.name = valueSetsName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueSetsEntity that = (ValueSetsEntity) o;
        return uuid == that.uuid && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name);
    }

}