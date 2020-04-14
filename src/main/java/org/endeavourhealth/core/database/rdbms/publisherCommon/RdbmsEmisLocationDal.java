package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.publisherCommon.EmisLocationDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisLocation;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisLocationOrganisation;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class RdbmsEmisLocationDal implements EmisLocationDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsEmisLocationDal.class);

    @Override
    public Set<String> retrieveAllIds() throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT location_guid FROM emis_location";
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
    public Set<EmisLocation> retrieveRecordsForIds(Set<String> hsIds) throws Exception {
        
        List<String> ids = new ArrayList<>(hsIds);
        
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT l.location_guid, l.location_name, l.location_type_description,"
                    + " l.parent_location_guid, l.open_date, l.close_date, l.main_contact_name, l.fax_number, l.email_address,"
                    + " l.phone_number, l.house_name_flat_number, l.number_and_street, l.village, l.town, l.county, l.postcode,"
                    + " l.deleted, l.published_file_id, l.published_file_record_number, "
                    + " o.organisation_guid, o.is_main_location, o.deleted, o.published_file_id, o.published_file_record_number"
                    + " FROM emis_location l"
                    + " LEFT OUTER JOIN emis_organisation_location o"
                    + " ON l.location_guid = o.location_guid"
                    + " WHERE l.location_guid IN (";
            for (int i=0; i<ids.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")"
                + " ORDER BY l.location_guid";
                
            ps = connection.prepareStatement(sql);
            
            int col = 1;
            for (int i=0; i<ids.size(); i++) {
                String id = ids.get(i);
                ps.setString(col++, id);
            }

            Set<EmisLocation> ret = new HashSet<>();

            EmisLocation lastOne = null;

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                col = 1;

                //fields from location
                EmisLocation l = new EmisLocation();
                l.setLocationGuid(rs.getString(col++));
                l.setLocationName(rs.getString(col++));
                l.setLocationTypeDescription(rs.getString(col++));
                l.setParentLocationGuid(rs.getString(col++));

                java.sql.Timestamp ts = rs.getTimestamp(col++);
                if (ts != null) {
                    l.setOpenDate(new java.util.Date(ts.getTime()));
                }

                ts = rs.getTimestamp(col++);
                if (ts != null) {
                    l.setCloseDate(new java.util.Date(ts.getTime()));
                }

                l.setMainContactName(rs.getString(col++));
                l.setFaxNumber(rs.getString(col++));
                l.setEmailAddress(rs.getString(col++));
                l.setPhoneNumber(rs.getString(col++));
                l.setHouseNameFlatNumber(rs.getString(col++));
                l.setNumberAndStreet(rs.getString(col++));
                l.setVillage(rs.getString(col++));
                l.setTown(rs.getString(col++));
                l.setCounty(rs.getString(col++));
                l.setPostcode(rs.getString(col++));
                l.setDeleted(rs.getBoolean(col++));
                l.setPublishedFileId(rs.getInt(col++));
                l.setPublishedFileRecordNumber(rs.getInt(col++));

                //fields from organisation_location
                EmisLocationOrganisation o = new EmisLocationOrganisation();
                o.setOrganisationGuid(rs.getString(col++));
                o.setMainLocation(rs.getBoolean(col++));
                o.setOrganisationLocationDeleted(rs.getBoolean(col++));
                o.setPublishedFileId(rs.getInt(col++));
                o.setPublishedFileRecordNumber(rs.getInt(col++));

                //work out if this is a new location record or a duplicate of the last one
                if (lastOne == null
                        || !lastOne.getLocationGuid().equals(l.getLocationGuid())) {
                    lastOne = l;
                    ret.add(l);
                }

                //and add the org details if present
                if (o.getOrganisationGuid() != null) {
                    Set<EmisLocationOrganisation> orgs = lastOne.getOrganisations();
                    if (orgs == null) {
                        orgs = new HashSet<>();
                        lastOne.setOrganisations(orgs);
                    }
                    orgs.add(o);
                }
            }
            
            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }
    
    /*private Connection retrievalConnection = null;
    private PreparedStatement retrievalPs = null;
    private ResultSet retrievalResultSet = null;

    @Override
    public void startRetrievingRecords() throws Exception {
        if (retrievalConnection != null) {
            throw new Exception("Already retrieving");
        }

        retrievalConnection = ConnectionManager.getPublisherCommonNonPooledConnection();

        String sql = "SELECT l.location_guid, l.location_name, l.location_type_description,"
                + " l.parent_location_guid, l.open_date, l.close_date, l.main_contact_name, l.fax_number, l.email_address,"
                + " l.phone_number, l.house_name_flat_number, l.number_and_street, l.village, l.town, l.county, l.postcode,"
                + " l.deleted, l.published_file_id, l.published_file_record_number, "
                + " o.organisation_guid, o.is_main_location, o.deleted, o.published_file_id, o.published_file_record_number"
                + " FROM emis_location l"
                + " INNER JOIN emis_organisation_location o"
                + " ON l.location_guid = o.location_guid";

        retrievalPs = retrievalConnection.prepareStatement(sql);
        retrievalPs.setFetchSize(10000); //only retrieve a limited amount at a time

        retrievalResultSet = retrievalPs.executeQuery();
    }

    @Override
    public EmisLocation getNextRecord() throws Exception {
        if (retrievalResultSet == null) {
            throw new Exception("Haven't started retrieving");
        }

        if (retrievalResultSet.next()) {


        } else {

            //if we've reeached the end, then close everything down
            retrievalPs.close();
            retrievalConnection.close();

            //and null everything out
            retrievalPs = null;
            retrievalConnection = null;
            retrievalResultSet = null;

            return null;
        }
    }*/



    @Override
    public void updateLocationStagingTable(String filePath, Date dataDate, int publishedFileId) throws Exception {
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
                    + "LocationGuid varchar(255), "
                    + "LocationName varchar(255), "
                    + "LocationTypeDescription varchar(255), "
                    + "ParentLocationGuid varchar(255), "
                    + "OpenDate varchar(255), " //converted to date later
                    + "CloseDate varchar(255), "
                    + "MainContactName varchar(255), "
                    + "FaxNumber varchar(255), "
                    + "EmailAddress varchar(255), "
                    + "PhoneNumber varchar(255), "
                    + "HouseNameFlatNumber varchar(255), "
                    + "NumberAndStreet varchar(255), "
                    + "Village varchar(255), "
                    + "Town varchar(255), "
                    + "County varchar(255), "
                    + "Postcode varchar(255), "
                    + "Deleted varchar(255), "
                    + "ProcessingId int, "
                    + "record_number int, " //note this is auto-generated by the bulk load
                    + "CONSTRAINT pk PRIMARY KEY (LocationGuid))";
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
            LOG.debug("Copying new records into target table emis_location");
            sql = "INSERT IGNORE INTO emis_location (location_guid, location_name, location_type_description,"
                    + " parent_location_guid, open_date, close_date, main_contact_name, fax_number, email_address,"
                    + " phone_number, house_name_flat_number, number_and_street, village, town, county, postcode,"
                    + " deleted, published_file_id, published_file_record_number, dt_last_updated)"
                    + " SELECT LocationGuid,"
                    + " IF(LocationName != '', TRIM(LocationName), null),"
                    + " IF(LocationTypeDescription != '', TRIM(LocationTypeDescription), null),"
                    + " IF(ParentLocationGuid != '', TRIM(ParentLocationGuid), null),"
                    + " IF(OpenDate != '', TRIM(OpenDate), null),"  //Emis data are in SQL format, so this will auto convert from string
                    + " IF(CloseDate != '', TRIM(CloseDate), null)," //Emis data are in SQL format, so this will auto convert from string
                    + " IF(MainContactName != '', TRIM(MainContactName), null),"
                    + " IF(FaxNumber != '', TRIM(FaxNumber), null),"
                    + " IF(EmailAddress != '', TRIM(EmailAddress), null),"
                    + " IF(PhoneNumber != '', TRIM(PhoneNumber), null),"
                    + " IF(HouseNameFlatNumber != '', TRIM(HouseNameFlatNumber), null),"
                    + " IF(NumberAndStreet != '', TRIM(NumberAndStreet), null),"
                    + " IF(Village != '', TRIM(Village), null),"
                    + " IF(Town != '', TRIM(Town), null),"
                    + " IF(County != '', TRIM(County), null),"
                    + " IF(Postcode != '', TRIM(Postcode), null),"
                    + " " + getSqlForEmisBooleans("Deleted") + ","
                    + publishedFileId + ", record_number, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            
            //update any records that previously existed, but have a changed term
            LOG.debug("Updating existing records in target table emis_location");
            sql = "UPDATE emis_location t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.location_guid = s.LocationGuid"
                    + " SET"
                    + " t.location_name = IF(s.LocationName != '', TRIM(s.LocationName), null),"
                    + " t.location_type_description = IF(s.LocationTypeDescription != '', TRIM(s.LocationTypeDescription), null),"
                    + " t.parent_location_guid = IF(s.ParentLocationGuid != '', TRIM(s.ParentLocationGuid), null),"
                    + " t.open_date = IF(s.OpenDate != '', TRIM(s.OpenDate), null)," //Emis data are in SQL format, so this will auto convert from string
                    + " t.close_date = IF(s.CloseDate != '', TRIM(s.CloseDate), null)," //Emis data are in SQL format, so this will auto convert from string
                    + " t.main_contact_name = IF(s.MainContactName != '', TRIM(s.MainContactName), null),"
                    + " t.fax_number = IF(s.FaxNumber != '', TRIM(s.FaxNumber), null),"
                    + " t.email_address = IF(s.EmailAddress != '', TRIM(s.EmailAddress), null),"
                    + " t.phone_number = IF(s.PhoneNumber != '', TRIM(s.PhoneNumber), null),"
                    + " t.house_name_flat_number = IF(s.HouseNameFlatNumber != '', TRIM(s.HouseNameFlatNumber), null),"
                    + " t.number_and_street = IF(s.NumberAndStreet != '', TRIM(s.NumberAndStreet), null),"
                    + " t.village = IF(s.Village != '', TRIM(s.Village), null),"
                    + " t.town = IF(s.Town != '', TRIM(s.Town), null),"
                    + " t.county = IF(s.County != '', TRIM(s.County), null),"
                    + " t.postcode = IF(s.Postcode != '', TRIM(s.Postcode), null),"
                    + " t.deleted = " + getSqlForEmisBooleans("s.Deleted") + ","
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
            LOG.debug("Update of emis_location Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }

    @Override
    public void updateOrganisationLocationStagingTable(String filePath, Date dataDate, int publishedFileId) throws Exception {
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
                    + "LocationGuid varchar(255), "
                    + "IsMainLocation varchar(255), "
                    + "Deleted varchar(255), "
                    + "ProcessingId int, "
                    + "record_number int, " //note this is auto-generated by the bulk load
                    + "CONSTRAINT pk PRIMARY KEY (LocationGuid, OrganisationGuid))";
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
            LOG.debug("Copying new records into target table emis_organisation_location");
            sql = "INSERT IGNORE INTO emis_organisation_location (organisation_guid, location_guid, is_main_location,"
                    + " deleted, published_file_id, published_file_record_number, dt_last_updated)"
                    + " SELECT OrganisationGuid,"
                    + " LocationGuid,"
                    + " " + getSqlForEmisBooleans("IsMainLocation")+ ","
                    + " " + getSqlForEmisBooleans("Deleted") + ","
                    + publishedFileId + ", record_number, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //update any records that previously existed, but have a changed term
            LOG.debug("Updating existing records in target table emis_organisation_location");
            sql = "UPDATE emis_organisation_location t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.location_guid = s.LocationGuid"
                    + " AND t.organisation_guid = s.OrganisationGuid"
                    + " SET"
                    + " t.is_main_location = " + getSqlForEmisBooleans("s.IsMainLocation") + ","
                    + " t.deleted = " + getSqlForEmisBooleans("s.Deleted") + ","
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
            LOG.debug("Update of emis_organisation_location Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }

    /**
     * Emis have sent a mix of 0/1 and true/false to represent boolean data, so we need
     * some logic to convert all variations into a SQL boolean type
     */
    public static String getSqlForEmisBooleans(String columnName) {
        return "IF(" + columnName + "='', 0, "
                    + "IF(" + columnName + "='true', 1, "
                    + "IF(" + columnName + "='false', 0, " + columnName + ")))";
        //return "IF(Deleted != '', Deleted, null)";
    }
}
