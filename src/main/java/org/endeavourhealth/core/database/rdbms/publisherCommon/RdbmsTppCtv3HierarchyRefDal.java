package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.publisherCommon.TppCtv3HierarchyRefDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class RdbmsTppCtv3HierarchyRefDal implements TppCtv3HierarchyRefDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppCtv3HierarchyRefDal.class);

    @Override
    public boolean isChildCodeUnderParentCode(String childReadCode, String parentReadCode) throws Exception {

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT 1"
                    + " FROM tpp_ctv3_hierarchy_ref_2"
                    + " WHERE child_code = ?"
                    + " AND parent_code = ?";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, childReadCode);
            ps.setString(col++, parentReadCode);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
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
    public void updateHierarchyTable(String filePath, Date dataDate) throws Exception {

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
                    + "IDOrganisationVisibleTo varchar(255), "
                    + "Ctv3CodeParent varchar(255) binary, "
                    + "Ctv3CodeChild varchar(255) binary, "
                    + "ChildLevel int, "
                    + "RemovedData int, "
                    + "key_exists boolean DEFAULT FALSE, "
                    + "CONSTRAINT pk PRIMARY KEY (Ctv3CodeChild, Ctv3CodeParent), "
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
            //LOG.debug("Finding records that exist in tpp_ctv3_hierarchy_ref_2");
            sql = "UPDATE " + tempTableName + " s"
                    + " INNER JOIN tpp_ctv3_hierarchy_ref_2 t"
                    + " ON t.child_code = s.Ctv3CodeChild"
                    + " AND t.parent_code = s.Ctv3CodeParent"
                    + " SET s.key_exists = true";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //insert records into the target table where the staging
            //LOG.debug("Copying into target table tpp_ctv3_hierarchy_ref_2");
            sql = "INSERT IGNORE INTO tpp_ctv3_hierarchy_ref_2 (parent_code, child_code, child_level, dt_last_updated)"
                    + " SELECT Ctv3CodeParent, Ctv3CodeChild, ChildLevel, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName
                    + " WHERE key_exists = false";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //unlike similar bulk load routines, there's no UPDATE statement
            //because this file has no unique ID we can use for updates

            //delete the temp table
            //LOG.debug("Deleting temp table");
            sql = "DROP TABLE " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            long msEnd = System.currentTimeMillis();
            LOG.debug("Update of tpp_ctv3_hierarchy_ref_2 Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }

    /*@Override
    public void save(TppCtv3HierarchyRef ref) throws Exception {
        if (ref == null) {
            throw new IllegalArgumentException("ref is null");
        }

        List<TppCtv3HierarchyRef> l = new ArrayList<>();
        l.add(ref);
        save(l);
    }

    @Override
    public void save(List<TppCtv3HierarchyRef> refs) throws Exception {
        if (refs == null || refs.isEmpty()) {
            throw new IllegalArgumentException("ref is null or empty");
        }

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_ctv3_hierarchy_ref "
                    + " (row_id, ctv3_parent_read_code, ctv3_child_read_code, child_level)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " ctv3_parent_read_code = VALUES(ctv3_parent_read_code),"
                    + " ctv3_child_read_code = VALUES(ctv3_child_read_code),"
                    + " child_level = VALUES(child_level)";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (TppCtv3HierarchyRef ref : refs) {

                int col = 1;

                ps.setLong(col++, ref.getRowId());
                ps.setString(col++, ref.getCtv3ParentReadCode());
                ps.setString(col++, ref.getCtv3ChildReadCode());
                ps.setInt(col++, ref.getChildLevel());

                ps.addBatch();
            }

            ps.executeBatch();

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
