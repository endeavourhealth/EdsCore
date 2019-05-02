package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.common.fhir.ReferenceComponents;
import org.endeavourhealth.common.fhir.ReferenceHelper;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.database.dal.subscriberTransform.SubscriberResourceMappingDalI;
import org.endeavourhealth.core.database.dal.subscriberTransform.models.SubscriberId;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsEnterpriseIdMap;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.Reference;
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
    public SubscriberId findSubscriberId(String resourceType, String resourceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            return findSubscriberIdImpl(resourceType, resourceId, entityManager);

        } finally {
            entityManager.close();
        }
    }

    private SubscriberId findSubscriberIdImpl(String resourceType, String resourceId, EntityManager entityManager) throws Exception {
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT subscriber_id, dt_updated_previously_sent "
                    + "FROM subscriber_id_map "
                    + "WHERE resource_type = ? "
                    + "AND resource_id = ?";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, resourceType);
            ps.setString(col++, resourceId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                col = 1;
                long id = rs.getLong(col++);
                Date dt = rs.getDate(col++);
                return new SubscriberId(id, dt);

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
    public Map<ResourceWrapper, SubscriberId> findSubscriberIds(List<ResourceWrapper> resources) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        try {
            Map<ResourceWrapper, SubscriberId> ret = new HashMap<>();
            findSubscriberIdsImpl(resources, ret, entityManager);
            return ret;

        } finally {
            entityManager.close();
        }
    }

    private void findSubscriberIdsImpl(List<ResourceWrapper> resources, Map<ResourceWrapper, SubscriberId> map, EntityManager entityManager) throws Exception {

        //hash our resources by ID and validate they're all of the same type
        String resourceType = null;
        List<String> resourceIds = new ArrayList<>();
        Map<String, ResourceWrapper> hmResourcesById = new HashMap<>();

        for (ResourceWrapper resource: resources) {

            if (resourceType == null) {
                resourceType = resource.getResourceType();
            } else if (!resourceType.equals(resource.getResourceType())) {
                throw new Exception("Can't find subscriber IDs for different resource types");
            }

            String id = resource.getResourceId().toString();
            resourceIds.add(id);
            hmResourcesById.put(id, resource);
        }

        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT resource_id, subscriber_id, dt_updated_previously_sent "
                    + "FROM subscriber_id_map "
                    + "HHERE resource_type = ? "
                    + "AND resource_id IN (";
            for (int i=0; i<resourceIds.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, resourceType);
            for (String resourceId: resourceIds) {
                ps.setString(col++, resourceId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                col = 1;
                String resourceId = rs.getString(col++);
                long subscriberId = rs.getLong(col++);
                Date dtUpdated = new Date(rs.getTimestamp(col++).getTime());

                SubscriberId o = new SubscriberId(subscriberId, dtUpdated);
                ResourceWrapper wrapper = hmResourcesById.get(resourceId);

                map.put(wrapper, o);
            }

            rs.close();

        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    @Override
    public SubscriberId findOrCreateSubscriberId(String resourceType, String resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            SubscriberId ret = findSubscriberIdImpl(resourceType, resourceId, entityManager);
            if (ret != null) {
                return ret;
            }

            return createSubscriberIdImpl(resourceType, resourceId, entityManager);

        } catch (Exception ex) {
            //if another thread has beat us to it, we'll get an exception, so try the find again
            SubscriberId ret = findSubscriberIdImpl(resourceType, resourceId, entityManager);
            if (ret != null) {
                return ret;
            }

            throw ex;
        } finally {
            entityManager.close();
        }
    }

    private SubscriberId createSubscriberIdImpl(String resourceType, String resourceId, EntityManager entityManager) throws Exception {
        PreparedStatement psInsert = null;
        PreparedStatement psLastId = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO subscriber_id_map (resource_type, resource_id)"
                    + " VALUES (?, ?)";
            psInsert = connection.prepareStatement(sql);

            sql = "SELECT LAST_INSERT_ID()";
            psLastId = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            int col = 1;
            psInsert.setString(col++, resourceType);
            psInsert.setString(col++, resourceId);

            psInsert.executeUpdate();

            entityManager.getTransaction().commit();

            //if the above worked, we can call another query to return us the ID just generated by the auto increment column
            ResultSet rs = psLastId.executeQuery();
            rs.next();
            long lastId = rs.getLong(1);
            rs.close();

            return new SubscriberId(lastId, null);

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
    public Map<ResourceWrapper, SubscriberId> findOrCreateSubscriberIds(List<ResourceWrapper> resources) throws Exception {

        Map<ResourceWrapper, SubscriberId> ret = new HashMap<>();

        List<ResourceWrapper> resourcesToCreate = null;

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        PreparedStatement psInsert = null;
        PreparedStatement psLastId = null;

        try {
            //check the DB for existing IDs
            findSubscriberIdsImpl(resources, ret, entityManager);

            //find the resources that didn't have an ID found above
            resourcesToCreate = new ArrayList<>();
            for (ResourceWrapper resource: resources) {
                if (!ret.containsKey(resource)) {
                    resourcesToCreate.add(resource);
                }
            }


            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO subscriber_id_map (resource_type, resource_id)"
                    + " VALUES (?, ?)";
            psInsert = connection.prepareStatement(sql);

            sql = "SELECT LAST_INSERT_ID()";
            psLastId = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (ResourceWrapper resource: resourcesToCreate) {

                int col = 1;
                psInsert.setString(col++, resource.getResourceType());
                psInsert.setString(col++, resource.getResourceId().toString());

                psInsert.addBatch();
            }

            psInsert.executeBatch();

            entityManager.getTransaction().commit();

            //if the above worked, we can call another query to return us the ID just generated by the auto increment column
            ResultSet rs = psLastId.executeQuery();
            rs.next();
            long lastId = rs.getLong(1);
            rs.close();

            //TODO - work out if batched inserts is ON or OFF

            //if the connection property "rewriteBatchedStatements=true" is specified then SELECT LAST_INSERT_ID()
            //will return the FIRST auto assigned ID for the batch. If that property is false or absent, then
            //SELECT LAST_INSERT_ID() will return the ID of the LAST assigned ID (because it sends the transactions one by one)
            String connectionUrl = connection.getMetaData().getURL().toLowerCase();
            boolean rewriteBatchedInsertedEnabled = connectionUrl.contains("rewriteBatchedStatements=true");
            if (rewriteBatchedInsertedEnabled) {
                for (ResourceWrapper resource: resourcesToCreate) {
                    SubscriberId o = new SubscriberId(lastId++, null);
                    ret.put(resource, o);
                }

            } else {
                for (int i=resourcesToCreate.size()-1; i>=0; i--) {
                    ResourceWrapper resource = resourcesToCreate.get(i);
                    SubscriberId o = new SubscriberId(lastId--, null);
                    ret.put(resource, o);
                }
            }


        } catch (Exception ex) {
            entityManager.getTransaction().rollback();

            //if another thread has beat us to it and created an ID for one of our records and we'll get an exception, so try the find again
            //but for each one individually
            LOG.warn("Failed to create " + resourcesToCreate.size() + " IDs in one go, so doing one by one");

            for (ResourceWrapper resource: resourcesToCreate) {
                SubscriberId subscriberId = findOrCreateSubscriberId(resource.getResourceType(), resource.getResourceId().toString());
                ret.put(resource, subscriberId);
            }

        } finally {
            entityManager.close();
        }

        return ret;
    }

    @Override
    public void updateDtUpdatedForSubscriber(Map<String, SubscriberId> hmResourceReferences) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "UPDATE subscriber_id_map "
                    + "SET dt_updated_previously_sent = ? "
                    + "WHERE resource_type = ? "
                    + "AND resource_id = ?";
            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (String referenceStr: hmResourceReferences.keySet()) {
                SubscriberId subscriberId = hmResourceReferences.get(referenceStr);


                int col = 1;
                if (subscriberId.getDtUpdatedPreviouslySent() == null) {
                    ps.setNull(col++, Types.TIMESTAMP);
                } else {
                    ps.setTimestamp(col++, new java.sql.Timestamp(subscriberId.getDtUpdatedPreviouslySent().getTime()));
                }

                Reference reference = ReferenceHelper.createReference(referenceStr);
                ReferenceComponents comps = ReferenceHelper.getReferenceComponents(reference);

                ps.setString(col++, comps.getResourceType().toString());
                ps.setString(col++, comps.getId());

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
