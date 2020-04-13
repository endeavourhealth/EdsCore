package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisOrganisation;

import java.util.Date;
import java.util.Set;

public interface EmisOrganisationDalI {

    //functions to retrieve data
    Set<String> retrieveAllIds() throws Exception;
    Set<EmisOrganisation> retrieveRecordsForIds(Set<String> ids) throws Exception;

    //fn to update the staging table
    void updateStagingTable(String s3FilePath, Date dataDate, int publishedFileId) throws Exception;
}
