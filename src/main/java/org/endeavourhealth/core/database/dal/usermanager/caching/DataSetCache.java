package org.endeavourhealth.core.database.dal.usermanager.caching;


import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.DataSetDalI;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreDataSetDal;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.DataSetEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSetCache {

    private static Map<String, DataSetEntity> dataSetMap = new ConcurrentHashMap<>();

    private static DataSetDalI repository = DalProvider.factoryDSMDataSetDal();

    public static List<DataSetEntity> getDataSetDetails(List<String> dataSets) throws Exception {
        List<DataSetEntity> datasetEntities = new ArrayList<>();
        List<String> missingDataSets = new ArrayList<>();

        for (String ds : dataSets) {
            DataSetEntity dsInMap = dataSetMap.get(ds);
            if (dsInMap != null) {
                datasetEntities.add(dsInMap);
            } else {
                missingDataSets.add(ds);
            }
        }

        if (missingDataSets.size() > 0) {
            List<DataSetEntity> entities = repository.getDataSetsFromList(missingDataSets);

            for (DataSetEntity ds : entities) {
                dataSetMap.put(ds.getUuid(), ds);
                datasetEntities.add(ds);
            }
        }

        CacheManager.startScheduler();

        return datasetEntities;

    }

    public static DataSetEntity getDataSetDetails(String dataSetId) throws Exception {

        DataSetEntity dataSetEntity = dataSetMap.get(dataSetId);
        if (dataSetEntity == null) {
            dataSetEntity = repository.getDataSet(dataSetId);
            dataSetMap.put(dataSetEntity.getUuid(), dataSetEntity);
        }

        CacheManager.startScheduler();

        return dataSetEntity;

    }

    public static void clearDataSetCache(String dataSetId) throws Exception {
        dataSetMap.remove(dataSetId);
    }

    public static void flushCache() throws Exception {
        dataSetMap.clear();
    }
}
