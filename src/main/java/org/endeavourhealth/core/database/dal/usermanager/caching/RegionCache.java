package org.endeavourhealth.core.database.dal.usermanager.caching;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.RegionDalI;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreRegionDal;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.OrganisationEntity;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.RegionEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegionCache {
    private static Map<String, RegionEntity> regionMap = new ConcurrentHashMap<>();
    private static boolean allRegionsFound = false;
    private static Map<String, List<OrganisationEntity>> allOrgsForAllChildRegion = new ConcurrentHashMap<>();
    private static Map<String, List<RegionEntity>> allRegionsForUser = new ConcurrentHashMap<>();

    private static RegionDalI repository = DalProvider.factoryDSMRegionDal();

    public static RegionEntity getRegionDetails(String regionId) throws Exception {

        RegionEntity foundRegion = regionMap.get(regionId);
        if (foundRegion == null) {
            foundRegion = repository.getSingleRegion(regionId);
            regionMap.put(foundRegion.getUuid(), foundRegion);

        }

        CacheManager.startScheduler();

        return foundRegion;

    }

    public static List<RegionEntity> getRegionDetails(List<String> regions) throws Exception {
        List<RegionEntity> regionEntities = new ArrayList<>();
        List<String> missingRegions = new ArrayList<>();

        for (String reg : regions) {
            RegionEntity regInMap = regionMap.get(reg);
            if (regInMap != null) {
                regionEntities.add(regInMap);
            } else {
                missingRegions.add(reg);
            }
        }

        if (missingRegions.size() > 0) {
            List<RegionEntity> entities = repository.getRegionsFromList(missingRegions);

            for (RegionEntity reg : entities) {
                regionMap.put(reg.getUuid(), reg);
                regionEntities.add(reg);
            }
        }

        CacheManager.startScheduler();

        return regionEntities;

    }

    public static List<RegionEntity> getAllRegions() throws Exception {

        if (!allRegionsFound) {
            List<RegionEntity> allRegions = repository.getAllRegions();
            for (RegionEntity reg : allRegions) {
                regionMap.put(reg.getUuid(), reg);
            }
        }

        CacheManager.startScheduler();

        allRegionsFound = true;

        return new ArrayList(regionMap.values());

    }

    public static List<OrganisationEntity> getAllOrganisationsForAllChildRegions(String regionId) throws Exception {

        List<OrganisationEntity> allOrgs = allOrgsForAllChildRegion.get(regionId);
        if (allOrgs == null) {
            allOrgs = repository.getAllOrganisationsForAllChildRegions(regionId);
            allOrgsForAllChildRegion.put(regionId, allOrgs);
        }

        CacheManager.startScheduler();

        return allOrgs;
    }

    public static List<RegionEntity> getAllChildRegionsForRegion(String regionId) throws Exception {

        List<RegionEntity> allRegions = allRegionsForUser.get(regionId);
        if (allRegions == null) {
            allRegions = repository.getAllChildRegionsForRegion(regionId);
            allRegionsForUser.put(regionId, allRegions);
        }

        CacheManager.startScheduler();

        return allRegions;
    }

    public static void clearRegionCache(String regionId) throws Exception {
        regionMap.remove(regionId);

        allOrgsForAllChildRegion.clear();
        allRegionsForUser.clear();

        allRegionsFound = false;
    }

    public static void flushCache() throws Exception {
        regionMap.clear();
        allOrgsForAllChildRegion.clear();
        allRegionsFound = false;
        allRegionsForUser.clear();
    }
}
