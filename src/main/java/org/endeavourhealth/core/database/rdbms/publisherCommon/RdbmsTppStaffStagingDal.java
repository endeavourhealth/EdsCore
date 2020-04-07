package org.endeavourhealth.core.database.rdbms.publisherCommon;

import com.fasterxml.jackson.core.type.TypeReference;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.dal.publisherCommon.TppStaffStagingDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppStaffMemberProfileStaging;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppStaffMemberStaging;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RdbmsTppStaffStagingDal implements TppStaffStagingDalI {

    @Override
    public void updateStaffMemberStagingTable(List<TppStaffMemberStaging> records) throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;

        try {
            //see https://thewebfellas.com/blog/conditional-duplicate-key-updates-with-mysql/ for an explanation of this syntax
            String sql = "INSERT INTO tpp_staging_staff_member (row_identifier, dt_last_updated, column_data, published_file_id, published_file_record_number)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " column_data = IF(VALUES(dt_last_updated) > dt_last_updated, VALUES(column_data), column_data),"
                    + " published_file_id = IF(VALUES(dt_last_updated) > dt_last_updated, VALUES(published_file_id), published_file_id),"
                    + " published_file_record_number = IF(VALUES(dt_last_updated) > dt_last_updated, VALUES(published_file_record_number), published_file_record_number),"
                    + " dt_last_updated = IF(VALUES(dt_last_updated) > dt_last_updated, VALUES(dt_last_updated), dt_last_updated)";
            //NOTE: updating dt_last_updated MUST always be last in the above UPDATE, otherwise it will NOT update any columns after

            ps = connection.prepareStatement(sql);

            for (TppStaffMemberStaging record: records) {

                String json = ObjectMapperPool.getInstance().writeValueAsString(record.getColumnData());

                int col = 1;

                ps.setInt(col++, record.getRowIdentifier());
                ps.setTimestamp(col++, new java.sql.Timestamp(record.getDtLastUpdated().getTime()));
                ps.setString(col++, json);
                ps.setInt(col++, record.getPublishedFileId());
                ps.setInt(col++, record.getPublishedRecordNumber());
                ps.addBatch();
            }

            ps.executeBatch();
            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    @Override
    public void updateStaffMemberProfileStagingTable(List<TppStaffMemberProfileStaging> records) throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;

        try {
            //see https://thewebfellas.com/blog/conditional-duplicate-key-updates-with-mysql/ for an explanation of this syntax
            String sql = "INSERT INTO tpp_staging_staff_member_profile (row_identifier, dt_last_updated, staff_member_row_identifier, column_data, published_file_id, published_file_record_number)"
                    + " VALUES (?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " column_data = IF(VALUES(dt_last_updated) > dt_last_updated, VALUES(column_data), column_data),"
                    + " staff_member_row_identifier = IF(VALUES(dt_last_updated) > dt_last_updated, VALUES(staff_member_row_identifier), staff_member_row_identifier),"
                    + " published_file_id = IF(VALUES(dt_last_updated) > dt_last_updated, VALUES(published_file_id), published_file_id),"
                    + " published_file_record_number = IF(VALUES(dt_last_updated) > dt_last_updated, VALUES(published_file_record_number), published_file_record_number),"
                    + " dt_last_updated = IF(VALUES(dt_last_updated) > dt_last_updated, VALUES(dt_last_updated), dt_last_updated)";
            //NOTE: updating dt_last_updated MUST always be last in the above UPDATE, otherwise it will NOT update any columns after

            ps = connection.prepareStatement(sql);

            for (TppStaffMemberProfileStaging record: records) {

                String json = ObjectMapperPool.getInstance().writeValueAsString(record.getColumnData());

                int col = 1;

                ps.setInt(col++, record.getRowIdentifier());
                ps.setTimestamp(col++, new java.sql.Timestamp(record.getDtLastUpdated().getTime()));
                ps.setInt(col++, record.getStaffMemberRowIdentifier());
                ps.setString(col++, json);
                ps.setInt(col++, record.getPublishedFileId());
                ps.setInt(col++, record.getPublishedRecordNumber());
                ps.addBatch();
            }

            ps.executeBatch();
            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    @Override
    public Map<TppStaffMemberProfileStaging, TppStaffMemberStaging> retrieveAllStagingRecordsForProfileIds(Set<Integer> hsStaffMemberProfileIds) throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;

        try {

            List<Integer> staffMemberProfileIds = new ArrayList<>(hsStaffMemberProfileIds);

            String sql = "SELECT"
                    + " p.row_identifier, p.dt_last_updated, p.column_data, p.published_file_id, p.published_file_record_number,"
                    + " s.row_identifier, s.dt_last_updated, s.column_data, s.published_file_id, s.published_file_record_number"
                    + " FROM tpp_staging_staff_member_profile p"
                    + " INNER JOIN tpp_staging_staff_member s"
                    + " ON s.row_identifier = p.staff_member_row_identifier"
                    + " WHERE p.row_identifier IN (";
            for (int i=0; i<staffMemberProfileIds.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;
            for (int i=0; i<staffMemberProfileIds.size(); i++) {
                Integer id = staffMemberProfileIds.get(i);
                ps.setInt(col++, id);
            }

            Map<TppStaffMemberProfileStaging, TppStaffMemberStaging> ret = new HashMap<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                col = 1;

                int profileId = rs.getInt(col++);
                Date dtProfileUpdated = new java.util.Date(rs.getTimestamp(col++).getTime());
                String profileColumnDataStr = rs.getString(col++);
                int profileFileId = rs.getInt(col++);
                int profileFileRecord = rs.getInt(col++);
                int staffId = rs.getInt(col++);
                Date dtStaffUpdated = new java.util.Date(rs.getTimestamp(col++).getTime());
                String staffColumnDataStr = rs.getString(col++);
                int staffFileId = rs.getInt(col++);
                int staffFileRecord = rs.getInt(col++);

                Map<String, String> profileColumnData = ObjectMapperPool.getInstance().readValue(profileColumnDataStr, new TypeReference<Map<String, String>>() {});
                Map<String, String> staffColumnData = ObjectMapperPool.getInstance().readValue(staffColumnDataStr, new TypeReference<Map<String, String>>() {});

                TppStaffMemberProfileStaging profile = new TppStaffMemberProfileStaging();
                profile.setRowIdentifier(profileId);
                profile.setDtLastUpdated(dtProfileUpdated);
                profile.setStaffMemberRowIdentifier(staffId);
                profile.setColumnData(profileColumnData);
                profile.setPublishedFileId(profileFileId);
                profile.setPublishedRecordNumber(profileFileRecord);

                TppStaffMemberStaging staff = new TppStaffMemberStaging();
                staff.setRowIdentifier(staffId);
                staff.setDtLastUpdated(dtStaffUpdated);
                staff.setColumnData(staffColumnData);
                staff.setPublishedFileId(staffFileId);
                staff.setPublishedRecordNumber(staffFileRecord);

                ret.put(profile, staff);
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    @Override
    public Map<Integer, List<Integer>> findStaffMemberProfileIdsForStaffMemberIds(Set<Integer> hsStaffMemberIds) throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;

        try {
            
            List<Integer> staffMemberIds = new ArrayList<>(hsStaffMemberIds);

            String sql = "SELECT row_identifier, staff_member_row_identifier"
                    + " FROM tpp_staging_staff_member_profile"
                    + " WHERE staff_member_row_identifier IN (";
            for (int i=0; i<staffMemberIds.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";
            
            ps = connection.prepareStatement(sql);
            
            int col = 1;
            for (int i=0; i<staffMemberIds.size(); i++) {
                Integer id = staffMemberIds.get(i);
                ps.setInt(col++, id);
            }

            Map<Integer, List<Integer>> ret = new HashMap<>();
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                col = 1;
                
                int profileId = rs.getInt(col++);
                int staffId = rs.getInt(col++);
                
                List<Integer> l = ret.get(staffId);
                if (l == null) {
                    l = new ArrayList<>();
                    ret.put(staffId, l);
                }
                l.add(profileId);
            }
            
            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }


}
