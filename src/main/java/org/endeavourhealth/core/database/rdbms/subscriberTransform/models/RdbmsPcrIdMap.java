package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "pcr_id_map")
@MappedSuperclass
public class RdbmsPcrIdMap implements Serializable {
    // Provides a map from eg ehr.resource_current to pcr db so we can see
    // where PCR data came from.
    // Mainly reserves an id in the pcr namespace for consistent upserts
    // Can be extended to allow support for more populous resource types
    private long id;
    private String resourceId = null;
    protected String resourceType = null;
    private Integer sourceDb = null;  // Pointer to pcr_db_map

    public RdbmsPcrIdMap() {
    }

    @Id
    @Generated(GenerationTime.INSERT)
    @Column(name = "id", insertable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "source_db")
    public Integer getSourceDb() {
        return sourceDb;
    }

    public void setSourceDb(Integer sourceDb) {
        this.sourceDb = sourceDb;
    }


    @Column(name = "discovery_resource_id")
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Column(name = "discovery_resource_type")
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

}
