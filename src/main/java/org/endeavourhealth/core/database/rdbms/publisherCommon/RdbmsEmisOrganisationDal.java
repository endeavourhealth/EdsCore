package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.publisherCommon.EmisOrganisationDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisOrganisation;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class RdbmsEmisOrganisationDal implements EmisOrganisationDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsEmisOrganisationDal.class);

    @Override
    public Set<String> retrieveAllIds() throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT organisation_guid FROM emis_organisation";
            ps = connection.prepareStatement(sql);

            Set<String> ret = new HashSet<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ret.add(rs.getString(1));
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
    public Set<EmisOrganisation> retrieveRecordsForIds(Set<String> hsIds) throws Exception {
        
        List<String> ids = new ArrayList<>(hsIds);

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {

            String sql = "SELECT organisation_guid, cdb, organisation_name, ods_code,"
                    + " parent_organisation_guid, ccg_organisation_guid, organisation_type, open_date, close_date,"
                    + " main_location_guid, published_file_id, published_file_record_number, dt_last_updated"
                    + " FROM emis_organisation"
                    + " WHERE organisation_guid IN (";
            for (int i=0; i<ids.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);
            
            int col = 1;
            for (int i=0; i<ids.size(); i++) {
                String id = ids.get(i);
                ps.setString(col++, id);
            }

            Set<EmisOrganisation> ret = new HashSet<>();

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                col = 1;

                EmisOrganisation o = new EmisOrganisation();
                o.setOrganisationGuid(rs.getString(col++));
                o.setCdb(rs.getString(col++));
                o.setOrganisationName(rs.getString(col++));
                o.setOdsCode(rs.getString(col++));
                o.setParentOrganisationGuid(rs.getString(col++));
                o.setCcgOrganisationGuid(rs.getString(col++));
                o.setOrganisationType(rs.getString(col++));

                java.sql.Timestamp ts = rs.getTimestamp(col++);
                if (ts != null) {
                    o.setOpenDate(new java.util.Date(ts.getTime()));
                }

                ts = rs.getTimestamp(col++);
                if (ts != null) {
                    o.setCloseDate(new java.util.Date(ts.getTime()));
                }

                o.setMainLocationGuid(rs.getString(col++));
                o.setPublishedFileId(rs.getInt(col++));
                o.setPublishedFileRecordNumber(rs.getInt(col++));

                ts = rs.getTimestamp(col++);

                ret.add(o);
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
    public void updateStagingTable(String filePath, Date dataDate, int publishedFileId) throws Exception {
        long msStart = System.currentTimeMillis();

        //copy the file from S3 to local disk
        File f = FileHelper.copyFileFromStorageToTempDirIfNecessary(filePath);
        filePath = f.getAbsolutePath();

        Connection connection = ConnectionManager.getPublisherCommonNonPooledConnection();
        try {
            //turn on auto commit so we don't need to separately commit these large SQL operations
            connection.setAutoCommit(true);

            //create a temporary table to load the data into
            String tempTableName = ConnectionManager.generateTempTableName(FilenameUtils.getBaseName(filePath));
            LOG.debug("Loading " + f + " into " + tempTableName);
            String sql = "CREATE TABLE " + tempTableName + " ("
                    + "OrganisationGuid varchar(255), "
                    + "CDB varchar(255), "
                    + "OrganisationName varchar(255), "
                    + "ODSCode varchar(255), "
                    + "ParentOrganisationGuid varchar(255), "
                    + "CCGOrganisationGuid varchar(255), "
                    + "OrganisationType varchar(255), "
                    + "OpenDate varchar(255), " //converted to date later
                    + "CloseDate varchar(255), "
                    + "MainLocationGuid varchar(255), "
                    + "ProcessingId varchar(255), "
                    + "record_number int, " //note this is auto-generated by the bulk load
                    + "CONSTRAINT pk PRIMARY KEY (OrganisationGuid))";
            Statement statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //bulk load temp table, adding record number as we go
            LOG.debug("Starting bulk load into " + tempTableName);

            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            sql = "set @row = 1";
            statement.executeUpdate(sql);
            sql = "LOAD DATA LOCAL INFILE '" + filePath.replace("\\", "\\\\") + "'"
                    + " INTO TABLE " + tempTableName
                    + " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\\\"'"
                    + " LINES TERMINATED BY '\\r\\n'"
                    + " IGNORE 1 LINES"
                    + " SET record_number = @row:=@row+1";
            statement.executeUpdate(sql);
            statement.close();


            //insert records into the target table where the staging has new records
            LOG.debug("Copying new records into target table emis_organisation");
            sql = "INSERT IGNORE INTO emis_organisation (organisation_guid, cdb, organisation_name, ods_code,"
                    + " parent_organisation_guid, ccg_organisation_guid, organisation_type, open_date, close_date,"
                    + " main_location_guid, published_file_id, published_file_record_number, dt_last_updated)"
                    + " SELECT OrganisationGuid,"
                    + " IF(CDB != '', TRIM(CDB), null),"
                    + " IF(OrganisationName != '', TRIM(OrganisationName), null),"
                    + " IF(ODSCode != '', TRIM(ODSCode), null),"
                    + " IF(ParentOrganisationGuid != '', TRIM(ParentOrganisationGuid), null),"
                    + " IF(CCGOrganisationGuid != '', TRIM(CCGOrganisationGuid), null),"
                    + " IF(OrganisationType != '', TRIM(OrganisationType), null),"
                    + " IF(OpenDate != '', TRIM(OpenDate), null)," //Emis data are in SQL format, so this will auto convert from string
                    + " IF(CloseDate != '', TRIM(CloseDate), null)," //Emis data are in SQL format, so this will auto convert from string
                    + " IF(MainLocationGuid != '', TRIM(MainLocationGuid), null),"
                    + publishedFileId + ", record_number, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();


            //update any records that previously existed, but have a changed term
            LOG.debug("Updating existing records in target table emis_organisation");
            sql = "UPDATE emis_organisation t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.organisation_guid = s.OrganisationGuid"
                    + " SET"
                    + " t.cdb = IF(s.CDB != '', TRIM(s.CDB), null),"
                    + " t.organisation_name = IF(s.OrganisationName != '', TRIM(s.OrganisationName), null),"
                    + " t.ods_code = IF(s.ODSCode != '', TRIM(s.ODSCode), null),"
                    + " t.parent_organisation_guid = IF(s.ParentOrganisationGuid != '', TRIM(s.ParentOrganisationGuid), null),"
                    + " t.ccg_organisation_guid = IF(s.CCGOrganisationGuid != '', TRIM(s.CCGOrganisationGuid), null),"
                    + " t.organisation_type = IF(s.OrganisationType != '', TRIM(s.OrganisationType), null),"
                    + " t.open_date = IF(s.OpenDate != '', TRIM(s.OpenDate), null)," //Emis data are in SQL format, so this will auto convert from string
                    + " t.close_date = IF(s.CloseDate != '', TRIM(s.CloseDate), null)," //Emis data are in SQL format, so this will auto convert from string
                    + " t.main_location_guid = IF(s.MainLocationGuid != '', TRIM(s.MainLocationGuid), null),"
                    + " t.published_file_id = " + publishedFileId + ","
                    + " t.published_file_record_number = s.record_number,"
                    + " t.dt_last_updated = " + ConnectionManager.formatDateString(dataDate, true)
                    + " WHERE t.dt_last_updated < " + ConnectionManager.formatDateString(dataDate, true);
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //delete the temp table
            LOG.debug("Deleting temp table");
            sql = "DROP TABLE " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            long msEnd = System.currentTimeMillis();
            LOG.debug("Update of emis_organisation Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }
}
