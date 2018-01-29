package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "source_file_type_column")
public class RdbmsSourceFileTypeColumn implements Serializable {

    private int sourceFileTypeId;
    private int columnIndex;
    private String columnName;

    public RdbmsSourceFileTypeColumn() {}

    @Id
    @Column(name = "source_file_type_id", nullable = false)
    public int getSourceFileTypeId() {
        return sourceFileTypeId;
    }

    public void setSourceFileTypeId(int sourceFileTypeId) {
        this.sourceFileTypeId = sourceFileTypeId;
    }

    @Id
    @Column(name = "column_index", nullable = false)
    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Column(name = "column_name", nullable = false)
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
