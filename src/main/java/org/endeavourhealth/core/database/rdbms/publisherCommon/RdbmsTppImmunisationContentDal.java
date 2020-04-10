package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.publisherCommon.TppImmunisationContentDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppImmunisationContent;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class RdbmsTppImmunisationContentDal implements TppImmunisationContentDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppImmunisationContentDal.class);

    @Override
    public TppImmunisationContent getContentFromRowId(int rowId) throws Exception {

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT row_id, name, content"
                    + " FROM tpp_immunisation_content_2"
                    + " WHERE row_id = ?";
            ps = connection.prepareStatement(sql);

            ps.setInt(1, rowId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int col = 1;
                TppImmunisationContent ret = new TppImmunisationContent();
                ret.setRowId(rs.getInt(col++));
                ret.setName(rs.getString(col++));
                ret.setContent(rs.getString(col++));

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
            LOG.debug("Loading " + f + " into " + tempTableName);
            String sql = "CREATE TABLE " + tempTableName + " ("
                    + "RowIdentifier int, "
                    + "Name varchar(255), "
                    + "Content varchar(255), "
                    + "DateDeleted varchar(255), "
                    + "RemovedData int, "
                    + "CONSTRAINT pk PRIMARY KEY (RowIdentifier))";
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
            LOG.debug("Copying new records into target table tpp_immunisation_content_2");
            sql = "INSERT IGNORE INTO tpp_immunisation_content_2 (row_id, name, content, dt_last_updated)"
                    + " SELECT RowIdentifier, Name, Content, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //update any records that previously existed, but have a changed term
            LOG.debug("Updating existing records in target table tpp_immunisation_content_2");
            sql = "UPDATE tpp_immunisation_content_2 t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.row_id = s.RowIdentifier"
                    + " SET t.name = s.Name,"
                    + " t.content = s.Content,"
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
            LOG.debug("Update of tpp_immunisation_content_2 Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }


    /*@Override
    public void save(TppImmunisationContent mapping) throws Exception {

        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsTppImmunisationContent tppImmunisationContent = new RdbmsTppImmunisationContent(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_immunisation_content "
                    + " (row_id, name, content, audit_json)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " name = VALUES(name),"
                    + " content = VALUES(content),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);
            // Only JSON audit field is nullable
            ps.setLong(1, tppImmunisationContent.getRowId());
            ps.setString(2, tppImmunisationContent.getName());
            ps.setString(3,tppImmunisationContent.getContent());
            if (tppImmunisationContent.getAuditJson() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, tppImmunisationContent.getAuditJson());
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
