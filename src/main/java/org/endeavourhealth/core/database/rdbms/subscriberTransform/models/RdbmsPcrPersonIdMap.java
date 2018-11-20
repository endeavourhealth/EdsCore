package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "pcr_person_id_map")
public class RdbmsPcrPersonIdMap implements Serializable {

    private String personId = null;
    //private String pcrConfigName = null;
    private Long pcrPersonId;

    public RdbmsPcrPersonIdMap() { }

    @Id
    @Column(name = "person_id", nullable = false)
    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    /*@Id
    @Column(name = "pcr_config_name", nullable = false)
    public String getPcrConfigName() {
        return pcrConfigName;
    }

    public void setPcrConfigName(String pcrConfigName) {
        this.pcrConfigName = pcrConfigName;
    }*/

    @Generated(GenerationTime.INSERT)
    @Column(name = "pcr_person_id", insertable = false)
    public Long getPcrPersonId() {
        return pcrPersonId;
    }

    public void setPcrPersonId(Long pcrPersonId) {
        this.pcrPersonId = pcrPersonId;
    }
}
