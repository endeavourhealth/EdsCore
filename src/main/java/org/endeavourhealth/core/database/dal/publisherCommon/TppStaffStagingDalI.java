package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppStaffMemberProfileStaging;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppStaffMemberStaging;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TppStaffStagingDalI {

    /**
     * update staging tables with new data
     */
    void updateStaffMemberStagingTable(List<TppStaffMemberStaging> records) throws Exception;
    void updateStaffMemberProfileStagingTable(List<TppStaffMemberProfileStaging> records) throws Exception;

    /**
     * retrieves staged records for the given profile IDs
     */
    Map<TppStaffMemberProfileStaging, TppStaffMemberStaging> retrieveAllStagingRecordsForProfileIds(Set<Integer> staffMemberProfileIds) throws Exception;

    /**
     * returns profile IDs for staff member IDs
     */
    Map<Integer, List<Integer>> findStaffMemberProfileIdsForStaffMemberIds(Set<Integer> staffMemberIds) throws Exception;
}
