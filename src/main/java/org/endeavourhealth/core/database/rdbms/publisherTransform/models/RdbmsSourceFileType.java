package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "source_file_type")
public class RdbmsSourceFileType implements Serializable {

    private Integer id;
    private String description;

    public RdbmsSourceFileType() {}


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false)
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
