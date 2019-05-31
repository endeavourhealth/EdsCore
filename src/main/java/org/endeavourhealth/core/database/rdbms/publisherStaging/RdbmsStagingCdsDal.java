package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingCdsDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCds;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCdsCount;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingConditionCds;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingConditionCdsCount;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.UUID;

public class RdbmsStagingCdsDal implements StagingCdsDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingCdsDal.class);

    private boolean wasCdsAlreadyFiled(UUID serviceId, StagingCds cds) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum "
                        + "from procedure_cds "
                        + "where cds_unique_identifier = ? "
                        + "and sus_record_type = ? "
                        + "and procedure_seq_nbr = ? "
                        + "and dt_received <= ? "
                        + "order by dt_received desc "
                        + "limit 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, cds.getCdsUniqueIdentifier());
            ps.setString(col++, cds.getSusRecordType());
            ps.setInt(col++, cds.getProcedureSeqNbr());
            ps.setTimestamp(col++, new java.sql.Timestamp(cds.getDtReceived().getTime()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int dbChecksum = rs.getInt(1);
                return dbChecksum == cds.getRecordChecksum();
            } else {
                return false;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public void save(StagingCds cds, UUID serviceId) throws Exception {

        if (cds == null) {
            throw new IllegalArgumentException("cds object is null");
        }

        cds.setRecordChecksum(cds.hashCode());

        //check if record already filed to avoid duplicates
        if (wasCdsAlreadyFiled(serviceId, cds)) {
            //   LOG.warn("procedure_cds data already filed with record_checksum: "+cds.hashCode());
            //   LOG.warn("cds:>" + cds.toString());
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO procedure_cds  "
                    + " (exchange_id, dt_received, record_checksum, cds_activity_date, sus_record_type, cds_unique_identifier, " +
                    " cds_update_type, mrn, nhs_number, withheld, date_of_birth, consultant_code, procedure_date, " +
                    " procedure_opcs_code, procedure_seq_nbr, primary_procedure_opcs_code, lookup_procedure_opcs_term, " +
                    " lookup_person_id, lookup_consultant_personnel_id, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_id = VALUES(exchange_id),"
                    + " dt_received = VALUES(dt_received),"
                    + " record_checksum = VALUES(record_checksum),"
                    + " cds_activity_date=VALUES(cds_activity_date),"
                    + " sus_record_type = VALUES(sus_record_type),"
                    + " cds_unique_identifier = VALUES(cds_unique_identifier),"
                    + " cds_update_type = VALUES(cds_update_type),"
                    + " mrn = VALUES(mrn),"
                    + " nhs_number = VALUES(nhs_number),"
                    + " withheld = VALUES(withheld),"
                    + " date_of_birth = VALUES(date_of_birth),"
                    + " consultant_code = VALUES(consultant_code),"
                    + " procedure_date = VALUES(procedure_date),"
                    + " procedure_opcs_code = VALUES(procedure_opcs_code),"
                    + " procedure_seq_nbr = VALUES(procedure_seq_nbr),"
                    + " primary_procedure_opcs_code = VALUES(primary_procedure_opcs_code),"
                    + " lookup_procedure_opcs_term = VALUES(lookup_procedure_opcs_term),"
                    + " lookup_person_id = VALUES(lookup_person_id),"
                    + " lookup_consultant_personnel_id = VALUES(lookup_consultant_personnel_id),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            int col = 1;

            //all columns except the last three are non-null
            ps.setString(col++, cds.getExchangeId());
            ps.setTimestamp(col++, new java.sql.Timestamp(cds.getDtReceived().getTime()));
            ps.setInt(col++, cds.getRecordChecksum());
            ps.setTimestamp(col++, new java.sql.Timestamp(cds.getCdsActivityDate().getTime()));
            ps.setString(col++, cds.getSusRecordType());
            ps.setString(col++, cds.getCdsUniqueIdentifier());
            ps.setInt(col++, cds.getCdsUpdateType());
            ps.setString(col++, cds.getMrn());
            ps.setString(col++, cds.getNhsNumber());
            if (cds.getWithheld() == null) {
                ps.setNull(col++, Types.BOOLEAN);
            } else {
                ps.setBoolean(col++, cds.getWithheld().booleanValue());
            }
            if (cds.getDateOfBirth() == null) {
                ps.setNull(col++, Types.NULL);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(cds.getDateOfBirth().getTime()));
            }
            ps.setString(col++, cds.getConsultantCode());

            if (cds.getProcedureDate() == null) {
                ps.setNull(col++, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(cds.getProcedureDate().getTime()));
            }

            ps.setString(col++, cds.getProcedureOpcsCode());
            ps.setInt(col++, cds.getProcedureSeqNbr());
            ps.setString(col++, cds.getPrimaryProcedureOpcsCode());
            ps.setString(col++, cds.getLookupProcedureOpcsTerm());

            if (cds.getLookupPersonId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, cds.getLookupPersonId());
            }

            if (cds.getLookupConsultantPersonnelId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, cds.getLookupConsultantPersonnelId());
            }

            if (cds.getAudit() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, cds.getAudit().writeToJson());
            }

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            LOG.error("Error saving " + cds);
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    private boolean wasConditionCdsAlreadyFiled(UUID serviceId, StagingConditionCds cdsCondition) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum "
                    + "from condition_cds "
                    + "where cds_unique_identifier = ? "
                    + "and sus_record_type = ? "
                    + "and condition_seq_nbr = ? "
                    + "and dt_received <= ? "
                    + "order by dt_received desc "
                    + "limit 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, cdsCondition.getCdsUniqueIdentifier());
            ps.setString(col++, cdsCondition.getSusRecordType());
            ps.setInt(col++, cdsCondition.getDiagnosisSeqNbr());
            ps.setTimestamp(col++, new java.sql.Timestamp(cdsCondition.getDtReceived().getTime()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int dbChecksum = rs.getInt(1);
                return dbChecksum == cdsCondition.getRecordChecksum();
            } else {
                return false;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public void save(StagingConditionCds cdsCondition, UUID serviceId) throws Exception {

        if (cdsCondition == null) {
            throw new IllegalArgumentException("cdsCondition object is null");
        }

        cdsCondition.setRecordChecksum(cdsCondition.hashCode());

        //check if record already filed to avoid duplicates
        if (wasConditionCdsAlreadyFiled(serviceId, cdsCondition)) {
            //   LOG.warn("condition_cds data already filed with record_checksum: "+cdsCondition.hashCode());
            //   LOG.warn("cdsCondition:>" + cdsCondition.toString());
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO condition_cds  "
                    + " (exchange_id, dt_received, record_checksum, cds_activity_date, sus_record_type, cds_unique_identifier, " +
                    " cds_update_type, mrn, nhs_number, withheld, date_of_birth, consultant_code, " +
                    " diagnosis_icd_code, diagnosis_seq_nbr, primary_diagnosis_icd_code, lookup_diagnosis_icd_term, " +
                    " lookup_person_id, lookup_consultant_personnel_id, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_id = VALUES(exchange_id),"
                    + " dt_received = VALUES(dt_received),"
                    + " record_checksum = VALUES(record_checksum),"
                    + " cds_activity_date=VALUES(cds_activity_date),"
                    + " sus_record_type = VALUES(sus_record_type),"
                    + " cds_unique_identifier = VALUES(cds_unique_identifier),"
                    + " cds_update_type = VALUES(cds_update_type),"
                    + " mrn = VALUES(mrn),"
                    + " nhs_number = VALUES(nhs_number),"
                    + " withheld = VALUES(withheld),"
                    + " date_of_birth = VALUES(date_of_birth),"
                    + " consultant_code = VALUES(consultant_code),"
                    + " diagnosis_icd_code = VALUES(diagnosis_icd_code),"
                    + " diagnosis_seq_nbr = VALUES(diagnosis_seq_nbr),"
                    + " primary_diagnosis_icd_code = VALUES(primary_diagnosis_icd_code),"
                    + " lookup_diagnosis_icd_term = VALUES(lookup_diagnosis_icd_term),"
                    + " lookup_person_id = VALUES(lookup_person_id),"
                    + " lookup_consultant_personnel_id = VALUES(lookup_consultant_personnel_id),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            int col = 1;

            //all columns except the last three are non-null
            ps.setString(col++, cdsCondition.getExchangeId());
            ps.setTimestamp(col++, new java.sql.Timestamp(cdsCondition.getDtReceived().getTime()));
            ps.setInt(col++, cdsCondition.getRecordChecksum());
            ps.setTimestamp(col++, new java.sql.Timestamp(cdsCondition.getCdsActivityDate().getTime()));
            ps.setString(col++, cdsCondition.getSusRecordType());
            ps.setString(col++, cdsCondition.getCdsUniqueIdentifier());
            ps.setInt(col++, cdsCondition.getCdsUpdateType());
            ps.setString(col++, cdsCondition.getMrn());
            ps.setString(col++, cdsCondition.getNhsNumber());
            if (cdsCondition.getWithheld() == null) {
                ps.setNull(col++, Types.BOOLEAN);
            } else {
                ps.setBoolean(col++, cdsCondition.getWithheld().booleanValue());
            }
            if (cdsCondition.getDateOfBirth() == null) {
                ps.setNull(col++, Types.NULL);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(cdsCondition.getDateOfBirth().getTime()));
            }
            ps.setString(col++, cdsCondition.getConsultantCode());

            ps.setString(col++, cdsCondition.getDiagnosisIcdCode());
            ps.setInt(col++, cdsCondition.getDiagnosisSeqNbr());
            ps.setString(col++, cdsCondition.getPrimaryDiagnosisIcdCode());
            ps.setString(col++, cdsCondition.getLookupDiagnosisIcdTerm());

            if (cdsCondition.getLookupPersonId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, cdsCondition.getLookupPersonId());
            }

            if (cdsCondition.getLookupConsultantPersonnelId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, cdsCondition.getLookupConsultantPersonnelId());
            }

            if (cdsCondition.getAudit() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, cdsCondition.getAudit().writeToJson());
            }

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            LOG.error("Error saving " + cdsCondition);
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    private boolean wasCdsCountAlreadyFiled(UUID serviceId, StagingCdsCount cdsCount) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum "
                    + "from procedure_cds_count "
                    + "where cds_unique_identifier = ? "
                    + "and sus_record_type = ? "
                    + "and dt_received <= ? "
                    + "order by dt_received desc "
                    + "limit 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, cdsCount.getCdsUniqueIdentifier());
            ps.setString(col++, cdsCount.getSusRecordType());
            ps.setTimestamp(col++, new java.sql.Timestamp(cdsCount.getDtReceived().getTime()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int dbChecksum = rs.getInt(1);
                return dbChecksum == cdsCount.getRecordChecksum();
            } else {
                return false;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public void save(StagingCdsCount cdsCount, UUID serviceId) throws Exception {
        if (cdsCount == null) {
            throw new IllegalArgumentException("cdsCount object is null");
        }

        cdsCount.setRecordChecksum(cdsCount.hashCode());

        //check if record already filed to avoid duplicates
        if (wasCdsCountAlreadyFiled(serviceId, cdsCount)) {
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO procedure_cds_count  "
                    + " (exchange_id, dt_received, record_checksum, sus_record_type, cds_unique_identifier, procedure_count, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_id = VALUES(exchange_id),"
                    + " dt_received = VALUES(dt_received),"
                    + " record_checksum = VALUES(record_checksum),"
                    + " sus_record_type = VALUES(sus_record_type),"
                    + " cds_unique_identifier = VALUES(cds_unique_identifier),"
                    + " procedure_count = VALUES(procedure_count),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            int col = 1;

            //all but the last of the columns allow nulls
            ps.setString(col++, cdsCount.getExchangeId());
            ps.setTimestamp(col++, new java.sql.Timestamp(cdsCount.getDtReceived().getTime()));
            ps.setInt(col++, cdsCount.getRecordChecksum());
            ps.setString(col++, cdsCount.getSusRecordType());
            ps.setString(col++, cdsCount.getCdsUniqueIdentifier());
            ps.setInt(col++, cdsCount.getProcedureCount());
            if (cdsCount.getAudit() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, cdsCount.getAudit().writeToJson());
            }

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            LOG.error("Error saving " + cdsCount);
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    private boolean wasConditionCdsCountAlreadyFiled(UUID serviceId, StagingConditionCdsCount cdsConditionCount) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum "
                    + "from condition_cds_count "
                    + "where cds_unique_identifier = ? "
                    + "and sus_record_type = ? "
                    + "and dt_received <= ? "
                    + "order by dt_received desc "
                    + "limit 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, cdsConditionCount.getCdsUniqueIdentifier());
            ps.setString(col++, cdsConditionCount.getSusRecordType());
            ps.setTimestamp(col++, new java.sql.Timestamp(cdsConditionCount.getDtReceived().getTime()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int dbChecksum = rs.getInt(1);
                return dbChecksum == cdsConditionCount.getRecordChecksum();
            } else {
                return false;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public void save(StagingConditionCdsCount cdsConditionCount, UUID serviceId) throws Exception {

        if (cdsConditionCount == null) {
            throw new IllegalArgumentException("cdsConditionCount object is null");
        }

        cdsConditionCount.setRecordChecksum(cdsConditionCount.hashCode());

        //check if record already filed to avoid duplicates
        if (wasConditionCdsCountAlreadyFiled(serviceId, cdsConditionCount)) {
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            /**
             create table condition_cds_count (

             exchange_id                    char(36)     NOT NULL COMMENT 'links to audit.exchange table (but on a different server)',
             dt_received                    datetime     NOT NULL COMMENT 'date time this record was received into Discovery',
             record_checksum                bigint       NOT NULL COMMENT 'checksum of the columns below to easily spot duplicates',
             sus_record_type                varchar(10)  NOT NULL COMMENT 'one of inpatient, outpatient, emergency',
             cds_unique_identifier          varchar(50)  NOT NULL COMMENT 'from CDSUniqueIdentifier',
             condition_count                int NOT NULL COMMENT 'number of procedures in this CDS record',
             CONSTRAINT pk_procedure_cds_count PRIMARY KEY (exchange_id, cds_unique_identifier, sus_record_type)
             );
             */

            String sql = "INSERT INTO condition_cds_count  "
                    + " (exchange_id, dt_received, record_checksum, sus_record_type, cds_unique_identifier, condition_count)"
                    + " VALUES (?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_id = VALUES(exchange_id),"
                    + " dt_received = VALUES(dt_received),"
                    + " record_checksum = VALUES(record_checksum),"
                    + " sus_record_type = VALUES(sus_record_type),"
                    + " cds_unique_identifier = VALUES(cds_unique_identifier),"
                    + " condition_count = VALUES(condition_count)";

            ps = connection.prepareStatement(sql);

            int col = 1;

            //NONE of the columns allow nulls
            ps.setString(col++, cdsConditionCount.getExchangeId());
            ps.setTimestamp(col++, new java.sql.Timestamp(cdsConditionCount.getDtReceived().getTime()));
            ps.setInt(col++, cdsConditionCount.getRecordChecksum());
            ps.setString(col++, cdsConditionCount.getSusRecordType());
            ps.setString(col++, cdsConditionCount.getCdsUniqueIdentifier());
            ps.setInt(col++, cdsConditionCount.getConditionCount());

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            LOG.error("Error saving " + cdsConditionCount);
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }
}
