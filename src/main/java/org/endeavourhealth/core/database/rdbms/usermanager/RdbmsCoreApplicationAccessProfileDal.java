package org.endeavourhealth.core.database.rdbms.usermanager;

import org.endeavourhealth.core.database.dal.usermanager.ApplicationAccessProfileDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationAccessProfileEntity;

import javax.persistence.EntityManager;

public class RdbmsCoreApplicationAccessProfileDal implements ApplicationAccessProfileDalI {

    public ApplicationAccessProfileEntity getApplicationProfile(String applicationProfileId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            ApplicationAccessProfileEntity ret = entityManager.find(ApplicationAccessProfileEntity.class, applicationProfileId);

            return ret;
        } finally {
            entityManager.close();
        }
    }
}
