package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "pcr_id_map")
public class RdbmsPcrIdMap implements Serializable {
    // Provides a map from eg ehr.resource_current to pcr db so we can see
    // where PCR data came from.
    // Mainly reserves an id in the pcr namespace for consistent upserts
    // Can be extended to allow support for more populous resource types

    private String resourceId = null;
    private String resourceType = null;
    private Long pcrId = null;

    public RdbmsPcrIdMap() { }

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
    public Long getPcrId() { return pcrId; }

    public void setPcrId(Long pcrId) {
        this.pcrId = pcrId;
    }

}
