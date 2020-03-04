package org.endeavourhealth.core.database.rdbms.usermanager;

import org.endeavourhealth.core.database.dal.usermanager.ApplicationDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationEntity;

import javax.persistence.EntityManager;

public class RdbmsCoreApplicationDal implements ApplicationDalI {

    public ApplicationEntity getApplication(String applicationId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            ApplicationEntity ret = entityManager.find(ApplicationEntity.class, applicationId);

            return ret;
        } finally {
            entityManager.close();
        }
    }
}
