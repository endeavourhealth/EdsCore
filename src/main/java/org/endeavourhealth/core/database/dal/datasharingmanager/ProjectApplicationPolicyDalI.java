package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ProjectApplicationPolicyEntity;

public interface ProjectApplicationPolicyDalI {
    public ProjectApplicationPolicyEntity getProjectApplicationPolicyId(String projectUuid) throws Exception;
}
