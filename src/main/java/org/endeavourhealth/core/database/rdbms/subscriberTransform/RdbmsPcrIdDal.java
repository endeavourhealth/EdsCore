package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.database.dal.subscriberTransform.PcrIdDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.*;
import org.hl7.fhir.instance.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

public class RdbmsPcrIdDal implements PcrIdDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsPcrIdDal.class);

    private String subscriberConfigName = null;

    public RdbmsPcrIdDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    public  Long findOrCreatePcrId(String resourceType, String resourceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            Long ret = findPcrId(resourceType, resourceId, entityManager);
            if (ret != null) {
                return ret;
            }

            return createPcrId(resourceType, resourceId, entityManager);

        } catch (Exception ex) {
            //if another thread has beat us to it, we'll get an exception, so try the find again
            Long ret = findPcrId(resourceType, resourceId, entityManager);
            if (ret != null) {
                return ret;
            }

            throw ex;
        } finally {
            entityManager.close();
        }
    }

    private static Long createPcrId(String resourceType, String resourceId, EntityManager entityManager) throws Exception {

        if (resourceId == null) {
            throw new IllegalArgumentException("Null resource ID");
        }

        RdbmsPcrIdMap mapping = new RdbmsPcrIdMap();
        mapping.setResourceType(resourceType);
        mapping.setResourceId(resourceId);
        //mapping.setPcrId(new Long(0));

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(mapping);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;
        }

        return mapping.getPcrId();
    }

    public Long findPcrId(String resourceType, String resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        try {
            return findPcrId(resourceType, resourceId, entityManager);

        } finally {
            entityManager.close();
        }
    }


    private static Long findPcrId(String resourceType, String resourceId, EntityManager entityManager) throws Exception {

        String sql = "select c"
                + " from"
                + " RdbmsPcrIdMap c"
                + " where c.resourceType = :resourceType"
                + " and c.resourceId = :resourceId";


        //LOG.debug("findPcrId query params: resourceType -> "+resourceType+" , resourceId -> "+resourceId);

        Query query = entityManager.createQuery(sql, RdbmsPcrIdMap.class)
                .setParameter("resourceType", resourceType)
                .setParameter("resourceId", resourceId);

        try {
            RdbmsPcrIdMap result = (RdbmsPcrIdMap)query.getSingleResult();
            return result.getPcrId();

        } catch (NoResultException ex) {
            return null;
        }
    }

    public void savePcrOrganisationId(String serviceId, Long pcrId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            RdbmsPcrOrganisationIdMap mapping = findPcrOrganisationMapping(serviceId, entityManager);
            if (mapping != null) {
                throw new Exception("PcrOrganisationIdMap already exists for service " + serviceId + " config " + subscriberConfigName);
            }

            mapping = new RdbmsPcrOrganisationIdMap();
            mapping.setServiceId(serviceId);
            mapping.setPcrId(pcrId);

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

    private static RdbmsPcrOrganisationIdMap findPcrOrganisationMapping(String serviceId, EntityManager entityManager) throws Exception {

        String sql = "select c"
                + " from"
                + " RdbmsPcrOrganisationIdMap c"
                + " where c.serviceId = :serviceId";

        Query query = entityManager.createQuery(sql, RdbmsPcrOrganisationIdMap.class)
                .setParameter("serviceId", serviceId);

        try {
            RdbmsPcrOrganisationIdMap result = (RdbmsPcrOrganisationIdMap)query.getSingleResult();
            return result;

        } catch (NoResultException ex) {
            return null;
        }
    }

    public Long findPcrOrganisationId(String serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            RdbmsPcrOrganisationIdMap mapping = findPcrOrganisationMapping(serviceId, entityManager);
            if (mapping != null) {
                return mapping.getPcrId();
            } else {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }


    public Long findOrCreatePcrPersonId(String discoveryPersonId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            Long ret = findPcrPersonId(discoveryPersonId, entityManager);
            if (ret != null) {
                return ret;
            }

            return createPcrPersonId(discoveryPersonId, entityManager);

        } catch (Exception ex) {
            //if another thread has beat us to it, we'll get an exception, so try the find again
            Long ret = findPcrPersonId(discoveryPersonId, entityManager);
            if (ret != null) {
                return ret;
            }

            throw ex;
        } finally {
            entityManager.close();
        }
    }

    private static Long findPcrPersonId(String discoveryPersonId, EntityManager entityManager) {

        String sql = "select c"
                + " from"
                + " RdbmsPcrPersonIdMap c"
                + " where c.personId = :personId";


        Query query = entityManager.createQuery(sql, RdbmsPcrPersonIdMap.class)
                .setParameter("personId", discoveryPersonId);

        try {
            RdbmsPcrPersonIdMap result = (RdbmsPcrPersonIdMap)query.getSingleResult();
            return result.getPcrPersonId();

        } catch (NoResultException ex) {
            return null;
        }
    }

    private static Long createPcrPersonId(String discoveryPersonId, EntityManager entityManager) throws Exception {

        RdbmsPcrPersonIdMap mapping = new RdbmsPcrPersonIdMap();
        mapping.setPersonId(discoveryPersonId);

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(mapping);
            entityManager.getTransaction().commit();

            return mapping.getPcrPersonId();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;
        }
    }




    public Long findPcrPersonId(String discoveryPersonId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        try {
            return findPcrPersonId(discoveryPersonId, entityManager);

        } finally {
            entityManager.close();
        }
    }

    public List<Long> findPcrPersonIdsForPersonId(String discoveryPersonId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPcrPersonIdMap c"
                    + " where c.personId = :personId";

            Query query = entityManager.createQuery(sql, RdbmsPcrPersonIdMap.class)
                    .setParameter("personId", discoveryPersonId);

            List<RdbmsPcrPersonIdMap> ret = query.getResultList();
            return ret
                    .stream()
                    .map(T -> T.getPcrPersonId())
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public Long createPcrFreeTextId(String resId, String resType) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        RdbmsPcrFreeTextIdMap mapping = new RdbmsPcrFreeTextIdMap();
        mapping.setResourceId(resId);
        mapping.setResourceType(resType);
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(mapping);
            entityManager.getTransaction().commit();

            return mapping.getPcrId();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;
        }
    }

    public void findPcrIds(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            findPcrIds(resources, ids, entityManager);

        } finally {
            entityManager.close();
        }
    }

    private static void findPcrIds(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids, EntityManager entityManager) throws Exception {

        String resourceType = null;
        List<String> resourceIds = new ArrayList<>();
        Map<String, ResourceWrapper> resourceIdMap = new HashMap<>();

        for (ResourceWrapper resource: resources) {

            if (resourceType == null) {
                resourceType = resource.getResourceType();
            } else if (!resourceType.equals(resource.getResourceType())) {
                throw new Exception("Can't find pcr IDs for different resource types");
            }

            String id = resource.getResourceId().toString();
            resourceIds.add(id);
            resourceIdMap.put(id, resource);
        }

        String sql = "select c"
                + " from"
                + " RdbmsPcrIdMap c"
                + " where c.resourceType = :resourceType"
                + " and c.resourceId IN :resourceId";


        Query query = entityManager.createQuery(sql, RdbmsPcrIdMap.class)
                .setParameter("resourceType", resourceType)
                .setParameter("resourceId", resourceIds);

        List<RdbmsPcrIdMap> results = query.getResultList();
        for (RdbmsPcrIdMap result: results) {
            String resourceId = result.getResourceId();
            Long pcrId = result.getPcrId();

            ResourceWrapper resource = resourceIdMap.get(resourceId);
            ids.put(resource, pcrId);
        }
    }

    public void findOrCreatePcrIds(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        List<ResourceWrapper> resourcesToCreate = null;
        try {
            //check the DB for existing IDs
            findPcrIds(resources, ids, entityManager);

            //find the resources that didn't have an ID
            resourcesToCreate = new ArrayList<>();
            for (ResourceWrapper resource: resources) {
                if (!ids.containsKey(resource)) {
                    resourcesToCreate.add(resource);
                }
            }

            //for any resource without an ID, we want to create one
            entityManager.getTransaction().begin();

            Map<ResourceWrapper, RdbmsPcrIdMap> mappingMap = new HashMap<>();

            for (ResourceWrapper resource: resourcesToCreate) {

                RdbmsPcrIdMap mapping = new RdbmsPcrIdMap();
                mapping.setResourceType(resource.getResourceType());
                mapping.setResourceId(resource.getResourceId().toString());

                entityManager.persist(mapping);

                mappingMap.put(resource, mapping);
            }

            entityManager.getTransaction().commit();

            for (ResourceWrapper resource: resourcesToCreate) {

                RdbmsPcrIdMap mapping = mappingMap.get(resource);
                Long pcrId = mapping.getPcrId();
                ids.put(resource, pcrId);
            }

        } catch (Exception ex) {
            //if another thread has beat us to it and created an ID for one of our records and we'll get an exception, so try the find again
            //but for each one individually
            entityManager.getTransaction().rollback();
            LOG.warn("Failed to create " + resourcesToCreate.size() + " IDs in one go, so doing one by one");

            for (ResourceWrapper resource: resourcesToCreate) {
                Long pcrId = findOrCreatePcrId(resource.getResourceType(), resource.getResourceId().toString());
                ids.put(resource, pcrId);
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
            RdbmsPcrInstanceMap mapping = findMappingByResourceId(entityManager, resourceType, resourceId);
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
            RdbmsPcrInstanceMap mapping = findMappingByResourceId(entityManager, resourceType, resourceId);

            //if this is the first time we've transformed this resource, then we won't have a mapping, so want to see if we should map it
            if (mapping == null) {

                mapping = new RdbmsPcrInstanceMap();
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

                    RdbmsPcrInstanceMap otherMapping = findMappingByMappedValue(entityManager, resourceType, mappingValue);
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
            //but also the resource_id_map so that the new resource ID links to the same pcr ID
            entityManager.getTransaction().begin();

            String sql = "update RdbmsPcrInstanceMap c"
                    + " set c.resourceIdTo = :new_resource_id"
                    + " where c.resourceType = :resourceType"
                    + " and c.resourceIdTo = :old_resource_id";

            Query query = entityManager.createQuery(sql)
                    .setParameter("resourceType", resourceType.toString())
                    .setParameter("old_resource_id", oldMappedResourceId.toString())
                    .setParameter("new_resource_id", newMappedResourceId.toString());
            query.executeUpdate();

            sql = "update RdbmsPcrIdMap c"
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

    private static RdbmsPcrInstanceMap findMappingByMappedValue(EntityManager entityManager, ResourceType resourceType, String mappingValue) {

        String sql = "select c"
                + " from"
                + " RdbmsPcrInstanceMap c"
                + " where c.resourceType = :resourceType"
                + " and c.mappingValue = :mappingValue";

        Query query = entityManager.createQuery(sql, RdbmsPcrInstanceMap.class)
                .setParameter("resourceType", resourceType.toString())
                .setParameter("mappingValue", mappingValue)
                .setMaxResults(1);

        try {
            RdbmsPcrInstanceMap result = (RdbmsPcrInstanceMap)query.getSingleResult();
            return result;

        } catch (NoResultException ex) {
            return null;
        }
    }

    private static RdbmsPcrInstanceMap findMappingByResourceId(EntityManager entityManager, ResourceType resourceType, UUID resourceId) {


        String sql = "select c"
                + " from"
                + " RdbmsPcrInstanceMap c"
                + " where c.resourceType = :resourceType"
                + " and c.resourceIdFrom = :resourceIdFrom";

        Query query = entityManager.createQuery(sql, RdbmsPcrInstanceMap.class)
                .setParameter("resourceType", resourceType.toString())
                .setParameter("resourceIdFrom", resourceId.toString())
                .setMaxResults(1);

        try {
            RdbmsPcrInstanceMap result = (RdbmsPcrInstanceMap)query.getSingleResult();
            return result;

        } catch (NoResultException ex) {
            return null;
        }
    }

}
