package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "source_file_record")
public class RdbmsSourceFileRecord implements Serializable {

    private Long id;
    private int sourceFileId;
    private String sourceLocation;
    private String value;

    public RdbmsSourceFileRecord() {}

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "source_file_id", nullable = false)
    public int getSourceFileId() {
        return sourceFileId;
    }

    public void setSourceFileId(int sourceFileId) {
        this.sourceFileId = sourceFileId;
    }

    @Column(name = "source_location", nullable = true)
    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    @Column(name = "value", nullable = false)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
