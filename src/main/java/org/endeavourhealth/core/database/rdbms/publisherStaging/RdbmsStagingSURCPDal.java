package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingSURCPDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingSURCP;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingSURCP;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class RdbmsStagingSURCPDal implements StagingSURCPDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingSURCPDal.class);

    @Override
    public boolean getRecordChecksumFiled(UUID serviceId, StagingSURCP surcp) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsStagingSURCP c"
                    + " where c.surgicalCaseProcedureId = :surgical_case_procedure_id"
                    + " order by c.dtReceived desc";

            Query query = entityManager.createQuery(sql, RdbmsStagingSURCP.class)
                    .setParameter("surgical_case_procedure_id", surcp.getSurgicalCaseProcedureId())
                    .setMaxResults(1);

            try {
                RdbmsStagingSURCP result = (RdbmsStagingSURCP)query.getSingleResult();
                return result.getRecordChecksum() == surcp.getRecordChecksum();
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
    public void save(StagingSURCP surcp, UUID serviceId) throws Exception {

        if (surcp == null) {
            throw new IllegalArgumentException("surcp object is null");
        }

        //check if record already filed to avoid duplicates
        if (getRecordChecksumFiled(serviceId, surcp)) {
            LOG.warn("procedure_SURCC data already filed with record_checksum: "+surcp.hashCode());
            return;
        }

        RdbmsStagingSURCP stagingSurcp = new RdbmsStagingSURCP(surcp);

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO procedure_SURCP  "
                    + " (exchange_id, dt_received, record_checksum, "
                    + " surgical_case_procedure_id, surgical_case_id, dt_extract, " +
                    " active_ind, procedure_code, procedure_text, modifier_text, primary_procedure_indicator, surgeon_personnel_id," +
                    " dt_start, dt_stop, wound_class_code, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_id = VALUES(exchange_id),"
                    + " dt_received = VALUES(dt_received),"
                    + " record_checksum = VALUES(record_checksum),"
                    + " surgical_case_procedure_id = VALUES(surgical_case_procedure_id),"
                    + " surgical_case_id = VALUES(surgical_case_id),"
                    + " dt_extract = VALUES(dt_extract),"
                    + " active_ind = VALUES(active_ind),"
                    + " procedure_code = VALUES(procedure_code),"
                    + " procedure_text = VALUES(procedure_text),"
                    + " modifier_text = VALUES(modifier_text),"
                    + " primary_procedure_indicator = VALUES(primary_procedure_indicator),"
                    + " surgeon_personnel_id = VALUES(surgeon_personnel_id),"
                    + " dt_start = VALUES(dt_start),"
                    + " dt_stop = VALUES(dt_stop),"
                    + " wound_class_code = VALUES(wound_class_code),"
                    + " audit_json = VALUES(audit_json)";
//                    + " cds_activity_date=VALUES(cds_activity_date)";

            ps = connection.prepareStatement(sql);

            ps.setString(1, stagingSurcp.getExchangeId());
            java.sql.Date sqlDate = new java.sql.Date(stagingSurcp.getDtReceived().getTime());
            ps.setDate(2, sqlDate);
            ps.setInt(3,stagingSurcp.getRecordChecksum());
            ps.setInt(4,stagingSurcp.getSurgicalCaseProcedureId());
            ps.setInt(5,stagingSurcp.getSurgicalCaseId());

            if (stagingSurcp.getDTExtract() != null) {
                ps.setDate(6, new java.sql.Date(stagingSurcp.getDTExtract().getTime()));
            } else {
                sqlDate = null;
                ps.setDate(6,sqlDate);
            }

            ps.setBoolean(7,stagingSurcp.getActiveInd());
            ps.setInt(8,stagingSurcp.getProcedureCode());
            ps.setString(9,stagingSurcp.getProcedureText());
            ps.setString(10,stagingSurcp.getModifierText());
            ps.setInt(11,stagingSurcp.getPrimaryProcedureIndicator());
            if (stagingSurcp.getSurgeonPersonnelId()!=null) {
                ps.setInt(12, stagingSurcp.getSurgeonPersonnelId());
            }
            if (stagingSurcp.getDTStart()!=null) {
                ps.setDate(13, new java.sql.Date(stagingSurcp.getDTStart().getTime()));
            } else {
                sqlDate = null;
                ps.setDate(13,sqlDate);
            }
            if (stagingSurcp.getDTStop()!=null) {
                ps.setDate(14, new java.sql.Date(stagingSurcp.getDTStop().getTime()));
            } else {
                sqlDate=null;
                ps.setDate(14,sqlDate);
            }
            ps.setString(15,stagingSurcp.getWoundClassCode());
            ps.setString(16,stagingSurcp.getAuditJson());
//            ps.setDate(17,new java.sql.Date(stagingSurcp.getCdsActivityDate().getTime()));

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
