package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.CTV3ToSnomedMapDalI;
import org.endeavourhealth.core.database.dal.reference.models.CTV3ToSnomedMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsCTV3ToSnomedMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class RdmsCTV3ToSnomedMapDal implements CTV3ToSnomedMapDalI {

    public CTV3ToSnomedMap getCTV3ToSnomedMap(String ctv3ConceptId) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsCTV3ToSnomedMap c"
                    + " where c.ctv3ConceptId = :ctv_concept_id"
                    + " and c.isAssured = :is_assured"
                    + " order by c.effectiveDate desc";

            Query query = entityManager.createQuery(sql, RdbmsCTV3ToSnomedMap.class)
                    .setParameter("ctv_concept_id", ctv3ConceptId)
                    .setParameter("is_assured", 1);

            try {
                List<RdbmsCTV3ToSnomedMap> result = (List<RdbmsCTV3ToSnomedMap>) query.getResultList();

                if (result.size() > 0) {
                    return new CTV3ToSnomedMap(result.get(0));
                } else {
                    return null;
                }
            } catch (NoResultException ex) {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }
}