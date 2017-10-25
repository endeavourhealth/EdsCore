package org.endeavourhealth.core.database.rdbms.subscriber;

import org.endeavourhealth.core.database.dal.subscriber.EnterprisePersonUpdaterHistoryDalI;
import org.endeavourhealth.core.database.rdbms.subscriber.models.RdbmsEnterprisePersonUpdateHistory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;

public class RdbmsEnterprisePersonUpdaterHistoryDal implements EnterprisePersonUpdaterHistoryDalI {

    private String subscriberConfigName = null;

    public RdbmsEnterprisePersonUpdaterHistoryDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    public Date findDatePersonUpdaterLastRun() throws Exception {

        EntityManager entityManager = SubscriberConnectionMananger.getEntityManager(subscriberConfigName);

        String sql = "select c"
                + " from"
                + " RdbmsEnterprisePersonUpdateHistory c"
                + " order by dateRun desc";

        Query query = entityManager.createQuery(sql, RdbmsEnterprisePersonUpdateHistory.class);
        query.setMaxResults(1);

        try {
            RdbmsEnterprisePersonUpdateHistory result = (RdbmsEnterprisePersonUpdateHistory)query.getSingleResult();
            return result.getDateRun();

        } catch (NoResultException ex) {
            return new Date(0);

        } finally {
            entityManager.close();
        }
    }

    public void updatePersonUpdaterLastRun(Date d) throws Exception {

        RdbmsEnterprisePersonUpdateHistory history = new RdbmsEnterprisePersonUpdateHistory();
        history.setDateRun(d);

        EntityManager entityManager = SubscriberConnectionMananger.getEntityManager(subscriberConfigName);

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(history);
            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
        }
    }
}
