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
import java.util.*;
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
            LOG.debug("Save sus_resource_map entries:" + serviceId.toString() + "/" + sourceRowId);

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
                LOG.debug("Save sus_resource_map entries(type):" + type.toCode());
                ps.setString(3, type.toCode());

                List<UUID> uuidList = resourceIds.get(type);
                Iterator resourceUUIDList = uuidList.iterator();
                while (resourceUUIDList.hasNext()) {
                    UUID uuid = (UUID) resourceUUIDList.next();
                    LOG.debug("Save sus_resource_map entries+:" + type.toCode() + "/" + uuid.toString());
                    ps.setString(4, uuid.toString());
                    ps.executeUpdate();
                }
            }

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

    @Override
    public void saveSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType, List<UUID> resourceIds) throws Exception {
        LOG.debug("Save sus_resource_map entries:" + serviceId.toString() + "/" + sourceRowId + "/" + resourceType.toCode());
        resourceIds.forEach((u) -> {LOG.debug("Save sus_resource_map entries(uuid):" + u.toString());});

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

            LOG.debug("Delete sus_resource_map entries:" + serviceId.toString() + "/" + sourceRowId);

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "DELETE FROM sus_resource_map"
                    + " WHERE service_id=? and source_row_id=? and destination_resource_type=? and destination_resource_id=?";

            ps = connection.prepareStatement(sql);

            ps.setString(1, serviceId.toString());
            ps.setString(2, sourceRowId);

            Iterator<Enumerations.ResourceType> it = resourceIds.keySet().iterator();
            while (it.hasNext()) {
                Enumerations.ResourceType type = it.next();
                LOG.debug("Delete sus_resource_map entries(type):" + type.toCode());
                ps.setString(3, type.toCode());

                List<UUID> uuidList = resourceIds.get(type);
                Iterator resourceUUIDList = uuidList.iterator();
                while (resourceUUIDList.hasNext()) {
                    UUID uuid = (UUID) resourceUUIDList.next();
                    LOG.debug("Delete sus_resource_map entries+:" + type.toCode() + "/" + uuid.toString());
                    ps.setString(4, uuid.toString());
                    ps.executeUpdate();
                }
            }

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

    @Override
    public void deleteSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType, List<UUID> resourceIds) throws Exception {
        LOG.debug("Delete sus_resource_map entries:" + serviceId.toString() + "/" + sourceRowId + "/" + resourceType.toCode());
        resourceIds.forEach((u) -> {LOG.debug("Delete sus_resource_map entries(uuid):" + u.toString());});

        HashMap hm = new HashMap();
        hm.put(resourceType, resourceIds);
        deleteSusResourceMappings(serviceId, sourceRowId, hm);
    }

    @Override
    public Map<Enumerations.ResourceType, List<UUID>> getSusResourceMappings(UUID serviceId, String sourceRowId) throws Exception {
        int i = 1 / 0;
        return null;
    }

    @Override
    public List<UUID> getSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        LOG.debug("Looking for sus_resource_map entries:" + serviceId.toString() + "/" + sourceRowId + "/" + resourceType.toCode());

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

            ret.forEach((u) -> {LOG.debug("Found sus_resource_map entries:" + u.toString());});

            return ret;

        } finally {
            entityManager.close();
        }
    }
}
