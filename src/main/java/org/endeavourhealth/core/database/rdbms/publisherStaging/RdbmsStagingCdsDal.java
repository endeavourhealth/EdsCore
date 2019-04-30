package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingCdsDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCds;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingCds;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class RdbmsStagingCdsDal implements StagingCdsDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingCdsDal.class);

    @Override
    public boolean getRecordChecksumFiled(UUID serviceId, StagingCds cds) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql ="select record_checksum from procedure_cds_latest where cds_unique_identifier = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, cds.getCdsUniqueIdentifier());
            ResultSet rs = ps.executeQuery();
            if (rs.wasNull()) {
                return false;
            } else {
                return (rs.getInt(1) == cds.getRecordChecksum());
            }
           //entityManager.getTransaction().commit();
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

    @Override
    public void save(StagingCds cds, UUID serviceId) throws Exception {

        if (cds == null) {
            throw new IllegalArgumentException("cds object is null");
        }

        //check if record already filed to avoid duplicates
        if (getRecordChecksumFiled(serviceId, cds)) {
         //   LOG.warn("procedure_cds data already filed with record_checksum: "+cds.hashCode());
         //   LOG.warn("cds:>" + cds.toString());
            return;
        }

        RdbmsStagingCds stagingCds = new RdbmsStagingCds(cds);

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO procedure_cds  "
                    + " (exchange_id, dt_received, record_checksum,sus_record_type, cds_unique_identifier, " +
                    " cds_update_type, mrn, nhs_number, date_of_birth, consultant_code, procedure_date, " +
                    " procedure_opcs_code, procedure_seq_nbr, primary_procedure_opcs_code, lookup_procedure_opcs_term, " +
                    " lookup_person_id, lookup_consultant_personnel_id, audit_json, cds_activity_date)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_id = VALUES(exchange_id),"
                    + " dt_received = VALUES(dt_received),"
                    + " record_checksum = VALUES(record_checksum),"
                    + " sus_record_type = VALUES(sus_record_type),"
                    + " cds_unique_identifier = VALUES(cds_unique_identifier),"
                    + " cds_update_type = VALUES(cds_update_type),"
                    + " mrn = VALUES(mrn),"
                    + " nhs_number = VALUES(nhs_number),"
                    + " date_of_birth = VALUES(date_of_birth),"
                    + " consultant_code = VALUES(consultant_code),"
                    + " procedure_date = VALUES(procedure_date),"
                    + " procedure_opcs_code = VALUES(procedure_opcs_code),"
                    + " procedure_seq_nbr = VALUES(procedure_seq_nbr),"
                    + " primary_procedure_opcs_code = VALUES(primary_procedure_opcs_code),"
                    + " lookup_procedure_opcs_term = VALUES(lookup_procedure_opcs_term),"
                    + " lookup_person_id = VALUES(lookup_person_id),"
                    + " lookup_consultant_personnel_id = VALUES(lookup_consultant_personnel_id),"
                    + " audit_json = VALUES(audit_json),"
                    + " cds_activity_date=VALUES(cds_activity_date)";

            ps = connection.prepareStatement(sql);
            java.sql.Timestamp sqlDate = null;

            ps.setString(1, stagingCds.getExchangeId());
            ps.setTimestamp(2, new java.sql.Timestamp(stagingCds.getDtReceived().getTime()));
            ps.setInt(3, stagingCds.getRecordChecksum());
            ps.setString(4, stagingCds.getSusRecordType());
            ps.setString(5, stagingCds.getCdsUniqueIdentifier());
            ps.setInt(6, stagingCds.getCdsUpdateType());
            ps.setString(7, stagingCds.getMrn());
            ps.setString(8, stagingCds.getNhsNumber());
            if (stagingCds.getDateOfBirth()!=null) {
                sqlDate = new java.sql.Timestamp(stagingCds.getDateOfBirth().getTime());
            } else {
                sqlDate=null;
            }
            ps.setTimestamp(9,sqlDate);
            ps.setString(10, stagingCds.getConsultantCode());

            if (stagingCds.getProcedureDate() != null) {
                sqlDate =  new java.sql.Timestamp(stagingCds.getProcedureDate().getTime());
            } else {
                sqlDate = null;
            }
            ps.setTimestamp(11, sqlDate);
            ps.setString(12, stagingCds.getProcedureOpcsCode());
            ps.setInt(13, stagingCds.getProcedureSeqNbr());
            ps.setString(14, stagingCds.getPrimaryProcedureOpcsCode());
            ps.setString(15, stagingCds.getLookupProcedureOpcsTerm());
            ps.setInt(16, stagingCds.getLookupPersonId());
            ps.setInt(17, stagingCds.getLookupConsultantPersonnelId());
            ps.setString(18, stagingCds.getAuditJson());
            ps.setTimestamp(19,new java.sql.Timestamp(stagingCds.getCdsActivityDate().getTime()));

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
