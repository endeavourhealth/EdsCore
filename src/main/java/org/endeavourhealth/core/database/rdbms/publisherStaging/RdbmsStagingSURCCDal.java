package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingSURCCDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingSURCC;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingSURCC;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.UUID;

public class RdbmsStagingSURCCDal implements StagingSURCCDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingSURCCDal.class);

    @Override
    public boolean getRecordChecksumFiled(UUID serviceId, StagingSURCC surcc) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsStagingSURCC c"
                    + " where c.recordChecksum = :record_checksum";

            Query query = entityManager.createQuery(sql, RdbmsStagingSURCC.class)
                    .setParameter("record_checksum", surcc.hashCode());

            try {
                RdbmsStagingSURCC result = (RdbmsStagingSURCC)query.getSingleResult();
                return true;
            }
            catch (NoResultException e) {
                return false;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void save(StagingSURCC surcc, UUID serviceId) throws Exception {

        if (surcc == null) {
            throw new IllegalArgumentException("surcc object is null");
        }

        //check if record already filed to avoid duplicates
        if (getRecordChecksumFiled(serviceId, surcc)) {
            LOG.error("procedure_SURCC data already filed with record_checksum: "+surcc.hashCode());
            return;
        }

        RdbmsStagingSURCC stagingSurcc = new RdbmsStagingSURCC(surcc);

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO procedure_SURCC  "
                    + " (exchange_id, dt_received, record_checksum, surgical_case_id, dt_extract, " +
                    " active_ind, person_id, encounter_id, dt_cancelled, institution_code, department_code, " +
                    " surgical_area_code, theatre_number_code, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_id = VALUES(exchange_id),"
                    + " dt_received = VALUES(dt_received),"
                    + " record_checksum = VALUES(record_checksum),"
                    + " surgical_case_id = VALUES(surgical_case_id),"
                    + " dt_extract = VALUES(dt_extract),"
                    + " active_ind = VALUES(active_ind),"
                    + " person_id = VALUES(person_id),"
                    + " encounter_id = VALUES(encounter_id),"
                    + " dt_cancelled = VALUES(dt_cancelled),"
                    + " institution_code = VALUES(institution_code),"
                    + " department_code = VALUES(department_code),"
                    + " surgical_area_code = VALUES(surgical_area_code),"
                    + " theatre_number_code = VALUES(theatre_number_code),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            ps.setString(1, stagingSurcc.getExchangeId());
            ps.setDate(2, new java.sql.Date(stagingSurcc.getDTReceived().getTime()));
            ps.setInt(3,stagingSurcc.getRecordChecksum());
            ps.setInt(4,stagingSurcc.getSurgicalCaseId());
            ps.setDate(5,new java.sql.Date(stagingSurcc.getDTExtract().getTime()));
            ps.setBoolean(6,stagingSurcc.getActiveInd());
            ps.setInt(7,stagingSurcc.getPersonId());
            ps.setInt(8,stagingSurcc.getEncounterId());
            ps.setDate(9,new java.sql.Date(stagingSurcc.getDTCancelled().getTime()));
            ps.setString(10,stagingSurcc.getInstitutionCode());
            ps.setString(11,stagingSurcc.getDepartmentCode());
            ps.setString(12,stagingSurcc.getSurgicalAreaCode());
            ps.setString(13,stagingSurcc.getTheatreNumberCode());
            ps.setString(14,stagingSurcc.getAuditJson());

            ps.executeUpdate();

            entityManager.getTransaction().commit();
        } catch (SQLIntegrityConstraintViolationException sqlE) {
            LOG.warn("SQLIntegrityConstraintViolationException hadled for " + surcc.toString());
            entityManager.getTransaction().rollback();
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
