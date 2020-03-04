package org.endeavourhealth.core.database.rdbms.datasharingmanager;

import org.endeavourhealth.core.database.dal.datasharingmanager.ProjectApplicationPolicyDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ProjectApplicationPolicyEntity;

import javax.persistence.EntityManager;

public class RdbmsCoreProjectApplicationPolicyDal implements ProjectApplicationPolicyDalI {

    public ProjectApplicationPolicyEntity getProjectApplicationPolicyId(String projectUuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            ProjectApplicationPolicyEntity ret = entityManager.find(ProjectApplicationPolicyEntity.class, projectUuid);

            return ret;
        } finally {
            entityManager.close();
        }
    }
}
