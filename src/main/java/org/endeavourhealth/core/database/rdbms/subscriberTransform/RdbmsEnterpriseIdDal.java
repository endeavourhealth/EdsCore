package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.database.dal.subscriberTransform.EnterpriseIdDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsEnterpriseIdMap;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsEnterpriseInstanceMap;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsEnterpriseOrganisationIdMap;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsEnterprisePersonIdMap;
import org.hl7.fhir.instance.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

public class RdbmsEnterpriseIdDal implements EnterpriseIdDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsEnterpriseIdDal.class);

    private String subscriberConfigName = null;

    public RdbmsEnterpriseIdDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    public Long findOrCreateEnterpriseId(String resourceType, String resourceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            Long ret = findEnterpriseId(resourceType, resourceId, entityManager);
            if (ret != null) {
                return ret;
            }

            return createEnterpriseId(resourceType, resourceId, entityManager);

        } catch (Exception ex) {
            //if another thread has beat us to it, we'll get an exception, so try the find again
            Long ret = findEnterpriseId(resourceType, resourceId, entityManager);
            if (ret != null) {
                return ret;
            }

            throw ex;
        } finally {
            entityManager.close();
        }
    }

    private static Long createEnterpriseId(String resourceType, String resourceId, EntityManager entityManager) throws Exception {

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

    public Long findEnterpriseId(String resourceType, String resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        try {
            return findEnterpriseId(resourceType, resourceId, entityManager);

        } finally {
            entityManager.close();
        }
    }

    private static Long findEnterpriseId(String resourceType, String resourceId, EntityManager entityManager) throws Exception {

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

    public void saveEnterpriseOrganisationId(String serviceId, String systemId, Long enterpriseId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            RdbmsEnterpriseOrganisationIdMap mapping = findEnterpriseOrganisationMapping(serviceId, systemId, entityManager);
            if (mapping != null) {
                throw new Exception("EnterpriseOrganisationIdMap already exists for service " + serviceId + " system " + systemId + " config " + subscriberConfigName);
            }

            mapping = new RdbmsEnterpriseOrganisationIdMap();
            mapping.setServiceId(serviceId);
            mapping.setEnterpriseId(enterpriseId);

            entityManager.getTransaction().begin();
            entityManager.persist(mapping);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    private static RdbmsEnterpriseOrganisationIdMap findEnterpriseOrganisationMapping(String serviceId, String systemId, EntityManager entityManager) throws Exception {

        String sql = "select c"
                + " from"
                + " RdbmsEnterpriseOrganisationIdMap c"
                + " where c.serviceId = :serviceId";

        Query query = entityManager.createQuery(sql, RdbmsEnterpriseOrganisationIdMap.class)
                .setParameter("serviceId", serviceId);

        try {
            RdbmsEnterpriseOrganisationIdMap result = (RdbmsEnterpriseOrganisationIdMap)query.getSingleResult();
            return result;

        } catch (NoResultException ex) {
            return null;
        }
    }

    public Long findEnterpriseOrganisationId(String serviceId, String systemId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            RdbmsEnterpriseOrganisationIdMap mapping = findEnterpriseOrganisationMapping(serviceId, systemId, entityManager);
            if (mapping != null) {
                return mapping.getEnterpriseId();
            } else {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }


    public Long findOrCreateEnterprisePersonId(String discoveryPersonId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            Long ret = findEnterprisePersonId(discoveryPersonId, entityManager);
            if (ret != null) {
                return ret;
            }

            return createEnterprisePersonId(discoveryPersonId, entityManager);

        } catch (Exception ex) {
            //if another thread has beat us to it, we'll get an exception, so try the find again
            Long ret = findEnterprisePersonId(discoveryPersonId, entityManager);
            if (ret != null) {
                return ret;
            }

            throw ex;
        } finally {
            entityManager.close();
        }
    }

    private static Long findEnterprisePersonId(String discoveryPersonId, EntityManager entityManager) {

        String sql = "select c"
                + " from"
                + " RdbmsEnterprisePersonIdMap c"
                + " where c.personId = :personId";


        Query query = entityManager.createQuery(sql, RdbmsEnterprisePersonIdMap.class)
                .setParameter("personId", discoveryPersonId);

        try {
            RdbmsEnterprisePersonIdMap result = (RdbmsEnterprisePersonIdMap)query.getSingleResult();
            return result.getEnterprisePersonId();

        } catch (NoResultException ex) {
            return null;
        }
    }

    private static Long createEnterprisePersonId(String discoveryPersonId, EntityManager entityManager) throws Exception {

        RdbmsEnterprisePersonIdMap mapping = new RdbmsEnterprisePersonIdMap();
        mapping.setPersonId(discoveryPersonId);

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(mapping);
            entityManager.getTransaction().commit();

            return mapping.getEnterprisePersonId();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;
        }
    }

    public Long findEnterprisePersonId(String discoveryPersonId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        try {
            return findEnterprisePersonId(discoveryPersonId, entityManager);

        } finally {
            entityManager.close();
        }
    }

    public List<Long> findEnterprisePersonIdsForPersonId(String discoveryPersonId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsEnterprisePersonIdMap c"
                    + " where c.personId = :personId";

            Query query = entityManager.createQuery(sql, RdbmsEnterprisePersonIdMap.class)
                    .setParameter("personId", discoveryPersonId);

            List<RdbmsEnterprisePersonIdMap> ret = query.getResultList();
            return ret
                    .stream()
                    .map(T -> T.getEnterprisePersonId())
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public void findEnterpriseIds(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            findEnterpriseIds(resources, ids, entityManager);

        } finally {
            entityManager.close();
        }
    }

    private static void findEnterpriseIds(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids, EntityManager entityManager) throws Exception {

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

    public void findOrCreateEnterpriseIds(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        List<ResourceWrapper> resourcesToCreate = null;
        try {
            //check the DB for existing IDs
            findEnterpriseIds(resources, ids, entityManager);

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
                Long enterpriseId = findOrCreateEnterpriseId(resource.getResourceType(), resource.getResourceId().toString());
                ids.put(resource, enterpriseId);
            }

        } finally {
            entityManager.close();
        }
    }


    @Override
    public UUID findInstanceMappedId(ResourceType resourceType, UUID resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            //first see if we've got a mapping by our resource ID
            RdbmsEnterpriseInstanceMap mapping = findMappingByResourceId(entityManager, resourceType, resourceId);
            if (mapping != null) {
                return UUID.fromString(mapping.getResourceIdTo());

            } else {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    @Override
    public UUID findOrCreateInstanceMappedId(ResourceType resourceType, UUID resourceId, String mappingValue) throws Exception {

        /*if (Strings.isNullOrEmpty(mappingValue)) {
            throw new IllegalArgumentException("Cannot create instance mapping without a mapping value");
        }*/

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            //first see if we've got a mapping by our resource ID
            RdbmsEnterpriseInstanceMap mapping = findMappingByResourceId(entityManager, resourceType, resourceId);

            //if this is the first time we've transformed this resource, then we won't have a mapping, so want to see if we should map it
            if (mapping == null) {

                mapping = new RdbmsEnterpriseInstanceMap();
                mapping.setResourceType(resourceType.toString());
                mapping.setResourceIdFrom(resourceId.toString());

                if (Strings.isNullOrEmpty(mappingValue)) {
                    //if we have a null mapping value (e.g. no ODS code for an org) then we can't accurately map
                    //to another instance of that org so just let it map to itself
                    mapping.setResourceIdTo(resourceId.toString());

                } else {
                    //if we have a mapping value (e.g. ODS code) then we can look to see if there's any other mappings
                    //with that same mapping value
                    mapping.setMappingValue(mappingValue);

                    RdbmsEnterpriseInstanceMap otherMapping = findMappingByMappedValue(entityManager, resourceType, mappingValue);
                    if (otherMapping == null) {
                        //if there's no other record with the same mapping value, then we create one for our resource mapping to itself
                        mapping.setResourceIdTo(resourceId.toString());

                    } else {
                        //if we have a mapping with the same mapping value, then we create one for our resource mapping to that other resource
                        String mappedResourceId = otherMapping.getResourceIdTo();
                        mapping.setResourceIdTo(mappedResourceId);
                    }
                }

                entityManager.getTransaction().begin();
                entityManager.persist(mapping);
                entityManager.getTransaction().commit();
            }

            return UUID.fromString(mapping.getResourceIdTo());

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void takeOverInstanceMapping(ResourceType resourceType, UUID oldMappedResourceId, UUID newMappedResourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            //we need to update BOTH the instance mapping table, to map everything to our new resource
            //but also the resource_id_map so that the new resource ID links to the same enterprise ID
            entityManager.getTransaction().begin();

            String sql = "update RdbmsEnterpriseInstanceMap c"
                    + " set c.resourceIdTo = :new_resource_id"
                    + " where c.resourceType = :resourceType"
                    + " and c.resourceIdTo = :old_resource_id";

            Query query = entityManager.createQuery(sql)
                    .setParameter("resourceType", resourceType.toString())
                    .setParameter("old_resource_id", oldMappedResourceId.toString())
                    .setParameter("new_resource_id", newMappedResourceId.toString());
            query.executeUpdate();

            sql = "update RdbmsEnterpriseIdMap c"
                    + " set c.resourceId = :new_resource_id"
                    + " where c.resourceType = :resourceType"
                    + " and c.resourceId = :old_resource_id";

            query = entityManager.createQuery(sql)
                    .setParameter("resourceType", resourceType.toString())
                    .setParameter("old_resource_id", oldMappedResourceId.toString())
                    .setParameter("new_resource_id", newMappedResourceId.toString());
            query.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    private static RdbmsEnterpriseInstanceMap findMappingByMappedValue(EntityManager entityManager, ResourceType resourceType, String mappingValue) {

        String sql = "select c"
                + " from"
                + " RdbmsEnterpriseInstanceMap c"
                + " where c.resourceType = :resourceType"
                + " and c.mappingValue = :mappingValue";

        Query query = entityManager.createQuery(sql, RdbmsEnterpriseInstanceMap.class)
                .setParameter("resourceType", resourceType.toString())
                .setParameter("mappingValue", mappingValue)
                .setMaxResults(1);

        try {
            RdbmsEnterpriseInstanceMap result = (RdbmsEnterpriseInstanceMap)query.getSingleResult();
            return result;

        } catch (NoResultException ex) {
            return null;
        }
    }

    private static RdbmsEnterpriseInstanceMap findMappingByResourceId(EntityManager entityManager, ResourceType resourceType, UUID resourceId) {


        String sql = "select c"
                + " from"
                + " RdbmsEnterpriseInstanceMap c"
                + " where c.resourceType = :resourceType"
                + " and c.resourceIdFrom = :resourceIdFrom";

        Query query = entityManager.createQuery(sql, RdbmsEnterpriseInstanceMap.class)
                .setParameter("resourceType", resourceType.toString())
                .setParameter("resourceIdFrom", resourceId.toString())
                .setMaxResults(1);

        try {
            RdbmsEnterpriseInstanceMap result = (RdbmsEnterpriseInstanceMap)query.getSingleResult();
            return result;

        } catch (NoResultException ex) {
            return null;
        }
    }

}
