package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "pcr_db_map")

public class RdbmsPcrDbMap implements Serializable {
    // For audit trail. Save where data came to PCR from
    private long id;
    private String discoveryDb = null;
    private String discoverySchema = null;

    public RdbmsPcrDbMap() {
    }


    @Id
    @Generated(GenerationTime.INSERT)
    @Column(name = "id", insertable = false)
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "discovery_db")
    public String getDiscoveryDb() {
        return discoveryDb;
    }

    public void setDiscoveryDb(String discoveryDb) {
        this.discoveryDb = discoveryDb;
    }

    @Column(name = "discovery_schema")
    public String getDiscoverySchema() {
        return discoverySchema;
    }

    public void setDiscoverySchema(String discoverySchema) {
        this.discoverySchema = discoverySchema;
    }


}
