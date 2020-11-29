package org.endeavourhealth.core.database.rdbms.reference;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.reference.SnomedToBnfChapterDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.DeadlockHandler;
import org.endeavourhealth.core.database.rdbms.publisherCommon.RdbmsTppCtv3SnomedRefDal;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsSnomedToBnfChapterLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

public class RdbmsSnomedToBnfChapterDal implements SnomedToBnfChapterDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsSnomedToBnfChapterDal.class);
    public String lookupSnomedCode(String snomedCode) throws Exception {
        snomedCode = snomedCode.trim();

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsSnomedToBnfChapterLookup c"
                    + " where c.snomedCode = :snomed_code";

            Query query = entityManager
                    .createQuery(sql, RdbmsSnomedToBnfChapterLookup.class)
                    .setParameter("snomed_code", snomedCode);

            try {
                RdbmsSnomedToBnfChapterLookup result = (RdbmsSnomedToBnfChapterLookup) query.getSingleResult();
                return result.getBnfChapterCode();

            } catch (NoResultException ex){
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    public void updateSnomedToBnfChapterLookup(String snomedCode, String bnfChapterCode) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select r"
                    + " from"
                    + " RdbmsSnomedToBnfChapterLookup r"
                    + " where r.snomedCode = :snomed_code";

            Query query = entityManager
                    .createQuery(sql, RdbmsSnomedToBnfChapterLookup.class)
                    .setParameter("snomed_code", snomedCode);

            RdbmsSnomedToBnfChapterLookup lookup = null;

            try {
                lookup = (RdbmsSnomedToBnfChapterLookup) query.getSingleResult();
            } catch (NoResultException ex) {
                lookup = new RdbmsSnomedToBnfChapterLookup();
                lookup.setSnomedCode(snomedCode);
            }

            lookup.setBnfChapterCode(bnfChapterCode);

            entityManager.getTransaction().begin();
            entityManager.persist(lookup);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    public void updateSnomedToBnfChapterLookup(String filePath, Date dataDate) throws Exception {
        DeadlockHandler h = new DeadlockHandler();
        h.setRetryDelaySeconds(60);
        while (true) {
            try {
                tryUpdateSnomedToBnfChapterLookupTable(filePath, dataDate);
                return;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }
    private void tryUpdateSnomedToBnfChapterLookupTable(String filePath, Date dataDate) throws Exception {

        long msStart = System.currentTimeMillis();

        Connection connection = ConnectionManager.getPublisherCommonNonPooledConnection();
        try {
            //turn on auto commit so we don't need to separately commit these large SQL operations
            connection.setAutoCommit(true);

            //create a temporary table to load the data into
            String tempTableName = ConnectionManager.generateTempTableName(FilenameUtils.getBaseName(filePath));
            LOG.debug("Loading " + filePath + " into " + tempTableName);
            String sql = "CREATE TABLE " + tempTableName + " ("
                    + "BNF_Code  varchar (30) , "
                    + "SNOMED_Code bigint(20), "
                    + "dt_last_updated datetime NOT NULL, "
                    + "record_exists boolean DEFAULT FALSE, "
                    + "process_record boolean DEFAULT TRUE, "
                   // + "CONSTRAINT pk PRIMARY KEY (SNOMED_Code), "
                    + "KEY ix_code_updated (SNOMED_Code, dt_last_updated),"
                    + "KEY ix_record_exists (record_exists))";
            Statement statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //bulk load temp table
            //LOAD DATA LOCAL INFILE for earlier versions of SQL
            LOG.debug("Starting bulk load into " + tempTableName);
            sql = "LOAD DATA INFILE '" + filePath.replace("\\", "\\\\") + "'"
                    + " INTO TABLE " + tempTableName
                    + " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\\\"' ESCAPED BY '\\\\'"
                    + " LINES TERMINATED BY '\\r\\n'"
                    + " IGNORE 1 LINES (BNF_Code, SNOMED_Code )";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //work out which records already exist in the target table
            LOG.debug("Finding records that exist in reference.snomed_to_bnf_chapter_lookup");
            String formattedDateString = ConnectionManager.formatDateString(dataDate, true);
            sql = "UPDATE " + tempTableName + " s"
                    + " INNER JOIN reference.snomed_to_bnf_chapter_lookup t"
                    + " ON t.snomed_code = s.snomed_code"
                    //+ " AND t.bnf_chapter_code = s.bnf_code"
                    + " SET s.record_exists = true, "
                    + " s.process_record = IF ((t.dt_last_updated = " + formattedDateString + ") OR (s.BNF_Code != t.bnf_chapter_code AND t.dt_last_updated < " + formattedDateString + "), true, false)";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //insert records into the target table where the staging
            LOG.debug("Copying into target table reference.snomed_to_bnf_chapter_lookup");
            sql = "INSERT IGNORE INTO reference.snomed_to_bnf_chapter_lookup (snomed_code, bnf_chapter_code, dt_last_updated)"
                    + " SELECT SNOMED_Code, BNF_Code, " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName
                    + " WHERE record_exists = false";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //update any records that previously existed, but have a changed term
            LOG.debug("Updating existing records in target table snomed_to_bnf_chapter_lookup");
            sql = "UPDATE reference.snomed_to_bnf_chapter_lookup t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.SNOMED_Code = s.SNOMED_Code"
                    + " SET t.bnf_chapter_code = s.bnf_code, t.dt_last_updated = " + ConnectionManager.formatDateString(dataDate, true)
                    + " WHERE s.record_exists = true"
                    + " AND s.process_record = true";
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
            LOG.debug("Update of snomed_to_bnf_chapter_lookup Completed in " + ((msEnd-msStart)/1000) + "s");
        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            //FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }
}