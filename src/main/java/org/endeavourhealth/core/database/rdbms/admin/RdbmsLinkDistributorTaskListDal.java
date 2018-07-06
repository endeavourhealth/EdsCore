package org.endeavourhealth.core.database.rdbms.admin;

import org.endeavourhealth.core.database.dal.admin.LinkDistributorTaskListDalI;
import org.endeavourhealth.core.database.dal.admin.models.LinkDistributorTaskList;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsLinkDistributorTaskList;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class RdbmsLinkDistributorTaskListDal implements LinkDistributorTaskListDalI {

    @Override
    public void insertTask(String configName) throws Exception {
        RdbmsLinkDistributorTaskList task = new RdbmsLinkDistributorTaskList();
        task.setConfigName(configName);
        task.setProcessStatus((byte)0);

        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(task);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }

    }

    @Override
    public boolean safeToRun() throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {

            String sql = "select count(t)"
                    + " from"
                    + " RdbmsLinkDistributorTaskList t"
                    + " where t.processStatus in (1, 3) ";

            Query query = entityManager.createQuery(sql);

            long count = (long)query.getSingleResult();
            return count < 1;

        } catch (NoResultException ex) {
            return false;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<LinkDistributorTaskList> getTaskList() throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select t"
                    + " from"
                    + " RdbmsLinkDistributorTaskList t";

            Query query = entityManager.createQuery(sql, RdbmsLinkDistributorTaskList.class);

            List<RdbmsLinkDistributorTaskList> results = query.getResultList();

            //can't use stream as the constructor throws an exception
            List<LinkDistributorTaskList> ret = new ArrayList<>();
            for (RdbmsLinkDistributorTaskList result : results) {
                ret.add(new LinkDistributorTaskList(result));
            }

            return ret;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public LinkDistributorTaskList getNextTaskToProcess() throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select t"
                    + " from"
                    + " RdbmsLinkDistributorTaskList t " +
                    " where t.processStatus = 0";

            Query query = entityManager.createQuery(sql, RdbmsLinkDistributorTaskList.class);

            List<RdbmsLinkDistributorTaskList> results = query.getResultList();

            List<LinkDistributorTaskList> ret = new ArrayList<>();
            for (RdbmsLinkDistributorTaskList result : results) {
                // just return the first result
                return new LinkDistributorTaskList(result);
            }

            return null;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void updateTaskStatus(String configName, byte status) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        entityManager.getTransaction().begin();

        try {
            String sql = "update RdbmsLinkDistributorTaskList"
                    + " set processStatus = :status "
                    + " where configName = :configName";

            Query query = entityManager.createQuery(sql)
                    .setParameter("status", status)
                    .setParameter("configName", configName);

            query.executeUpdate();

            entityManager.getTransaction().commit();


        } finally {
            entityManager.close();
        }
    }
}
