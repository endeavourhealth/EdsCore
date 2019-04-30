package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingSURCPDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingSURCP;
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

public class RdbmsStagingSURCPDal implements StagingSURCPDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingSURCPDal.class);

    @Override
    public boolean getRecordChecksumFiled(UUID serviceId, StagingSURCP obj) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum from procedure_SURCP_latest where surgical_case_procedure_id = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, obj.getSurgicalCaseProcedureId());

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
    public void save(StagingSURCP surcp, UUID serviceId) throws Exception {

        if (surcp == null) {
            throw new IllegalArgumentException("surcp object is null");
        }

        //check if record already filed to avoid duplicates
        if (getRecordChecksumFiled(serviceId, surcp)) {
            //  LOG.warn("procedure_SURCC data already filed with record_checksum: "+surcp.hashCode());
            return;
        }

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
                    " dt_start, dt_stop, wound_class_code, lookup_procedure_code_term, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
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
                    + " lookup_procedure_code_term=VALUES( lookup_procedure_code_term),"
                    + " audit_json = VALUES(audit_json)";
//                    + " cds_activity_date=VALUES(cds_activity_date)";

            ps = connection.prepareStatement(sql);

            int col = 1;
            //first four, then cols six and seven are non-null
            ps.setString(col++, surcp.getExchangeId());
            ps.setTimestamp(col++, new java.sql.Timestamp(surcp.getDtReceived().getTime()));
            ps.setInt(col++, surcp.getRecordChecksum());
            ps.setInt(col++, surcp.getSurgicalCaseProcedureId());
            if (surcp.getSurgicalCaseId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, surcp.getSurgicalCaseId());
            }
            ps.setTimestamp(col++, new java.sql.Timestamp(surcp.getDtExtract().getTime()));
            ps.setBoolean(col++, surcp.isActiveInd());

            if (surcp.getProcedureCode() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, surcp.getProcedureCode());
            }

            if (surcp.getProcedureText() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, surcp.getProcedureText());
            }

            if (surcp.getModifierText() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, surcp.getModifierText());
            }

            if (surcp.getPrimaryProcedureIndicator() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, surcp.getPrimaryProcedureIndicator());
            }

            if (surcp.getSurgeonPersonnelId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, surcp.getSurgeonPersonnelId());
            }

            if (surcp.getDtStart() == null) {
                ps.setNull(col++, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(surcp.getDtStart().getTime()));
            }

            if (surcp.getDtStop() == null) {
                ps.setNull(col++, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(surcp.getDtStop().getTime()));
            }

            if (surcp.getWoundClassCode() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, surcp.getWoundClassCode());
            }

            if (surcp.getLookupProcedureCodeTerm() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, surcp.getLookupProcedureCodeTerm());
            }

            if (surcp.getAudit() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, surcp.getAudit().writeToJson());
            }

//            ps.setDate(17,new java.sql.Date(surcp.getCdsActivityDate().getTime()));

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
