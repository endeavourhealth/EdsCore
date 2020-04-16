package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.publisherCommon.TppMappingRefDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppMappingRef;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class RdbmsTppMappingRefDal implements TppMappingRefDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppMappingRefDal.class);

    @Override
    public TppMappingRef getMappingFromRowId(int rowId) throws Exception {

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT row_id, group_id, mapped_term"
                    + " FROM tpp_mapping_ref_2"
                    + " WHERE row_id = ?";
            ps = connection.prepareStatement(sql);

            ps.setInt(1, rowId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int col = 1;
                TppMappingRef ret = new TppMappingRef();
                ret.setRowId(rs.getInt(col++));
                ret.setGroupId(rs.getInt(col++));
                ret.setMappedTerm(rs.getString(col++));
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
                    + "IdMappingGroup int, "
                    + "Mapping varchar(255), "
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
            //LOG.debug("Finding records that exist in tpp_mapping_ref_2");
            sql = "UPDATE " + tempTableName + " s"
                    + " INNER JOIN tpp_mapping_ref_2 t"
                    + " ON t.row_id = s.RowIdentifier"
                    + " SET s.key_exists = true";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //insert records into the target table where the staging has new records
            //LOG.debug("Copying new records into target table tpp_mapping_ref_2");
            sql = "INSERT IGNORE INTO tpp_mapping_ref_2 (row_id, group_id, mapped_term, dt_last_updated)"
                    + " SELECT RowIdentifier, IdMappingGroup, Mapping, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName
                    + " WHERE key_exists = false";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //update any records that previously existed, but have a changed term
            //LOG.debug("Updating existing records in target table tpp_mapping_ref_2");
            sql = "UPDATE tpp_mapping_ref_2 t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.row_id = s.RowIdentifier"
                    + " SET t.group_id = s.IdMappingGroup,"
                    + " t.mapped_term = s.Mapping,"
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
            LOG.debug("Update of tpp_mapping_ref_2 Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }

   /* @Override
    public TppMappingRef getMappingFromRowAndGroupId(Long rowId, Long groupId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsTppMappingRef c"
                    + " where c.groupId = :group_id"
                    + " and c.rowId = :row_id";

            Query query = entityManager.createQuery(sql, RdbmsTppMappingRef.class)
                    .setParameter("row_id", rowId)
                    .setParameter("group_id", groupId)
                    .setMaxResults(1);

            try {
                RdbmsTppMappingRef result = (RdbmsTppMappingRef)query.getSingleResult();
                return new TppMappingRef(result);
            }
            catch (NoResultException e) {
                LOG.error("No mapping code found for rowId " + rowId + ", groupId " + groupId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void save(TppMappingRef mapping) throws Exception
    {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsTppMappingRef tppMapping = new RdbmsTppMappingRef(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_mapping_ref "
                    + " (row_id, group_id, mapped_term, audit_json)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " row_id = VALUES(row_id),"
                    + " group_id = VALUES(group_id),"
                    + " mapped_term = VALUES(mapped_term),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);
            // Only JSON audit field is nullable
            ps.setLong(1, tppMapping.getRowId());
            ps.setLong(2, tppMapping.getGroupId());
            ps.setString(3,tppMapping.getMappedTerm());
            if (tppMapping.getAuditJson() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, tppMapping.getAuditJson());
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
    }*/
}
