package org.endeavourhealth.core.database.cassandra.transform.models;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceIdMap;

import java.util.UUID;

@Table(keyspace = "transform", name = "resource_id_map")
public class CassandraResourceIdMap {

    @PartitionKey
    @Column(name = "service_id")
    private UUID serviceId = null;
    @ClusteringColumn(0)
    @Column(name = "system_id")
    private UUID systemId = null;
    @ClusteringColumn(1)
    @Column(name = "resource_type")
    private String resourceType = null;
    @ClusteringColumn(2)
    @Column(name = "source_id")
    private String sourceId = null;
    @Column(name = "eds_id")
    private UUID edsId = null;

    public CassandraResourceIdMap() {}


    public CassandraResourceIdMap(ResourceIdMap proxy) {
        this.serviceId = proxy.getServiceId();
        this.systemId = proxy.getSystemId();
        this.resourceType = proxy.getResourceType();
        this.sourceId = proxy.getSourceId();
        this.edsId = proxy.getEdsId();
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
