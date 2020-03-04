package org.endeavourhealth.core.database.dal.usermanager.caching;



import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.usermanager.DelegationDalI;
import org.endeavourhealth.core.database.rdbms.usermanager.RdbmsCoreDelegationDal;
import org.endeavourhealth.core.database.rdbms.usermanager.models.DelegationEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DelegationCache {

    private static Map<String, DelegationEntity> delegationMap = new ConcurrentHashMap<>();

    private static DelegationDalI repository = DalProvider.factoryUMDelegationDal();

    public static DelegationEntity getDelegationDetails(String delgationId) throws Exception {

        DelegationEntity foundDelegation = delegationMap.get(delgationId);
        if (foundDelegation == null) {
            foundDelegation = repository.getDelegation(delgationId);
            delegationMap.put(foundDelegation.getUuid(), foundDelegation);
        }

        CacheManager.startScheduler();

        return foundDelegation;

    }

    public static void clearDelegationCache(String delegationId) throws Exception {
        delegationMap.remove(delegationId);
    }

    public static void flushCache() throws Exception {
        delegationMap.clear();
    }
}
