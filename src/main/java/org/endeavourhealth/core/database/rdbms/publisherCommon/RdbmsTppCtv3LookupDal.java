package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.publisherCommon.TppCtv3LookupDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppCtv3Lookup;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class RdbmsTppCtv3LookupDal implements TppCtv3LookupDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppCtv3LookupDal.class);

    @Override
    public TppCtv3Lookup getContentFromCtv3Code(String ctv3Code) throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT ctv3_code, ctv3_term"
                    + " FROM tpp_ctv3_lookup_2 "
                    + " WHERE ctv3_code = ?";

            ps = connection.prepareStatement(sql);
            ps.setString(1, ctv3Code);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int col = 1;
                String code = rs.getString(col++);
                String term = rs.getString(col++);

                TppCtv3Lookup ret = new TppCtv3Lookup();
                ret.setCtv3Code(code);
                ret.setCtv3Text(term);
                return ret;

            } else {
                return null;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

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
    public void updateLookupTable(String filePath, Date dataDate) throws Exception {

        long msStart = System.currentTimeMillis();

        //copy the file from S3 to local disk
        File f = FileHelper.copyFileFromStorageToTempDirIfNecessary(filePath);

        Connection connection = ConnectionManager.getPublisherCommonNonPooledConnection();
        try {
            //turn on auto commit so we don't need to separately commit these large SQL operations
            connection.setAutoCommit(true);

            //create a temporary table to load the data into
            String tempTableName = ConnectionManager.generateTempTableName(FilenameUtils.getBaseName(filePath));
            LOG.debug("Loading " + f + " into " + tempTableName);
            String sql = "CREATE TABLE " + tempTableName + " ("
                    + "RowIdentifier int, "
                    + "IDOrganisationVisibleTo varchar(255), "
                    + "Ctv3Code varchar(255) binary, "
                    + "Ctv3Text varchar(255), "
                    + "RemovedData int, "
                    + "CONSTRAINT pk PRIMARY KEY (Ctv3Code))";
            Statement statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //bulk load temp table
            LOG.debug("Starting bulk load into " + tempTableName);
            sql = "LOAD DATA LOCAL INFILE '" + filePath.replace("\\", "\\\\") + "'"
                    + " INTO TABLE " + tempTableName
                    + " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\\\"'"
                    + " LINES TERMINATED BY '\\r\\n'"
                    + " IGNORE 1 LINES";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //insert records into the target table where the staging has new records
            LOG.debug("Copying new records into target table tpp_ctv3_lookup_2");
            sql = "INSERT IGNORE INTO tpp_ctv3_lookup_2 (ctv3_code, ctv3_term, dt_last_updated)"
                    + " SELECT Ctv3Code, Ctv3Text, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //update any records that previously existed, but have a changed term
            LOG.debug("Updating existing records in target table tpp_ctv3_lookup_2");
            sql = "UPDATE tpp_ctv3_lookup_2 t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.ctv3_code = s.Ctv3Code"
                    + " SET t.ctv3_term = s.Ctv3Text, t.dt_last_updated = " + ConnectionManager.formatDateString(dataDate, true)
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
            LOG.debug("Update of tpp_ctv3_lookup_2 Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }

    /*@Override
    public void save(TppCtv3Lookup ctv3Lookup) throws Exception {
        if (ctv3Lookup == null) {
            throw new IllegalArgumentException("ctv3 lookup is null");
        }

        List<TppCtv3Lookup> l = new ArrayList<>();
        l.add(ctv3Lookup);
        save(l);
    }

    @Override
    public void save(List<TppCtv3Lookup> ctv3Lookups) throws Exception {
        if (ctv3Lookups == null || ctv3Lookups.isEmpty()) {
            throw new IllegalArgumentException("ctv3 lookup is null or empty");
        }

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {

            String sql = "INSERT INTO tpp_ctv3_lookup "
                    + " (ctv3_code, ctv3_text, audit_json)"
                    + " VALUES (?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " ctv3_text = VALUES(ctv3_text),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            for (TppCtv3Lookup lookup : ctv3Lookups) {

                int col = 1;
                // Only JSON audit field is nullable
                ps.setString(col++, lookup.getCtv3Code());
                ps.setString(col++, lookup.getCtv3Text());
                if (lookup.getAudit() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, lookup.getAudit().writeToJson());
                }

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
    }*/
}
