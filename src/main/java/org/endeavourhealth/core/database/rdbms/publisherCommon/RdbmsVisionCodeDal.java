package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.core.database.dal.publisherCommon.VisionCodeDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.VisionClinicalCodeForIMUpdate;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.DeadlockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RdbmsVisionCodeDal implements VisionCodeDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsVisionCodeDal.class);

    /*@Override
    public void updateLookupTable(String filePath, Date dataDate) throws Exception {
        DeadlockHandler h = new DeadlockHandler();
        h.setRetryDelaySeconds(60);
        while (true) {
            try {
                tryUpdateLookupTable(filePath, dataDate);
                return;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    private void tryUpdateLookupTable(String filePath, Date dataDate) throws Exception {
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
            //LOG.debug("Loading " + f + " into " + tempTableName);
            String sql = "CREATE TABLE " + tempTableName + " ("
                    + "ReadCode varchar(255) binary, "
                    + "ReadTerm varchar(255), "
                    + "SnomedConceptId bigint, "
                    + "IsVisionCode boolean, "
                    + "key_exists boolean DEFAULT FALSE, "
                    + "CONSTRAINT pk PRIMARY KEY (ReadCode), "
                    + "KEY ix_key_exists (key_exists))";
            Statement statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //bulk load temp table
            //LOG.debug("Starting bulk load into " + tempTableName);
            sql = "LOAD DATA LOCAL INFILE '" + filePath.replace("\\", "\\\\") + "'"
                    + " INTO TABLE " + tempTableName
                    + " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\\\"' ESCAPED BY '\b'" //escaping stops if going wrong if slashes are in the file
                    + " LINES TERMINATED BY '\\r\\n'"
                    + " IGNORE 1 LINES";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //work out which records already exist in the target table
            //LOG.debug("Finding records that exist in tpp_ctv3_lookup_2");
            sql = "UPDATE " + tempTableName + " s"
                    + " INNER JOIN vision_read2_lookup t"
                    + " ON t.read_code = s.ReadCode"
                    + " SET s.key_exists = true";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //insert records into the target table where the staging has new records
            //LOG.debug("Copying new records into target table tpp_ctv3_lookup_2");
            sql = "INSERT IGNORE INTO vision_read2_lookup (read_code, read_term, snomed_concept_id, is_vision_code, dt_last_updated)"
                    + " SELECT ReadCode, ReadTerm, SnomedConceptId, IsVisionCode, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName
                    + " WHERE key_exists = false";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            *//**
             read_code varchar(5) binary COMMENT 'read2 code itself',
             read_term varchar(255) null COMMENT 'term for read2 code',
             snomed_concept_id bigint COMMENT 'mapped snomed concept ID',
             is_vision_code boolean NOT NULL COMMENT 'whether true Read2 or locally added',
             dt_last_updated datetime NOT NULL,
             *//*

            //update any records that previously existed, but have a changed term
            //LOG.debug("Updating existing records in target table tpp_ctv3_lookup_2");
            sql = "UPDATE vision_read2_lookup t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.read_code = s.ReadCode"
                    + " SET t.read_term = s.ReadTerm,"
                    + " t.snomed_concept_id = s.SnomedConceptId,"
                    + " t.is_vision_code = s.IsVisionCode,"
                    + " t.dt_last_updated = " + ConnectionManager.formatDateString(dataDate, true)
                    + " WHERE t.dt_last_updated < " + ConnectionManager.formatDateString(dataDate, true);
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //delete the temp table
            //LOG.debug("Deleting temp table");
            sql = "DROP TABLE " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            long msEnd = System.currentTimeMillis();
            LOG.debug("Update of vision_read2_lookup Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }*/

    @Override
    public void updateRead2TermTable(String sourceFile, Date dataDate) throws Exception {
        DeadlockHandler h = new DeadlockHandler();
        h.setRetryDelaySeconds(60);
        while (true) {
            try {
                updateRead2TermTableImpl(sourceFile, dataDate);
                return;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    /**
     * Loads a file of Vision Read2 codes and terms into the publisher_common DB
     * file has headers: "Code", "Term", "UsageCount", "IsVisionCode"
     *
     * note: that the file being uploaded is created by the Vision transform and is on the local disk,
     * and will be deleted as soon as this completes
     *
     * note 2: unlike similar tables, this table supports MULTIPLE codes having a mapping. The Vision data has several terms
     * for many codes, so we cannot use it as a unique key. But we track the approx usage of each term, so can infer which is the preferred one.
     */
    private void updateRead2TermTableImpl(String filePath, Date dataDate) throws Exception {
        long msStart = System.currentTimeMillis();

        Connection connection = ConnectionManager.getPublisherCommonNonPooledConnection();
        try {
            //turn on auto commit so we don't need to separately commit these large SQL operations
            connection.setAutoCommit(true);

            //create a temporary table to load the data into
            String tempTableName = ConnectionManager.generateTempTableName(FilenameUtils.getBaseName(filePath));
            String sql = "CREATE TABLE " + tempTableName + " ("
                    + "Code varchar(255) binary, "
                    + "Term varchar(255), "
                    + "UsageCount bigint, "
                    + "IsVisionCode boolean, "
                    + "key_exists boolean DEFAULT FALSE, " //populated after the bulk load
                    + "CONSTRAINT pk PRIMARY KEY (Code, Term), "
                    + "KEY ix_key_exists (key_exists))";
            Statement statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //bulk load temp table
            sql = "LOAD DATA LOCAL INFILE '" + filePath.replace("\\", "\\\\") + "'"
                    + " INTO TABLE " + tempTableName
                    + " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\\\"' ESCAPED BY '\b'" //escaping stops if going wrong if slashes are in the file
                    + " LINES TERMINATED BY '\\r\\n'"
                    + " IGNORE 1 LINES";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //work out which records already exist in the target table
            sql = "UPDATE " + tempTableName + " s"
                    + " INNER JOIN vision_read2_code t"
                    + " ON t.read_code = s.Code"
                    + " AND t.read_term = s.Term" //code AND term make up the primary key
                    + " SET s.key_exists = true";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //insert records into the target table where the staging has new records
            sql = "INSERT IGNORE INTO vision_read2_code (read_code, read_term, is_vision_code, approx_usage, dt_last_updated)"
                    + " SELECT Code, Term, IsVisionCode, UsageCount, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName
                    + " WHERE key_exists = false";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //update any records that previously existed, but with the revised usage count
            sql = "UPDATE vision_read2_code t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.read_code = s.Code"
                    + " AND t.read_term = s.Term"
                    + " SET t.approx_usage = t.approx_usage + s.UsageCount," //ADD TO the usage count
                    + " t.is_vision_code = s.IsVisionCode,"
                    + " t.dt_last_updated = " + ConnectionManager.formatDateString(dataDate, true)
                    + " WHERE t.dt_last_updated < " + ConnectionManager.formatDateString(dataDate, true);
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //delete the temp table
            //LOG.debug("Deleting temp table");
            sql = "DROP TABLE " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            long msEnd = System.currentTimeMillis();
            LOG.debug("Update of vision_read2_code Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();
        }
    }

    @Override
    public void updateRead2ToSnomedMapTable(String sourceFile, Date dataDate) throws Exception {
        DeadlockHandler h = new DeadlockHandler();
        h.setRetryDelaySeconds(60);
        while (true) {
            try {
                updateRead2ToSnomedMapTableImpl(sourceFile, dataDate);
                return;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    /**
     * updates the Vision read2 to snomed mapping table using a file generated during the Vision transform
     * file headers are: "Read2", "SnomedConcept", "DateLastUsed"
     */
    private void updateRead2ToSnomedMapTableImpl(String filePath, Date dataDate) throws Exception {
        long msStart = System.currentTimeMillis();

        Connection connection = ConnectionManager.getPublisherCommonNonPooledConnection();
        try {
            //turn on auto commit so we don't need to separately commit these large SQL operations
            connection.setAutoCommit(true);

            //create a temporary table to load the data into
            String tempTableName = ConnectionManager.generateTempTableName(FilenameUtils.getBaseName(filePath));
            String sql = "CREATE TABLE " + tempTableName + " ("
                    + "ReadCode varchar(255) binary, "
                    + "SnomedConcept bigint, "
                    + "DateLastUsed date, "
                    + "key_exists boolean DEFAULT FALSE, " //populated after the bulk load
                    + "CONSTRAINT pk PRIMARY KEY (ReadCode), "
                    + "KEY ix_key_exists (key_exists))";
            Statement statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //bulk load temp table
            sql = "LOAD DATA LOCAL INFILE '" + filePath.replace("\\", "\\\\") + "'"
                    + " INTO TABLE " + tempTableName
                    + " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\\\"' ESCAPED BY '\b'" //escaping stops if going wrong if slashes are in the file
                    + " LINES TERMINATED BY '\\r\\n'"
                    + " IGNORE 1 LINES";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //work out which records already exist in the target table
            sql = "UPDATE " + tempTableName + " s"
                    + " INNER JOIN vision_read2_to_snomed_map t"
                    + " ON t.read_code = s.ReadCode"
                    + " SET s.key_exists = true";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //insert records into the target table where the staging has new records
            sql = "INSERT IGNORE INTO vision_read2_to_snomed_map (read_code, snomed_concept_id, d_last_used, dt_last_updated)"
                    + " SELECT ReadCode, SnomedConcept, DateLastUsed, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName
                    + " WHERE key_exists = false";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //update any records that previously existed, but with the revised usage count
            sql = "UPDATE vision_read2_to_snomed_map t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.read_code = s.ReadCode"
                    + " SET t.snomed_concept_id = s.SnomedConcept,"
                    + " t.d_last_used = s.DateLastUsed,"
                    + " t.dt_last_updated = " + ConnectionManager.formatDateString(dataDate, true)
                    + " WHERE t.dt_last_updated < " + ConnectionManager.formatDateString(dataDate, true)
                    + " AND t.d_last_used < s.DateLastUsed"; //only update if BOTH dates are newer
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //delete the temp table
            //LOG.debug("Deleting temp table");
            sql = "DROP TABLE " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            long msEnd = System.currentTimeMillis();
            LOG.debug("Update of vision_read2_code Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();
        }
    }

    public List<VisionClinicalCodeForIMUpdate> getClinicalCodesForIMUpdate() throws Exception {

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {

            String sql = "SELECT c.read_term, c.read_code, c.is_vision_code, s.snomed_concept_id"
                    + " FROM vision_read2_code c "
                    + " LEFT OUTER JOIN vision_read2_to_snomed_map s"
                    + " ON s.read_code = c.read_code"
                    + " WHERE c.is_vision_code = 1"
                    + " GROUP BY c.read_code"
                    + " ORDER BY c.read_code asc, approx_usage desc";

            ps = connection.prepareStatement(sql);
            ps.executeQuery();
            ResultSet resultSet = ps.executeQuery();

            List<VisionClinicalCodeForIMUpdate> returnList = new ArrayList<>();

            while (resultSet.next()) {

                VisionClinicalCodeForIMUpdate code = new VisionClinicalCodeForIMUpdate();

                code.setReadTerm(resultSet.getString("read_term"));
                code.setReadCode(resultSet.getString("read_code"));
                code.setSnomedConceptId(resultSet.getLong("snomed_concept_id"));
                code.setIsVisionCode(resultSet.getBoolean("is_vision_code"));

                returnList.add(code);
            }

            return returnList;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

}
