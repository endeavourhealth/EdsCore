package org.endeavourhealth.core.database.rdbms.datasharingmanager;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.MasterMappingDalI;
import org.endeavourhealth.core.database.dal.datasharingmanager.RegionDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.OrganisationEntity;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.RegionEntity;
import org.endeavourhealth.core.database.dal.datasharingmanager.enums.MapType;
import org.endeavourhealth.core.database.dal.usermanager.caching.OrganisationCache;
import org.endeavourhealth.core.database.dal.usermanager.caching.RegionCache;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class RdbmsCoreRegionDal implements RegionDalI {

    private static MasterMappingDalI masterMappingRepository = DalProvider.factoryDSMMasterMappingDal();

    public RegionEntity getSingleRegion(String uuid) throws Exception {

        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            RegionEntity ret = entityManager.find(RegionEntity.class, uuid);

            return ret;
        } finally {
            entityManager.close();
        }

    }

    public List<RegionEntity> getRegionsFromList(List<String> regions) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<RegionEntity> cq = cb.createQuery(RegionEntity.class);
            Root<RegionEntity> rootEntry = cq.from(RegionEntity.class);

            Predicate predicate = rootEntry.get("uuid").in(regions);

            cq.where(predicate);
            TypedQuery<RegionEntity> query = entityManager.createQuery(cq);

            List<RegionEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public List<RegionEntity> getAllRegions() throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<RegionEntity> cq = cb.createQuery(RegionEntity.class);
            Root<RegionEntity> rootEntry = cq.from(RegionEntity.class);
            CriteriaQuery<RegionEntity> all = cq.select(rootEntry);
            TypedQuery<RegionEntity> allQuery = entityManager.createQuery(all);
            List<RegionEntity> ret = allQuery.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public List<OrganisationEntity> getAllOrganisationsForAllChildRegions(String regionUUID) throws Exception {
        List<String> organisationUuids = new ArrayList<>();

        organisationUuids = getOrganisations(regionUUID, organisationUuids);
        List<OrganisationEntity> ret = new ArrayList<>();

        if (!organisationUuids.isEmpty())
            ret = OrganisationCache.getOrganisationDetails(organisationUuids);

        return ret;

    }

    public List<String> getOrganisations(String regionUUID, List<String> organisationUuids) throws Exception {

        organisationUuids.addAll(masterMappingRepository.getChildMappings(regionUUID, MapType.REGION.getMapType(), MapType.ORGANISATION.getMapType()));

        List<String> childRegions = masterMappingRepository.getChildMappings(regionUUID, MapType.REGION.getMapType(), MapType.REGION.getMapType());

        for (String region : childRegions) {
            organisationUuids = getOrganisations(region, organisationUuids);
        }

        return organisationUuids;
    }

    public List<RegionEntity> getAllChildRegionsForRegion(String regionId) throws Exception {
        List<String> regionUUIDs = new ArrayList<>();

        List<RegionEntity> ret = new ArrayList<>();

        regionUUIDs.add(regionId);

        regionUUIDs = getRegions(regionId, regionUUIDs);

        for (String regionUUID : regionUUIDs) {
            ret.add(RegionCache.getRegionDetails(regionUUID));
        }

        return ret;

    }

    public List<String> getRegions(String regionUUID, List<String> regionUUIDs) throws Exception {
        List<String> childRegions = masterMappingRepository.getChildMappings(regionUUID, MapType.REGION.getMapType(), MapType.REGION.getMapType());
        for (String region : childRegions) {
            // added to make sure that the values obtained from the map are existing entities, this method is recursive
            // and might cause an infinite loop if the master mapping table have loose values
            if (getSingleRegion(region) != null) {
                //Only add regions to the list if they have not been added previously
                if (!regionUUIDs.contains(region)) {
                    regionUUIDs.add(region);
                    regionUUIDs = getRegions(region, regionUUIDs);
                }
            }
        }
        return regionUUIDs;
    }

}
