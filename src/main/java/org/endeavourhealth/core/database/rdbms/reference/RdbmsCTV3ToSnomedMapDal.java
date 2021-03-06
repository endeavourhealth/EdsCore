package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.CTV3ToSnomedMapDalI;
import org.endeavourhealth.core.database.dal.reference.models.CTV3ToSnomedMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsCTV3ToSnomedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class RdbmsCTV3ToSnomedMapDal implements CTV3ToSnomedMapDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsCTV3ToSnomedMapDal.class);

    public CTV3ToSnomedMap getCTV3ToSnomedMap(String ctv3ConceptId) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsCTV3ToSnomedMap c"
                    + " where c.ctv3ConceptId = :ctv3_concept_id"
                    //+ " and c.isAssured = :is_assured"
                    + " and c.ctv3TermType = :ctv3_term_type"
                    + " order by c.effectiveDate desc";

            Query query = entityManager.createQuery(sql, RdbmsCTV3ToSnomedMap.class)
                    .setParameter("ctv3_concept_id", ctv3ConceptId)
                    .setParameter("ctv3_term_type", "P");
                    //.setParameter("is_assured", 1);

            try {
                List<RdbmsCTV3ToSnomedMap> results = (List<RdbmsCTV3ToSnomedMap>) query.getResultList();
                for (RdbmsCTV3ToSnomedMap result: results) {

                    //there are thousands of CTV3->Snomed mappings where the snomed concept is _DRUG
                    //which I can't explain. But ignore those and return ones with proper snomed concept IDs
                    if (!result.getSctConceptId().equals("_DRUG")) {
                        return new CTV3ToSnomedMap(result);
                    }
                }

                return null;

            } catch (NoResultException ex) {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }
}