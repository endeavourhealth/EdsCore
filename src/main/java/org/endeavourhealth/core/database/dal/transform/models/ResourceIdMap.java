package org.endeavourhealth.core.database.dal.transform.models;

import org.endeavourhealth.core.database.cassandra.transform.models.CassandraResourceIdMap;
import org.endeavourhealth.core.database.cassandra.transform.models.CassandraResourceIdMapByEdsId;
import org.endeavourhealth.core.database.rdbms.transform.models.RdbmsResourceIdMap;

import java.util.UUID;

public class ResourceIdMap {

    private UUID serviceId = null;
    private UUID systemId = null;
    private String resourceType = null;
    private String sourceId = null;
    private UUID edsId = null;

    public ResourceIdMap() {}

    public ResourceIdMap(CassandraResourceIdMap proxy) {
        this.serviceId = proxy.getServiceId();
        this.systemId = proxy.getSystemId();
        this.resourceType = proxy.getResourceType();
        this.sourceId = proxy.getSourceId();
        this.edsId = proxy.getEdsId();
    }

    public ResourceIdMap(CassandraResourceIdMapByEdsId proxy) {
        this.serviceId = proxy.getServiceId();
        this.systemId = proxy.getSystemId();
        this.resourceType = proxy.getResourceType();
        this.sourceId = proxy.getSourceId();
        this.edsId = proxy.getEdsId();
    }

    public ResourceIdMap(RdbmsResourceIdMap proxy) {
        this.serviceId = UUID.fromString(proxy.getServiceId());
        this.systemId = UUID.fromString(proxy.getSystemId());
        this.resourceType = proxy.getResourceType();
        this.sourceId = proxy.getSourceId();
        this.edsId = UUID.fromString(proxy.getEdsId());
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getSystemId() {
        return systemId;
    }

    public void setSystemId(UUID systemId) {
        this.systemId = systemId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public UUID getEdsId() {
        return edsId;
    }

    public void setEdsId(UUID edsId) {
        this.edsId = edsId;
    }
}
