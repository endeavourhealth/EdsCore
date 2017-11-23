package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.BartsSusResourceMapDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsBartsSusResourceMap;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.Enumerations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RdbmsBartsSusResourceMapDal implements BartsSusResourceMapDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsBartsSusResourceMapDal.class);

    @Override
    public void saveSusResourceMappings(UUID serviceId, String sourceRowId, Map<Enumerations.ResourceType, List<UUID>> resourceIds) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        PreparedStatement ps = null;

        try {

            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO sus_resource_map"
                    + " (service_id, source_row_id, destination_resource_type, destination_resource_id)"
                    + " VALUES (?, ?, ?, ?)";

            ps = connection.prepareStatement(sql);

            ps.setString(1, serviceId.toString());
            ps.setString(2, sourceRowId);

            Iterator<Enumerations.ResourceType> it = resourceIds.keySet().iterator();
            while (it.hasNext()) {
                Enumerations.ResourceType type = it.next();
                LOG.trace("saveSusResourceMappings-Key:" + type.toCode());
                ps.setString(3, type.toCode());

                List<UUID> uuidList = resourceIds.get(type);
                Iterator resourceUUIDList = uuidList.iterator();
                while (resourceUUIDList.hasNext()) {
                    ps.setString(4, ((UUID) resourceUUIDList.next()).toString());
                    ps.executeUpdate();
                }
            }

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
            if (ps != null) {
                ps.close();
            }

        }

    }

    @Override
    public void saveSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType, List<UUID> resourceIds) throws Exception {
        HashMap<Enumerations.ResourceType, List<UUID>> hm = new HashMap<Enumerations.ResourceType, List<UUID>>();
        hm.put(resourceType, resourceIds);
        saveSusResourceMappings(serviceId, sourceRowId, hm);
    }

    @Override
    public void deleteSusResourceMappings(UUID serviceId, String sourceRowId, Map<Enumerations.ResourceType, List<UUID>> resourceIds) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        PreparedStatement ps = null;

        try {

            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "DELETE FROM sus_resource_map"
                    + " WHERE service_id=?, source_row_id=?, destination_resource_type=?, destination_resource_id=?";

            ps = connection.prepareStatement(sql);

            ps.setString(1, serviceId.toString());
            ps.setString(2, sourceRowId);

            Iterator<Enumerations.ResourceType> it = resourceIds.keySet().iterator();
            while (it.hasNext()) {
                Enumerations.ResourceType type = it.next();
                ps.setString(3, type.toCode());

                List<UUID> uuidList = resourceIds.get(type.toCode());
                Iterator resourceUUIDList = uuidList.iterator();
                while (resourceUUIDList.hasNext()) {
                    ps.setString(4, ((UUID) resourceUUIDList.next()).toString());
                    ps.executeUpdate();
                }
            }

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
            if (ps != null) {
                ps.close();
            }

        }

    }

    @Override
    public void deleteSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType, List<UUID> resourceIds) throws Exception {
        HashMap hm = new HashMap();
        hm.put(resourceType, resourceIds);
        deleteSusResourceMappings(serviceId, sourceRowId, hm);
    }

    @Override
    public Map<Enumerations.ResourceType, List<UUID>> getSusResourceMappings(UUID serviceId, String sourceRowId) throws Exception {
        return null;
    }

    @Override
    public List<UUID> getSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsBartsSusResourceMap c"
                    + " where c.serviceId = :service_id and c.sourceRowId = :source_row_id and c.destinationResourceType = :destination_resource_type";

            Query query = entityManager.createQuery(sql, RdbmsBartsSusResourceMap.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("source_row_id", sourceRowId)
                    .setParameter("destination_resource_type", resourceType.toCode());

            List<RdbmsBartsSusResourceMap> results = query.getResultList();

            List<UUID> ret = results
                    .stream()
                    .map(T -> UUID.fromString(T.getDestinationResourceId()))
                    .collect(Collectors.toList());

            return ret;

        } finally {
            entityManager.close();
        }
    }
}
