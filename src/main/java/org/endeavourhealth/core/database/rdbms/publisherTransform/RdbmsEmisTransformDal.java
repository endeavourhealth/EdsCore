package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.EmisTransformDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.EmisAdminResourceCache;
import org.endeavourhealth.core.database.dal.publisherTransform.models.EmisCsvCodeMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsEmisAdminResourceCache;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsEmisCsvCodeMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

public class RdbmsEmisTransformDal implements EmisTransformDalI {

    public void save(EmisCsvCodeMap mapping) throws Exception
    {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsEmisCsvCodeMap emisMapping = new RdbmsEmisCsvCodeMap(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        entityManager.persist(emisMapping);
        entityManager.close();
    }

    public EmisCsvCodeMap getMostRecentCode(String dataSharingAgreementGuid, boolean medication, Long codeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsEmisCsvCodeMap c"
                + " where c.dataSharingAgreementGuid = :data_sharing_agreement_guid"
                + " and c.medication = :medication"
                + " and c.codeId = :code_id";

        Query query = entityManager.createQuery(sql, RdbmsEmisCsvCodeMap.class)
                .setParameter("data_sharing_agreement_guid", dataSharingAgreementGuid)
                .setParameter("medication", new Boolean(medication))
                .setParameter("code_id", codeId);

        EmisCsvCodeMap ret = null;
        try {
            RdbmsEmisCsvCodeMap emisMapping = (RdbmsEmisCsvCodeMap)query.getSingleResult();
            ret = new EmisCsvCodeMap(emisMapping);

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    public void save(EmisAdminResourceCache resourceCache) throws Exception {
        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache is null");
        }

        RdbmsEmisAdminResourceCache emisObj = new RdbmsEmisAdminResourceCache(resourceCache);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        entityManager.persist(emisObj);
        entityManager.close();
    }

    public void delete(EmisAdminResourceCache resourceCache) throws Exception {
        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache is null");
        }

        RdbmsEmisAdminResourceCache emisObj = new RdbmsEmisAdminResourceCache(resourceCache);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        entityManager.remove(emisObj);
        entityManager.close();
    }

    public List<EmisAdminResourceCache> getCachedResources(String dataSharingAgreementGuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsEmisAdminResourceCache c"
                + " where c.dataSharingAgreementGuid = :data_sharing_agreement_guid";

        Query query = entityManager.createQuery(sql, RdbmsEmisAdminResourceCache.class)
                .setParameter("data_sharing_agreement_guid", dataSharingAgreementGuid);

        List<RdbmsEmisAdminResourceCache> results = query.getResultList();

        entityManager.close();

        List<EmisAdminResourceCache> ret = results
                .stream()
                .map(T -> new EmisAdminResourceCache(T))
                .collect(Collectors.toList());

        return ret;
    }
}
