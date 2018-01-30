package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.Icd10DalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsIcd10Lookup;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RdbmsIcd10Dal implements Icd10DalI {

    @Override
    public String lookupCode(String icd10Code) throws Exception {

        //sanitise the code to be in the expected format:
        //either three chars only e.g. Y51
        //or three chars, a dot, then more chars e.g. Y51.2
        icd10Code = icd10Code.trim();

        if (icd10Code.length() > 3
                && icd10Code.indexOf(".") == -1) {

            String prefix = icd10Code.substring(0, 3);
            String suffix = icd10Code.substring(3);
            icd10Code = prefix + "." + suffix;
        }

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsIcd10Lookup c"
                    + " where c.code = :code";

            Query query = entityManager.createQuery(sql, RdbmsIcd10Lookup.class)
                    .setParameter("code", icd10Code);

            try {
                RdbmsIcd10Lookup result = (RdbmsIcd10Lookup)query.getSingleResult();
                return result.getDescription();

            } catch (NoResultException ex) {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void updateIcd10Lookup(String code, String description) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();
        try {
            String sql = "select r"
                    + " from RdbmsIcd10Lookup r"
                    + " where r.code = :code";


            Query query = entityManager
                    .createQuery(sql, RdbmsIcd10Lookup.class)
                    .setParameter("code", code);

            RdbmsIcd10Lookup lookup = null;
            try {
                lookup = (RdbmsIcd10Lookup)query.getSingleResult();
            } catch (NoResultException e) {
                lookup = new RdbmsIcd10Lookup();
                lookup.setCode(code);
            }

            lookup.setDescription(description);

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
