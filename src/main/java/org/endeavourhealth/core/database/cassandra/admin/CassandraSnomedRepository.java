package org.endeavourhealth.core.database.cassandra.admin;

import com.datastax.driver.mapping.Mapper;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraSnomedLookup;
import org.endeavourhealth.core.database.dal.reference.SnomedDalI;
import org.endeavourhealth.core.database.dal.reference.models.SnomedLookup;

public class CassandraSnomedRepository extends Repository implements SnomedDalI {

    public SnomedLookup getSnomedLookup(String conceptId) {
        Mapper<CassandraSnomedLookup> mapper = getMappingManager().mapper(CassandraSnomedLookup.class);
        CassandraSnomedLookup result = mapper.get(conceptId);
        if (result != null) {
            return new SnomedLookup(result);
        } else {
            return null;
        }
    }
}
