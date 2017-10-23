package org.endeavourhealth.core.rdbms.audit;

import org.endeavourhealth.core.rdbms.ConnectionManager;
import org.endeavourhealth.core.rdbms.admin.models.Organisation;
import org.endeavourhealth.core.rdbms.audit.models.QueuedMessage;
import org.endeavourhealth.core.rdbms.audit.models.QueuedMessageType;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.UUID;

public class QueuedMessageHelper {

    public void save(UUID messageId, String messageBody, QueuedMessageType type) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        QueuedMessage queuedMessage = new QueuedMessage();
        queuedMessage.setId(messageId.toString());
        queuedMessage.setMessageBody(messageBody);
        queuedMessage.setTimestamp(new DateTime());
        queuedMessage.setQueuedMessageTypeId(type.getValue());

        entityManager.persist(queuedMessage);
        entityManager.close();
    }

    public QueuedMessage getById(UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " QueuedMessage c"
                + " where c.id = :id";

        Query query = entityManager.createQuery(sql, QueuedMessage.class)
                .setParameter("id", id.toString());

        QueuedMessage ret = null;
        try {
            ret = (QueuedMessage)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }
}
