package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingDiagnosisDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingDiagnosis;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RdbmsStagingDiagnosisDal implements StagingDiagnosisDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingDiagnosisDal.class);


    private boolean wasSavedAlready(UUID serviceId, StagingDiagnosis obj) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityManager(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum "
                    + "from condition_diagnosis "
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
    public void saveDiag(StagingDiagnosis stagingDiag, UUID serviceId) throws Exception {

        if (stagingDiag == null) {
            throw new IllegalArgumentException("stagingDiagnosis is null");
        }

        List<StagingDiagnosis> l = new ArrayList<>();
        l.add(stagingDiag);
        saveDiags(l, serviceId);
    }

    @Override
    public void saveDiags(List<StagingDiagnosis> stagingDiags, UUID serviceId) throws Exception {

        List<StagingDiagnosis> toSave = new ArrayList<>();

        for (StagingDiagnosis stagingDiagnosis: stagingDiags) {
            stagingDiagnosis.setRecordChecksum(stagingDiagnosis.hashCode());

            //check if record already filed to avoid duplicates
            if (!wasSavedAlready(serviceId, stagingDiagnosis)) {
                toSave.add(stagingDiagnosis);
            }
        }

        if (toSave.isEmpty()) {
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityManager(serviceId);
        PreparedStatement ps = null;

        try {

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO condition_diagnosis "
                    + " (exchange_id, dt_received, record_checksum, diagnosis_id, person_id, active_ind, mrn, "
                    + " encounter_id, diag_dt_tm, diag_type, diag_prnsl, vocab, diag_code, diag_term, diag_notes, "
                    + " confirmation, classification, ranking, axis, location, lookup_consultant_personnel_id, audit_json) "
                    + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                    + " ON DUPLICATE KEY UPDATE "
                    + " exchange_id = VALUES(exchange_id), "
                    + " dt_received = VALUES(dt_received), "
                    + " record_checksum = VALUES(record_checksum), "
                    + " diagnosis_id = VALUES(diagnosis_id), "
                    + " person_id = VALUES(person_id), "
                    + " active_ind = VALUES(active_ind), "
                    + " mrn = VALUES(mrn), "
                    + " encounter_id = VALUES(encounter_id), "
                    + " diag_dt_tm = VALUES(diag_dt_tm), "
                    + " diag_type = VALUES(diag_type), "
                    + " diag_prnsl = VALUES(diag_prnsl), "
                    + " vocab = VALUES(vocab), "
                    + " diag_code = VALUES(diag_code), "
                    + " diag_term = VALUES(diag_term), "
                    + " diag_notes = VALUES(diag_notes), "
                    + " confirmation = VALUES(confirmation), "
                    + " classification = VALUES(classification), "
                    + " ranking = VALUES(ranking), "
                    + " axis = VALUES(axis), "
                    + " location = VALUES(location), "
                    + " lookup_consultant_personnel_id = VALUES(lookup_consultant_personnel_id), "
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (StagingDiagnosis stagingDiagnosis: toSave) {

                int col = 1;

                //all but the last five columns are non-null
                ps.setString(col++, stagingDiagnosis.getExchangeId());
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingDiagnosis.getDtReceived().getTime()));
                ps.setInt(col++, stagingDiagnosis.getRecordChecksum());
                ps.setInt(col++, stagingDiagnosis.getDiagnosisId());
                ps.setInt(col++, stagingDiagnosis.getPersonId());
                ps.setBoolean(col++, stagingDiagnosis.isActiveInd());
                ps.setString(col++, stagingDiagnosis.getMrn());
                ps.setInt(col++, stagingDiagnosis.getEncounterId());

                if (stagingDiagnosis.getDiagDtTm() == null) {
                    ps.setNull(col++, Types.TIMESTAMP);
                } else {
                    ps.setTimestamp(col++, new java.sql.Timestamp(stagingDiagnosis.getDiagDtTm().getTime()));
                }

                //ps.setTimestamp(col++, new java.sql.Timestamp(stagingDiagnosis.getDiagDtTm().getTime()));
                ps.setString(col++, stagingDiagnosis.getDiagType());
                ps.setString(col++, stagingDiagnosis.getConsultant());
                ps.setString(col++, stagingDiagnosis.getVocab());
                ps.setString(col++, stagingDiagnosis.getDiagCd());
                ps.setString(col++, stagingDiagnosis.getDiagTerm());

                if (stagingDiagnosis.getNotes() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingDiagnosis.getNotes());
                }

                if (stagingDiagnosis.getConfirmation() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingDiagnosis.getConfirmation());
                }

                if (stagingDiagnosis.getClassification() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingDiagnosis.getClassification());
                }

                if (stagingDiagnosis.getRanking() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingDiagnosis.getRanking());
                }

                if (stagingDiagnosis.getAxis() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingDiagnosis.getAxis());
                }

                if (stagingDiagnosis.getLocation() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingDiagnosis.getLocation());
                }

                if (stagingDiagnosis.getLookupConsultantPersonnelId() == null) {
                    ps.setNull(col++, Types.INTEGER);
                } else {
                    ps.setInt(col++, stagingDiagnosis.getLookupConsultantPersonnelId());
                }

                if (stagingDiagnosis.getAudit() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingDiagnosis.getAudit().writeToJson());
                }

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
    }
}