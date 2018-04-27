package org.endeavourhealth.core.database.rdbms.audit;

import org.endeavourhealth.core.database.dal.audit.ExchangeGeneralErrorDalI;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeGeneralError;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeGeneralError;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RdbmsExchangeGeneralErrorDal implements ExchangeGeneralErrorDalI {

    @Override
    public void save(UUID exchangeId, String errorMessage) throws Exception {

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO exchange_general_error"
                    + " (exchange_id, error_message)"
                    + " VALUES (?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_id = VALUES(exchange_id),"
                    + " inserted_at = VALUES(inserted_at),"
                    + " error_message = VALUES(error_message);";

            ps = connection.prepareStatement(sql);

            ps.setString(1, exchangeId.toString());
            ps.setString(2, errorMessage);

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
    public List<ExchangeGeneralError> getGeneralErrors() throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeGeneralError c"
                    + " order by c.insertedAt desc ";

            Query query = entityManager.createQuery(sql, RdbmsExchangeGeneralError.class);


            List<RdbmsExchangeGeneralError> results = query.getResultList();

            //can't use stream() here as the constructor can throw an exception
            List<ExchangeGeneralError> ret = new ArrayList<>();
            for (RdbmsExchangeGeneralError result: results) {
                ret.add(new ExchangeGeneralError(result));
            }
            return ret;

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void deleteExchangeGeneralError(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        /*RdbmsQueuedMessage dbObj = new RdbmsQueuedMessage();
        dbObj.setId(id.toString());*/

        PreparedStatement ps = null;
        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support deletes without retrieving first
            //entityManager.remove(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "DELETE FROM exchange_general_error"
                    + " WHERE id = ?;";

            ps = connection.prepareStatement(sql);

            ps.setString(1, exchangeId.toString());

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
