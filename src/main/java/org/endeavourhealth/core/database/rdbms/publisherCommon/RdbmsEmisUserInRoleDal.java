package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.publisherCommon.EmisUserInRoleDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisUserInRole;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class RdbmsEmisUserInRoleDal implements EmisUserInRoleDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsEmisOrganisationDal.class);


    @Override
    public Set<String> retrieveAllIds() throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT user_in_role_guid FROM emis_user_in_role";
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
    public Set<EmisUserInRole> retrieveRecordsForIds(Set<String> hsIds) throws Exception {
        
        List<String> ids = new ArrayList<>(hsIds);

        Connection connection = ConnectionManager.getPublisherCommonNonPooledConnection();
        PreparedStatement ps = null;
        try {

            String sql = "SELECT user_in_role_guid, organisation_guid, title, given_name,"
                    + " surname, job_category_code, job_category_name, contract_start_date, contract_end_date,"
                    + " published_file_id, published_file_record_number, dt_last_updated"
                    + " FROM emis_user_in_role"
                    + " WHERE user_in_role_guid IN (";
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

            Set<EmisUserInRole> ret = new HashSet<>();

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                col = 1;

                EmisUserInRole u = new EmisUserInRole();

                u.setUserInRoleGuid(rs.getString(col++));
                u.setOrganisationGuid(rs.getString(col++));
                u.setTitle(rs.getString(col++));
                u.setGivenName(rs.getString(col++));
                u.setSurname(rs.getString(col++));
                u.setJobCategoryCode(rs.getString(col++));
                u.setJobCategoryName(rs.getString(col++));

                java.sql.Timestamp ts = rs.getTimestamp(col++);
                if (ts != null) {
                    u.setContractStartDate(new java.util.Date(ts.getTime()));
                }

                ts = rs.getTimestamp(col++);
                if (ts != null) {
                    u.setContractEndDate(new java.util.Date(ts.getTime()));
                }

                u.setPublishedFileId(rs.getInt(col++));
                u.setPublishedFileRecordNumber(rs.getInt(col++));

                ts = rs.getTimestamp(col++);

                ret.add(u);
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
                    + "UserInRoleGuid varchar(255), "
                    + "OrganisationGuid varchar(255), "
                    + "Title varchar(255), "
                    + "GivenName varchar(255), "
                    + "Surname varchar(255), "
                    + "JobCategoryCode varchar(255), "
                    + "JobCategoryName varchar(255), "
                    + "ContractStartDate varchar(255), "
                    + "ContractEndDate varchar(255), "
                    + "ProcessingId varchar(255), "
                    + "record_number int, " //note this is auto-generated by the bulk load
                    + "CONSTRAINT pk PRIMARY KEY (UserInRoleGuid))";
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
            LOG.debug("Copying new records into target table emis_user_in_role");
            sql = "INSERT IGNORE INTO emis_user_in_role (user_in_role_guid, organisation_guid, title, given_name,"
                    + " surname, job_category_code, job_category_name, contract_start_date, contract_end_date,"
                    + " published_file_id, published_file_record_number, dt_last_updated)"
                    + " SELECT UserInRoleGuid,"
                    + " IF(OrganisationGuid != '', OrganisationGuid, null),"
                    + " IF(Title != '', Title, null),"
                    + " IF(GivenName != '', GivenName, null),"
                    + " IF(Surname != '', Surname, null),"
                    + " IF(JobCategoryCode != '', JobCategoryCode, null),"
                    + " IF(JobCategoryName != '', JobCategoryName, null),"
                    + " IF(ContractStartDate != '', ContractStartDate, null)," //Emis data are in SQL format, so this will auto convert from string
                    + " IF(ContractEndDate != '', ContractEndDate, null)," //Emis data are in SQL format, so this will auto convert from string
                    + publishedFileId + ", record_number, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();


            //update any records that previously existed, but have a changed term
            LOG.debug("Updating existing records in target table emis_user_in_role");
            sql = "UPDATE emis_user_in_role t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.user_in_role_guid = s.UserInRoleGuid"
                    + " SET"
                    + " t.organisation_guid = IF(s.OrganisationGuid != '', s.OrganisationGuid, null),"
                    + " t.title = IF(s.Title != '', s.Title, null),"
                    + " t.given_name = IF(s.GivenName != '', s.GivenName, null),"
                    + " t.surname = IF(s.Surname != '', s.Surname, null),"
                    + " t.job_category_code = IF(s.JobCategoryCode != '', s.JobCategoryCode, null),"
                    + " t.job_category_name = IF(s.JobCategoryName != '', s.JobCategoryName, null),"
                    + " t.contract_start_date = IF(s.ContractStartDate != '', s.ContractStartDate, null),"
                    + " t.contract_end_date = IF(s.ContractEndDate != '', s.ContractEndDate, null),"
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
            LOG.debug("Update of emis_user_in_role Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }
}
