package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.Msoa2011DalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsMsoa2011Lookup;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RdbmsMsoa2011Dal implements Msoa2011DalI {

    public String lookupMsoa2011Code(String msoa2011Code) throws Exception {
        msoa2011Code = msoa2011Code.trim();

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsMsoa2011Lookup c"
                    + " where c.msoa2011Code = :msoa_2011_code";

            Query query = entityManager
                    .createQuery(sql, RdbmsMsoa2011Lookup.class)
                    .setParameter("msoa_2011_code", msoa2011Code);

            try {
                RdbmsMsoa2011Lookup result = (RdbmsMsoa2011Lookup) query.getSingleResult();
                return result.getMsoa2011Code();

            } catch (NoResultException ex){
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    public void updateMsoa2011Lookup(String msoa2011Code, String msoa2011name) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select r"
                    + " from"
                    + " RdbmsMsoa2011Lookup r"
                    + " where r.msoa2011Code = :msoa_2011_code";

            Query query = entityManager
                    .createQuery(sql, RdbmsMsoa2011Lookup.class)
                    .setParameter("msoa_2011_code", msoa2011Code);

            RdbmsMsoa2011Lookup lookup = null;

            try {
                lookup = (RdbmsMsoa2011Lookup) query.getSingleResult();
            } catch (NoResultException ex) {
                lookup = new RdbmsMsoa2011Lookup();
                lookup.setMsoa2011Code(msoa2011Code);
            }

            lookup.setMsoa2011Name(msoa2011name);

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