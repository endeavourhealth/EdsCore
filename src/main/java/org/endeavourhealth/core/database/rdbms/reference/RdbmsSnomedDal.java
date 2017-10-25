package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.SnomedDalI;
import org.endeavourhealth.core.database.dal.reference.models.SnomedLookup;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsSnomedLookup;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RdbmsSnomedDal implements SnomedDalI {

    public SnomedLookup getSnomedLookup(String conceptId) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsSnomedLookup c"
                + " where c.conceptId = :concept_id";

        Query query = entityManager.createQuery(sql, RdbmsSnomedLookup.class)
                .setParameter("concept_id", conceptId);

        SnomedLookup ret = null;
        try {
            RdbmsSnomedLookup result = (RdbmsSnomedLookup)query.getSingleResult();
            ret = new SnomedLookup(result);

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }
}
