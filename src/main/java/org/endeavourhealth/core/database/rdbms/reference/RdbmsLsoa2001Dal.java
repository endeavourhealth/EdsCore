package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.Lsoa2001DalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsLsoa2001Lookup;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RdbmsLsoa2001Dal implements Lsoa2001DalI {

    public String lookupLsoa2001Code(String lsoa2001Code) throws Exception {
        lsoa2001Code = lsoa2001Code.trim();

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsLsoa2001Lookup c"
                    + " where c.lsoa2001Code = :lsoa_2001_code";

            Query query = entityManager
                    .createQuery(sql, RdbmsLsoa2001Lookup.class)
                    .setParameter("lsoa_2001_code", lsoa2001Code);

            try {
                RdbmsLsoa2001Lookup result = (RdbmsLsoa2001Lookup) query.getSingleResult();
                return result.getLsoa2001Code();

            } catch (NoResultException ex){
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    public void updateLsoa2001Lookup(String lsoa2001Code, String lsoa2001name) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select r"
                    + " from"
                    + " RdbmsLsoa2001Lookup r"
                    + " where r.lsoa2001Code = :lsoa_2001_code";

            Query query = entityManager
                    .createQuery(sql, RdbmsLsoa2001Lookup.class)
                    .setParameter("lsoa_2001_code", lsoa2001Code);

            RdbmsLsoa2001Lookup lookup = null;

            try {
                lookup = (RdbmsLsoa2001Lookup) query.getSingleResult();
            } catch (NoResultException ex) {
                lookup = new RdbmsLsoa2001Lookup();
                lookup.setLsoa2001Code(lsoa2001Code);
            }

            lookup.setLsoa2001Name(lsoa2001name);

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