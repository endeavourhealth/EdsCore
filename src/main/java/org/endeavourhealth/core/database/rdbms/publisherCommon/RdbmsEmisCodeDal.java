package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.core.database.dal.publisherCommon.EmisCodeDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisClinicalCode;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisDrugCode;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class RdbmsEmisCodeDal implements EmisCodeDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsEmisCodeDal.class);

    /**
     * unlike all the other similar functions, this one is slightly different, needing TWO raw files to
     * populate the staging table. The second file is generated in realtime by the Emis ClinicalCodeTransformer class
     */
    @Override
    public void updateClinicalCodeTable(String filePath, String validReadCodesFile, Date dataDate) throws Exception {
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
                    + "CodeId bigint, "
                    + "Term varchar(500), "
                    + "ReadTermId varchar(255), "
                    + "SnomedCTConceptId varchar(255), "
                    + "SnomedCTDescriptionId varchar(255), "
                    + "NationalCode varchar(255), "
                    + "NationalCodeCategory varchar(255), "
                    + "NationalDescription varchar(255), "
                    + "EmisCodeCategoryDescription varchar(255), "
                    + "ProcessingId int, "
                    + "ParentCodeId bigint, "
                    + "CONSTRAINT pk PRIMARY KEY (CodeId))";
            Statement statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //bulk load temp table, adding record number as we go
            LOG.debug("Starting bulk load into " + tempTableName);

            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            sql = "LOAD DATA LOCAL INFILE '" + filePath.replace("\\", "\\\\") + "'"
                    + " INTO TABLE " + tempTableName
                    + " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\\\"'"
                    + " LINES TERMINATED BY '\\r\\n'"
                    + " IGNORE 1 LINES";
            statement.executeUpdate(sql);
            statement.close();

            //we also have a second file, containing additional columns that we've generated in the code
            String extraTempTableName = ConnectionManager.generateTempTableName(FilenameUtils.getBaseName(validReadCodesFile));
            LOG.debug("Loading " + validReadCodesFile + " into " + extraTempTableName);
            sql = "CREATE TABLE " + extraTempTableName + " ("
                    + "CodeId bigint, "
                    + "AdjustedCode varchar(255), "
                    + "IsEmisCode boolean, "
                    + "SnomedTerm varchar(500), "
                    + "CONSTRAINT pk PRIMARY KEY (CodeId))";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //bulk load temp table, adding record number as we go
            LOG.debug("Starting bulk load into " + extraTempTableName);

            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            sql = "LOAD DATA LOCAL INFILE '" + validReadCodesFile.replace("\\", "\\\\") + "'"
                    + " INTO TABLE " + extraTempTableName
                    + " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\\\"'"
                    + " LINES TERMINATED BY '\\r\\n'"
                    + " IGNORE 1 LINES";
            statement.executeUpdate(sql);
            statement.close();


            //insert records into the target table where the staging has new records
            LOG.debug("Copying new records into target table emis_clinical_code");
            sql = "INSERT IGNORE INTO emis_clinical_code (code_id, code_type, read_term, read_code, snomed_concept_id,"
                    + " snomed_description_id, snomed_term, national_code, national_code_category,"
                    + " national_code_description, parent_code_id, adjusted_code, is_emis_code, dt_last_updated)"
                    + " SELECT s.CodeId,"
                    + " IF(s.EmisCodeCategoryDescription != '', TRIM(EmisCodeCategoryDescription), null),"
                    + " IF(s.Term != '', TRIM(s.Term), null),"
                    + " IF(s.ReadTermId != '', TRIM(s.ReadTermId), null),"
                    + " IF(s.SnomedCTConceptId != '', TRIM(s.SnomedCTConceptId), null),"
                    + " IF(s.SnomedCTDescriptionId != '', TRIM(s.SnomedCTDescriptionId), null),"
                    + " IF(x.SnomedTerm != '', TRIM(x.SnomedTerm), null),"
                    + " IF(s.NationalCode != '', TRIM(s.NationalCode), null),"
                    + " IF(s.NationalCodeCategory != '', TRIM(s.NationalCodeCategory), null),"
                    + " IF(s.NationalDescription != '', TRIM(s.NationalDescription), null),"
                    + " IF(s.ParentCodeId != '', s.ParentCodeId, null),"
                    + " IF(x.AdjustedCode != '', TRIM(x.AdjustedCode), null),"
                    + " x.IsEmisCode,"
                    + " " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName + " s "
                    + " LEFT OUTER JOIN " + extraTempTableName + " x"
                    + " ON s.CodeId = x.CodeId";
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //update any records that previously existed, but have a changed term
            LOG.debug("Updating existing records in target table emis_clinical_code");
            sql = "UPDATE emis_clinical_code t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.code_id = s.CodeId"
                    + " LEFT OUTER JOIN " + extraTempTableName + " x"
                    + " ON s.CodeId = x.CodeId"
                    + " SET"
                    + " t.code_type = IF(s.EmisCodeCategoryDescription != '', TRIM(EmisCodeCategoryDescription), null),"
                    + " t.read_term = IF(s.Term != '', TRIM(s.Term), null),"
                    + " t.read_code = IF(s.ReadTermId != '', TRIM(s.ReadTermId), null),"
                    + " t.snomed_concept_id = IF(s.SnomedCTConceptId != '', TRIM(s.SnomedCTConceptId), null),"
                    + " t.snomed_description_id = IF(s.SnomedCTDescriptionId != '', TRIM(s.SnomedCTDescriptionId), null),"
                    + " t.snomed_term = IF(x.SnomedTerm != '', TRIM(x.SnomedTerm), null),"
                    + " t.national_code = IF(s.NationalCode != '', TRIM(s.NationalCode), null),"
                    + " t.national_code_category = IF(s.NationalCodeCategory != '', TRIM(s.NationalCodeCategory), null),"
                    + " t.national_code_description = IF(s.NationalDescription != '', TRIM(s.NationalDescription), null),"
                    + " t.parent_code_id = IF(s.ParentCodeId != '', s.ParentCodeId, null),"
                    + " t.adjusted_code = IF(x.AdjustedCode != '', TRIM(x.AdjustedCode), null),"
                    + " t.is_emis_code = x.IsEmisCode,"
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

            LOG.debug("Deleting extra temp table");
            sql = "DROP TABLE " + extraTempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            long msEnd = System.currentTimeMillis();
            LOG.debug("Update of emis_drug_code Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }

    @Override
    public void updateDrugCodeTable(String filePath, Date dataDate) throws Exception {
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
                    + "CodeId bigint, "
                    + "Term varchar(500), "
                    + "DmdProductCodeId varchar(255), "
                    + "ProcessingId int, "
                    + "CONSTRAINT pk PRIMARY KEY (CodeId))";
            Statement statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();


            //bulk load temp table, adding record number as we go
            LOG.debug("Starting bulk load into " + tempTableName);

            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            sql = "LOAD DATA LOCAL INFILE '" + filePath.replace("\\", "\\\\") + "'"
                    + " INTO TABLE " + tempTableName
                    + " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\\\"'"
                    + " LINES TERMINATED BY '\\r\\n'"
                    + " IGNORE 1 LINES";
            statement.executeUpdate(sql);
            statement.close();


            //insert records into the target table where the staging has new records
            LOG.debug("Copying new records into target table emis_drug_code");
            sql = "INSERT IGNORE INTO emis_drug_code (code_id, dmd_concept_id, dmd_term, dt_last_updated)"
                    + " SELECT CodeId,"
                    + " IF(DmdProductCodeId != '', DmdProductCodeId, null),"
                    + " IF(Term != '', Term, null),"
                    + " " + ConnectionManager.formatDateString(dataDate, true)
                    + " FROM " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name, so don't use prepared statement
            statement.executeUpdate(sql);
            statement.close();

            //update any records that previously existed, but have a changed term
            LOG.debug("Updating existing records in target table emis_drug_code");
            sql = "UPDATE emis_drug_code t"
                    + " INNER JOIN " + tempTableName + " s"
                    + " ON t.code_id = s.CodeId"
                    + " SET"
                    + " t.dmd_concept_id = IF(s.DmdProductCodeId != '', s.DmdProductCodeId, null),"
                    + " t.dmd_term = IF(s.Term != '', s.Term, null),"
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
            LOG.debug("Update of emis_drug_code Completed in " + ((msEnd-msStart)/1000) + "s");

        } finally {
            //MUST change this back to false
            connection.setAutoCommit(false);
            connection.close();

            //delete the temp file
            FileHelper.deleteFileFromTempDirIfNecessary(f);
        }
    }

    @Override
    public EmisDrugCode getDrugCode(long codeId) throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT code_id, dmd_concept_id, dmd_term, dt_last_updated"
                    + " FROM emis_drug_code"
                    + " WHERE code_id = ?";
            ps = connection.prepareStatement(sql);

            ps.setLong(1, codeId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int col = 1;
                EmisDrugCode ret = new EmisDrugCode();
                ret.setCodeId(rs.getLong(col++));

                //this may be null
                long dmdId = rs.getLong(col++);
                if (!rs.wasNull()) {
                    ret.setDmdConceptId(dmdId);
                }

                ret.setDmdTerm(rs.getString(col++));

                rs.getTimestamp(col++);
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

    @Override
    public EmisClinicalCode getClinicalCode(long codeId) throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT code_id, code_type, read_term, read_code, snomed_concept_id,"
                    + " snomed_description_id, snomed_term, national_code, national_code_category,"
                    + " national_code_description, parent_code_id, adjusted_code, is_emis_code, dt_last_updated"
                    + " FROM emis_clinical_code"
                    + " WHERE code_id = ?";
            ps = connection.prepareStatement(sql);

            ps.setLong(1, codeId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int col = 1;
                EmisClinicalCode ret = new EmisClinicalCode();
                ret.setCodeId(rs.getLong(col++));
                ret.setCodeType(rs.getString(col++));
                ret.setReadTerm(rs.getString(col++));
                ret.setReadCode(rs.getString(col++));

                long l = rs.getLong(col++);
                if (!rs.wasNull()) {
                    ret.setSnomedConceptId(l);
                }

                l = rs.getLong(col++);
                if (!rs.wasNull()) {
                    ret.setSnomedDescriptionId(l);
                }

                ret.setSnomedTerm(rs.getString(col++));
                ret.setNationalCode(rs.getString(col++));
                ret.setNationalCodeCategory(rs.getString(col++));
                ret.setNationalCodeDescription(rs.getString(col++));

                l = rs.getLong(col++);
                if (!rs.wasNull()) {
                    ret.setParentCode(l);
                }

                ret.setAdjustedCode(rs.getString(col++));
                ret.setEmisCode(rs.getBoolean(col++));

                rs.getTimestamp(col++);
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

    /*

    @Override
    public void saveCodeMappings(List<EmisCsvCodeMap> mappings) throws Exception {
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

    public void trySaveCodeMappings(List<EmisCsvCodeMap> mappings) throws Exception {
        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalArgumentException("Trying to save null or empty mappings");
        }

        //ensure all mappings are meds or not
        Boolean medication = null;
        Map<Long, EmisCsvCodeMap> hmMappings = new HashMap<>();

        for (EmisCsvCodeMap mapping : mappings) {
            if (medication == null) {
                medication = new Boolean(mapping.isMedication());
            } else if (medication.booleanValue() != mapping.isMedication()) {
                throw new Exception("Must be saving all medications or all non-medications");
            }

            hmMappings.put(new Long(mapping.getCodeId()), mapping);
        }

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement psSelect = null;
        PreparedStatement psInsert = null;

        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT code_id, dt_last_received"
                    + " FROM emis_csv_code_map"
                    + " WHERE medication = ?"
                    + " AND code_id IN (";
            for (int i = 0; i < mappings.size(); i++) {
                if (i > 0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";
            psSelect = connection.prepareStatement(sql);

            int col = 1;
            psSelect.setBoolean(col++, medication.booleanValue());
            for (EmisCsvCodeMap mapping : mappings) {
                psSelect.setLong(col++, mapping.getCodeId());
            }

            ResultSet rs = psSelect.executeQuery();
            while (rs.next()) {
                col = 1;
                long codeId = rs.getLong(col++);
                Date dtLastReceived = null;
                Timestamp ts = rs.getTimestamp(col++);
                if (!rs.wasNull()) {
                    dtLastReceived = new Date(ts.getTime());
                }

                EmisCsvCodeMap mapping = hmMappings.get(new Long(codeId));

                //if the one already on the DB has a date and that date is the same or
                //later than the one we're trying to save, then SKIP the one we're saving
                if (dtLastReceived != null
                        && !mapping.getDtLastReceived().after(dtLastReceived)) {

                    hmMappings.remove(new Long(codeId));
                }
            }

            //if there's nothing new to save, return out
            if (hmMappings.isEmpty()) {
                return;
            }

            sql = "INSERT INTO emis_csv_code_map"
                    + " (medication, code_id, code_type, read_term, read_code, snomed_concept_id, snomed_description_id, snomed_term, national_code, national_code_category, national_code_description, parent_code_id, audit_json, dt_last_received, adjusted_code, codeable_concept_system)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " code_type = VALUES(code_type),"
                    + " read_term = VALUES(read_term),"
                    + " read_code = VALUES(read_code),"
                    + " snomed_concept_id = VALUES(snomed_concept_id),"
                    + " snomed_description_id = VALUES(snomed_description_id),"
                    + " snomed_term = VALUES(snomed_term),"
                    + " national_code = VALUES(national_code),"
                    + " national_code_category = VALUES(national_code_category),"
                    + " national_code_description = VALUES(national_code_description),"
                    + " parent_code_id = VALUES(parent_code_id),"
                    + " audit_json = VALUES(audit_json),"
                    + " dt_last_received = VALUES(dt_last_received),"
                    + " adjusted_code = VALUES(adjusted_code),"
                    + " codeable_concept_system = VALUES(codeable_concept_system)";

            psInsert = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (Long codeId : hmMappings.keySet()) {
                EmisCsvCodeMap mapping = hmMappings.get(new Long(codeId));

                col = 1;
                psInsert.setBoolean(col++, mapping.isMedication());
                psInsert.setLong(col++, mapping.getCodeId());
                if (Strings.isNullOrEmpty(mapping.getCodeType())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getCodeType());
                }
                if (Strings.isNullOrEmpty(mapping.getReadTerm())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getReadTerm());
                }
                if (Strings.isNullOrEmpty(mapping.getReadCode())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getReadCode());
                }
                if (mapping.getSnomedConceptId() == null) {
                    psInsert.setNull(col++, Types.BIGINT);
                } else {
                    psInsert.setLong(col++, mapping.getSnomedConceptId());
                }
                if (mapping.getSnomedDescriptionId() == null) {
                    psInsert.setNull(col++, Types.BIGINT);
                } else {
                    psInsert.setLong(col++, mapping.getSnomedDescriptionId());
                }
                if (Strings.isNullOrEmpty(mapping.getSnomedTerm())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getSnomedTerm());
                }
                if (Strings.isNullOrEmpty(mapping.getNationalCode())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getNationalCode());
                }
                if (Strings.isNullOrEmpty(mapping.getNationalCodeCategory())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getNationalCodeCategory());
                }
                if (Strings.isNullOrEmpty(mapping.getNationalCodeDescription())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getNationalCodeDescription());
                }
                if (mapping.getParentCodeId() == null) {
                    psInsert.setNull(col++, Types.BIGINT);
                } else {
                    psInsert.setLong(col++, mapping.getParentCodeId());
                }
                if (mapping.getAudit() == null) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getAudit().writeToJson());
                }
                if (mapping.getDtLastReceived() == null) {
                    psInsert.setNull(col++, Types.TIMESTAMP);
                } else {
                    psInsert.setTimestamp(col++, new Timestamp(mapping.getDtLastReceived().getTime()));
                }
                if (mapping.getAdjustedCode() == null) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getAdjustedCode());
                }
                if (mapping.getCodeableConceptSystem() == null) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getCodeableConceptSystem());
                }

                psInsert.addBatch();
            }

            psInsert.executeBatch();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (psSelect != null) {
                psSelect.close();
            }
            if (psInsert != null) {
                psInsert.close();
            }
            entityManager.close();
        }
    }

    @Override
    public void saveCodeMapping(EmisCsvCodeMap mapping) throws Exception {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        List<EmisCsvCodeMap> l = new ArrayList<>();
        l.add(mapping);
        saveCodeMappings(l);
    }

    @Override
    public EmisCsvCodeMap getCodeMapping(boolean medication, Long codeId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT medication, code_id, code_type, read_term, read_code, snomed_concept_id, "
                    + "snomed_description_id, snomed_term, national_code, national_code_category, "
                    + "national_code_description, parent_code_id, audit_json, dt_last_received, adjusted_code, codeable_concept_system "
                    + "FROM emis_csv_code_map "
                    + "WHERE medication = ? "
                    + "AND code_id = ?";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setBoolean(col++, medication);
            ps.setLong(col++, codeId.longValue());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                EmisCsvCodeMap ret = new EmisCsvCodeMap();

                col = 1;
                ret.setMedication(rs.getBoolean(col++));
                ret.setCodeId(rs.getLong(col++));
                ret.setCodeType(rs.getString(col++));
                ret.setReadTerm(rs.getString(col++));
                ret.setReadCode(rs.getString(col++));

                //have to use isNull for this field because it returns a primitive long
                long snomedConceptId = rs.getLong(col++);
                if (!rs.wasNull()) {
                    ret.setSnomedConceptId(new Long(snomedConceptId));
                }

                long snomedDescriptionId = rs.getLong(col++);
                if (!rs.wasNull()) {
                    ret.setSnomedDescriptionId(new Long(snomedDescriptionId));
                }

                ret.setSnomedTerm(rs.getString(col++));
                ret.setNationalCode(rs.getString(col++));
                ret.setNationalCodeCategory(rs.getString(col++));
                ret.setNationalCodeDescription(rs.getString(col++));

                long parentCodeId = rs.getLong(col++);
                if (!rs.wasNull()) {
                    ret.setParentCodeId(new Long(parentCodeId));
                }

                String auditJson = rs.getString(col++);
                if (!rs.wasNull()) {
                    ret.setAudit(ResourceFieldMappingAudit.readFromJson(auditJson));
                }

                Timestamp ts = rs.getTimestamp(col++);
                if (!rs.wasNull()) {
                    ret.setDtLastReceived(new Date(ts.getTime()));
                }

                ret.setAdjustedCode(rs.getString(col++));
                ret.setCodeableConceptSystem(rs.getString(col++));

                return ret;

            } else {
                return null;
            }


        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }
     */
}
