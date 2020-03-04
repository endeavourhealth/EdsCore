package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "data_flow", schema = "data_sharing_manager")
public class DataFlowEntity {
    private String uuid;
    private String purpose;
    private String name;
    private short storageProtocolId;
    private Short consentModelId;
    private Short deidentificationLevel;

    @Id
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "purpose")
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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
    @Column(name = "storage_protocol_id")
    public short getStorageProtocolId() {
        return storageProtocolId;
    }

    public void setStorageProtocolId(short storageProtocolId) {
        this.storageProtocolId = storageProtocolId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataFlowEntity that = (DataFlowEntity) o;
        return storageProtocolId == that.storageProtocolId &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(purpose, that.purpose) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, purpose, name, storageProtocolId);
    }

    @Basic
    @Column(name = "consent_model_id")
    public Short getConsentModelId() {
        return consentModelId;
    }

    public void setConsentModelId(Short consentModelId) {
        this.consentModelId = consentModelId;
    }

    @Basic
    @Column(name = "deidentification_level")
    public Short getDeidentificationLevel() {
        return deidentificationLevel;
    }

    public void setDeidentificationLevel(Short deidentificationLevel) {
        this.deidentificationLevel = deidentificationLevel;
    }
}
