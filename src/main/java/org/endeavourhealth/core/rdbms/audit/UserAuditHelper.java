package org.endeavourhealth.core.rdbms.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.rdbms.ConnectionManager;
import org.endeavourhealth.core.rdbms.admin.models.Organisation;
import org.endeavourhealth.core.rdbms.audit.models.AuditAction;
import org.endeavourhealth.core.rdbms.audit.models.AuditModule;
import org.endeavourhealth.core.rdbms.audit.models.IAuditModule;
import org.endeavourhealth.core.rdbms.audit.models.UserEvent;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

public class UserAuditHelper {

    public static void save(IAuditModule auditModule, UUID userId, UUID organisationUuid, AuditAction action) throws Exception  {
        save(auditModule, userId, organisationUuid, action, null);
    }

    public static void save(IAuditModule auditModule, UUID userId, UUID organisationUuid, AuditAction action, String title) throws Exception  {
        save(auditModule, userId, organisationUuid, action, title, (Object[])null);
    }

    public static void save(IAuditModule auditModule, UUID userId, UUID organisationUuid, AuditAction action, String title, Object... paramValuePairs) throws Exception {

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

        UserEvent userEvent = new UserEvent();
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
                + " UserEvent c"
                + " where c.module like :module"
                + " and c.userId = :user_id"
                + " and c.timestamp >= :start_date"
                + " and c.timestamp <= :end_date";

        if (organisationId != null) {
            sql += " and c.organisationId = :organisation_id";
        }

        Query query = entityManager.createQuery(sql, UserEvent.class)
                .setParameter("module", module)
                .setParameter("user_id", userId.toString())
                .setParameter("start_date", startDate)
                .setParameter("end_date", endDate);

        if (organisationId != null) {
            query.setParameter("organisation_id", organisationId.toString());
        }

        List<UserEvent> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public List<String> getModuleList() {
        List<String> modules = new ArrayList<>();
        for (AuditModule module : AuditModule.values()) {
            modules.add(module.name());
        }
        return modules;
    }

    public List<String> getSubModuleList(String module) {
        List<String> submodules = new ArrayList<>();
        submodules.addAll(AuditModule.allSubModules().stream()
                .filter(subModule -> ((Enum) subModule.getParent()).name().equals(module))
                .map(subModule -> ((Enum) subModule).name())
                .collect(Collectors.toList()));
        return submodules;
    }

    public List<String> getActionList() {
        List<String> actions = new ArrayList<>();
        for (AuditAction action : AuditAction.values()) {
            actions.add(action.name());
        }
        return actions;
    }
}
