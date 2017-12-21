package org.endeavourhealth.core.database.rdbms.publisherTransform;


import org.endeavourhealth.common.fhir.ReferenceComponents;
import org.endeavourhealth.common.fhir.ReferenceHelper;
import org.endeavourhealth.core.database.dal.publisherTransform.ResourceIdTransformDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsResourceIdMap;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.ResourceType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class RdbmsResourceIdDal implements ResourceIdTransformDalI {

    private static final Map<String, AtomicInteger> syncLocks = new HashMap<>();

    private RdbmsResourceIdMap getResourceIdMap(UUID serviceId, UUID systemId, String resourceType, String sourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();

        try {
            return getResourceIdMap(entityManager, serviceId, systemId, resourceType, sourceId);

        } finally {
            entityManager.close();
        }
    }

    private RdbmsResourceIdMap getResourceIdMap(EntityManager entityManager, UUID serviceId, UUID systemId, String resourceType, String sourceId) throws Exception {

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceIdMap c"
                    + " where c.serviceId = :service_id"
                    + " and c.systemId = :system_id"
                    + " and c.resourceType = :resource_type"
                    + " and c.sourceId = :source_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceIdMap.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString())
                    .setParameter("resource_type", resourceType)
                    .setParameter("source_id", sourceId)
                    .setMaxResults(1);

            RdbmsResourceIdMap result = (RdbmsResourceIdMap)query.getSingleResult();
            return result;

        } catch (NoResultException ex) {
            return null;

        }
    }

    private RdbmsResourceIdMap getResourceIdMapByEdsId(String resourceType, String edsId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceIdMap c"
                    + " where c.resourceType = :resource_type"
                    + " and c.edsId = :eds_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceIdMap.class)
                    .setParameter("resource_type", resourceType)
                    .setParameter("eds_id", edsId);

            RdbmsResourceIdMap result = (RdbmsResourceIdMap)query.getSingleResult();
            return result;

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }


    @Override
    public UUID findOrCreateThreadSafe(UUID serviceId, UUID systemId, String resourceType, String sourceId) throws Exception {

        //we need to sync to prevent two threads generating an ID for the same source ID at the same time
        //use an AtomicInt for each cache key as a synchronisation object and as a way to track
        String cacheKey = resourceType + "\\" + sourceId;
        AtomicInteger atomicInteger = null;
        synchronized (syncLocks) {
            atomicInteger = syncLocks.get(cacheKey);
            if (atomicInteger == null) {
                atomicInteger = new AtomicInteger(0);
                syncLocks.put(cacheKey, atomicInteger);
            }

            atomicInteger.incrementAndGet();
        }

        RdbmsResourceIdMap mapping = new RdbmsResourceIdMap();
        mapping.setServiceId(serviceId.toString());
        mapping.setSystemId(systemId.toString());
        mapping.setResourceType(resourceType);
        mapping.setSourceId(sourceId);
        mapping.setEdsId(UUID.randomUUID().toString());

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(mapping);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();

            //if we get an exception raised, it's most likely because another thread has created
            //a mapping for the same source and resource type, so we should try and retrieve it
            mapping = getResourceIdMap(entityManager, serviceId, systemId, resourceType, sourceId);
            if (mapping == null) {
                //if the mapping is still null, then something else went wrong, so throw the exception up
                throw ex;
            }

        } finally {
            entityManager.close();
        }

        //decrement our lock count and if zero remove from the map
        synchronized (syncLocks) {
            int val = atomicInteger.decrementAndGet();
            if (val == 0) {
                syncLocks.remove(cacheKey);
            }
        }

        return UUID.fromString(mapping.getEdsId());
    }

    @Override
    public Map<Reference, Reference> findEdsReferencesFromSourceReferences(UUID serviceId, UUID systemId, List<Reference> sourceReferences) throws Exception {

        Map<Reference, Reference> ret = new HashMap<>();

        if (sourceReferences.isEmpty()) {
            return ret;
        }

        //changing to use a dynamic IN query, as temporary tables are too slow in MySQL, table variables don't exist,
        //and this is the only other way I can find to do this lookup in a single transaction

        String sql = "SELECT resource_type, source_id, eds_id"
                + " FROM resource_id_map"
                + " WHERE service_id = '" + serviceId + "'"
                + " AND system_id = '" + systemId + "'"
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

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
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
    public Map<Reference, Reference> findSourceReferencesFromEdsReferences(List<Reference> edsReferences) throws Exception {

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

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
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

    /*@Override
    public Map<Reference, Reference> findEdsReferencesFromSourceReferences(UUID serviceId, UUID systemId, List<Reference> sourceReferences) throws Exception {

        Map<Reference, Reference> ret = new HashMap<>();

        if (sourceReferences.isEmpty()) {
            return ret;
        }

        //if there's only one reference, do a normal query without a table variable
        if (sourceReferences.size() == 1) {
            Reference sourceReference = sourceReferences.get(0);
            ReferenceComponents comps = ReferenceHelper.getReferenceComponents(sourceReference);
            String resourceType = comps.getResourceType().toString();
            String sourceId = comps.getId();

            RdbmsResourceIdMap mapping = getResourceIdMap(serviceId, systemId, resourceType, sourceId);
            if (mapping != null) {
                Reference edsReference = ReferenceHelper.createReference(resourceType, mapping.getEdsId());
                ret.put(sourceReference, edsReference);
            }
            return ret;
        }


        //can't find a way to do this using neat prepared statements or anything, since this is specific to MySQL syntax
        //and does updates and selects in a single statement
        String tmpTable = "`resource_lookup_tmp_" + UUID.randomUUID().toString() + "`";

        String sqlCreate = "CREATE TEMPORARY TABLE " + tmpTable + " ("
                + "service_id char(36), "
                + "system_id char(36), "
                + "resource_type varchar(50), "
                + "source_id varchar(500), "
                + "ordinal int"
                + ") ENGINE = MEMORY;";

        List<String> sqlInserts = new ArrayList<>();

        for (int i=0; i<sourceReferences.size(); i++) {
            Reference sourceReference = sourceReferences.get(i);
            ReferenceComponents comps = ReferenceHelper.getReferenceComponents(sourceReference);
            ResourceType resourceType = comps.getResourceType();
            String sourceId = comps.getId();

            sqlInserts.add("INSERT INTO " + tmpTable + " VALUES ('" + serviceId.toString() + "', '" + systemId.toString() + "', '" + resourceType.toString() + "', '" + sourceId + "', " + i + ");");
        }

        String sqlSelect = "SELECT m.resource_type, m.eds_id, t.ordinal"
                + " FROM resource_id_map m"
                + " JOIN " + tmpTable + " t"
                + " ON m.service_id = t.service_id"
                + " AND m.system_id = t.system_id"
                + " AND m.resource_type = t.resource_type"
                + " AND m.source_id = t.source_id;";

        String sqlDrop = "DROP TABLE " + tmpTable + ";";

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        Statement statement = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            statement = connection.createStatement();

            //create and populate the table
            statement.addBatch(sqlCreate);
            for (String insert: sqlInserts) {
                statement.addBatch(insert);
            }
            statement.executeBatch();

            //run our query
            ResultSet resultSet = statement.executeQuery(sqlSelect);

            while (resultSet.next()) {
                String resourceType = resultSet.getString(1);
                String edsId = resultSet.getString(2);
                int ordinal = resultSet.getInt(3);

                Reference edsReference = ReferenceHelper.createReference(resourceType, edsId);
                Reference sourceReference = sourceReferences.get(ordinal);
                ret.put(sourceReference, edsReference);
            }

            //drop the table
            statement.executeUpdate(sqlDrop);

            return ret;

        } finally {
            entityManager.close();
            if (statement != null) {
                statement.close();
            }
        }
    }

    @Override
    public Map<Reference, Reference> findSourceReferencesFromEdsReferences(List<Reference> edsReferences) throws Exception {

        Map<Reference, Reference> ret = new HashMap<>();

        //shouldn't be necessary, but can't hurt to avoid going to the DB if we don't need to
        if (edsReferences.isEmpty()) {
            return ret;
        }

        //if there's only one item in the map then just call into the lookup function that doesn't use a table variable
        if (edsReferences.size() == 1) {
            Reference edsReference = edsReferences.get(0);
            ReferenceComponents comps = ReferenceHelper.getReferenceComponents(edsReference);
            String resourceType = comps.getResourceType().toString();
            String resourceId = comps.getId();

            RdbmsResourceIdMap mapping = getResourceIdMapByEdsId(resourceType, resourceId);

            if (mapping != null) {
                Reference sourceReference = ReferenceHelper.createReference(resourceType, mapping.getSourceId());
                ret.put(edsReference, sourceReference);
            }
            return ret;
        }


        //can't find a way to do this using neat prepared statements or anything, since this is specific to MySQL syntax
        //and does updates and selects in a single statement
        String tmpTable = "`resource_lookup_tmp_" + UUID.randomUUID().toString() + "`";

        String sqlCreate = "CREATE TEMPORARY TABLE " + tmpTable + " ("
                + "resource_type varchar(50), "
                + "eds_id char(36), "
                + "ordinal int"
                + ") ENGINE = MEMORY;";

        List<String> sqlInserts = new ArrayList<>();

        for (int i=0; i<edsReferences.size(); i++) {
            Reference edsReference = edsReferences.get(i);
            ReferenceComponents comps = ReferenceHelper.getReferenceComponents(edsReference);
            ResourceType resourceType = comps.getResourceType();
            String resourceId = comps.getId();

            sqlInserts.add("INSERT INTO " + tmpTable + " VALUES ('" + resourceType.toString() + "', '" + resourceId + "', " + i + ");");
        }

        String sqlSelect = "SELECT m.resource_type, m.source_id, t.ordinal"
                + " FROM resource_id_map m"
                + " JOIN " + tmpTable + " t"
                + " ON m.resource_type = t.resource_type"
                + " AND m.eds_id = t.eds_id;";

        String sqlDrop = "DROP TABLE " + tmpTable + ";";


        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        Statement statement = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            statement = connection.createStatement();

            //create and populate the table
            statement.addBatch(sqlCreate);
            for (String insert: sqlInserts) {
                statement.addBatch(insert);
            }
            statement.executeBatch();

            //run our query
            ResultSet resultSet = statement.executeQuery(sqlSelect);

            while (resultSet.next()) {
                String resourceType = resultSet.getString(1);
                String sourceId = resultSet.getString(2);
                int ordinal = resultSet.getInt(3);

                Reference sourceReference = ReferenceHelper.createReference(resourceType, sourceId);
                Reference edsReference = edsReferences.get(ordinal);
                ret.put(edsReference, sourceReference);
            }

            //drop the table
            statement.executeUpdate(sqlDrop);

            return ret;

        } finally {
            entityManager.close();
            if (statement != null) {
                statement.close();
            }
        }
    }*/


}
