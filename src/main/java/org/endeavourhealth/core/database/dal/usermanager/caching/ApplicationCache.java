package org.endeavourhealth.core.database.dal.usermanager.caching;


import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.usermanager.ApplicationDalI;
import org.endeavourhealth.core.database.rdbms.usermanager.RdbmsCoreApplicationDal;
import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationCache {
    private static Map<String, ApplicationEntity> applicationMap = new ConcurrentHashMap<>();

    private static ApplicationDalI repository = DalProvider.factoryUMApplicationDal();

    public static ApplicationEntity getApplicationDetails(String applicationId) throws Exception {

        ApplicationEntity foundRole = applicationMap.get(applicationId);
        if (foundRole == null) {
            foundRole = repository.getApplication(applicationId);
            applicationMap.put(foundRole.getId(), foundRole);

        }

        CacheManager.startScheduler();
        return foundRole;

    }

    public static void clearApplicationCache(String applicationId) throws Exception {
        applicationMap.remove(applicationId);
    }

    public static void flushCache() throws Exception {
        applicationMap.clear();
    }
}
