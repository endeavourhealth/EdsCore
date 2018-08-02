package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.Read2ToSnomedMapDalI;
import org.endeavourhealth.core.database.dal.reference.models.Read2ToSnomedMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsRead2ToSnomedMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class RdbmsRead2ToSnomedMapDal implements Read2ToSnomedMapDalI {

    public Read2ToSnomedMap getRead2ToSnomedMap(String readCode) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsRead2ToSnomedMap c"
                    + " where c.readCode = :read_code"
                    + " order by c.termCode asc, c.effectiveDate desc";

            Query query = entityManager.createQuery(sql, RdbmsRead2ToSnomedMap.class)
                    .setParameter("read_code", readCode);

            try {
                List<RdbmsRead2ToSnomedMap> result = (List<RdbmsRead2ToSnomedMap>) query.getResultList();

                if (result.size() > 0) {
                    return new Read2ToSnomedMap(result.get(0));
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