package org.endeavourhealth.core.database.cassandra.ehr;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.Result;
import com.google.common.collect.Lists;
import org.endeavourhealth.common.cache.ParserPool;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.ehr.accessors.ResourceAccessor;
import org.endeavourhealth.core.database.cassandra.ehr.accessors.ResourceHistoryAccessor;
import org.endeavourhealth.core.database.cassandra.ehr.models.CassandraResourceByExchangeBatch;
import org.endeavourhealth.core.database.cassandra.ehr.models.CassandraResourceByService;
import org.endeavourhealth.core.database.cassandra.ehr.models.CassandraResourceHistory;
import org.endeavourhealth.core.database.dal.ehr.ResourceDalI;
import org.endeavourhealth.core.database.dal.ehr.ResourceMetadataIterator;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.fhirStorage.metadata.ResourceMetadata;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CassandraResourceRepository extends Repository implements ResourceDalI {

    private static final Logger LOG = LoggerFactory.getLogger(CassandraResourceRepository.class);
    private static final ParserPool PARSER_POOL = new ParserPool();

    private static final String SCHEMA_VERSION = "0.1";
    private static final String METADATA_COLUMN_NAME = "resource_metadata";

    public void save(ResourceWrapper resourceEntry){
        if (resourceEntry == null) {
            throw new IllegalArgumentException("resourceEntry is null");
        }

        CassandraResourceHistory resourceHistory = new CassandraResourceHistory();
        resourceHistory.setResourceId(resourceEntry.getResourceId());
        resourceHistory.setResourceType(resourceEntry.getResourceType());
        resourceHistory.setVersion(resourceEntry.getVersion());
        resourceHistory.setCreatedAt(resourceEntry.getCreatedAt());
        resourceHistory.setServiceId(resourceEntry.getServiceId());
        resourceHistory.setSystemId(resourceEntry.getSystemId());
        resourceHistory.setIsDeleted(false);
        resourceHistory.setSchemaVersion(SCHEMA_VERSION);
        resourceHistory.setResourceData(resourceEntry.getResourceData());
        resourceHistory.setResourceChecksum(resourceEntry.getResourceChecksum());
        save(resourceHistory);

        //remove this table
        /*ResourceHistoryByService resourceHistoryByService = new ResourceHistoryByService();
        resourceHistoryByService.setResourceId(resourceEntry.getResourceId());
        resourceHistoryByService.setResourceType(resourceEntry.getResourceType());
        resourceHistoryByService.setVersion(resourceEntry.getVersion());
        resourceHistoryByService.setCreatedAt(resourceEntry.getCreatedAt());
        resourceHistoryByService.setServiceId(resourceEntry.getServiceId());
        resourceHistoryByService.setSystemId(resourceEntry.getSystemId());
        resourceHistoryByService.setIsDeleted(false);
        resourceHistoryByService.setSchemaVersion(resourceEntry.getSchemaVersion());
        resourceHistoryByService.setResourceData(resourceEntry.getResourceData());
        Mapper<ResourceHistoryByService> mapperResourceHistoryByService = getMappingManager().mapper(ResourceHistoryByService.class);
        mapperResourceHistoryByService.save(resourceHistoryByService);*/

        CassandraResourceByService resourceByService = new CassandraResourceByService();
        resourceByService.setServiceId(resourceEntry.getServiceId());
        resourceByService.setSystemId(resourceEntry.getSystemId());
        resourceByService.setResourceType(resourceEntry.getResourceType());
        resourceByService.setResourceId(resourceEntry.getResourceId());
        resourceByService.setCurrentVersion(resourceEntry.getVersion());
        resourceByService.setUpdatedAt(resourceEntry.getCreatedAt());
        resourceByService.setPatientId(resourceEntry.getPatientId());
        resourceByService.setSchemaVersion(SCHEMA_VERSION);
        resourceByService.setResourceMetadata(resourceEntry.getResourceMetadata());
        resourceByService.setResourceData(resourceEntry.getResourceData());
        save(resourceByService);

        if (resourceEntry.getExchangeId() != null && resourceEntry.getExchangeBatchId() != null) {
            CassandraResourceByExchangeBatch resourceByExchangeBatch = new CassandraResourceByExchangeBatch();
            resourceByExchangeBatch.setBatchId(resourceEntry.getExchangeBatchId());
            resourceByExchangeBatch.setExchangeId(resourceEntry.getExchangeId());
            resourceByExchangeBatch.setResourceType(resourceEntry.getResourceType());
            resourceByExchangeBatch.setResourceId(resourceEntry.getResourceId());
            resourceByExchangeBatch.setVersion(resourceEntry.getVersion());
            resourceByExchangeBatch.setIsDeleted(false);
            resourceByExchangeBatch.setSchemaVersion(SCHEMA_VERSION);
            resourceByExchangeBatch.setResourceData(resourceEntry.getResourceData());
            save(resourceByExchangeBatch);
        }
    }


    public void delete(ResourceWrapper resourceEntry){
        if (resourceEntry == null) throw new IllegalArgumentException("resourceEntry is null");

        CassandraResourceHistory resourceHistory = new CassandraResourceHistory();
        resourceHistory.setResourceId(resourceEntry.getResourceId());
        resourceHistory.setResourceType(resourceEntry.getResourceType());
        resourceHistory.setVersion(resourceEntry.getVersion());
        resourceHistory.setCreatedAt(resourceEntry.getCreatedAt());
        resourceHistory.setServiceId(resourceEntry.getServiceId());
        resourceHistory.setSystemId(resourceEntry.getSystemId());
        resourceHistory.setIsDeleted(true);
        save(resourceHistory);

        //remove this table
        /*ResourceHistoryByService resourceHistoryByService = new ResourceHistoryByService();
        resourceHistoryByService.setResourceId(resourceEntry.getResourceId());
        resourceHistoryByService.setResourceType(resourceEntry.getResourceType());
        resourceHistoryByService.setVersion(resourceEntry.getVersion());
        resourceHistoryByService.setCreatedAt(resourceEntry.getCreatedAt());
        resourceHistoryByService.setServiceId(resourceEntry.getServiceId());
        resourceHistoryByService.setSystemId(resourceEntry.getSystemId());
        resourceHistoryByService.setIsDeleted(true);
        Mapper<ResourceHistoryByService> mapperResourceHistoryByService = getMappingManager().mapper(ResourceHistoryByService.class);
        mapperResourceHistoryByService.save(resourceHistoryByService);*/

        CassandraResourceByService resourceByService = new CassandraResourceByService();
        resourceByService.setServiceId(resourceEntry.getServiceId());
        resourceByService.setSystemId(resourceEntry.getSystemId());
        resourceByService.setResourceType(resourceEntry.getResourceType());
        resourceByService.setResourceId(resourceEntry.getResourceId());
        resourceByService.setCurrentVersion(resourceEntry.getVersion()); //was missing - so it wasn't clear when something was deleted
        resourceByService.setUpdatedAt(resourceEntry.getCreatedAt());
        save(resourceByService);

        if (resourceEntry.getExchangeId() != null && resourceEntry.getExchangeBatchId() != null) {
            CassandraResourceByExchangeBatch resourceByExchangeBatch = new CassandraResourceByExchangeBatch();
            resourceByExchangeBatch.setBatchId(resourceEntry.getExchangeBatchId());
            resourceByExchangeBatch.setExchangeId(resourceEntry.getExchangeId());
            resourceByExchangeBatch.setResourceType(resourceEntry.getResourceType());
            resourceByExchangeBatch.setResourceId(resourceEntry.getResourceId());
            resourceByExchangeBatch.setVersion(resourceEntry.getVersion());
            resourceByExchangeBatch.setIsDeleted(true);
            save(resourceByExchangeBatch);
        }
    }


    public void hardDelete(ResourceWrapper keys) {

        Mapper<CassandraResourceHistory> mapperResourceHistory = getMappingManager().mapper(CassandraResourceHistory.class);
        mapperResourceHistory.delete(keys.getResourceId(), keys.getResourceType(), keys.getVersion());

        //remove this table
        /*Mapper<ResourceHistoryByService> mapperResourceHistoryByService = getMappingManager().mapper(ResourceHistoryByService.class);
        mapperResourceHistoryByService.delete(keys.getServiceId(), keys.getSystemId(), keys.getResourceType(), keys.getResourceId(), keys.getVersion());*/

        Mapper<CassandraResourceByService> mapperResourceByService = getMappingManager().mapper(CassandraResourceByService.class);
        mapperResourceByService.delete(keys.getServiceId(), keys.getSystemId(), keys.getResourceType(), keys.getResourceId());

        Mapper<CassandraResourceByExchangeBatch> mapperResourceByExchangeBatch = getMappingManager().mapper(CassandraResourceByExchangeBatch.class);
        mapperResourceByExchangeBatch.delete(keys.getExchangeBatchId(), keys.getResourceType(), keys.getResourceId(), keys.getVersion());
    }


    private void save(CassandraResourceHistory resourceHistory) {
        Mapper<CassandraResourceHistory> mapper = getMappingManager().mapper(CassandraResourceHistory.class);
        mapper.save(resourceHistory);
    }

    private void save(CassandraResourceByService resourceByService) {
        Mapper<CassandraResourceByService> mapper = getMappingManager().mapper(CassandraResourceByService.class);
        mapper.save(resourceByService);
    }

    private void save(CassandraResourceByExchangeBatch resourceByExchangeBatch) {
        Mapper<CassandraResourceByExchangeBatch> mapper = getMappingManager().mapper(CassandraResourceByExchangeBatch.class);
        mapper.save(resourceByExchangeBatch);
    }

    public ResourceWrapper getResourceHistoryByKey(UUID resourceId, String resourceType, UUID version) {
        Mapper<CassandraResourceHistory> mapper = getMappingManager().mapper(CassandraResourceHistory.class);
        CassandraResourceHistory result = mapper.get(resourceId, resourceType, version);
        if (result != null) {
            return new ResourceWrapper(result);
        } else {
            return null;
        }
    }

    public ResourceWrapper getResourceByServiceByKey(UUID serviceId, UUID systemId, String resourceType, UUID resourceId) {
        Mapper<CassandraResourceByService> mapper = getMappingManager().mapper(CassandraResourceByService.class);
        CassandraResourceByService result = mapper.get(serviceId, systemId, resourceType, resourceId);
        if (result != null) {
            return new ResourceWrapper(result);
        } else {
            return null;
        }
    }

    public ResourceWrapper getResourceByExchangeBatchByKey(UUID batchId, String resourceType, UUID resourceId, UUID version) {
        Mapper<CassandraResourceByExchangeBatch> mapper = getMappingManager().mapper(CassandraResourceByExchangeBatch.class);
        CassandraResourceByExchangeBatch result = mapper.get(batchId, resourceType, resourceId, version);
        if (result != null) {
            return new ResourceWrapper(result);
        } else {
            return null;
        }
    }

    /**
     * convenience fn to save repetitive code
     */
    public Resource getCurrentVersionAsResource(ResourceType resourceType, String resourceIdStr) throws Exception {
        ResourceWrapper resourceHistory = getCurrentVersion(resourceType.toString(), UUID.fromString(resourceIdStr));

        if (resourceHistory == null
            || resourceHistory.isDeleted()) {
            return null;
        } else {
            return PARSER_POOL.parse(resourceHistory.getResourceData());
        }
    }

    public ResourceWrapper getCurrentVersion(String resourceType, UUID resourceId) {
        ResourceHistoryAccessor accessor = getMappingManager().createAccessor(ResourceHistoryAccessor.class);
        CassandraResourceHistory result = accessor.getCurrentVersion(resourceType, resourceId);
        if (result != null) {
            return new ResourceWrapper(result);
        } else {
            return null;
        }
    }

    public List<ResourceWrapper> getResourceHistory(String resourceType, UUID resourceId) {
        ResourceHistoryAccessor accessor = getMappingManager().createAccessor(ResourceHistoryAccessor.class);
        return Lists.newArrayList(accessor.getResourceHistory(resourceType, resourceId))
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID systemId, UUID patientId) {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        return Lists.newArrayList(accessor.getResourcesByPatient(serviceId, systemId, patientId))
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID systemId, UUID patientId, String resourceType) {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        return Lists.newArrayList(accessor.getResourcesByPatient(serviceId, systemId, patientId, resourceType))
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesByPatientAllSystems(UUID serviceId, UUID patientId, String resourceType) {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        return Lists.newArrayList(accessor.getResourcesByPatientAllSystems(serviceId, patientId, resourceType))
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesByService(UUID serviceId, UUID systemId, String resourceType, List<UUID> resourceIds) {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        return Lists.newArrayList(accessor.getResourcesByService(serviceId, systemId, resourceType, resourceIds))
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesByServiceAllSystems(UUID serviceId, String resourceType, List<UUID> resourceIds) {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        return Lists.newArrayList(accessor.getResourcesByServiceAllSystems(serviceId, resourceType, resourceIds))
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesForBatch(UUID batchId) {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        return Lists.newArrayList(accessor.getResourcesForBatch(batchId))
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesForBatch(UUID batchId, String resourceType) {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        return Lists.newArrayList(accessor.getResourcesForBatch(batchId, resourceType))
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public List<ResourceWrapper> getResourcesForBatch(UUID batchId, String resourceType, UUID resourceId) {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        return Lists.newArrayList(accessor.getResourcesForBatch(batchId, resourceType, resourceId))
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    public long getResourceCountByService(UUID serviceId, UUID systemId, String resourceType) {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        ResultSet result = accessor.getResourceCountByService(serviceId, systemId, resourceType);
        Row row = result.one();
        return row.getLong(0);
    }

    public <T extends ResourceMetadata> ResourceMetadataIterator<T> getMetadataByService(UUID serviceId, UUID systemId, String resourceType, Class<T> classOfT) {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        ResultSet result = accessor.getMetadataByService(serviceId, systemId, resourceType);
        List<Row> results = Lists.newArrayList(result.iterator());

        List<String> metadata = new ArrayList<>();
        for (Row row: results) {
            String matadataString = row.getString(METADATA_COLUMN_NAME);
            metadata.add(matadataString);
        }

        return new ResourceMetadataIterator<>(metadata.iterator(), classOfT);
    }

    public Long getResourceChecksum(String resourceType, UUID resourceId) {
        ResourceHistoryAccessor accessor = getMappingManager().createAccessor(ResourceHistoryAccessor.class);
        ResultSet resultSet = accessor.getCurrentChecksum(resourceType, resourceId);
        Row row = resultSet.one();
        if (row != null) {
            return Long.valueOf(row.getLong(0));
        } else {
            return null;
        }
    }

    /**
     * tests if we have any patient cassandra stored for the given service and system
     */
    public boolean dataExists(UUID serviceId, UUID systemId) {

        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        Result<CassandraResourceByService> result = accessor.getFirstResourceByService(serviceId, systemId, ResourceType.Patient.toString());
        return result.iterator().hasNext();
    }

    public ResourceWrapper getFirstResourceByService(UUID serviceId, UUID systemId, ResourceType resourceType) {

        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        Result<CassandraResourceByService> resultSet = accessor.getFirstResourceByService(serviceId, systemId, resourceType.toString());
        Iterator<CassandraResourceByService> it = resultSet.iterator();
        if (it.hasNext()) {
            CassandraResourceByService result = it.next();
            return new ResourceWrapper(result);
        } else {
            return null;
        }
    }

    public List<ResourceWrapper> getResourcesByService(UUID serviceId, UUID systemId, String resourceType) {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        return Lists.newArrayList(accessor.getResourcesByService(serviceId, systemId, resourceType))
                .stream()
                .map(T -> new ResourceWrapper(T))
                .collect(Collectors.toList());
    }

    /*public ResourceByExchangeBatch getFirstResourceByExchangeBatch(String resourceType, UUID resourceId) {
        ResourceAccessor accessor = getMappingManager().createAccessor(ResourceAccessor.class);
        return accessor.getFirstResourceByExchangeBatch(resourceType, resourceId);
    }*/
}
