package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "pcr_event_id_map")
public class RdbmsPcrEventIdMap  implements Serializable  {

    private String resourceId = null;
    private String resourceType = null;
    private Long pcrId = null;

    public RdbmsPcrEventIdMap() { }

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
