package org.endeavourhealth.core.database.rdbms.logback;

import org.endeavourhealth.core.database.dal.logback.LogbackDalI;
import org.endeavourhealth.core.database.dal.logback.models.LoggingEvent;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.logback.models.RdbmsLoggingEvent;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

public class RdbmsLogbackDal implements LogbackDalI {

    private static final int PAGESIZE = 15;

    public List<LoggingEvent> getLoggingEvents(int page, String serviceId, String level) throws Exception {
        EntityManager entityManager = ConnectionManager.getLogbackEntityManager();

        String sql = "select e" +
                " from " +
                "    RdbmsLoggingEvent e," +
                "    RdbmsLoggingEventProperty p" +
                " where" +
                "    e.eventId = p.eventId" +
                "    and p.mappedKey = 'ServiceId'" +
                "    and p.mappedValue = :serviceId";

        if (level != null && !level.isEmpty())
            sql += "    and e.levelString = :level";

        sql += "    order by e.timestmp desc";

        Query query = entityManager.createQuery(sql, RdbmsLoggingEvent.class)
                .setParameter("serviceId", serviceId)
                .setFirstResult(page * PAGESIZE)
                .setMaxResults(PAGESIZE);

        if (level != null && !level.isEmpty())
            query.setParameter("level", level);

        List<RdbmsLoggingEvent> events = query.getResultList();

        entityManager.close();

        return events
                .stream()
                .map(T -> new LoggingEvent(T))
                .collect(Collectors.toList());
    }

    public String getStackTrace(Long eventId) throws Exception {
        EntityManager entityManager = ConnectionManager.getLogbackEntityManager();

        String sql = "select e.traceLine" +
                " from " +
                "    RdbmsLoggingEventExceptionEntity e" +
                " where" +
                "    e.eventId = :eventId";

        Query query = entityManager.createQuery(sql, String.class)
                .setParameter("eventId", eventId);

        List<String> stackTrace = query.getResultList();

        entityManager.close();

        return String.join(System.lineSeparator(), stackTrace);
    }
}
