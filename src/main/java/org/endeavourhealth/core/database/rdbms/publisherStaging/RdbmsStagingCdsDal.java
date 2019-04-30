package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingCdsDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCds;
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

    @Override
    public boolean getRecordChecksumFiled(UUID serviceId, StagingCds cds) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum from procedure_cds_latest where cds_unique_identifier = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, cds.getCdsUniqueIdentifier());

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

        //check if record already filed to avoid duplicates
        if (getRecordChecksumFiled(serviceId, cds)) {
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
                    " cds_update_type, mrn, nhs_number, date_of_birth, consultant_code, procedure_date, " +
                    " procedure_opcs_code, procedure_seq_nbr, primary_procedure_opcs_code, lookup_procedure_opcs_term, " +
                    " lookup_person_id, lookup_consultant_personnel_id, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
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
            ps.setTimestamp(col++, new java.sql.Timestamp(cds.getDateOfBirth().getTime()));
            ps.setString(col++, cds.getConsultantCode());
            ps.setTimestamp(col++, new java.sql.Timestamp(cds.getProcedureDate().getTime()));
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
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }
}
