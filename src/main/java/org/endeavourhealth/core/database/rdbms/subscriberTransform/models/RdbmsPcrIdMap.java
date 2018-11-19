package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "pcr_id_map")

public class RdbmsPcrIdMap implements Serializable {
    // Provides a map from eg ehr.resource_current to pcr db so we can see
    // where PCR data came from.
    // Mainly reserves an id in the pcr namespace for consistent upserts
    // Can be extended to allow support for more populous resource types
    private Long pcrId = null;
    private String resourceId = null;
    protected String resourceType = null;
    private Integer sourceDb = null;  // Pointer to pcr_db_map

    public RdbmsPcrIdMap() {
    }

    @Id
    @Column(name = "resource_id", nullable = false)
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Id
    @Column(name = "resource_type", nullable = false)
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Generated(GenerationTime.INSERT)
    @Column(name = "pcr_id", insertable = false)
    public Long getId() {
        return pcrId;
    }

    public void setId(Long pcrId) {
        this.pcrId = pcrId;
    }


//    @Column(name = "source_db")
//    public Integer getSourceDb() {
//        return sourceDb;
//    }
//
//    public void setSourceDb(Integer sourceDb) {
//        this.sourceDb = sourceDb;
//    }

}
