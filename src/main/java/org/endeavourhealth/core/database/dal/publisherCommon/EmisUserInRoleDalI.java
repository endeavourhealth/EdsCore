package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisUserInRole;

import java.util.Date;
import java.util.Set;

public interface EmisUserInRoleDalI {

    //functions to retrieve data
    Set<String> retrieveAllIds() throws Exception;
    Set<EmisUserInRole> retrieveRecordsForIds(Set<String> ids) throws Exception;


    //fn to update the staging table
    void updateStagingTable(String s3FilePath, Date dataDate, int publishedFileId) throws Exception;
}
