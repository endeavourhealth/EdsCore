package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.ScheduledTaskAudit;

import java.util.List;

public interface ScheduledTaskAuditDalI {

    void auditTaskSuccess(String taskName) throws Exception;
    void auditTaskFailure(String taskName, Throwable ex) throws Exception;
    void auditTaskFailure(String taskName, String error) throws Exception;

    List<ScheduledTaskAudit> getLatestAudits() throws Exception;


}
