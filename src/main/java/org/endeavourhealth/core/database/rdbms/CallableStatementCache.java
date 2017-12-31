package org.endeavourhealth.core.database.rdbms;

import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class CallableStatementCache {
    private static final Logger LOG = LoggerFactory.getLogger(CallableStatementCache.class);

    //private Map<EntityManager, ConcurrentLinkedDeque<CallableStatement>> cache = new ConcurrentHashMap<>();
    private String sql = null;

    public CallableStatementCache(String sql) {
        this.sql = sql;
    }

    /**
     * NOTE: all caching has been taken out of this class, as the MySQL Java driver supports caching
     * which is now enabled in the persistence.xml file
     */
    public CallableStatement getCallableStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl) entityManager.getDelegate();
        Connection connection = session.connection();
        return connection.prepareCall(sql);
    }

    public void returnCallableStatement(EntityManager entityManager, CallableStatement callableStatement) {
        //just close the statement
        try {
            callableStatement.close();
        } catch (SQLException ex) {
            LOG.error("Error closing prepared statement " + sql, ex);
        }
    }

    /*public CallableStatement getCallableStatement(EntityManager entityManager) throws Exception {
        ConcurrentLinkedDeque<CallableStatement> queue = findOrCreateQueue(entityManager);
        LOG.info("Queue for " + sql + " now " + queue.size());

        CallableStatement ret = null;

        try {
            ret = queue.pop();

        } catch (NoSuchElementException nse) {

            //if there wasn't one in the cache, create a new one
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            ret = connection.prepareCall(sql);
        }

        return ret;
    }

    public void returnCallableStatement(EntityManager entityManager, CallableStatement callableStatement) {

        if (callableStatement == null) {
            return;
        }

        ConcurrentLinkedDeque<CallableStatement> queue = findOrCreateQueue(entityManager);
        queue.push(callableStatement);
        LOG.info("Queue for " + sql + " now " + queue.size());
    }

    private ConcurrentLinkedDeque<CallableStatement> findOrCreateQueue(EntityManager entityManager) {
        ConcurrentLinkedDeque<CallableStatement> queue = cache.get(entityManager);
        if (queue == null) {
            synchronized (cache) {
                //repeat the check once inside the sync block
                queue = cache.get(entityManager);
                if (queue == null) {
                    queue = new ConcurrentLinkedDeque<>();
                    cache.put(entityManager, queue);
                }
            }
        }
        return queue;
    }*/
}
