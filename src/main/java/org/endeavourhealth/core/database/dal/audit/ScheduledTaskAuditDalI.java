package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.ScheduledTaskAudit;

import java.util.List;

public interface ScheduledTaskAuditDalI {

    void auditTaskSuccess(String taskName, String[] taskParameters) throws Exception;
    void auditTaskFailure(String taskName, String[] taskParameters, Throwable ex) throws Exception;
    void auditTaskFailure(String taskName, String[] taskParameters, String error) throws Exception;

    List<ScheduledTaskAudit> getLatestAudits() throws Exception;
    List<ScheduledTaskAudit> getHistory(String applicationName, String taskName) throws Exception;


}
