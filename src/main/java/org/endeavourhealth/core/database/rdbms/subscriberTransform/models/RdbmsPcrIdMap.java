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

    //private String pcrTableName = null;
    private String resourceId = null;
    private String resourceType = null;
    private Long pcrId = null;

    public RdbmsPcrIdMap() {}

    /*@Id
    @Column(name = "pcr_table_name", nullable = false)
    public String getPcrTableName() {
        return pcrTableName;
    }

    public void setPcrTableName(String pcrTableName) {
        this.pcrTableName = pcrTableName;
    }*/

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
    public Long getPcrId() {
        return pcrId;
    }

    public void setPcrId(Long pcrId) {
        this.pcrId = pcrId;
    }
}
