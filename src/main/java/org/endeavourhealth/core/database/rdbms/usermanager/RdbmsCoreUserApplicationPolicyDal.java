package org.endeavourhealth.core.database.rdbms.usermanager;

import org.endeavourhealth.core.database.dal.usermanager.UserApplicationPolicyDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.usermanager.models.UserApplicationPolicyEntity;

import javax.persistence.EntityManager;

public class RdbmsCoreUserApplicationPolicyDal implements UserApplicationPolicyDalI {

    public UserApplicationPolicyEntity getUserApplicationPolicy(String userId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            UserApplicationPolicyEntity ret = entityManager.find(UserApplicationPolicyEntity.class, userId);

            return ret;
        } finally {
            entityManager.close();
        }
    }
}
