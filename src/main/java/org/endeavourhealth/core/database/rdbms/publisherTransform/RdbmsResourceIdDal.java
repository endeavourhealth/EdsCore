package org.endeavourhealth.core.database.rdbms.publisherTransform;


import org.endeavourhealth.common.fhir.ReferenceComponents;
import org.endeavourhealth.common.fhir.ReferenceHelper;
import org.endeavourhealth.core.database.dal.publisherTransform.ResourceIdTransformDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsResourceIdMap;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class RdbmsResourceIdDal implements ResourceIdTransformDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsResourceIdDal.class);

    private static final int MAX_RECENT_IDS_TO_CACHE = 10000;

    private static final Map<String, AtomicInteger> syncLocks = new HashMap<>();

    private static final ReentrantLock recentIdLock = new ReentrantLock();
    private static final List<String> recentIdsGenerated = new ArrayList<>();
    private static final Map<String, UUID> recentIdsGeneratedMap = new HashMap<>();



    private RdbmsResourceIdMap getResourceIdMap(EntityManager entityManager, UUID serviceId, String resourceType, String sourceId) throws Exception {

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceIdMap c"
                    + " where c.serviceId = :service_id"
                    + " and c.resourceType = :resource_type"
                    + " and c.sourceId = :source_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceIdMap.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("resource_type", resourceType)
                    .setParameter("source_id", sourceId)
                    .setMaxResults(1);

            RdbmsResourceIdMap result = (RdbmsResourceIdMap)query.getSingleResult();
            return result;

        } catch (NoResultException ex) {
            return null;

        }
    }

    @Override
    public UUID findOrCreate(UUID serviceId, String resourceType, String sourceId) throws Exception {
        return findOrCreate(serviceId, resourceType, sourceId, null);
    }

    @Override
    public UUID findOrCreate(UUID serviceId, String resourceType, String sourceId, UUID explicitDestinationUuid) throws Exception {

        String cacheKey = serviceId.toString() + "\\" + resourceType + "\\" + sourceId;
        //LOG.trace("<<<<Looking for " + cacheKey);

        //we need to sync to prevent two threads generating an ID for the same source ID at the same time
        //use an AtomicInt for each cache key as a synchronisation object and as a way to track
        AtomicInteger atomicInteger = null;
        synchronized (syncLocks) {
            atomicInteger = syncLocks.get(cacheKey);
            if (atomicInteger == null) {
                atomicInteger = new AtomicInteger(0);
                syncLocks.put(cacheKey, atomicInteger);
            }

            atomicInteger.incrementAndGet();
        }

        UUID edsId = null;

        synchronized (atomicInteger) {

            //see if we've JUST generated an ID for this source ID
            try {
                recentIdLock.lock();

                edsId = recentIdsGeneratedMap.get(cacheKey);
                if (edsId != null) {
                    //LOG.trace("    " + cacheKey + " found in recentIdsGeneratedMap map = " + edsId + " sz " + recentIdsGeneratedMap.size());
                    return edsId;
                }
            } finally {
                recentIdLock.unlock();
            }

            //if we get here, we want to generate a new ID, unless an explicit one was passed in
            edsId = explicitDestinationUuid;
            if (edsId == null) {
                edsId = UUID.randomUUID();
            }

            RdbmsResourceIdMap mapping = new RdbmsResourceIdMap();
            mapping.setServiceId(serviceId.toString());
            mapping.setResourceType(resourceType);
            mapping.setSourceId(sourceId);
            mapping.setEdsId(edsId.toString());

            EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
            //CallableStatement callableStatement = null;

            //simplest approach is to try saving the new ID and if that fails, retrieve the existing one from the DB
            try {
                entityManager.getTransaction().begin();

                entityManager.persist(mapping);

                entityManager.getTransaction().commit();

                /*if (resourceType.equals("Organization")) {
                    LOG.trace("Created org map for " + sourceId + " -> " + edsId);
                }*/
                //LOG.trace("    " + cacheKey + " successfully created ID " + edsId);

            } catch (Exception ex) {
                entityManager.getTransaction().rollback();

                //if we get an exception raised, it's most likely because another thread has created
                //a mapping for the same source and resource type, so we should try and retrieve it
                mapping = getResourceIdMap(entityManager, serviceId, resourceType, sourceId);
                if (mapping == null) {
                    //if the mapping is still null, then something else went wrong, so throw the exception up
                    throw ex;
                } else {
                    edsId = UUID.fromString(mapping.getEdsId());
                    //LOG.trace("    " + cacheKey + " failed and re-retreived ID " + edsId);
                }

            } finally {
                //saveMappingStatementCache.returnCallableStatement(entityManager, callableStatement);
                entityManager.close();
            }

            //add to our recent ID cache
            try {
                recentIdLock.lock();

                //add the new ID to the cache
                recentIdsGenerated.add(cacheKey);
                recentIdsGeneratedMap.put(cacheKey, edsId);
                //LOG.trace("    " + cacheKey + " added to cache " + edsId);

                //apply the size limit
                while (recentIdsGenerated.size() > MAX_RECENT_IDS_TO_CACHE) {
                    String key = recentIdsGenerated.remove(0);
                    recentIdsGeneratedMap.remove(key);
                }
            } finally {
                recentIdLock.unlock();
            }
        }

        //decrement our lock count and if zero remove from the map
        synchronized (syncLocks) {
            int val = atomicInteger.decrementAndGet();
            if (val == 0) {
                syncLocks.remove(cacheKey);
            }
        }

        //LOG.trace(">>>>Looking for " + cacheKey + " -> " + edsId);
        return edsId;
    }

    @Override
    public Map<Reference, Reference> findEdsReferencesFromSourceReferences(UUID serviceId, List<Reference> sourceReferences) throws Exception {

        Map<Reference, Reference> ret = new HashMap<>();

        if (sourceReferences.isEmpty()) {
            return ret;
        }

        //changing to use a dynamic IN query, as temporary tables are too slow in MySQL, table variables don't exist,
        //and this is the only other way I can find to do this lookup in a single transaction

        String sql = "SELECT resource_type, source_id, eds_id"
                + " FROM resource_id_map"
                + " WHERE service_id = '" + serviceId + "'"
                + " AND (";

        Map<String, Reference> hmTmp = new HashMap<>();

        for (int i=0; i<sourceReferences.size(); i++) {
            Reference sourceReference = sourceReferences.get(i);
            ReferenceComponents comps = ReferenceHelper.getReferenceComponents(sourceReference);
            ResourceType resourceType = comps.getResourceType();
            String sourceId = comps.getId();

            if (i>0) {
                sql += " OR ";
            }
            sql += "(resource_type = '" + resourceType + "' AND source_id = '" + sourceId + "')";

            //hash the references by their reference value so we can look them up quickly later
            hmTmp.put(sourceReference.getReference(), sourceReference);
        }

        sql += ");";

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        Statement statement = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String resourceType = resultSet.getString(1);
                String sourceId = resultSet.getString(2);
                String edsId = resultSet.getString(3);

                String sourceReferenceValue = ReferenceHelper.createResourceReference(resourceType, sourceId);
                Reference sourceReference = hmTmp.get(sourceReferenceValue);

                Reference edsReference = ReferenceHelper.createReference(resourceType, edsId);

                ret.put(sourceReference, edsReference);
            }

            return ret;

        } finally {
            if (statement != null) {
                statement.close();
            }
            entityManager.close();
        }
    }

    @Override
    public Map<Reference, Reference> findSourceReferencesFromEdsReferences(UUID serviceId, List<Reference> edsReferences) throws Exception {

        Map<Reference, Reference> ret = new HashMap<>();

        //shouldn't be necessary, but can't hurt to avoid going to the DB if we don't need to
        if (edsReferences.isEmpty()) {
            return ret;
        }

        //changing to use a dynamic IN query, as temporary tables are too slow in MySQL, table variables don't exist,
        //and this is the only other way I can find to do this lookup in a single transaction

        String sql = "SELECT resource_type, source_id, eds_id"
                + " FROM resource_id_map"
                + " WHERE (";

        Map<String, Reference> hmTmp = new HashMap<>();

        for (int i=0; i<edsReferences.size(); i++) {
            Reference sourceReference = edsReferences.get(i);
            ReferenceComponents comps = ReferenceHelper.getReferenceComponents(sourceReference);
            ResourceType resourceType = comps.getResourceType();
            String edsId = comps.getId();

            if (i>0) {
                sql += " OR ";
            }
            sql += "(resource_type = '" + resourceType + "' AND eds_id = '" + edsId + "')";

            //hash the references by their reference value so we can look them up quickly later
            hmTmp.put(sourceReference.getReference(), sourceReference);
        }

        sql += ");";

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        Statement statement = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String resourceType = resultSet.getString(1);
                String sourceId = resultSet.getString(2);
                String edsId = resultSet.getString(3);

                String edsReferenceValue = ReferenceHelper.createResourceReference(resourceType, edsId);
                Reference edsReference = hmTmp.get(edsReferenceValue);

                Reference sourceReference = ReferenceHelper.createReference(resourceType, sourceId);

                ret.put(edsReference, sourceReference);
            }

            return ret;

        } finally {
            if (statement != null) {
                statement.close();
            }
            entityManager.close();
        }
    }



}
