package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;

import javax.persistence.*;

@Entity
@Table(name = "documentation", schema = "data_sharing_manager")
public class DocumentationEntity {
    private String uuid;
    private String title;
    private String filename;
    private String fileData;

    @Id
    @Column(name = "uuid", nullable = false, length = 36)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "title", nullable = false, length = 50)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "filename", nullable = false, length = 50)
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Basic
    @Column(name = "fileData", nullable = false, length = -1)
    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentationEntity that = (DocumentationEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (filename != null ? !filename.equals(that.filename) : that.filename != null) return false;
        if (fileData != null ? !fileData.equals(that.fileData) : that.fileData != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        result = 31 * result + (fileData != null ? fileData.hashCode() : 0);
        return result;
    }
}
