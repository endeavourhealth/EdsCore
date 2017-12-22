package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.ExchangeBatchExtraResourceDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsExchangeBatchExtraResources;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

public class RdbmsExchangeBatchExtraResourcesDal implements ExchangeBatchExtraResourceDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsExchangeBatchExtraResourcesDal.class);

    private String subscriberConfigName = null;

    public RdbmsExchangeBatchExtraResourcesDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    public void saveExtraResource(UUID exchangeId, UUID batchId, ResourceType resourceType, UUID resourceId) throws Exception {

        RdbmsExchangeBatchExtraResources dbObj = new RdbmsExchangeBatchExtraResources();
        dbObj.setExchangeId(exchangeId.toString());
        dbObj.setBatchId(batchId.toString());
        dbObj.setResourceType(resourceType.toString());
        dbObj.setResourceId(resourceId.toString());

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //changing to use prepared statement syntax so multiple threads saving the same extra resource don't cause an exception
            //entityManager.persist(dbObj);

            //MySQL "insert ignore" syntax means that if the insert fails due to a duplicate key then no error is thrown
            String sql = "INSERT IGNORE INTO exchange_batch_extra_resources"
                    + " (exchange_id, batch_id, resource_id, resource_type)"
                    + " VALUES (?, ?, ?, ?);";

            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();
            ps = connection.prepareStatement(sql);

            ps.setString(1, dbObj.getExchangeId());
            ps.setString(2, dbObj.getBatchId());
            ps.setString(3, dbObj.getResourceId());
            ps.setString(4, dbObj.getResourceType());

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
            if (ps != null) {
                ps.close();
            }
        }
    }

    public Map<ResourceType, List<UUID>> findExtraResources(UUID exchangeId, UUID batchId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeBatchExtraResources c"
                    + " where c.exchangeId = :exchange_id"
                    + " and c.batchId = :batch_id";

            Query query = entityManager.createQuery(sql, RdbmsExchangeBatchExtraResources.class)
                    .setParameter("exchange_id", exchangeId.toString())
                    .setParameter("batch_id", batchId.toString());

            List<RdbmsExchangeBatchExtraResources> results = query.getResultList();

            Map<ResourceType, List<UUID>> ret = new HashMap<>();
            for (RdbmsExchangeBatchExtraResources result: results) {
                ResourceType resourceType = ResourceType.valueOf(result.getResourceType());
                String resourceId = result.getResourceId();

                List<UUID> list = ret.get(resourceType);
                if (list == null) {
                    list = new ArrayList<>();
                    ret.put(resourceType, list);
                }
                list.add(UUID.fromString(resourceId));
            }

            return ret;

        } finally {
            entityManager.close();
        }
    }
}
