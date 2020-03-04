package org.endeavourhealth.core.database.rdbms.usermanager;

import org.endeavourhealth.core.database.dal.usermanager.DelegationDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.usermanager.models.DelegationEntity;

import javax.persistence.EntityManager;

public class RdbmsCoreDelegationDal implements DelegationDalI {

    public DelegationEntity getDelegation(String delegationId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            DelegationEntity ret = entityManager.find(DelegationEntity.class, delegationId);

            return ret;
        } finally {
            entityManager.close();
        }
    }
}
