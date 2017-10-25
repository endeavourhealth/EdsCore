package org.endeavourhealth.core.database.rdbms.ehr;

import org.endeavourhealth.common.cache.ParserPool;
import org.endeavourhealth.core.database.dal.ehr.ResourceDalI;
import org.endeavourhealth.core.database.dal.ehr.ResourceMetadataIterator;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsService;
import org.endeavourhealth.core.database.rdbms.ehr.models.RdbmsResourceCurrent;
import org.endeavourhealth.core.database.rdbms.ehr.models.RdbmsResourceHistory;
import org.endeavourhealth.core.fhirStorage.metadata.ResourceMetadata;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RdbmsResourceDal implements ResourceDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsResourceDal.class);
    private static final ParserPool PARSER_POOL = new ParserPool();

    public void save(ResourceWrapper resourceEntry) throws Exception {
        if (resourceEntry == null) {
            throw new IllegalArgumentException("resourceEntry is null");
        }

        RdbmsResourceHistory resourceHistory = new RdbmsResourceHistory();
        resourceHistory.setServiceId(resourceEntry.getServiceId().toString());
        resourceHistory.setSystemId(resourceEntry.getSystemId().toString());
        resourceHistory.setResourceType(resourceEntry.getResourceType());
        resourceHistory.setResourceId(resourceEntry.getResourceId().toString());
        resourceHistory.setCreatedAt(resourceEntry.getCreatedAt());
        resourceHistory.setDeleted(false);
        resourceHistory.setResourceData(resourceEntry.getResourceData());
        resourceHistory.setResourceChecksum(resourceEntry.getResourceChecksum());
        resourceHistory.setExchangeBatchId(resourceEntry.getExchangeBatchId().toString());

        RdbmsResourceCurrent resourceCurrent = new RdbmsResourceCurrent();
        resourceCurrent.setServiceId(resourceEntry.getServiceId().toString());
        resourceCurrent.setSystemId(resourceEntry.getSystemId().toString());
        resourceCurrent.setResourceType(resourceEntry.getResourceType());
        resourceCurrent.setResourceId(resourceEntry.getResourceId().toString());
        resourceCurrent.setUpdatedAt(resourceEntry.getCreatedAt());
        resourceCurrent.setResourceData(resourceEntry.getResourceData());
        resourceCurrent.setResourceChecksum(resourceEntry.getResourceChecksum());
        resourceCurrent.setResourceMetadata(resourceEntry.getResourceMetadata());

        if (resourceEntry.getPatientId() != null) {
            resourceHistory.setPatientId(resourceEntry.getPatientId().toString());
            resourceCurrent.setPatientId(resourceEntry.getPatientId().toString());
        }

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(resourceHistory);
        entityManager.persist(resourceCurrent);
        entityManager.getTransaction().rollback();
        entityManager.close();
    }


    /**
     * logical delete, when we want to delete a resource but maintain our audits
     */
    public void delete(ResourceWrapper resourceEntry) throws Exception {
        if (resourceEntry == null) {
            throw new IllegalArgumentException("resourceEntry is null");
        }

        RdbmsResourceHistory resourceHistory = new RdbmsResourceHistory();
        resourceHistory.setServiceId(resourceEntry.getServiceId().toString());
        resourceHistory.setSystemId(resourceEntry.getSystemId().toString());
        resourceHistory.setResourceType(resourceEntry.getResourceType());
        resourceHistory.setResourceId(resourceEntry.getResourceId().toString());
        resourceHistory.setCreatedAt(resourceEntry.getCreatedAt());
        resourceHistory.setDeleted(true);
        resourceHistory.setResourceData(null);
        resourceHistory.setResourceChecksum(null);
        resourceHistory.setExchangeBatchId(resourceEntry.getExchangeBatchId().toString());

        //we're going to DELETE from the resource_current table, so really only need
        //to populate the primary key columns, but I'm doing all of them for consistency with the above method
        RdbmsResourceCurrent resourceCurrent = new RdbmsResourceCurrent();
        resourceCurrent.setServiceId(resourceEntry.getServiceId().toString());
        resourceCurrent.setSystemId(resourceEntry.getSystemId().toString());
        resourceCurrent.setResourceType(resourceEntry.getResourceType());
        resourceCurrent.setResourceId(resourceEntry.getResourceId().toString());
        resourceCurrent.setUpdatedAt(resourceEntry.getCreatedAt());
        resourceCurrent.setResourceData(null);
        resourceCurrent.setResourceChecksum(resourceEntry.getResourceChecksum());
        resourceCurrent.setResourceMetadata(resourceEntry.getResourceMetadata());

        if (resourceEntry.getPatientId() != null) {
            resourceHistory.setPatientId(resourceEntry.getPatientId().toString());
            resourceCurrent.setPatientId(resourceEntry.getPatientId().toString());
        }

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(resourceHistory);
        entityManager.remove(resourceCurrent);
        entityManager.getTransaction().rollback();
        entityManager.close();
    }

    /**
     * physical delete, when we want to remove all trace of cassandra from Discovery
     */
    public void hardDelete(ResourceWrapper keys) throws Exception {

        RdbmsResourceHistory resourceHistory = new RdbmsResourceHistory();
        resourceHistory.setServiceId(keys.getServiceId().toString());
        resourceHistory.setSystemId(keys.getSystemId().toString());
        resourceHistory.setResourceType(keys.getResourceType());
        resourceHistory.setResourceId(keys.getResourceId().toString());
        resourceHistory.setCreatedAt(keys.getCreatedAt());
        resourceHistory.setDeleted(true);
        resourceHistory.setResourceData(null);
        resourceHistory.setResourceChecksum(null);
        resourceHistory.setExchangeBatchId(keys.getExchangeBatchId().toString());

        //we're going to DELETE from the resource_current table, so really only need
        //to populate the primary key columns, but I'm doing all of them for consistency with the above method
        RdbmsResourceCurrent resourceCurrent = new RdbmsResourceCurrent();
        resourceCurrent.setServiceId(keys.getServiceId().toString());
        resourceCurrent.setSystemId(keys.getSystemId().toString());
        resourceCurrent.setResourceType(keys.getResourceType());
        resourceCurrent.setResourceId(keys.getResourceId().toString());
        resourceCurrent.setUpdatedAt(keys.getCreatedAt());
        resourceCurrent.setResourceData(null);
        resourceCurrent.setResourceChecksum(keys.getResourceChecksum());
        resourceCurrent.setResourceMetadata(keys.getResourceMetadata());

        if (keys.getPatientId() != null) {
            resourceHistory.setPatientId(keys.getPatientId().toString());
            resourceCurrent.setPatientId(keys.getPatientId().toString());
        }

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();
        entityManager.getTransaction().begin();
        entityManager.remove(resourceHistory);
        entityManager.remove(resourceCurrent);
        entityManager.getTransaction().rollback();
        entityManager.close();
    }


    /*public void save(ResourceHistory resourceHistory) {
        Mapper<ResourceHistory> mapper = getMappingManager().mapper(ResourceHistory.class);
        mapper.save(resourceHistory);
    }

    public void save(ResourceByService resourceByService) {
        Mapper<ResourceByService> mapper = getMappingManager().mapper(ResourceByService.class);
        mapper.save(resourceByService);
    }

    public void save(ResourceByExchangeBatch resourceByExchangeBatch) {
        Mapper<ResourceByExchangeBatch> mapper = getMappingManager().mapper(ResourceByExchangeBatch.class);
        mapper.save(resourceByExchangeBatch);
    }*/

    /*public ResourceHistory getResourceHistoryByKey(UUID resourceId, String resourceType, UUID version) {
        Mapper<ResourceHistory> mapper = getMappingManager().mapper(ResourceHistory.class);
        return mapper.get(resourceId, resourceType, version);
    }*/

    /*public ResourceCurrent getResourceByServiceByKey(UUID serviceId, UUID systemId, String resourceType, UUID resourceId) {
        Mapper<ResourceByService> mapper = getMappingManager().mapper(ResourceByService.class);
        return mapper.get(serviceId, systemId, resourceType, resourceId);
    }*/

    /*public ResourceByExchangeBatch getResourceByExchangeBatchByKey(UUID batchId, String resourceType, UUID resourceId, UUID version) {
        Mapper<ResourceByExchangeBatch> mapper = getMappingManager().mapper(ResourceByExchangeBatch.class);
        return mapper.get(batchId, resourceType, resourceId, version);
    }*/

    /**
     * convenience fn to save repetitive code
     */
    public Resource getCurrentVersionAsResource(ResourceType resourceType, String resourceIdStr) throws Exception {
        ResourceWrapper resourceHistory = getCurrentVersion(resourceType.toString(), UUID.fromString(resourceIdStr));

        if (resourceHistory == null) {
            return null;
        } else {
            return PARSER_POOL.parse(resourceHistory.getResourceData());
        }
    }

    public ResourceWrapper getCurrentVersion(String resourceType, UUID resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsResourceCurrent c"
                + " where c.resourceType = :resource_type"
                + " and c.resourceId = :resource_id";

        Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                .setParameter("resource_type", resourceType)
                .setParameter("resource_id", resourceId.toString());

        ResourceWrapper ret = null;
        try {
            RdbmsResourceCurrent result = (RdbmsResourceCurrent)query.getSingleResult();
            ret = new ResourceWrapper(result);

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    public List<ResourceWrapper> getResourceHistory(String resourceType, UUID resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsResourceHistory c"
                + " where c.resourceType = :resource_type"
                + " and c.resourceId = :resource_id";

        Query query = entityManager.createQuery(sql, RdbmsService.class)
                .setParameter("resource_type", resourceType)
                .setParameter("resource_id", resourceId.toString());

        List<RdbmsResourceHistory> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID systemId, UUID patientId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsResourceCurrent c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.patientId = :patient_id";

        Query query = entityManager.createQuery(sql, RdbmsService.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("patient_id", patientId.toString());

        List<RdbmsResourceCurrent> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID systemId, UUID patientId, String resourceType) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsResourceCurrent c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.patientId = :patient_id"
                + " and c.resourceType = :resource_type";

        Query query = entityManager.createQuery(sql, RdbmsService.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("patient_id", patientId.toString())
                .setParameter("resource_type", resourceType);

        List<RdbmsResourceCurrent> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesByPatientAllSystems(UUID serviceId, UUID patientId, String resourceType) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsResourceCurrent c"
                + " where c.serviceId = :service_id"
                + " and c.patientId = :patient_id"
                + " and c.resourceType = :resource_type";

        Query query = entityManager.createQuery(sql, RdbmsService.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("patient_id", patientId.toString())
                .setParameter("resource_type", resourceType);

        List<RdbmsResourceCurrent> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesByService(UUID serviceId, UUID systemId, String resourceType, List<UUID> resourceIds) throws Exception {

        //convert the list of UUIDs to strings
        List<String> resourceIdStrs = resourceIds
                .stream()
                .map(T -> T.toString())
                .collect(Collectors.toList());

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsResourceCurrent c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.resourceType = :resource_type"
                + " and c.resourceId IN :resource_ids";

        Query query = entityManager.createQuery(sql, RdbmsService.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("resource_type", resourceType)
                .setParameter("resource_ids", resourceIdStrs);

        List<RdbmsResourceCurrent> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesByServiceAllSystems(UUID serviceId, String resourceType, List<UUID> resourceIds) throws Exception {

        //convert the list of UUIDs to strings
        List<String> resourceIdStrs = resourceIds
                .stream()
                .map(T -> T.toString())
                .collect(Collectors.toList());

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsResourceCurrent c"
                + " where c.serviceId = :service_id"
                + " and c.resourceType = :resource_type"
                + " and c.resourceId IN :resource_ids";

        Query query = entityManager.createQuery(sql, RdbmsService.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("resource_type", resourceType)
                .setParameter("resource_ids", resourceIdStrs);

        List<RdbmsResourceCurrent> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesForBatch(UUID batchId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsResourceHistory c"
                + " where c.batchId = :batch_id";

        Query query = entityManager.createQuery(sql, RdbmsService.class)
                .setParameter("batch_id", batchId.toString());

        List<RdbmsResourceHistory> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    /*public List<ResourceByExchangeBatch> getResourcesForBatch(UUID batchId, String resourceType) throws Exception {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        return Lists.newArrayList(accessor.getResourcesForBatch(batchId, resourceType));
    }*/

    /*public List<ResourceByExchangeBatch> getResourcesForBatch(UUID batchId, String resourceType, UUID resourceId) throws Exception {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        return Lists.newArrayList(accessor.getResourcesForBatch(batchId, resourceType, resourceId));
    }*/

    /*public long getResourceCountByService(UUID serviceId, UUID systemId, String resourceType) throws Exception {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        ResultSet result = accessor.getResourceCountByService(serviceId, systemId, resourceType);
        Row row = result.one();
        return row.getLong(0);
    }*/

    /*public <T extends ResourceMetadata> ResourceMetadataIterator<T> getMetadataByService(UUID serviceId, UUID systemId, String resourceType, Class<T> classOfT) throws Exception {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        ResultSet result = accessor.getMetadataByService(serviceId, systemId, resourceType);
        return new ResourceMetadataIterator<>(result.iterator(), classOfT);
    }*/

    public Long getResourceChecksum(String resourceType, UUID resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        /*Query q=session.createQuery("select sum(salary) from Emp");
        List<Integer> list=q.list();
        System.out.println(list.get(0));*/

        String sql = "select c.resourceChecksum"
                + " from"
                + " RdbmsResourceCurrent c"
                + " where c.resourceType = :resource_type"
                + " and c.resourceId = :resource_id";

        Query query = entityManager.createQuery(sql)
                .setParameter("resource_type", resourceType)
                .setParameter("resource_id", resourceId.toString());

        Long ret = null;

        List<Long> list = query.getResultList();
        for (Long s: list) {
            ret = s;
        }

        entityManager.close();

        return ret;
    }

    /**
     * tests if we have any patient cassandra stored for the given service and system
     */
    public boolean dataExists(UUID serviceId, UUID systemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsResourceCurrent c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id";

        Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setMaxResults(1);

        boolean ret = false;
        try {
            RdbmsResourceCurrent r = (RdbmsResourceCurrent)query.getSingleResult();
            ret = true;

        } catch (NoResultException ex) {
            ret = false;
        }

        entityManager.close();

        return ret;
    }

    public ResourceWrapper getFirstResourceByService(UUID serviceId, UUID systemId, ResourceType resourceType) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsResourceCurrent c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.resourceType = :resource_type";

        Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("resource_type", resourceType)
                .setMaxResults(1);

        ResourceWrapper ret = null;
        try {
            RdbmsResourceCurrent result = (RdbmsResourceCurrent)query.getSingleResult();
            ret = new ResourceWrapper(result);

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    public List<ResourceWrapper> getResourcesByService(UUID serviceId, UUID systemId, String resourceType) throws Exception {

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsResourceCurrent c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.resourceType = :resource_type";

        Query query = entityManager.createQuery(sql, RdbmsService.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("resource_type", resourceType);

        List<RdbmsResourceCurrent> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public <T extends ResourceMetadata> ResourceMetadataIterator<T> getMetadataByService(UUID serviceId, UUID systemId, String resourceType, Class<T> classOfT) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "SELECT c.ResourceMetadata"
                + " FROM RdbmsResourceCurrent c"
                + " WHERE c.ServiceId = :service_id"
                + " AND c.SystemId = :system_id"
                + " AND c.ResourceType = :resource_type";

        Query q = entityManager.createQuery(sql)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("resource_type", resourceType);

        List<String> results = q.getResultList();
        entityManager.close();

        return new ResourceMetadataIterator<>(results.iterator(), classOfT);
    }

    public long getResourceCountByService(UUID serviceId, UUID systemId, String resourceType) throws Exception {

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        String sql = "SELECT COUNT(c)"
                + " FROM RdbmsResourceCurrent c"
                + " WHERE c.ServiceId = :service_id"
                + " AND c.SystemId = :system_id"
                + " AND c.ResourceType = :resource_type";

        Query q = entityManager.createQuery(sql)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("resource_type", resourceType);

        long count = (long)q.getSingleResult();

        entityManager.close();

        return count;
    }
}
