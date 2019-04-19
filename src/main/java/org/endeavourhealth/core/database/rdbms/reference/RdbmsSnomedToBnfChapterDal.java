package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.SnomedToBnfChapterDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsSnomedToBnfChapterLookup;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RdbmsSnomedToBnfChapterDal implements SnomedToBnfChapterDalI {

    public String lookupSnomedCode(String snomedCode) throws Exception {
        snomedCode = snomedCode.trim();

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsSnomedToBnfChapterLookup c"
                    + " where c.snomedCode = :snomed_code";

            Query query = entityManager
                    .createQuery(sql, RdbmsSnomedToBnfChapterLookup.class)
                    .setParameter("snomed_code", snomedCode);

            try {
                RdbmsSnomedToBnfChapterLookup result = (RdbmsSnomedToBnfChapterLookup) query.getSingleResult();
                return result.getBnfChapterCode();

            } catch (NoResultException ex){
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    public void updateSnomedToBnfChapterLookup(String snomedCode, String bnfChapterCode) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select r"
                    + " from"
                    + " RdbmsSnomedToBnfChapterLookup r"
                    + " where r.snomedCode = :snomed_code";

            Query query = entityManager
                    .createQuery(sql, RdbmsSnomedToBnfChapterLookup.class)
                    .setParameter("snomed_code", snomedCode);

            RdbmsSnomedToBnfChapterLookup lookup = null;

            try {
                lookup = (RdbmsSnomedToBnfChapterLookup) query.getSingleResult();
            } catch (NoResultException ex) {
                lookup = new RdbmsSnomedToBnfChapterLookup();
                lookup.setSnomedCode(snomedCode);
            }

            lookup.setBnfChapterCode(bnfChapterCode);

            entityManager.getTransaction().begin();
            entityManager.persist(lookup);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }
}