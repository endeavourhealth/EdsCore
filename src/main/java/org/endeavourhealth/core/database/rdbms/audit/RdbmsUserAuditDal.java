package org.endeavourhealth.core.database.rdbms.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.dal.audit.UserAuditDalI;
import org.endeavourhealth.core.database.dal.audit.models.AuditAction;
import org.endeavourhealth.core.database.dal.audit.models.IAuditModule;
import org.endeavourhealth.core.database.dal.audit.models.UserEvent;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsUserEvent;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RdbmsUserAuditDal implements UserAuditDalI {

    private IAuditModule auditModule;

    public RdbmsUserAuditDal(IAuditModule auditModule) {
        this.auditModule = auditModule;
    }


    public void save(UUID userId, UUID organisationUuid, AuditAction action) throws Exception  {
        save(userId, organisationUuid, action, null);
    }

    public void save(UUID userId, UUID organisationUuid, AuditAction action, String title) throws Exception  {
        save(userId, organisationUuid, action, title, (Object[])null);
    }

    public void save(UUID userId, UUID organisationUuid, AuditAction action, String title, Object... paramValuePairs) throws Exception {

        StringBuilder buf = new StringBuilder();

        if (!Strings.isNullOrEmpty(title)) {
            buf.append(title);
        }

        if (paramValuePairs != null) {
            for (Object object : paramValuePairs) {

                buf.append(System.lineSeparator());
                try {
                    String s = ObjectMapperPool.getInstance().writeValueAsString(object);
                    buf.append(s);

                } catch (JsonProcessingException e) {
                    buf.append(object.toString());
                }
            }
        }

        String module = null;
        String subModule = null;

        if (auditModule.getParent() == null) {
            module = ((Enum)auditModule).name();
            subModule = null;
        } else {
            module = ((Enum)auditModule.getParent()).name();;
            subModule = ((Enum)auditModule).name();;
        }

        RdbmsUserEvent userEvent = new RdbmsUserEvent();
        userEvent.setUserId(userId.toString());
        userEvent.setModule(module);
        userEvent.setSubModule(subModule);
        userEvent.setAction(action.name());
        userEvent.setOrganisationId(organisationUuid.toString());
        userEvent.setData(buf.toString());

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        entityManager.persist(userEvent);
        entityManager.close();
    }

    public List<UserEvent> load(String module, UUID userId, Date month, UUID organisationId) throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.setTime(month);
        cal.set(Calendar.DATE,1);
        Date startDate = cal.getTime();
        cal.add(Calendar.MONTH,1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date endDate = cal.getTime();


        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsUserEvent c"
                + " where c.module like :module"
                + " and c.userId = :user_id"
                + " and c.timestamp >= :start_date"
                + " and c.timestamp <= :end_date";

        if (organisationId != null) {
            sql += " and c.organisationId = :organisation_id";
        }

        Query query = entityManager.createQuery(sql, RdbmsUserEvent.class)
                .setParameter("module", module)
                .setParameter("user_id", userId.toString())
                .setParameter("start_date", startDate)
                .setParameter("end_date", endDate);

        if (organisationId != null) {
            query.setParameter("organisation_id", organisationId.toString());
        }

        List<RdbmsUserEvent> ret = query.getResultList();

        entityManager.close();

        return ret
                .stream()
                .map(T -> new UserEvent(T))
                .collect(Collectors.toList());
    }

}
