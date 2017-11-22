package org.endeavourhealth.core.database.rdbms.publisherTransform;


import org.endeavourhealth.common.fhir.ReferenceComponents;
import org.endeavourhealth.common.fhir.ReferenceHelper;
import org.endeavourhealth.core.database.dal.publisherTransform.ResourceIdTransformDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceIdMap;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RdbmsResourceIdDal implements ResourceIdTransformDalI {

    public void insert(ResourceIdMap resourceIdMap) throws Exception {
        if (resourceIdMap == null) {
            throw new IllegalArgumentException("resourceIdMap is null");
        }

        RdbmsResourceIdMap dbObj = new RdbmsResourceIdMap(resourceIdMap);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(dbObj);
            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
        }
    }

    public ResourceIdMap getResourceIdMap(UUID serviceId, UUID systemId, String resourceType, String sourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();

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
                    .setParameter("resource_type", resourceType.toString())
                    .setParameter("source_id", sourceId.toString());

            RdbmsResourceIdMap result = (RdbmsResourceIdMap)query.getSingleResult();
            return new ResourceIdMap(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public ResourceIdMap getResourceIdMapByEdsId(String resourceType, String edsId) throws Exception {
        return getResourceIdMapByEdsId(resourceType, UUID.fromString(edsId));
    }

    public ResourceIdMap getResourceIdMapByEdsId(String resourceType, UUID edsId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceIdMap c"
                    + " where c.resourceType = :resource_type"
                    + " and c.edsId = :eds_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceIdMap.class)
                    .setParameter("resource_type", resourceType.toString())
                    .setParameter("eds_id", edsId.toString());

            RdbmsResourceIdMap result = (RdbmsResourceIdMap)query.getSingleResult();
            return new ResourceIdMap(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Reference> convertEdsToSourceReferences(List<Reference> edsReferences) throws Exception {

        //shouldn't be necessary, but can't hurt to avoid going to the DB if we don't need to
        if (edsReferences.isEmpty()) {
            return new ArrayList<>();
        }

        //if there's only one item in the map then just call into the standard lookup function that doesn't use an im-memory table
        if (edsReferences.size() == 1) {
            Reference edsReference = edsReferences.get(0);
            ReferenceComponents comps = ReferenceHelper.getReferenceComponents(edsReference);
            String resourceType = comps.getResourceType().toString();
            String resourceId = comps.getId();

            ResourceIdMap mapping = getResourceIdMapByEdsId(resourceType, resourceId);

            List<Reference> ret = new ArrayList<>();
            if (mapping != null) {
                Reference sourceReference = ReferenceHelper.createReference(resourceType, mapping.getSourceId());
                ret.add(sourceReference);
            }
            return ret;
        }


        //can't find a way to do this using neat prepared statements or anything, since this is specific to MySQL syntax
        //and does updates and selects in a single statement
        String tmpTable = "`resource_lookup_tmp_" + UUID.randomUUID().toString() + "`";

        String sqlCreate = "CREATE TABLE " + tmpTable + " ("
                + "resource_type varchar(50), "
                + "eds_id varchar(36), "
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

        String sqlSelect = "SELECT m.resource_type, m.source_id"
                + " FROM resource_id_map m"
                + " JOIN " + tmpTable + " t"
                + " ON m.resource_type = t.resource_type"
                + " AND m.eds_id = t.eds_id"
                + " ORDER BY t.ordinal ASC;"; //use this order by to maintain the same order of the eds and source references

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

            List<Reference> ret = new ArrayList<>();

            while (resultSet.next()) {
                String resourceType = resultSet.getString(1);
                String sourceId = resultSet.getString(2);

                Reference sourceReference = ReferenceHelper.createReference(resourceType, sourceId);
                ret.add(sourceReference);
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

    /*@Override
    public List<ResourceIdMap> getResourceIdMapByEdsReferences(Map<String, List<String>> resourcesByResourceType) throws Exception {

        //shouldn't be necessary, but can't hurt to avoid going to the DB if we don't need to
        if (resourcesByResourceType.isEmpty()) {
            return new ArrayList<>();
        }

        //can't find a way to do this using neat prepared statements or anything, since this is specific to MySQL syntax
        //and does updates and selects in a single statement
        String tmpTable = "resource_lookup_tmp " + UUID.randomUUID().toString();

        String sql = "CREATE TABLE " + tmpTable + "("
                + " resource_type varchar(50),"
                + " eds_id varchar(36)"
                + ") ENGINE = MEMORY;";

        int count = 0;
        for (String resourceType: resourcesByResourceType.keySet()) {
            for (String resourceId: resourcesByResourceType.get(resourceType)) {
                sql += "INSERT INTO " + tmpTable + " VALUES ('" + resourceType + "', '" + resourceId + "');";
                count ++;
            }
        }

        sql += "SELECT m.resource_type, m.source_id"
                + " FROM resource_id_map m"
                + " JOIN " + tmpTable + " t"
                + " ON m.resource_type = t.resource_type"
                + " AND m.resource_id = t.resource_id;";

        sql += "DROP TABLE " + tmpTable + ";";

        //if there's only one item in the map then just call into the standard lookup function that doesn't use an im-memory table
        if (count == 1) {
            for (String resourceType: resourcesByResourceType.keySet()) {
                for (String resourceId: resourcesByResourceType.get(resourceType)) {
                    ResourceIdMap mapping = getResourceIdMapByEdsId(resourceType, resourceId);
                    List<ResourceIdMap> ret = new ArrayList<>();
                    if (mapping != null) {
                        ret.add(mapping);
                    }
                    return ret;
                }
            }
        }


        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        Statement statement = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            List<ResourceIdMap> ret = new ArrayList<>();

            while (resultSet.next()) {
                String resourceType = resultSet.getString(1);
                String sourceId = resultSet.getString(2);

                ResourceIdMap mapping = new ResourceIdMap();
                mapping.setResourceType(resourceType);
                mapping.setSourceId(sourceId);
                ret.add(mapping);
            }

            return ret;

        } finally {
            entityManager.close();
            if (statement != null) {
                statement.close();
            }
        }
    }*/

}
