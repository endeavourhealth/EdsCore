package org.endeavourhealth.core.database.dal.usermanager.caching;


import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.DataSharingAgreementDalI;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreDataSharingAgreementDal;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.DataSharingAgreementEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSharingAgreementCache {

    private static Map<String, DataSharingAgreementEntity> dataSharingAgreementMap = new ConcurrentHashMap<>();
    private static Map<String, List<DataSharingAgreementEntity>> allDSAsForAllChildRegion = new ConcurrentHashMap<>();
    private static Map<String, List<DataSharingAgreementEntity>> allDSAsForPublisher = new ConcurrentHashMap<>();
    private static Map<String, List<DataSharingAgreementEntity>> allDSAsForPublisherAndSubscriber = new ConcurrentHashMap<>();

    private static DataSharingAgreementDalI repository = DalProvider.factoryDSMDataSharingAgreementDal();

    public static List<DataSharingAgreementEntity> getDSADetails(List<String> sharingAgreements) throws Exception {
        List<DataSharingAgreementEntity> dataSharingAgreementEntities = new ArrayList<>();
        List<String> missingDSAs = new ArrayList<>();

        for (String dsa : sharingAgreements) {
            DataSharingAgreementEntity dsaInMap = dataSharingAgreementMap.get(dsa);
            if (dsaInMap != null) {
                dataSharingAgreementEntities.add(dsaInMap);
            } else {
                missingDSAs.add(dsa);
            }
        }

        if (missingDSAs.size() > 0) {
            List<DataSharingAgreementEntity> entities = repository.getDSAsFromList(missingDSAs);

            for (DataSharingAgreementEntity org : entities) {
                dataSharingAgreementMap.put(org.getUuid(), org);
                dataSharingAgreementEntities.add(org);
            }
        }

        CacheManager.startScheduler();

        return dataSharingAgreementEntities;

    }

    public static DataSharingAgreementEntity getDSADetails(String dsaId) throws Exception {

        DataSharingAgreementEntity dataSharingAgreementEntity = dataSharingAgreementMap.get(dsaId);
        if (dataSharingAgreementEntity == null) {
            dataSharingAgreementEntity = repository.getDSA(dsaId);
            dataSharingAgreementMap.put(dataSharingAgreementEntity.getUuid(), dataSharingAgreementEntity);
        }

        CacheManager.startScheduler();

        return dataSharingAgreementEntity;

    }

    public static List<DataSharingAgreementEntity> getAllDSAsForAllChildRegions(String regionId) throws Exception {

        List <DataSharingAgreementEntity> allDSAs = allDSAsForAllChildRegion.get(regionId);
        if (allDSAs == null) {
            allDSAs = repository.getAllDSAsForAllChildRegions(regionId);
            allDSAsForAllChildRegion.put(regionId, allDSAs);
        }

        CacheManager.startScheduler();

        return allDSAs;
    }

    public static List<DataSharingAgreementEntity> getAllDSAsForPublisherOrg(String odsCode) throws Exception {

        List <DataSharingAgreementEntity> allDSAs = allDSAsForPublisher.get(odsCode);
        if (allDSAs == null) {
            allDSAs = repository.getAllDSAsForPublisherOrganisation(odsCode);
            allDSAsForPublisher.put(odsCode, allDSAs);
        }

        CacheManager.startScheduler();

        return allDSAs;
    }

    public static List<DataSharingAgreementEntity> getAllDSAsForPublisherAndSubscriber(String publisherOdsCode, String subscriberOdsCode) throws Exception {

        String key = publisherOdsCode + ":" + subscriberOdsCode;

        List <DataSharingAgreementEntity> allDSAs = allDSAsForPublisherAndSubscriber.get(key);
        if (allDSAs == null) {
            allDSAs = repository.getDSAsWithMatchingPublisherAndSubscriber(publisherOdsCode, subscriberOdsCode);
            allDSAsForPublisherAndSubscriber.put(key, allDSAs);
        }

        CacheManager.startScheduler();

        return allDSAs;
    }

    public static void clearDataSharingAgreementCache(String dsaId) throws Exception {

        dataSharingAgreementMap.remove(dsaId);

        allDSAsForAllChildRegion.clear();
        allDSAsForPublisher.clear();
        allDSAsForPublisherAndSubscriber.clear();

    }

    public static void flushCache() throws Exception {
        dataSharingAgreementMap.clear();
        allDSAsForAllChildRegion.clear();
        allDSAsForPublisher.clear();
        allDSAsForPublisherAndSubscriber.clear();
    }
}
