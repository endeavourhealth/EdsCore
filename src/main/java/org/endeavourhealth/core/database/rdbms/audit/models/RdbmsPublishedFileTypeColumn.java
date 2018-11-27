package org.endeavourhealth.core.database.rdbms.audit.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "published_file_type_column")
public class RdbmsPublishedFileTypeColumn implements Serializable {

    private int publishedFileTypeId;
    private int columnIndex;
    private String columnName;
    private Integer fixedColumnStart;
    private Integer fixedColumnLength;

    public RdbmsPublishedFileTypeColumn() {}

    @Id
    @Column(name = "published_file_type_id", nullable = false)
    public int getPublishedFileTypeId() {
        return publishedFileTypeId;
    }

    public void setPublishedFileTypeId(int publishedFileTypeId) {
        this.publishedFileTypeId = publishedFileTypeId;
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

    @Column(name = "fixed_column_start", nullable = true)
    public Integer getFixedColumnStart() {
        return fixedColumnStart;
    }

    public void setFixedColumnStart(Integer fixedColumnStart) {
        this.fixedColumnStart = fixedColumnStart;
    }

    @Column(name = "fixed_column_length", nullable = true)
    public Integer getFixedColumnLength() {
        return fixedColumnLength;
    }

    public void setFixedColumnLength(Integer fixedColumnLength) {
        this.fixedColumnLength = fixedColumnLength;
    }
}
