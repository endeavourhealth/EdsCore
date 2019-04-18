package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.Lsoa2011DalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsLsoa2011Lookup;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RdbmsLsoa2011Dal implements Lsoa2011DalI {

    public String lookupLsoa2011Code(String lsoa2011Code) throws Exception {
        lsoa2011Code = lsoa2011Code.trim();

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsLsoa2011Lookup c"
                    + " where c.lsoa2011Code = :lsoa_2011_code";

            Query query = entityManager
                    .createQuery(sql, RdbmsLsoa2011Lookup.class)
                    .setParameter("lsoa_2011_code", lsoa2011Code);

            try {
                RdbmsLsoa2011Lookup result = (RdbmsLsoa2011Lookup) query.getSingleResult();
                return result.getLsoa2011Code();

            } catch (NoResultException ex){
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    public void updateLsoa2011Lookup(String lsoa2011Code, String lsoa2011name) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select r"
                    + " from"
                    + " RdbmsLsoa2011Lookup r"
                    + " where r.lsoa2011Code = :lsoa_2011_code";

            Query query = entityManager
                    .createQuery(sql, RdbmsLsoa2011Lookup.class)
                    .setParameter("lsoa_2011_code", lsoa2011Code);

            RdbmsLsoa2011Lookup lookup = null;

            try {
                lookup = (RdbmsLsoa2011Lookup) query.getSingleResult();
            } catch (NoResultException ex) {
                lookup = new RdbmsLsoa2011Lookup();
                lookup.setLsoa2011Code(lsoa2011Code);
            }

            lookup.setLsoa2011Name(lsoa2011name);

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