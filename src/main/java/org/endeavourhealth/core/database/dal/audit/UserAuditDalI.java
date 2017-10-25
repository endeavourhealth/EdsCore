package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.AuditAction;
import org.endeavourhealth.core.database.dal.audit.models.UserEvent;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface UserAuditDalI {

    void save(UUID userId, UUID organisationUuid, AuditAction action) throws Exception;
    void save(UUID userId, UUID organisationUuid, AuditAction action, String title) throws Exception;
    void save(UUID userId, UUID organisationUuid, AuditAction action, String title, Object... paramValuePairs) throws Exception;

    List<UserEvent> load(String module, UUID userId, Date month, UUID organisationId) throws Exception;
}
