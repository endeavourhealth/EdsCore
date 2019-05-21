package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingCdsTailDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCdsTail;
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

public class RdbmsStagingCdsTailDal implements StagingCdsTailDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingCdsTailDal.class);

    private boolean wasAlreadySaved(UUID serviceId, StagingCdsTail obj) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum "
                        + "from procedure_cds_tail "
                        + "where cds_unique_identifier = ? "
                        + "and sus_record_type = ? "
                        + "and dt_received <= ? "
                        + "order by dt_received desc "
                        + "limit 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, obj.getCdsUniqueIdentifier());
            ps.setString(col++, obj.getSusRecordType());
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
    public void save(StagingCdsTail cdsTail, UUID serviceId) throws Exception {

        if (cdsTail == null) {
            throw new IllegalArgumentException("cds tail object is null");
        }

        cdsTail.setRecordChecksum(cdsTail.hashCode());

        //check if record already filed to avoid duplicates
        if (wasAlreadySaved(serviceId, cdsTail)) {
            // LOG.warn("procedure_cds_tail data already filed with record_checksum: "+cdsTail.hashCode());
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO procedure_cds_tail  "
                    + " (exchange_id, dt_received, record_checksum,  sus_record_type, cds_unique_identifier, " +
                    " cds_update_type, mrn, nhs_number, person_id, encounter_id, responsible_hcp_personnel_id, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?)"
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
            //        + " cds_activity_date=VALUES(cds_activity_date)";

            ps = connection.prepareStatement(sql);

            int col = 1;

            //all columns except the last one are non-null
            ps.setString(col++, cdsTail.getExchangeId());
            ps.setTimestamp(col++, new java.sql.Timestamp(cdsTail.getDtReceived().getTime()));
            ps.setInt(col++, cdsTail.getRecordChecksum());
            ps.setString(col++, cdsTail.getSusRecordType());
            ps.setString(col++, cdsTail.getCdsUniqueIdentifier());
            ps.setInt(col++, cdsTail.getCdsUpdateType());
            ps.setString(col++, cdsTail.getMrn());
            ps.setString(col++, cdsTail.getNhsNumber());
            ps.setInt(col++, cdsTail.getPersonId());
            ps.setInt(col++, cdsTail.getEncounterId());
            ps.setInt(col++, cdsTail.getResponsibleHcpPersonnelId());

            if (cdsTail.getAudit() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, cdsTail.getAudit().writeToJson());
            }
            //  ps.setDate(13,new java.sql.Date((stagingCdsTail.getCdsActivityDate().getTime())));

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
