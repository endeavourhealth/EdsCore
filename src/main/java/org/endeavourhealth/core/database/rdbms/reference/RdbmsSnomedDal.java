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

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsSnomedLookup c"
                    + " where c.conceptId = :concept_id";

            Query query = entityManager.createQuery(sql, RdbmsSnomedLookup.class)
                    .setParameter("concept_id", conceptId);

            try {
                RdbmsSnomedLookup result = (RdbmsSnomedLookup) query.getSingleResult();
                return new SnomedLookup(result);

            } catch (NoResultException ex) {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    public SnomedLookup getSnomedLookupForDescId(String descriptionId) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsSnomedLookup c"
                    + " left join RdbmsSnomedDescriptionLink l"
                    + " on c.conceptId = l.conceptId"
                    + " where l.descriptionId = :description_id";

            Query query = entityManager.createQuery(sql, RdbmsSnomedLookup.class)
                    .setParameter("description_id", descriptionId);

            try {
                RdbmsSnomedLookup result = (RdbmsSnomedLookup) query.getSingleResult();
                return new SnomedLookup(result);

            } catch (NoResultException ex) {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }


}
