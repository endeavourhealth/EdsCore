package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.publisherCommon.TppMultiLexToCtv3MapDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppMultiLexToCtv3Map;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class RdbmsTppMultiLexToCtv3MapDal implements TppMultiLexToCtv3MapDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppMultiLexToCtv3MapDal.class);

    @Override
    public TppMultiLexToCtv3Map getMultiLexToCTV3Map(int multiLexProductId) throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT multilex_product_id, ctv3_code, ctv3_term"
                    + " FROM tpp_multilex_to_ctv3_map_2"
                    + " WHERE multilex_product_id = ?";
            ps = connection.prepareStatement(sql);

            ps.setInt(1, multiLexProductId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int col = 1;

                TppMultiLexToCtv3Map ret = new TppMultiLexToCtv3Map();
                ret.setMultiLexProductId(rs.getInt(col++));
                ret.setCtv3ReadCode(rs.getString(col++));
                ret.setCtv3ReadTerm(rs.getString(col++));
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
        filePath = f.getAbsolutePath();

        Connection connection = ConnectionManager.getPublisherCommonNonPooledConnection();
        try {
            //turn on auto commit so we don't need to separately commit these large SQL operations
            connection.setAutoCommit(true);

            //create a temporary table to load the data into
            String tempTableName = ConnectionManager.generateTempTableName(FilenameUtils.getBaseName(filePath));
            //LOG.debug("Loading " + f + " into " + tempTableName);
            String sql = "CREATE TABLE " + tempTableName + " ("
                    + "RowIdentifier int, "
                    + "IDMultiLexProduct int, "
                    + "DrugReadCode varchar(255), "
                    + "DrugReadCodeDesc varchar(255), "
                    + "RemovedData int, "
                    + "key_exists boolean DEFAULT FALSE, "
                    + "CONSTRAINT pk PRIMARY KEY (IDMultiLexProduct), "
                    + "KEY ix_key_exists (key_exists))";
            Statement statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //bulk load temp table
            //LOG.debug("Starting bulk load into " + tempTableName);
            sql = "LOAD DATA LOCAL INFILE '" + filePath.replace("\\", "\\\\") + "'"
                    + " INTO TABLE " + tempTableName
                    + " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\\\"'"
                    + " LINES TERMINATED BY '\\r\\n'"
                    + " IGNORE 1 LINES";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //work out which records already exist in the target table
            //LOG.debug("Finding records that exist in tpp_multilex_to_ctv3_map_2");
            sql = "UPDATE " + tempTableName + " s"
                    + " INNER JOIN tpp_multilex_to_ctv3_map_2 t"
                    + " ON t.multilex_product_id = s.IDMultiLexProduct"
                    + " SET s.key_exists = true";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();


            //insert records into the target table where the staging has new records
            //LOG.debug("Copying new records into target table tpp_multilex_to_ctv3_map_2");
            sql = "INSERT IGNORE INTO tpp_multilex_to_ctv3_map_2 (multilex_product_id, ctv3_code, ctv3_term, dt_last_updated)"
                    + " SELECT IDMultiLexProduct, DrugReadCode, DrugReadCodeDesc, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName
                    + " WHERE key_exists = false";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //update any records that previously existed, but have a changed term
            //LOG.debug("Updating existing records in target table tpp_multilex_to_ctv3_map_2");
            sql = "UPDATE tpp_multilex_to_ctv3_map_2 t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.multilex_product_id = s.IDMultiLexProduct"
                    + " SET t.ctv3_code = s.DrugReadCode,"
                    + " t.ctv3_term = s.DrugReadCodeDesc,"
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
            LOG.debug("Update of tpp_multilex_to_ctv3_map_2 Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }

    /*@Override
    public void save(TppMultiLexToCtv3Map mapping) throws Exception {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        List<TppMultiLexToCtv3Map> l = new ArrayList<>();
        l.add(mapping);
        save(l);
    }

    @Override
    public void save(List<TppMultiLexToCtv3Map> mappings) throws Exception {
        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalArgumentException("mappings is null or empty");
        }

        DeadlockHandler h = new DeadlockHandler();
        while (true) {
            try {
                trySaveCodeMappings(mappings);
                break;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    private void trySaveCodeMappings(List<TppMultiLexToCtv3Map> mappings) throws Exception {

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO tpp_multilex_to_ctv3_map "
                    + " (row_id, multilex_product_id, ctv3_read_code, ctv3_read_term, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " multilex_product_id = VALUES(multilex_product_id),"
                    + " ctv3_read_code = VALUES(ctv3_read_code),"
                    + " ctv3_read_term = VALUES(ctv3_read_term),"
                    + " audit_json = VALUES(audit_json)";
            ps = connection.prepareStatement(sql);

            for (TppMultiLexToCtv3Map mapping: mappings) {

                int col = 1;

                ps.setLong(col++, mapping.getRowId());
                ps.setLong(col++, mapping.getMultiLexProductId());
                ps.setString(col++, mapping.getCtv3ReadCode());
                ps.setString(col++, mapping.getCtv3ReadTerm());
                ResourceFieldMappingAudit audit = mapping.getAudit();
                if (audit != null) {
                    ps.setString(col++, audit.writeToJson());
                } else {
                    ps.setNull(col++, Types.VARCHAR);
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
