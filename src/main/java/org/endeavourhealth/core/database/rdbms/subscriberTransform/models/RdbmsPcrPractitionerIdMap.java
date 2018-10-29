package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "pcr_practitioner_id_map")

public class RdbmsPcrPractitionerIdMap extends RdbmsPcrIdMap implements Serializable  {

    public RdbmsPcrPractitionerIdMap() {
        super();
        this.resourceType="Practioner";
    }
}
