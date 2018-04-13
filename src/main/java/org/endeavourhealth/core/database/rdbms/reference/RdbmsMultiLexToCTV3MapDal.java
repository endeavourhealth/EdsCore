package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.MultiLexToCTV3MapDalI;
import org.endeavourhealth.core.database.dal.reference.models.MultiLexToCTV3Map;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsCTV3ToSnomedMap;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsMultiLexToCTV3Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class RdbmsMultiLexToCTV3MapDal implements MultiLexToCTV3MapDalI {

    public MultiLexToCTV3Map getMultiLexToCTV3Map(long multiLexProductId) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsMultiLexToCTV3Map c"
                    + " where c.multiLexProductId = :multilex_product_id";

            Query query = entityManager.createQuery(sql, RdbmsCTV3ToSnomedMap.class)
                    .setParameter("multilex_product_id", multiLexProductId);

            try {
                List<RdbmsMultiLexToCTV3Map> result = (List<RdbmsMultiLexToCTV3Map>) query.getResultList();

                if (result.size() > 0) {
                    return new MultiLexToCTV3Map(result.get(0));
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