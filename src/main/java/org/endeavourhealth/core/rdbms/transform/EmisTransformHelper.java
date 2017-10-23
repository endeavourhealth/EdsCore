package org.endeavourhealth.core.rdbms.transform;

import org.endeavourhealth.core.rdbms.ConnectionManager;
import org.endeavourhealth.core.rdbms.admin.models.Service;
import org.endeavourhealth.core.rdbms.transform.models.EmisAdminResourceCache;
import org.endeavourhealth.core.rdbms.transform.models.EmisCsvCodeMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Iterator;
import java.util.List;

public class EmisTransformHelper {

    public void save(EmisCsvCodeMap mapping) throws Exception {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        EntityManager entityManager = ConnectionManager.getTransformEntityManager();
        entityManager.persist(mapping);
        entityManager.close();
    }

    public EmisCsvCodeMap getMostRecentCode(String dataSharingAgreementGuid, boolean medication, Long codeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " EmisCsvCodeMap c"
                + " where c.dataSharingAgreementGuid = :data_sharing_agreement_guid"
                + " and c.medication = :medication"
                + " and c.codeId = :code_id";

        Query query = entityManager.createQuery(sql, EmisCsvCodeMap.class)
                .setParameter("data_sharing_agreement_guid", dataSharingAgreementGuid)
                .setParameter("medication", new Boolean(medication))
                .setParameter("code_id", codeId);

        EmisCsvCodeMap ret = null;
        try {
            ret = (EmisCsvCodeMap)query.getSingleResult();

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

        EntityManager entityManager = ConnectionManager.getTransformEntityManager();
        entityManager.persist(resourceCache);
        entityManager.close();
    }

    public void delete(EmisAdminResourceCache resourceCache) throws Exception {
        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache is null");
        }

        EntityManager entityManager = ConnectionManager.getTransformEntityManager();
        entityManager.remove(resourceCache);
        entityManager.close();
    }

    public List<EmisAdminResourceCache> getCachedResources(String dataSharingAgreementGuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " EmisAdminResourceCache c"
                + " where c.dataSharingAgreementGuid = :data_sharing_agreement_guid";

        Query query = entityManager.createQuery(sql, EmisAdminResourceCache.class)
                .setParameter("data_sharing_agreement_guid", dataSharingAgreementGuid);

        List<EmisAdminResourceCache> ret = query.getResultList();

        entityManager.close();

        return ret;
    }
}
