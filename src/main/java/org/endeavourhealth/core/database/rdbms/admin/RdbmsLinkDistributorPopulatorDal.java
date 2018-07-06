package org.endeavourhealth.core.database.rdbms.admin;

import org.endeavourhealth.core.database.dal.admin.LinkDistributorPopulatorDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class RdbmsLinkDistributorPopulatorDal implements LinkDistributorPopulatorDalI {

    @Override
    public void populate() throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        PreparedStatement ps = null;
        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = null;

            sql = "INSERT INTO admin.link_distributor_populator"
                    + " select patient_id, nhs_number, date_of_birth, 0 " +
                    " from eds.patient_search;";


            ps = connection.prepareStatement(sql);

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

    @Override
    public long countDone() throws Exception {
        return countBasedOnStatus((byte)1);
    }

    @Override
    public long countToDo() throws Exception {
       return countBasedOnStatus((byte)0);
    }

    private long countBasedOnStatus(byte status) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {

            String sql = "select count(p)"
                    + " from"
                    + " RdbmsLinkDistributorPopulator p"
                    + " where p.done = :status";

            Query query = entityManager.createQuery(sql)
                    .setParameter("status", status);

            return (long)query.getSingleResult();

        } catch (NoResultException ex) {
            return 0;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void clearDown() throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        PreparedStatement ps = null;
        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = null;

            sql = "TRUNCATE TABLE admin.link_distributor_populator;";

            ps = connection.prepareStatement(sql);

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

    @Override
    public void updateDoneFlag(List<String> patients) throws Exception {
        EntityManager adminEntityManager = ConnectionManager.getAdminEntityManager();
        SessionImpl adminSession = (SessionImpl) adminEntityManager.getDelegate();
        Connection adminConnection = adminSession.connection();
        adminConnection.setAutoCommit(true);

        String updateSQL = "UPDATE link_distributor_populator  " +
            " SET done = 1" +
            " WHERE patient_id = ? ;";

        PreparedStatement update = adminConnection.prepareStatement(updateSQL);

        for (String pat : patients) {
            update.setString(1, pat);
            update.addBatch();
        }

        try {
            update.executeBatch();
            update.clearBatch();
        } catch (Exception ex) {
            throw ex;
        } finally {
            adminEntityManager.close();
        }
    }
}
