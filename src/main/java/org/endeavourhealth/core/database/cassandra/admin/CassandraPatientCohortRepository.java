package org.endeavourhealth.core.database.cassandra.admin;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.Mapper;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.admin.accessors.PatientCohortAccessor;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraPatientCohort;
import org.endeavourhealth.core.database.dal.admin.PatientCohortDalI;

import java.util.Date;
import java.util.UUID;

public class CassandraPatientCohortRepository extends Repository implements PatientCohortDalI {

    @Override
    public void saveInCohort(UUID protocolId, UUID serviceId, String nhsNumber, boolean inCohort) throws Exception {

        CassandraPatientCohort obj = new CassandraPatientCohort();
        obj.setProtocolId(protocolId);
        obj.setServiceId(serviceId);
        obj.setNhsNumber(nhsNumber);
        obj.setInCohort(inCohort);
        obj.setVersion(UUIDs.timeBased());
        obj.setInserted(new Date());

        Mapper<CassandraPatientCohort> mapper = getMappingManager().mapper(CassandraPatientCohort.class);
        mapper.save(obj);
    }

    public boolean isInCohort(UUID protocolId, UUID serviceId, String nhsNumber) {
        PatientCohortAccessor accessor = getMappingManager().createAccessor(PatientCohortAccessor.class);
        ResultSet resultSet = accessor.getLatestCohortStatus(protocolId, serviceId, nhsNumber);
        Row row = resultSet.one();
        if (row != null) {
            return row.getBool(0);
        } else {
            //if there's no row in the result set, the patient has never been in the cohort
            return false;
        }
    }
}
