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
import java.util.UUID;

public class RdbmsStagingDiagnosisDal implements StagingDiagnosisDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingDiagnosisDal.class);


    private boolean wasSavedAlready(UUID serviceId, StagingDiagnosis obj) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
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
    public void save(StagingDiagnosis stagingDiagnosis, UUID serviceId) throws Exception {

        if (stagingDiagnosis == null) {
            throw new IllegalArgumentException("stagingDiagnosis is null");
        }

        stagingDiagnosis.setRecordChecksum(stagingDiagnosis.hashCode());

        //check if record already filed to avoid duplicates
        if (wasSavedAlready(serviceId, stagingDiagnosis)) {
            //   LOG.warn("stagingDiagnosis data already filed with record_checksum: "+stagingDiagnosis.hashCode());
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO condition_diagnosis "
                    + " (exchange_id, dt_received, record_checksum, diagnosis_id, person_id, active_ind, mrn, "
                    + " encounter_id, diag_dt_tm, diag_type, diag_prnsl, vocab, diag_code, diag_term, diag_notes, "
                    + " qualifier, confirmation, lookup_consultant_personnel_id, location, audit_json) "
                    + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
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
                    + " qualifier = VALUES(qualifier), "
                    + " confirmation = VALUES(confirmation), "
                    + " location = VALUES(location), "
                    + " lookup_consultant_personnel_id = VALUES(lookup_consultant_personnel_id), "
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

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
            ps.setTimestamp(col++, new java.sql.Timestamp(stagingDiagnosis.getDiagDtTm().getTime()));
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

            if (stagingDiagnosis.getQualifier() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingDiagnosis.getQualifier());
            }

            if (stagingDiagnosis.getQualifier() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingDiagnosis.getConfirmation());
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

            ps.executeUpdate();

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
