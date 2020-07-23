package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.publisherCommon.VisionCodeDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.DeadlockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

public class RdbmsVisionCodeDal implements VisionCodeDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsVisionCodeDal.class);

    @Override
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

            /**
             read_code varchar(5) binary COMMENT 'read2 code itself',
             read_term varchar(255) null COMMENT 'term for read2 code',
             snomed_concept_id bigint COMMENT 'mapped snomed concept ID',
             is_vision_code boolean NOT NULL COMMENT 'whether true Read2 or locally added',
             dt_last_updated datetime NOT NULL,
             */

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
    }
}
