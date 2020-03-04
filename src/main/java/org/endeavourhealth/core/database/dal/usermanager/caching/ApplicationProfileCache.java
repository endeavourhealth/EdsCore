package org.endeavourhealth.core.database.dal.usermanager.caching;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.usermanager.ApplicationAccessProfileDalI;
import org.endeavourhealth.core.database.rdbms.usermanager.RdbmsCoreApplicationAccessProfileDal;
import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationAccessProfileEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationProfileCache {
    private static Map<String, ApplicationAccessProfileEntity> applicationProfileMap = new ConcurrentHashMap<>();

    private static ApplicationAccessProfileDalI repository = DalProvider.factoryUMApplicationAccessProfileDal();

    public static ApplicationAccessProfileEntity getApplicationProfileDetails(String applicationProfileId) throws Exception {

        ApplicationAccessProfileEntity foundRole = applicationProfileMap.get(applicationProfileId);
        if (foundRole == null) {
            foundRole = repository.getApplicationProfile(applicationProfileId);
            applicationProfileMap.put(foundRole.getId(), foundRole);
        }

        CacheManager.startScheduler();

        return foundRole;

    }

    public static void clearApplicationProfileCache(String applicationProfileId) throws Exception {
            applicationProfileMap.remove(applicationProfileId);
    }

    public static void flushCache() throws Exception {
        applicationProfileMap.clear();
    }
}
