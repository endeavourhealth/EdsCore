package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisLocation;

import java.util.Date;
import java.util.Set;

public interface EmisLocationDalI {

    //functions to retrieve data
    Set<String> retrieveAllIds() throws Exception;
    Set<EmisLocation> retrieveRecordsForIds(Set<String> ids) throws Exception;

    //fn to update the staging table
    void updateLocationStagingTable(String s3FilePath, Date dataDate, int publishedFileId) throws Exception;
    void updateOrganisationLocationStagingTable(String s3FilePath, Date dataDate, int publishedFileId) throws Exception;

}
