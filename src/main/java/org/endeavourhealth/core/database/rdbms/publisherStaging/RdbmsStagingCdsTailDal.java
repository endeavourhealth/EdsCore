package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingCdsTailDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCdsTail;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingCdsTail;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class RdbmsStagingCdsTailDal implements StagingCdsTailDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingCdsTailDal.class);

    @Override
    public boolean getRecordChecksumFiled(UUID serviceId, StagingCdsTail cdsTail) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsStagingCdsTail c"
                    + " where c.cdsUniqueIdentifier = :cds_unique_identifier"
                    + " order by c.dtReceived desc";

            Query query = entityManager.createQuery(sql, RdbmsStagingCdsTail.class)
                    .setParameter("cds_unique_identifier", cdsTail.getCdsUniqueIdentifier())
                    .setMaxResults(1);

            try {
                RdbmsStagingCdsTail result = (RdbmsStagingCdsTail) query.getSingleResult();
                return result.getRecordChecksum() == cdsTail.getRecordChecksum();
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
    public void save(StagingCdsTail cdsTail, UUID serviceId) throws Exception {

        if (cdsTail == null) {
            throw new IllegalArgumentException("cds tail object is null");
        }

        //check if record already filed to avoid duplicates
        if (getRecordChecksumFiled(serviceId, cdsTail)) {
            LOG.warn("procedure_cds_tail data already filed with record_checksum: "+cdsTail.hashCode());
            return;
        }

        RdbmsStagingCdsTail stagingCdsTail = new RdbmsStagingCdsTail(cdsTail);

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO procedure_cds_tail  "
                    + " (exchange_id, dt_received, record_checksum, cds_acvitity_type, sus_record_type, cds_unique_identifier, " +
                    " cds_update_type, mrn, nhs_number, person_id, encounter_id, responsible_hcp_personnel_id, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
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
                    + " audit_json = VALUES(audit_json),"
                    + " cds_acvitity_type=VALUES(cds_acvitity_type)";

            ps = connection.prepareStatement(sql);

            ps.setString(1, stagingCdsTail.getExchangeId());
            ps.setDate(2, new java.sql.Date(stagingCdsTail.getDtReceived().getTime()));
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
            ps.setDate(13,new java.sql.Date((stagingCdsTail.getCdsActivityDate().getTime())));

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
