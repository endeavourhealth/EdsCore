package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingCdsTailDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCdsTail;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingCdsTail;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class RdbmsStagingCdsTailDal implements StagingCdsTailDalI {

    @Override
    public void save(StagingCdsTail cdsTail, UUID serviceId) throws Exception {

        if (cdsTail == null) {
            throw new IllegalArgumentException("cds tail object is null");
        }

        RdbmsStagingCdsTail stagingCdsTail = new RdbmsStagingCdsTail(cdsTail);

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO staging_cds_tail  "
                    + " (exchange_id, dt_received, record_checksum, sus_record_type, cds_unique_identifier, " +
                    " cds_update_type, mrn, nhs_number, person_id, encounter_id, responsible_hcp_personnel_id, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_id = VALUES(exchange_id),"
                    + " dt_received = VALUES(dt_received),"
                    + " record_checksum = VALUES(record_checksum),"
                    + " sus_record_type = VALUES(sus_record_type),"
                    + " cds_unique_identifier = VALUES(cds_unique_identifier),"
                    + " cds_update_type = VALUES(cds_update_type),"
                    + " mrn = VALUES(mrn),"
                    + " nhs_number = VALUES(nhs_number),"
                    + " person_id = VALUES(person_id),"
                    + " encounter_id = VALUES(encounter_id),"
                    + " responsible_hcp_personnel_id = VALUES(responsible_hcp_personnel_id),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            ps.setString(1, stagingCdsTail.getExchangeId());
            ps.setDate(2, new java.sql.Date(stagingCdsTail.getDTReceived().getTime()));
            ps.setInt(3,stagingCdsTail.getRecordChecksum());
            ps.setString(4,stagingCdsTail.getSusRecordType());
            ps.setString(5,stagingCdsTail.getCdsUniqueIdentifier());
            ps.setInt(6,stagingCdsTail.getCdsUpdateType());
            ps.setString(7,stagingCdsTail.getMrn());
            ps.setString(8,stagingCdsTail.getNhsNumber());
            ps.setInt(9,stagingCdsTail.getPersonId());
            ps.setInt(10,stagingCdsTail.getEncounterId());
            ps.setInt(11, stagingCdsTail.getResponsibleHcpPersonnelId());
            ps.setString(12,stagingCdsTail.getAuditJson());

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
