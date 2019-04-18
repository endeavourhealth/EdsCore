package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.Msoa2001DalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsMsoa2001Lookup;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RdbmsMsoa2001Dal implements Msoa2001DalI {

    public String lookupMsoa2001Code(String msoa2001Code) throws Exception {
        msoa2001Code = msoa2001Code.trim();

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsMsoa2001Lookup c"
                    + " where c.msoa2001Code = :msoa_2001_code";

            Query query = entityManager
                    .createQuery(sql, RdbmsMsoa2001Lookup.class)
                    .setParameter("msoa_2001_code", msoa2001Code);

            try {
                RdbmsMsoa2001Lookup result = (RdbmsMsoa2001Lookup) query.getSingleResult();
                return result.getMsoa2001Code();

            } catch (NoResultException ex){
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    public void updateMsoa2001Lookup(String msoa2001Code, String msoa2001name) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select r"
                    + " from"
                    + " RdbmsMsoa2001Lookup r"
                    + " where r.msoa2001Code = :msoa_2001_code";

            Query query = entityManager
                    .createQuery(sql, RdbmsMsoa2001Lookup.class)
                    .setParameter("msoa_2001_code", msoa2001Code);

            RdbmsMsoa2001Lookup lookup = null;

            try {
                lookup = (RdbmsMsoa2001Lookup) query.getSingleResult();
            } catch (NoResultException ex) {
                lookup = new RdbmsMsoa2001Lookup();
                lookup.setMsoa2001Code(msoa2001Code);
            }

            lookup.setMsoa2001Name(msoa2001name);

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