package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "pcr_event_id_map")

public class RdbmsPcrEventIdMap extends RdbmsPcrIdMap implements Serializable  {

    public RdbmsPcrEventIdMap() {
        super();
        }
}
