package org.endeavourhealth.core.database.rdbms.audit;

import org.endeavourhealth.core.database.dal.audit.ExchangeProtocolErrorDalI;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeProtocolError;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeProtocolError;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RdbmsExchangeProtocolErrorDal implements ExchangeProtocolErrorDalI {

    @Override
    public void save(UUID exchangeId) throws Exception {

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO exchange_protocol_error"
                    + " (exchange_id)"
                    + " VALUES (?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_id = VALUES(exchange_id),"
                    + " inserted_at = VALUES(inserted_at);";

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

    @Override
    public List<ExchangeProtocolError> getProtocolErrors() throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeProtocolError c"
                    + " order by c.insertedAt desc ";

            Query query = entityManager.createQuery(sql, RdbmsExchangeProtocolError.class);


            List<RdbmsExchangeProtocolError> results = query.getResultList();

            //can't use stream() here as the constructor can throw an exception
            List<ExchangeProtocolError> ret = new ArrayList<>();
            for (RdbmsExchangeProtocolError result: results) {
                ret.add(new ExchangeProtocolError(result));
            }
            return ret;

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void deleteExchangeProtocolError(UUID exchangeId) throws Exception {
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

            String sql = "DELETE FROM exchange_protocol_error"
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
