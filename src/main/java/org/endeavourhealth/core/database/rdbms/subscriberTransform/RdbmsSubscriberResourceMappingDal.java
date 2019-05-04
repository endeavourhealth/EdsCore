package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.database.dal.subscriberTransform.SubscriberResourceMappingDalI;
import org.endeavourhealth.core.database.dal.subscriberTransform.models.SubscriberId;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsEnterpriseIdMap;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RdbmsSubscriberResourceMappingDal implements SubscriberResourceMappingDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsSubscriberResourceMappingDal.class);

    private String subscriberConfigName = null;

    public RdbmsSubscriberResourceMappingDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    @Override
    public Long findEnterpriseIdOldWay(String resourceType, String resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        try {
            return findEnterpriseIdOldWay(resourceType, resourceId, entityManager);

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void findEnterpriseIdsOldWay(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            findEnterpriseIdsOldWay(resources, ids, entityManager);

        } finally {
            entityManager.close();
        }
    }

    @Override
    public Long findOrCreateEnterpriseIdOldWay(String resourceType, String resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            Long ret = findEnterpriseIdOldWay(resourceType, resourceId, entityManager);
            if (ret != null) {
                return ret;
            }

            return createEnterpriseIdOldWay(resourceType, resourceId, entityManager);

        } catch (Exception ex) {
            //if another thread has beat us to it, we'll get an exception, so try the find again
            Long ret = findEnterpriseIdOldWay(resourceType, resourceId, entityManager);
            if (ret != null) {
                return ret;
            }

            throw ex;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void findOrCreateEnterpriseIdsOldWay(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        List<ResourceWrapper> resourcesToCreate = null;
        try {
            //check the DB for existing IDs
            findEnterpriseIdsOldWay(resources, ids, entityManager);

            //find the resources that didn't have an ID
            resourcesToCreate = new ArrayList<>();
            for (ResourceWrapper resource: resources) {
                if (!ids.containsKey(resource)) {
                    resourcesToCreate.add(resource);
                }
            }

            //for any resource without an ID, we want to create one
            entityManager.getTransaction().begin();

            Map<ResourceWrapper, RdbmsEnterpriseIdMap> mappingMap = new HashMap<>();

            for (ResourceWrapper resource: resourcesToCreate) {

                RdbmsEnterpriseIdMap mapping = new RdbmsEnterpriseIdMap();
                mapping.setResourceType(resource.getResourceType());
                mapping.setResourceId(resource.getResourceId().toString());

                entityManager.persist(mapping);

                mappingMap.put(resource, mapping);
            }

            entityManager.getTransaction().commit();

            for (ResourceWrapper resource: resourcesToCreate) {

                RdbmsEnterpriseIdMap mapping = mappingMap.get(resource);
                Long enterpriseId = mapping.getEnterpriseId();
                ids.put(resource, enterpriseId);
            }

        } catch (Exception ex) {
            //if another thread has beat us to it and created an ID for one of our records and we'll get an exception, so try the find again
            //but for each one individually
            entityManager.getTransaction().rollback();
            LOG.warn("Failed to create " + resourcesToCreate.size() + " IDs in one go, so doing one by one");

            for (ResourceWrapper resource: resourcesToCreate) {
                Long enterpriseId = findOrCreateEnterpriseIdOldWay(resource.getResourceType(), resource.getResourceId().toString());
                ids.put(resource, enterpriseId);
            }

        } finally {
            entityManager.close();
        }
    }

    @Override
    public SubscriberId findSubscriberId(byte subscriberTable, String sourceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            return findSubscriberIdImpl(subscriberTable, sourceId, entityManager);

        } finally {
            entityManager.close();
        }
    }



    /*
    subscriber_table tinyint NOT NULL COMMENT 'ID of the target table this ID is for',
  subscriber_id bigint NOT NULL COMMENT 'unique ID allocated for the subscriber DB',
  source_id varchar(250) NOT NULL COMMENT 'Source ID (e.g. FHIR reference) that this ID is mapped from',
  dt_previously_sent datetime NOT NULL COMMENT 'the date time of the previously sent version of this resource (or null if deleted)',
     */

    private SubscriberId findSubscriberIdImpl(byte subscriberTable, String sourceId, EntityManager entityManager) throws Exception {
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT subscriber_id, dt_updated_previously_sent "
                    + "FROM subscriber_id_map "
                    + "WHERE source_id = ? "
                    + "AND subscriber_table = ?";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, sourceId);
            ps.setInt(col++, subscriberTable);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                col = 1;
                long id = rs.getLong(col++);
                Date dt = rs.getDate(col++);
                return new SubscriberId(subscriberTable, id, sourceId, dt);

            } else {
                return null;
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    @Override
    public Map<String, SubscriberId> findSubscriberIds(byte subscriberTable, List<String> sourceIds) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        try {
            Map<String, SubscriberId> ret = new HashMap<>();
            findSubscriberIdsImpl(subscriberTable, sourceIds, ret, entityManager);
            return ret;

        } finally {
            entityManager.close();
        }
    }

    private void findSubscriberIdsImpl(byte subscriberTable, List<String> sourceIds, Map<String, SubscriberId> map, EntityManager entityManager) throws Exception {

        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT source_id, subscriber_id, dt_updated_previously_sent "
                    + "FROM subscriber_id_map "
                    + "HHERE subscriber_table = ? "
                    + "AND source_id IN (";
            for (int i=0; i<sourceIds.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setInt(col++, subscriberTable);
            for (String sourceId: sourceIds) {
                ps.setString(col++, sourceId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                col = 1;
                String sourceId = rs.getString(col++);
                long subscriberId = rs.getLong(col++);
                Date dtUpdated = new Date(rs.getTimestamp(col++).getTime());

                SubscriberId o = new SubscriberId(subscriberTable, subscriberId, sourceId, dtUpdated);
                map.put(sourceId, o);
            }

            rs.close();

        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    @Override
    public SubscriberId findOrCreateSubscriberId(byte subscriberTable, String sourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            SubscriberId ret = findSubscriberIdImpl(subscriberTable, sourceId, entityManager);
            if (ret != null) {
                return ret;
            }

            return createSubscriberIdImpl(subscriberTable, sourceId, entityManager);

        } catch (Exception ex) {
            //if another thread has beat us to it, we'll get an exception, so try the find again
            SubscriberId ret = findSubscriberIdImpl(subscriberTable, sourceId, entityManager);
            if (ret != null) {
                return ret;
            }

            throw ex;
        } finally {
            entityManager.close();
        }
    }

    private SubscriberId createSubscriberIdImpl(byte subscriberTable, String sourceId, EntityManager entityManager) throws Exception {
        PreparedStatement psInsert = null;
        PreparedStatement psLastId = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO subscriber_id_map (subscriber_table, source_id)"
                    + " VALUES (?, ?)";
            psInsert = connection.prepareStatement(sql);

            sql = "SELECT LAST_INSERT_ID()";
            psLastId = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            int col = 1;
            psInsert.setInt(col++, subscriberTable);
            psInsert.setString(col++, sourceId);

            psInsert.executeUpdate();

            entityManager.getTransaction().commit();

            //if the above worked, we can call another query to return us the ID just generated by the auto increment column
            ResultSet rs = psLastId.executeQuery();
            rs.next();
            long lastId = rs.getLong(1);
            rs.close();

            return new SubscriberId(subscriberTable, lastId, sourceId, null);

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (psInsert != null) {
                psInsert.close();
            }
            if (psLastId != null) {
                psLastId.close();
            }
        }
    }

    @Override
    public Map<String, SubscriberId> findOrCreateSubscriberIds(byte subscriberTable, List<String> sourceIds) throws Exception {

        Map<String, SubscriberId> ret = new HashMap<>();

        List<String> sourceIdsToCreate = null;

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        PreparedStatement psInsert = null;
        PreparedStatement psLastId = null;

        try {
            //check the DB for existing IDs
            findSubscriberIdsImpl(subscriberTable, sourceIds, ret, entityManager);

            //find the resources that didn't have an ID found above
            sourceIdsToCreate = new ArrayList<>();
            for (String sourceId: sourceIds) {
                if (!ret.containsKey(sourceId)) {
                    sourceIdsToCreate.add(sourceId);
                }
            }

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO subscriber_id_map (subscriber_table, source_id)"
                    + " VALUES (?, ?)";
            psInsert = connection.prepareStatement(sql);

            sql = "SELECT LAST_INSERT_ID()";
            psLastId = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (String sourceId: sourceIdsToCreate) {

                int col = 1;
                psInsert.setInt(col++, subscriberTable);
                psInsert.setString(col++, sourceId);

                psInsert.addBatch();
            }

            psInsert.executeBatch();

            entityManager.getTransaction().commit();

            //if the above worked, we can call another query to return us the ID just generated by the auto increment column
            ResultSet rs = psLastId.executeQuery();
            rs.next();
            long lastId = rs.getLong(1);
            rs.close();

            //if the connection property "rewriteBatchedStatements=true" is specified then SELECT LAST_INSERT_ID()
            //will return the FIRST auto assigned ID for the batch. If that property is false or absent, then
            //SELECT LAST_INSERT_ID() will return the ID of the LAST assigned ID (because it sends the transactions one by one)
            String connectionUrl = connection.getMetaData().getURL().toLowerCase();
            boolean rewriteBatchedInsertedEnabled = connectionUrl.contains("rewriteBatchedStatements=true");
            if (rewriteBatchedInsertedEnabled) {
                for (String sourceId: sourceIdsToCreate) {
                    SubscriberId o = new SubscriberId(subscriberTable, lastId++, sourceId, null);
                    ret.put(sourceId, o);
                }

            } else {
                for (int i=sourceIdsToCreate.size()-1; i>=0; i--) {
                    String sourceId = sourceIdsToCreate.get(i);
                    SubscriberId o = new SubscriberId(subscriberTable, lastId--, sourceId, null);
                    ret.put(sourceId, o);
                }
            }


        } catch (Exception ex) {
            entityManager.getTransaction().rollback();

            //if another thread has beat us to it and created an ID for one of our records and we'll get an exception, so try the find again
            //but for each one individually
            LOG.warn("Failed to create " + sourceIdsToCreate.size() + " IDs in one go, so doing one by one");

            for (String sourceId: sourceIdsToCreate) {
                SubscriberId subscriberId = findOrCreateSubscriberId(subscriberTable, sourceId);
                ret.put(sourceId, subscriberId);
            }

        } finally {
            entityManager.close();
        }

        return ret;
    }

    @Override
    public void updateDtUpdatedForSubscriber(List<SubscriberId> subscriberIds) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "UPDATE subscriber_id_map "
                    + "SET dt_updated_previously_sent = ? "
                    + "WHERE subscriber_id = ?";
            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (SubscriberId subscriberId: subscriberIds) {

                int col = 1;
                if (subscriberId.getDtUpdatedPreviouslySent() == null) {
                    ps.setNull(col++, Types.TIMESTAMP);
                } else {
                    ps.setTimestamp(col++, new java.sql.Timestamp(subscriberId.getDtUpdatedPreviouslySent().getTime()));
                }

                ps.setLong(col++, subscriberId.getSubscriberId());

                ps.addBatch();
            }

            ps.executeBatch();

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


    private static Long findEnterpriseIdOldWay(String resourceType, String resourceId, EntityManager entityManager) throws Exception {

        String sql = "select c"
                + " from"
                + " RdbmsEnterpriseIdMap c"
                + " where c.resourceType = :resourceType"
                + " and c.resourceId = :resourceId";


        Query query = entityManager.createQuery(sql, RdbmsEnterpriseIdMap.class)
                .setParameter("resourceType", resourceType)
                .setParameter("resourceId", resourceId);

        try {
            RdbmsEnterpriseIdMap result = (RdbmsEnterpriseIdMap)query.getSingleResult();
            return result.getEnterpriseId();

        } catch (NoResultException ex) {
            return null;
        }
    }


    private static void findEnterpriseIdsOldWay(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids, EntityManager entityManager) throws Exception {

        String resourceType = null;
        List<String> resourceIds = new ArrayList<>();
        Map<String, ResourceWrapper> resourceIdMap = new HashMap<>();

        for (ResourceWrapper resource: resources) {

            if (resourceType == null) {
                resourceType = resource.getResourceType();
            } else if (!resourceType.equals(resource.getResourceType())) {
                throw new Exception("Can't find enterprise IDs for different resource types");
            }

            String id = resource.getResourceId().toString();
            resourceIds.add(id);
            resourceIdMap.put(id, resource);
        }

        String sql = "select c"
                + " from"
                + " RdbmsEnterpriseIdMap c"
                + " where c.resourceType = :resourceType"
                + " and c.resourceId IN :resourceId";


        Query query = entityManager.createQuery(sql, RdbmsEnterpriseIdMap.class)
                .setParameter("resourceType", resourceType)
                .setParameter("resourceId", resourceIds);

        List<RdbmsEnterpriseIdMap> results = query.getResultList();
        for (RdbmsEnterpriseIdMap result: results) {
            String resourceId = result.getResourceId();
            Long enterpriseId = result.getEnterpriseId();

            ResourceWrapper resource = resourceIdMap.get(resourceId);
            ids.put(resource, enterpriseId);
        }
    }


    private static Long createEnterpriseIdOldWay(String resourceType, String resourceId, EntityManager entityManager) throws Exception {

        if (resourceId == null) {
            throw new IllegalArgumentException("Null resource ID");
        }

        RdbmsEnterpriseIdMap mapping = new RdbmsEnterpriseIdMap();
        mapping.setResourceType(resourceType);
        mapping.setResourceId(resourceId);
        //mapping.setEnterpriseId(new Long(0));

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(mapping);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;
        }

        return mapping.getEnterpriseId();
    }

}
