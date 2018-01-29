package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "source_file_type")
public class RdbmsSourceFileType implements Serializable {

    private Integer id;
    private String description;

    public RdbmsSourceFileType() {}

    @Id
    @Generated(GenerationTime.INSERT)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
