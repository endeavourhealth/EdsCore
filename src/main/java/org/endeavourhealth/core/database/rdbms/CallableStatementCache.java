package org.endeavourhealth.core.database.rdbms;

import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CallableStatementCache {

    private Map<EntityManager, ConcurrentLinkedDeque<CallableStatement>> cache = new ConcurrentHashMap<>();
    private String sql = null;

    public CallableStatementCache(String sql) {
        this.sql = sql;
    }

    public CallableStatement getCallableStatement(EntityManager entityManager) throws Exception {
        ConcurrentLinkedDeque<CallableStatement> queue = findOrCreateQueue(entityManager);

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
    }
}
