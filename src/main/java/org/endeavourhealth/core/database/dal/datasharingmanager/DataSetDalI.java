package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.DataSetEntity;

import java.util.List;

public interface DataSetDalI {


    public List<DataSetEntity> getDataSetsFromList(List<String> datasets) throws Exception;
    public DataSetEntity getDataSet(String uuid) throws Exception;
}
