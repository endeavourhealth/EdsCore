package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.publisherCommon.TppConfigListOptionDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppConfigListOption;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class RdbmsTppConfigListOptionDal implements TppConfigListOptionDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppConfigListOptionDal.class);


    @Override
    public TppConfigListOption getListOptionFromRowId(int rowId) throws Exception {

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT row_id, config_list_id, list_option_name"
                    + " FROM tpp_config_list_option_2"
                    + " WHERE row_id = ?";
            ps = connection.prepareStatement(sql);

            ps.setInt(1, rowId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int col = 1;

                TppConfigListOption ret = new TppConfigListOption();
                ret.setRowId(rs.getInt(col++));
                ret.setConfigListId(rs.getInt(col++));
                ret.setListOptionName(rs.getString(col++));
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
        //create a temporary table to load the data into
        String tempTableName = ConnectionManager.generateTempTableName(FilenameUtils.getBaseName(filePath));
        try {
            //turn on auto commit so we don't need to separately commit these large SQL operations
            connection.setAutoCommit(true);

            //LOG.debug("Loading " + f + " into " + tempTableName);
            String sql = "CREATE TABLE " + tempTableName + " ("
                    + "RowIdentifier int, "
                    + "ConfiguredList varchar(255), "
                    + "ConfiguredListOption varchar(255), "
                    + "CDSCode varchar(255), "
                    + "MHLDDSCode varchar(255), "
                    + "CAMHSCode varchar(255), "
                    + "MHSDSCode varchar(255), "
                    + "RemovedData int, "
                    + "key_exists boolean DEFAULT FALSE, "
                    + "CONSTRAINT pk PRIMARY KEY (RowIdentifier), "
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
            //LOG.debug("Finding records that exist in tpp_config_list_option_2");
            sql = "UPDATE " + tempTableName + " s"
                    + " INNER JOIN tpp_config_list_option_2 t"
                    + " ON t.row_id = s.RowIdentifier"
                    + " SET s.key_exists = true";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //insert records into the target table where the staging has new records
            //LOG.debug("Copying new records into target table tpp_config_list_option_2");
            sql = "INSERT IGNORE INTO tpp_config_list_option_2 (row_id, config_list_id, list_option_name, dt_last_updated)"
                    + " SELECT RowIdentifier, ConfiguredList, ConfiguredListOption, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName
                    + " WHERE key_exists = false";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //update any records that previously existed, but have a changed term
            //LOG.debug("Updating existing records in target table tpp_config_list_option_2");
            sql = "UPDATE tpp_config_list_option_2 t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.row_id = s.RowIdentifier"
                    + " SET t.config_list_id = s.ConfiguredList,"
                    + " t.list_option_name = s.ConfiguredListOption,"
                    + " t.dt_last_updated = " + ConnectionManager.formatDateString(dataDate, true)
                    + " WHERE t.dt_last_updated < " + ConnectionManager.formatDateString(dataDate, true);
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            long msEnd = System.currentTimeMillis();
            LOG.debug("Update of tpp_config_list_option_2 Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            ConnectionManager.dropTempTable(tempTableName, ConnectionManager.Db.PublisherCommon);

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }

    /*@Override
    public TppConfigListOption getListOptionFromRowAndListId(Long rowId, Long configListId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsTppConfigListOption c"
                    + " where c.serviceId = :service_id"
                    + " and c.configListId = :config_list_id"
                    + " and c.rowId = :row_id";

            Query query = entityManager.createQuery(sql, RdbmsTppConfigListOption.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("config_list_id", configListId)
                    .setParameter("row_id", rowId)
                    .setMaxResults(1);

            try {
                RdbmsTppConfigListOption result = (RdbmsTppConfigListOption) query.getSingleResult();
                return new TppConfigListOption(result);
            } catch (NoResultException e) {
                LOG.error("No code found for rowId " + rowId + ", configListId " + configListId + ", service " + serviceId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void save(UUID serviceId, TppConfigListOption configListOption) throws Exception {
        if (configListOption == null) {
            throw new IllegalArgumentException("configListOption is null");
        }

        RdbmsTppConfigListOption tppConfigListOption = new RdbmsTppConfigListOption(configListOption);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_config_list_option "
                    + " (row_id, config_list_id, list_option_name, service_id, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " list_option_name = VALUES(list_option_name),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);
            // Only JSON audit field is nullable
            ps.setLong(1, tppConfigListOption.getRowId());
            ps.setLong(2, tppConfigListOption.getConfigListId());
            ps.setString(3, tppConfigListOption.getListOptionName());
            ps.setString(4, tppConfigListOption.getServiceId());
            if (tppConfigListOption.getAuditJson() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, tppConfigListOption.getAuditJson());
            }

            ps.executeUpdate();

            //transaction.commit();
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public void save(UUID serviceId, List<TppConfigListOption> mappings) throws Exception {
        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalArgumentException("configListOption is null or empty");
        }

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_config_list_option "
                    + " (row_id, config_list_id, list_option_name, service_id, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " list_option_name = VALUES(list_option_name),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (TppConfigListOption mapping: mappings) {

                int col = 1;

                // Only JSON audit field is nullable
                ps.setLong(col++, mapping.getRowId());
                ps.setLong(col++, mapping.getConfigListId());
                ps.setString(col++, mapping.getListOptionName());
                ps.setString(col++, mapping.getServiceId());
                if (mapping.getAudit() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, mapping.getAudit().writeToJson());
                }

                ps.addBatch();
            }

            ps.executeBatch();

            //transaction.commit();
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }*/
}
