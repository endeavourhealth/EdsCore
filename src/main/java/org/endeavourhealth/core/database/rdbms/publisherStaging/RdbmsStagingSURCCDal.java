package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingSURCCDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingSURCC;
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

public class RdbmsStagingSURCCDal implements StagingSURCCDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingSURCCDal.class);

    private boolean wasAlreadySaved(UUID serviceId, StagingSURCC obj) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum "
                    + "from procedure_SURCC "
                    + "where surgical_case_id = ? "
                    + "and dt_received <= ? "
                    + "order by dt_received desc "
                    + "limit 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setInt(col++, obj.getSurgicalCaseId());
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
    public void save(StagingSURCC surcc, UUID serviceId) throws Exception {

        if (surcc == null) {
            throw new IllegalArgumentException("surcc object is null");
        }

        surcc.setRecordChecksum(surcc.hashCode());

        //check if record already filed to avoid duplicates
        if (wasAlreadySaved(serviceId, surcc)) {
           // LOG.warn("procedure_SURCC data already filed with record_checksum: " + surcc.hashCode());
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO procedure_SURCC  "
                    + " (exchange_id, dt_received, record_checksum, surgical_case_id, dt_extract, " +
                    " active_ind, person_id, encounter_id, dt_start, dt_stop, " +
                    " dt_cancelled, institution_code, department_code, " +
                    " surgical_area_code, theatre_number_code, specialty_code, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_id = VALUES(exchange_id),"
                    + " dt_received = VALUES(dt_received),"
                    + " record_checksum = VALUES(record_checksum),"
                    + " surgical_case_id = VALUES(surgical_case_id),"
                    + " dt_extract = VALUES(dt_extract),"
                    + " active_ind = VALUES(active_ind),"
                    + " person_id = VALUES(person_id),"
                    + " encounter_id = VALUES(encounter_id),"
                    + " dt_start = VALUES(dt_start),"
                    + " dt_stop = VALUES(dt_stop),"
                    + " dt_cancelled = VALUES(dt_cancelled),"
                    + " institution_code = VALUES(institution_code),"
                    + " department_code = VALUES(department_code),"
                    + " surgical_area_code = VALUES(surgical_area_code),"
                    + " theatre_number_code = VALUES(theatre_number_code),"
                    + " specialty_code = VALUES(specialty_code),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            int col = 1;

            //only the first six columns are non-null
            ps.setString(col++, surcc.getExchangeId());
            ps.setTimestamp(col++, new java.sql.Timestamp(surcc.getDtReceived().getTime()));
            ps.setInt(col++, surcc.getRecordChecksum());
            ps.setInt(col++, surcc.getSurgicalCaseId());
            ps.setTimestamp(col++, new java.sql.Timestamp(surcc.getDtExtract().getTime()));
            ps.setBoolean(col++, surcc.isActiveInd());

            if (surcc.getPersonId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, surcc.getPersonId());
            }

            if (surcc.getEncounterId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, surcc.getEncounterId());
            }

            if (surcc.getDtStart() == null) {
                ps.setNull(col++, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(surcc.getDtStart().getTime()));
            }
            if (surcc.getDtStop() == null) {
                ps.setNull(col++, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(surcc.getDtStop().getTime()));
            }
            if (surcc.getDtCancelled() == null) {
                ps.setNull(col++, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(surcc.getDtCancelled().getTime()));
            }

            if (surcc.getInstitutionCode() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, surcc.getInstitutionCode());
            }

            if (surcc.getDepartmentCode() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, surcc.getDepartmentCode());
            }

            if (surcc.getSurgicalAreaCode() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, surcc.getSurgicalAreaCode());
            }

            if (surcc.getTheatreNumberCode() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, surcc.getTheatreNumberCode());
            }

            if (surcc.getSpecialtyCode() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, surcc.getSpecialtyCode());
            }

            if (surcc.getAudit() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, surcc.getAudit().writeToJson());
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
