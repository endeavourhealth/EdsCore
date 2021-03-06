package org.endeavourhealth.core.database.rdbms.audit;

import org.endeavourhealth.core.database.dal.audit.SubscriberApiAuditDalI;
import org.endeavourhealth.core.database.dal.audit.models.SubscriberApiAudit;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

public class RdbmsSubscriberApiAuditDal implements SubscriberApiAuditDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsSubscriberApiAuditDal.class);

    @Override
    public void saveSubscriberApiAudit(SubscriberApiAudit audit) throws Exception {

        //if the duration hasn't been set, calculate it now
        if (audit.getDurationMs() == null) {
            long duration = java.lang.System.currentTimeMillis() - audit.getTimestmp().getTime();
            audit.setDurationMs(new Long(duration));
        }

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO subscriber_api_audit"
                    + " (timestmp, user_uuid, remote_address, request_path, request_headers, response_code, response_body, duration_ms)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setTimestamp(col++, new java.sql.Timestamp(audit.getTimestmp().getTime()));
            ps.setString(col++, audit.getUserUuid().toString());
            ps.setString(col++, audit.getRemoteAddress());
            ps.setString(col++, audit.getRequestPath());
            ps.setString(col++, audit.getRequestHeaders());
            if (audit.getResponseCode() == null){
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, audit.getResponseCode().intValue());
            }
            ps.setString(col++, audit.getResponseBody());
            ps.setLong(col++, audit.getDurationMs());

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            LOG.error("Error saving " + ps);
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
