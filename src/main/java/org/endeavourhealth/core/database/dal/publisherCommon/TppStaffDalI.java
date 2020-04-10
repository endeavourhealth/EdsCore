package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppStaffMember;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppStaffMemberProfile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TppStaffDalI {

    /**
     * update staging tables with new data
     */
    void updateStaffMemberLookupTable(String s3FilePath, Date dataDate, int publishedFileId) throws Exception;
    void updateStaffProfileLookupTable(String s3FilePath, Date dataDate, int publishedFileId) throws Exception;

    /**
     * retrieves staged records for the given profile IDs
     */
    Map<TppStaffMemberProfile, TppStaffMember> retrieveRecordsForProfileIds(Set<Integer> staffMemberProfileIds) throws Exception;

    /**
     * returns a profile ID for each staff member ID, choosing the "best" when multiple are found
     */
    Map<Integer, Integer> findProfileIdsForStaffMemberIdsAtOrg(String organisationId, Set<Integer> staffMemberIds) throws Exception;

    /**
     * returns all profile IDs for each staff member ID
     */
    Map<Integer, List<Integer>> findAllProfileIdsForStaffMemberIds(Set<Integer> staffMemberIds) throws Exception;
}
