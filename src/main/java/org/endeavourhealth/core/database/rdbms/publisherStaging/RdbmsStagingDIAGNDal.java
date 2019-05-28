package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingDIAGNDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingDIAGN;
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

public class RdbmsStagingDIAGNDal implements StagingDIAGNDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingDIAGNDal.class);

    private boolean wasAlreadySaved(UUID serviceId, StagingDIAGN obj) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum "
                    + "from condition_DIAGN "
                    + "where diagnosis_id = ? "
                    + "and dt_received <= ? "
                    + "order by dt_received desc "
                    + "limit 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setInt(col++, obj.getDiagnosisId());
            ps.setTimestamp(col++, new java.sql.Timestamp(obj.getDtReceived().getTime()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int dbChecksum = rs.getInt(1);
                return dbChecksum == obj.getRecordChecksum();
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
    public void save(StagingDIAGN stagingDIAGN, UUID serviceId) throws Exception {

        if (stagingDIAGN == null) {
            throw new IllegalArgumentException("stagingDIAGN is null");
        }

        stagingDIAGN.setRecordChecksum(stagingDIAGN.hashCode());

        //check if record already filed to avoid duplicates
        if (wasAlreadySaved(serviceId, stagingDIAGN)) {
            // LOG.warn("diagnosis_DIAGN data already filed with record_checksum: "+stagingDIAGN.hashCode());
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO condition_DIAGN "
                    + " (exchange_id, dt_received, record_checksum, diagnosis_id, active_ind, "
                    + " encounter_id, encounter_slice_id, diagnosis_dt_tm, diagnosis_code_type, "
                    + " diagnosis_code, diagnosis_term, diagnosis_notes, diagnosis_type_cd, diagnosis_seq_nbr, "
                    + " diag_personnel_id, lookup_person_id, lookup_mrn, audit_json)  "
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                    + " ON DUPLICATE KEY UPDATE "
                    + " exchange_id = VALUES(exchange_id), "
                    + " dt_received = VALUES(dt_received), "
                    + " record_checksum = VALUES(record_checksum), "
                    + " diagnosis_id = VALUES(diagnosis_id), "
                    + " active_ind = VALUES(active_ind), "
                    + " encounter_id = VALUES(encounter_id), "
                    + " encounter_slice_id = VALUES(encounter_slice_id), "
                    + " diagnosis_dt_tm = VALUES(diagnosis_dt_tm), "
                    + " diagnosis_code_type = VALUES(diagnosis_code_type), "
                    + " diagnosis_code = VALUES(diagnosis_code), "
                    + " diagnosis_term = VALUES(diagnosis_term), "
                    + " diagnosis_notes = VALUES(diagnosis_notes), "
                    + " diagnosis_type_cd = VALUES(diagnosis_type_cd), "
                    + " diagnosis_seq_nbr = VALUES(diagnosis_seq_nbr), "
                    + " diag_personnel_id = VALUES(diag_personnel_id), "
                    + " lookup_person_id = VALUES(lookup_person_id), "
                    + " lookup_mrn = VALUES(lookup_mrn), "
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            int col = 1;

            //first five columns are non-null
            ps.setString(col++, stagingDIAGN.getExchangeId());
            ps.setTimestamp(col++, new java.sql.Timestamp(stagingDIAGN.getDtReceived().getTime()));
            ps.setInt(col++, stagingDIAGN.getRecordChecksum());
            ps.setInt(col++, stagingDIAGN.getDiagnosisId());
            ps.setBoolean(col++, stagingDIAGN.isActiveInd());

            if (stagingDIAGN.getEncounterId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingDIAGN.getEncounterId());
            }

            if (stagingDIAGN.getEncounterSliceId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingDIAGN.getEncounterSliceId());
            }

            if (stagingDIAGN.getDiagnosisDtTm() == null) {
                ps.setNull(col++, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingDIAGN.getDiagnosisDtTm().getTime()));
            }

            if (stagingDIAGN.getDiagnosisCodeType() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingDIAGN.getDiagnosisCodeType());
            }

            if (stagingDIAGN.getDiagnosisCode() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingDIAGN.getDiagnosisCode());
            }

            if (stagingDIAGN.getDiagnosisTerm() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else{
                ps.setString(col++, stagingDIAGN.getDiagnosisTerm());
            }

            if (stagingDIAGN.getDiagnosisNotes() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else{
                ps.setString(col++, stagingDIAGN.getDiagnosisNotes());
            }

            if (stagingDIAGN.getDiagnosisType() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else{
                ps.setString(col++, stagingDIAGN.getDiagnosisType());
            }

            if (stagingDIAGN.getDiagnosisSeqNo() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingDIAGN.getDiagnosisSeqNo());
            }

            if (stagingDIAGN.getDiagnosisPersonnelId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingDIAGN.getDiagnosisPersonnelId());
            }

            if (stagingDIAGN.getLookupPersonId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingDIAGN.getLookupPersonId());
            }

            if (stagingDIAGN.getLookupMrn() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingDIAGN.getLookupMrn());
            }

            if (stagingDIAGN.getAudit() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingDIAGN.getAudit().writeToJson());
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
}