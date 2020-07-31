package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.publisherCommon.TppCtv3SnomedRefDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.DeadlockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

public class RdbmsTppCtv3SnomedRefDal implements TppCtv3SnomedRefDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppCtv3SnomedRefDal.class);

    /**
     * new-style approach to load data into reference and stating tables using bulk operations
     * - copy file from S3 to local temp dir
     * - use MySQL bulk load command to get into temp table
     * - use SQL to update reference table
     * - drop temp table
     * - delete temp file
     *
     * NOTE: the RowIdentifier on the TPP file is inconsistent. Each copy of the file has completely
     * new identifiers, so this field cannot be used as a unique ID to handle new records and updated
     */
   @Override
    public void updateSnomedTable(String filePath, Date dataDate) throws Exception {
        DeadlockHandler h = new DeadlockHandler();
        h.setRetryDelaySeconds(60);
        while (true) {
            try {
                tryUpdateSnomedTable(filePath, dataDate);
                return;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    private void tryUpdateSnomedTable(String filePath, Date dataDate) throws Exception {

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
                    + "RowIdentifier varchar(20), "
                    + "IDOrganisationVisibleTo varchar(20), "
                    + "Ctv3Code varchar(5) NOT NULL, "
                    + "SnomedCode bigint(20) NOT NULL, "
                    + "dt_last_updated datetime NOT NULL, "
                    + "record_exists boolean DEFAULT FALSE, "
                    + "CONSTRAINT pk PRIMARY KEY (RowIdentifier), "
                    + "KEY ix_Ctv3Code_SnomedCode (Ctv3Code, SnomedCode),"
                    + "KEY ix_record_exists (record_exists))";
            Statement statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //bulk load temp table
            //LOAD DATA LOCAL INFILE for earlier versions of SQL
            LOG.debug("Starting bulk load into " + tempTableName);
            sql = "LOAD DATA LOCAL INFILE '" + filePath.replace("\\", "\\\\") + "'"
                    + " INTO TABLE " + tempTableName
                    + " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\\\"' ESCAPED BY '\\\\'"
                    + " LINES TERMINATED BY '\\r\\n'"
                    + " IGNORE 1 LINES (RowIdentifier, IDOrganisationVisibleTo, Ctv3Code, SnomedCode)";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //work out which records already exist in the target table
            LOG.debug("Finding records that exist in tpp_ctv3_to_snomed");
            sql = "UPDATE " + tempTableName + " s"
                    + " INNER JOIN tpp_ctv3_to_snomed t"
                    + " ON t.ctv3_code = s.Ctv3Code"
                    + " AND t.snomed_concept_id = s.SnomedCode"
                    + " SET s.record_exists = true";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //insert records into the target table where the staging
            LOG.debug("Copying into target table tpp_ctv3_to_snomed");
            sql = "INSERT IGNORE INTO tpp_ctv3_to_snomed (ctv3_code, snomed_concept_id, dt_last_updated)"
                    + " SELECT Ctv3Code, SnomedCode, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName
                    + " WHERE record_exists = false";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //update any records that previously existed, but have a changed term
            LOG.debug("Updating existing records in target table tpp_ctv3_to_snomed");
            sql = "UPDATE tpp_ctv3_to_snomed t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.ctv3_code = s.Ctv3Code"
                    + " SET t.snomed_concept_id = s.SnomedCode, t.dt_last_updated = " + ConnectionManager.formatDateString(dataDate, true)
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
            LOG.debug("Update of tpp_ctv3_to_snomed Completed in " + ((msEnd-msStart)/1000) + "s");
         } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }

}
