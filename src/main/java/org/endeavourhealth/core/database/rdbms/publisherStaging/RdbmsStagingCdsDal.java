package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingCdsDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCds;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingCds;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class RdbmsStagingCdsDal implements StagingCdsDalI {

    @Override
    public void save(StagingCds cds, UUID serviceId) throws Exception {

        if (cds == null) {
            throw new IllegalArgumentException("cds object is null");
        }

        RdbmsStagingCds stagingCds = new RdbmsStagingCds(cds);

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO staging_cds  "
                    + " (exchange_id, dt_received, record_checksum, sus_record_type, cds_unique_identifier, " +
                    " cds_update_type, mrn, nhs_number, date_of_birth, procedure_date, procedure_opcs_code, " +
                    " procedure_opcs_term, procedure_seq_nbr, consultant_code, location, person_id, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
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
                    + " procedure_date = VALUES(procedure_date),"
                    + " procedure_opcs_code = VALUES(procedure_opcs_code),"
                    + " procedure_opcs_term = VALUES(procedure_opcs_term),"
                    + " procedure_seq_nbr = VALUES(procedure_seq_nbr),"
                    + " consultant_code = VALUES(consultant_code),"
                    + " location = VALUES(location),"
                    + " person_id = VALUES(person_id),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            ps.setString(1, stagingCds.getExchangeId());
            ps.setDate(2, new java.sql.Date(stagingCds.getDTReceived().getTime()));
            ps.setInt(3,stagingCds.getRecordChecksum());
            ps.setString(4,stagingCds.getSusRecordType());
            ps.setString(5,stagingCds.getCdsUniqueIdentifier());
            ps.setInt(6,stagingCds.getCdsUpdateType());
            ps.setString(7,stagingCds.getMrn());
            ps.setString(8,stagingCds.getNhsNumber());
            ps.setDate(9, new java.sql.Date(stagingCds.getDateOfBirth().getTime()));
            ps.setDate(10, new java.sql.Date(stagingCds.getProcedureDate().getTime()));
            ps.setString(11,stagingCds.getProcedureOpcsCode());
            ps.setString(12,stagingCds.getProcedureOpcsTerm());
            ps.setInt(13,stagingCds.getProcedureSeqNbr());
            ps.setString(14,stagingCds.getConsultantCode());
            ps.setString(15,stagingCds.getLocation());
            ps.setInt(16,stagingCds.getPersonId());
            ps.setString(17,stagingCds.getAuditJson());

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
