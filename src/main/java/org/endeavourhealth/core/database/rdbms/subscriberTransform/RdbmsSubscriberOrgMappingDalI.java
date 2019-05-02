package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.SubscriberOrgMappingDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsEnterpriseOrganisationIdMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RdbmsSubscriberOrgMappingDalI implements SubscriberOrgMappingDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsSubscriberOrgMappingDalI.class);

    private String subscriberConfigName = null;

    public RdbmsSubscriberOrgMappingDalI(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    @Override
    public void saveEnterpriseOrganisationId(String serviceId, Long enterpriseId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            RdbmsEnterpriseOrganisationIdMap mapping = findEnterpriseOrganisationMapping(serviceId, entityManager);
            if (mapping != null) {
                throw new Exception("EnterpriseOrganisationIdMap already exists for service " + serviceId + " config " + subscriberConfigName);
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

    @Override
    public Long findEnterpriseOrganisationId(String serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            RdbmsEnterpriseOrganisationIdMap mapping = findEnterpriseOrganisationMapping(serviceId, entityManager);
            if (mapping != null) {
                return mapping.getEnterpriseId();
            } else {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    private static RdbmsEnterpriseOrganisationIdMap findEnterpriseOrganisationMapping(String serviceId, EntityManager entityManager) throws Exception {

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

}
