package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.PcrPersonUpdaterHistoryDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsPcrPersonUpdateHistory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;

public class RdbmsPcrPersonUpdaterHistoryDal implements PcrPersonUpdaterHistoryDalI {

    private String subscriberConfigName = null;

    public RdbmsPcrPersonUpdaterHistoryDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    public Date findDatePersonUpdaterLastRun() throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPcrPersonUpdateHistory c"
                    + " order by dateRun desc";

            Query query = entityManager.createQuery(sql, RdbmsPcrPersonUpdateHistory.class);
            query.setMaxResults(1);

            RdbmsPcrPersonUpdateHistory result = (RdbmsPcrPersonUpdateHistory)query.getSingleResult();
            return result.getDateRun();

        } catch (NoResultException ex) {
            return new Date(0);

        } finally {
            entityManager.close();
        }
    }

    public void updatePersonUpdaterLastRun(Date d) throws Exception {

        RdbmsPcrPersonUpdateHistory history = new RdbmsPcrPersonUpdateHistory();
        history.setDateRun(d);

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(history);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }
}
