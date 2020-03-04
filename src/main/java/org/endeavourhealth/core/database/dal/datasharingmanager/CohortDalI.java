package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.CohortEntity;

import java.util.List;

public interface CohortDalI {

    public List<CohortEntity> getCohortsFromList(List<String> cohorts) throws Exception;
    public CohortEntity getCohort(String uuid) throws Exception;
}
