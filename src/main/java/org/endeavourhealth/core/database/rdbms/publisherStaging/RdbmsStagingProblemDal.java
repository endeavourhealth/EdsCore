package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingProblemDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProblem;
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

public class RdbmsStagingProblemDal implements StagingProblemDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingProblemDal.class);


    private boolean wasSavedAlready(UUID serviceId, StagingProblem obj) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityManager(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum "
                    + "from condition_problem "
                    + "where problem_id = ? "
                    + "and dt_received <= ? "
                    + "order by dt_received desc "
                    + "limit 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setInt(col++, obj.getProblemId());
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
    public void saveProblem(StagingProblem stagingProblem, UUID serviceId) throws Exception {

        if (stagingProblem == null) {
            throw new IllegalArgumentException("stagingProblem is null");
        }

        List<StagingProblem> l = new ArrayList<>();
        l.add(stagingProblem);
        saveProblems(l, serviceId);
    }

    @Override
    public void saveProblems(List<StagingProblem> stagingProblems, UUID serviceId) throws Exception {

        List<StagingProblem> toSave = new ArrayList<>();

        for (StagingProblem stagingProblem: stagingProblems) {
            stagingProblem.setRecordChecksum(stagingProblem.hashCode());

            //check if record already filed to avoid duplicates
            if (!wasSavedAlready(serviceId, stagingProblem)) {
                toSave.add(stagingProblem);
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

            String sql = "INSERT INTO condition_problem "
                    + " (exchange_id, dt_received, record_checksum, problem_id, person_id, mrn, onset_dt_tm, onset_precision, updated_by, "
                    + " vocab, problem_code, problem_term, problem_txt, classification, confirmation, ranking, axis, "
                    + " problem_status, problem_status_date, location, lookup_consultant_personnel_id, audit_json) "
                    + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                    + " ON DUPLICATE KEY UPDATE "
                    + " exchange_id = VALUES(exchange_id), "
                    + " dt_received = VALUES(dt_received), "
                    + " record_checksum = VALUES(record_checksum), "
                    + " problem_id = VALUES(problem_id), "
                    + " person_id = VALUES(person_id), "
                    + " mrn = VALUES(mrn), "
                    + " onset_dt_tm = VALUES(onset_dt_tm), "
                    + " onset_precision = VALUES(onset_precision), "
                    + " updated_by = VALUES(updated_by), "
                    + " vocab = VALUES(vocab), "
                    + " problem_code = VALUES(problem_code), "
                    + " problem_term = VALUES(problem_term), "
                    + " problem_txt = VALUES(problem_txt), "
                    + " classification = VALUES(classification), "
                    + " confirmation = VALUES(confirmation), "
                    + " ranking = VALUES(ranking), "
                    + " axis = VALUES(axis), "
                    + " problem_status = VALUES(problem_status), "
                    + " problem_status_date = VALUES(problem_status_date), "
                    + " location = VALUES(location), "
                    + " lookup_consultant_personnel_id = VALUES(lookup_consultant_personnel_id), "
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (StagingProblem stagingProblem: toSave) {

                int col = 1;

                //all but the first five columns are non-null
                ps.setString(col++, stagingProblem.getExchangeId());
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingProblem.getDtReceived().getTime()));
                ps.setInt(col++, stagingProblem.getRecordChecksum());
                ps.setInt(col++, stagingProblem.getProblemId());
                ps.setInt(col++, stagingProblem.getPersonId());

                if (stagingProblem.getMrn() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getMrn());
                }

                if (stagingProblem.getOnsetDtTm() == null) {
                    ps.setNull(col++, Types.TIMESTAMP);
                } else {
                    ps.setTimestamp(col++, new java.sql.Timestamp(stagingProblem.getOnsetDtTm().getTime()));
                }

                if (stagingProblem.getOnsetPrecision() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getOnsetPrecision());
                }

                if (stagingProblem.getUpdatedBy() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getUpdatedBy());
                }
                if (stagingProblem.getVocab() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getVocab());
                }
                if (stagingProblem.getProblemCd() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getProblemCd());
                }

                if (stagingProblem.getProblemTerm() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getProblemTerm());
                }

                if (stagingProblem.getProblemTxt() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getProblemTxt());
                }

                if (stagingProblem.getClassification() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getClassification());
                }

                if (stagingProblem.getConfirmation() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getConfirmation());
                }

                if (stagingProblem.getRanking() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getRanking());
                }

                if (stagingProblem.getAxis() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getAxis());
                }

                if (stagingProblem.getProblemStatus() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getProblemStatus());
                }

                if (stagingProblem.getProblemStatusDtTm() == null) {
                    ps.setNull(col++, Types.TIMESTAMP);
                } else {
                    ps.setTimestamp(col++, new java.sql.Timestamp(stagingProblem.getProblemStatusDtTm().getTime()));
                }

                if (stagingProblem.getLocation() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getLocation());
                }

                if (stagingProblem.getLookupConsultantPersonnelId() == null) {
                    ps.setNull(col++, Types.INTEGER);
                } else {
                    ps.setInt(col++, stagingProblem.getLookupConsultantPersonnelId());
                }

                if (stagingProblem.getAudit() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProblem.getAudit().writeToJson());
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