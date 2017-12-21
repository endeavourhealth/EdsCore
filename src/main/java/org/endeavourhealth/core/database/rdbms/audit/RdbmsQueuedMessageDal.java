package org.endeavourhealth.core.database.rdbms.audit;

import org.endeavourhealth.core.database.dal.audit.QueuedMessageDalI;
import org.endeavourhealth.core.database.dal.audit.models.QueuedMessageType;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsQueuedMessage;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.UUID;

public class RdbmsQueuedMessageDal implements QueuedMessageDalI {

    public void save(UUID messageId, String messageBody, QueuedMessageType type) throws Exception {

        RdbmsQueuedMessage queuedMessage = new RdbmsQueuedMessage();
        queuedMessage.setId(messageId.toString());
        queuedMessage.setMessageBody(messageBody);
        queuedMessage.setTimestamp(new Date());
        queuedMessage.setQueuedMessageTypeId(type.getValue());

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //entityManager.persist(queuedMessage);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO queued_message"
                    + " (id, message_body, timestamp, queued_message_type_id)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " message_body = VALUES(message_body),"
                    + " timestamp = VALUES(timestamp),"
                    + " queued_message_type_id = VALUES(queued_message_type_id);";


            ps = connection.prepareStatement(sql);

            ps.setString(1, queuedMessage.getId());
            ps.setString(2, queuedMessage.getMessageBody());
            ps.setTimestamp(3, new java.sql.Timestamp(queuedMessage.getTimestamp().getTime()));
            ps.setInt(4, new Integer(queuedMessage.getQueuedMessageTypeId()));

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
            if (ps != null) {
                ps.close();
            }
        }
    }

    public String getById(UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsQueuedMessage c"
                    + " where c.id = :id";

            Query query = entityManager.createQuery(sql, RdbmsQueuedMessage.class)
                    .setParameter("id", id.toString());

            RdbmsQueuedMessage result = (RdbmsQueuedMessage)query.getSingleResult();
            return result.getMessageBody();

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }
}
