package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.subscriberTransform.EnterpriseInstanceMapDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsEnterpriseInstanceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.UUID;

public class RdbmsEnterpriseInstanceMapDal implements EnterpriseInstanceMapDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsEnterpriseInstanceMapDal.class);

    private String subscriberConfigName = null;

    public RdbmsEnterpriseInstanceMapDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }


    @Override
    public UUID findInstanceMappedId(String resourceType, UUID resourceId) throws Exception {
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
    public UUID findOrCreateInstanceMappedId(String resourceType, UUID resourceId, String mappingValue) throws Exception {

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
                mapping.setResourceType(resourceType);
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

        } finally {
            entityManager.close();
        }
    }

    private static RdbmsEnterpriseInstanceMap findMappingByMappedValue(EntityManager entityManager, String resourceType, String mappingValue) {


        String sql = "select c"
                + " from"
                + " RdbmsEnterpriseInstanceMap c"
                + " where c.resourceType = :resourceType"
                + " and c.resourceIdFrom = :mappingValue";

        Query query = entityManager.createQuery(sql, RdbmsEnterpriseInstanceMap.class)
                .setParameter("resourceType", resourceType)
                .setParameter("mappingValue", mappingValue)
                .setMaxResults(1);

        try {
            RdbmsEnterpriseInstanceMap result = (RdbmsEnterpriseInstanceMap)query.getSingleResult();
            return result;

        } catch (NoResultException ex) {
            return null;
        }
    }

    private static RdbmsEnterpriseInstanceMap findMappingByResourceId(EntityManager entityManager, String resourceType, UUID resourceId) {


        String sql = "select c"
                + " from"
                + " RdbmsEnterpriseInstanceMap c"
                + " where c.resourceType = :resourceType"
                + " and c.resourceIdFrom = :resourceIdFrom";

        Query query = entityManager.createQuery(sql, RdbmsEnterpriseInstanceMap.class)
                .setParameter("resourceType", resourceType)
                .setParameter("resourceIdFrom", resourceId.toString());

        try {
            RdbmsEnterpriseInstanceMap result = (RdbmsEnterpriseInstanceMap)query.getSingleResult();
            return result;

        } catch (NoResultException ex) {
            return null;
        }
    }
}
