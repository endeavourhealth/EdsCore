package org.endeavourhealth.core.database.rdbms.usermanager;

import org.endeavourhealth.core.database.dal.usermanager.UserRegionDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.usermanager.models.UserRegionEntity;

import javax.persistence.EntityManager;

public class RdbmsCoreUserRegionDal implements UserRegionDalI {

    public UserRegionEntity getUserRegion(String userId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            UserRegionEntity ret = entityManager.find(UserRegionEntity.class, userId);

            return ret;
        } finally {
            entityManager.close();
        }
    }
}
