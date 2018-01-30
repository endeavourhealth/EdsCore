package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.Opcs4DalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsOpcs4Lookup;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RdbmsOpcs4Dal implements Opcs4DalI {

    @Override
    public String lookupCode(String opcs4Code) throws Exception {

        //sanitise the code to be in the expected format:
        //either three chars only e.g. Y51
        //or three chars, a dot, then more chars e.g. Y51.2
        opcs4Code = opcs4Code.trim();

        if (opcs4Code.length() > 3
                && opcs4Code.indexOf(".") == -1) {

            String prefix = opcs4Code.substring(0, 3);
            String suffix = opcs4Code.substring(3);
            opcs4Code = prefix + "." + suffix;
        }

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsOpcs4Lookup c"
                    + " where c.procedureCode = :procedure_code";

            Query query = entityManager.createQuery(sql, RdbmsOpcs4Lookup.class)
                    .setParameter("procedure_code", opcs4Code);

            try {
                RdbmsOpcs4Lookup result = (RdbmsOpcs4Lookup)query.getSingleResult();
                return result.getProcedureName();

            } catch (NoResultException ex) {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    public void updateOpcs4Lookup(String procedureCode, String procedureName) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();
        try {
            String sql = "select r"
                    + " from RdbmsOpcs4Lookup r"
                    + " where r.procedureCode = :procedure_code";


            Query query = entityManager
                    .createQuery(sql, RdbmsOpcs4Lookup.class)
                    .setParameter("procedure_code", procedureCode);

            RdbmsOpcs4Lookup lookup = null;
            try {
                lookup = (RdbmsOpcs4Lookup)query.getSingleResult();
            } catch (NoResultException e) {
                lookup = new RdbmsOpcs4Lookup();
                lookup.setProcedureCode(procedureCode);
            }

            lookup.setProcedureName(procedureName);

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
