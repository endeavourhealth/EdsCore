package org.endeavourhealth.core.database.rdbms.audit;

import org.endeavourhealth.core.database.dal.audit.QueuedMessageDalI;
import org.endeavourhealth.core.database.dal.audit.models.QueuedMessageType;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsQueuedMessage;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;
import java.util.UUID;

public class RdbmsQueuedMessageDal implements QueuedMessageDalI {

    public void save(UUID messageId, String messageBody, QueuedMessageType type) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        RdbmsQueuedMessage queuedMessage = new RdbmsQueuedMessage();
        queuedMessage.setId(messageId.toString());
        queuedMessage.setMessageBody(messageBody);
        queuedMessage.setTimestamp(new Date());
        queuedMessage.setQueuedMessageTypeId(type.getValue());

        entityManager.persist(queuedMessage);
        entityManager.close();
    }

    public String getById(UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsQueuedMessage c"
                + " where c.id = :id";

        Query query = entityManager.createQuery(sql, RdbmsQueuedMessage.class)
                .setParameter("id", id.toString());

        String ret = null;
        try {
            RdbmsQueuedMessage result = (RdbmsQueuedMessage)query.getSingleResult();
            ret = result.getMessageBody();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }
}
