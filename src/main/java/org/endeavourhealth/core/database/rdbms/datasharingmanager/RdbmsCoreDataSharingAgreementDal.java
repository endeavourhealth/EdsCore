package org.endeavourhealth.core.database.rdbms.datasharingmanager;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.DataSharingAgreementDalI;
import org.endeavourhealth.core.database.dal.datasharingmanager.MasterMappingDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.DataSharingAgreementEntity;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.OrganisationEntity;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.RegionEntity;
import org.endeavourhealth.core.database.dal.datasharingmanager.enums.MapType;
import org.endeavourhealth.core.database.dal.usermanager.caching.DataSharingAgreementCache;
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
import java.util.stream.Collectors;

public class RdbmsCoreDataSharingAgreementDal implements DataSharingAgreementDalI {
    private static MasterMappingDalI masterMappingRepository = DalProvider.factoryDSMMasterMappingDal();

    public DataSharingAgreementEntity getDSA(String uuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            DataSharingAgreementEntity ret = entityManager.find(DataSharingAgreementEntity.class, uuid);

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public List<DataSharingAgreementEntity> getDSAsFromList(List<String> dsas) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<DataSharingAgreementEntity> cq = cb.createQuery(DataSharingAgreementEntity.class);
            Root<DataSharingAgreementEntity> rootEntry = cq.from(DataSharingAgreementEntity.class);

            Predicate predicate = rootEntry.get("uuid").in(dsas);

            cq.where(predicate);
            TypedQuery<DataSharingAgreementEntity> query = entityManager.createQuery(cq);

            List<DataSharingAgreementEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public List<DataSharingAgreementEntity> getAllDSAsForAllChildRegions(String regionUUID) throws Exception {
        List<String> dsaUUIDs = new ArrayList<>();

        List<RegionEntity> allRegions = RegionCache.getAllChildRegionsForRegion(regionUUID);

        for (RegionEntity region : allRegions) {
            dsaUUIDs.addAll(masterMappingRepository.getChildMappings(region.getUuid(), MapType.REGION.getMapType(), MapType.DATASHARINGAGREEMENT.getMapType()));
        }

        List<DataSharingAgreementEntity> ret = new ArrayList<>();

        if (!dsaUUIDs.isEmpty())
            ret = DataSharingAgreementCache.getDSADetails(dsaUUIDs);

        return ret;

    }

    public List<DataSharingAgreementEntity> getAllDSAsForPublisherOrganisation(String odsCode) throws Exception {
        List<String> dsaUUIDs = new ArrayList<>();

        // find org details from ods code
        OrganisationEntity org = OrganisationCache.getOrganisationDetailsFromOdsCode(odsCode);

        // get all DSAs where the org is a publisher
        dsaUUIDs = masterMappingRepository.getParentMappings(org.getUuid(),
                MapType.PUBLISHER.getMapType(), MapType.DATASHARINGAGREEMENT.getMapType());


        List<DataSharingAgreementEntity> ret = new ArrayList<>();

        // get the full DSA details
        if (!dsaUUIDs.isEmpty())
            ret = DataSharingAgreementCache.getDSADetails(dsaUUIDs);

        return ret;

    }

    public List<DataSharingAgreementEntity> getDSAsWithMatchingPublisherAndSubscriber(String publisherOds, String subscriberOds) throws Exception {

        List<String> pubOdsCodes = new ArrayList<>();

        OrganisationEntity pubOrg = OrganisationCache.getOrganisationDetailsFromOdsCode(publisherOds);
        OrganisationEntity subOrg = OrganisationCache.getOrganisationDetailsFromOdsCode(subscriberOds);

        // get DSAs for the publisher
        List<String> publisherDSAs = masterMappingRepository.getParentMappings(pubOrg.getUuid(),
                MapType.PUBLISHER.getMapType(), MapType.DATASHARINGAGREEMENT.getMapType());

        // get DSAs for the subscriber
        List<String> subscriberDSAs = masterMappingRepository.getParentMappings(subOrg.getUuid(),
                MapType.SUBSCRIBER.getMapType(), MapType.DATASHARINGAGREEMENT.getMapType());

        List<String> matchingDSAs = publisherDSAs.stream()
                .filter(subscriberDSAs::contains).collect(Collectors.toList());

        List<DataSharingAgreementEntity> dsas = DataSharingAgreementCache.getDSADetails(matchingDSAs);

        return dsas;
    }
}
