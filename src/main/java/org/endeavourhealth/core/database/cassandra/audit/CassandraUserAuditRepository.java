package org.endeavourhealth.core.database.cassandra.audit;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.audit.models.CassandraUserEvent;
import org.endeavourhealth.core.database.dal.audit.UserAuditDalI;
import org.endeavourhealth.core.database.dal.audit.models.AuditAction;
import org.endeavourhealth.core.database.dal.audit.models.IAuditModule;
import org.endeavourhealth.core.database.dal.audit.models.UserEvent;

import java.util.*;

public class CassandraUserAuditRepository extends Repository implements UserAuditDalI {
    private IAuditModule module, subModule;

    public CassandraUserAuditRepository(IAuditModule auditModule) {
        super();
        if (auditModule.getParent() == null) {
            module = auditModule;
            subModule = null;
        } else {
            module = auditModule.getParent();
            subModule = auditModule;
        }
    }

    public void save(UUID userId, UUID organisationUuid, AuditAction action) {
        save(userId, organisationUuid, action, null);
    }

    public void save(UUID userId, UUID organisationUuid, AuditAction action, String title) {
        save(userId, organisationUuid, action, title, (Object[])null);
    }

    public void save(UUID userId, UUID organisationUuid, AuditAction action, String title, Object... paramValuePairs) {

        StringBuffer buf = new StringBuffer();
        buf.append((title == null ? "" : title));

        if (paramValuePairs != null) {
            for (Object object : paramValuePairs) {
                buf.append(System.lineSeparator());
                try {
                    buf.append(ObjectMapperPool.getInstance().writeValueAsString(object));
                } catch (JsonProcessingException e) {
                    buf.append(object.toString());
                }
            }
        }

        Mapper<CassandraUserEvent> userEventMapper = getMappingManager().mapper(CassandraUserEvent.class);

        CassandraUserEvent userEvent = new CassandraUserEvent(
            userId,
            module,
            subModule,
            action.name(),
            organisationUuid,
            buf.toString()
        );

        userEventMapper.save(userEvent);
    }

    public List<UserEvent> load(String module, UUID userId, Date month, UUID organisationId) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(month);
        cal.set(Calendar.DATE,1);
        Date startDate = cal.getTime();
        cal.add(Calendar.MONTH,1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date endDate = cal.getTime();

        Select.Where statement;
        if (organisationId == null)
            statement = QueryBuilder.select()
                .from("audit", "user_event_by_module_user_timestamp")
                .where(QueryBuilder.eq("module", module))
                .and(QueryBuilder.eq("user_id", userId));
        else {
            statement = QueryBuilder.select()
                .from("audit", "user_event_by_module_user_organisation_timestamp")
                .where(QueryBuilder.eq("module", module))
                .and(QueryBuilder.eq("user_id", userId))
                .and(QueryBuilder.eq("organisation_id", organisationId));

        }

        statement = statement
            .and(QueryBuilder.gte("timestamp", startDate))
            .and(QueryBuilder.lte("timestamp", endDate));

        Session session = getSession();
        ResultSet resultSet = session.execute(statement);

        List<UserEvent> ret = new ArrayList<>();
        int remaining = resultSet.getAvailableWithoutFetching();
        for (Row row : resultSet) {

            CassandraUserEvent dbObj = new CassandraUserEvent(row);
            ret.add(new UserEvent(dbObj));
            if (--remaining == 0) {
                break;
            }
        }

        return ret;
    }

}
