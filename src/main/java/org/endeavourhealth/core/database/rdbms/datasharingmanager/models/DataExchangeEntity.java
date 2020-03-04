package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "data_exchange", schema = "data_sharing_manager")
public class DataExchangeEntity {
    private String uuid;
    private String name;
    private byte publisher;
    private Short directionId;
    private String systemName;
    private short flowScheduleId;
    private int approximateVolume;
    private short dataExchangeMethodId;
    private short flowStatusId;
    private short securityInfrastructureId;
    private short securityArchitectureId;
    private String endpoint;

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

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "publisher")
    public byte getPublisher() {
        return publisher;
    }

    public void setPublisher(byte publisher) {
        this.publisher = publisher;
    }

    @Basic
    @Column(name = "direction_id")
    public Short getDirectionId() {
        return directionId;
    }

    public void setDirectionId(Short directionId) {
        this.directionId = directionId;
    }

    @Basic
    @Column(name = "system_name")
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @Basic
    @Column(name = "flow_schedule_id")
    public short getFlowScheduleId() {
        return flowScheduleId;
    }

    public void setFlowScheduleId(short flowScheduleId) {
        this.flowScheduleId = flowScheduleId;
    }

    @Basic
    @Column(name = "approximate_volume")
    public int getApproximateVolume() {
        return approximateVolume;
    }

    public void setApproximateVolume(int approximateVolume) {
        this.approximateVolume = approximateVolume;
    }

    @Basic
    @Column(name = "data_exchange_method_id")
    public short getDataExchangeMethodId() {
        return dataExchangeMethodId;
    }

    public void setDataExchangeMethodId(short dataExchangeMethodId) {
        this.dataExchangeMethodId = dataExchangeMethodId;
    }

    @Basic
    @Column(name = "flow_status_id")
    public short getFlowStatusId() {
        return flowStatusId;
    }

    public void setFlowStatusId(short flowStatusId) {
        this.flowStatusId = flowStatusId;
    }

    @Basic
    @Column(name = "security_infrastructure_id")
    public short getSecurityInfrastructureId() {
        return securityInfrastructureId;
    }

    public void setSecurityInfrastructureId(short securityInfrastructureId) {
        this.securityInfrastructureId = securityInfrastructureId;
    }

    @Basic
    @Column(name = "security_architecture_id")
    public short getSecurityArchitectureId() {
        return securityArchitectureId;
    }

    public void setSecurityArchitectureId(short securityArchitectureId) {
        this.securityArchitectureId = securityArchitectureId;
    }

    @Basic
    @Column(name = "endpoint")
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataExchangeEntity that = (DataExchangeEntity) o;
        return publisher == that.publisher &&
                flowScheduleId == that.flowScheduleId &&
                approximateVolume == that.approximateVolume &&
                dataExchangeMethodId == that.dataExchangeMethodId &&
                flowStatusId == that.flowStatusId &&
                securityInfrastructureId == that.securityInfrastructureId &&
                securityArchitectureId == that.securityArchitectureId &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(directionId, that.directionId) &&
                Objects.equals(systemName, that.systemName) &&
                Objects.equals(endpoint, that.endpoint);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, name, publisher, directionId, systemName, flowScheduleId, approximateVolume, dataExchangeMethodId, flowStatusId, securityInfrastructureId, securityArchitectureId, endpoint);
    }
}
