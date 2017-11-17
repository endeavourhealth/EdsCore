package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.ExchangeBatchExtraResourceDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsExchangeBatchExtraResources;
import org.hl7.fhir.instance.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(dbObj);
            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
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
