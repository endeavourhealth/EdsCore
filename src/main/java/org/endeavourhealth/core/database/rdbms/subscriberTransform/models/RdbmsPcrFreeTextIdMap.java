package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "pcr_free_text_id_map")

public class RdbmsPcrFreeTextIdMap extends RdbmsPcrIdMap implements Serializable  {

    public RdbmsPcrFreeTextIdMap() {
        super();
        }


}
