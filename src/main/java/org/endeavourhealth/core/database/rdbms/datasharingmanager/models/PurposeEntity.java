package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;

import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonPurpose;

import javax.persistence.*;

@Entity
@Table(name = "purpose", schema = "data_sharing_manager")
public class PurposeEntity {
    private String uuid;
    private String title;
    private String detail;

    public PurposeEntity() {
    }

    public PurposeEntity(JsonPurpose jp) {
        updateFromJson(jp);
    }

    public void updateFromJson(JsonPurpose jp) {
        this.uuid = jp.getUuid();
        this.title = jp.getTitle();
        this.detail = jp.getDetail();
    }

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
    @Column(name = "detail", nullable = false, length = 10000)
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PurposeEntity that = (PurposeEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (detail != null ? !detail.equals(that.detail) : that.detail != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (detail != null ? detail.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return this.getTitle() + " (" + this.getDetail() + ", " + this.getUuid() + ")";
    }
}
