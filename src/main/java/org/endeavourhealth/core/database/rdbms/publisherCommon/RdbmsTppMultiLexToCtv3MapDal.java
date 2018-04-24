package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.TppMultiLexToCtv3MapDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppMultiLexToCtv3Map;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsTppMultilexToCtv3Map;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class RdbmsTppMultiLexToCtv3MapDal implements TppMultiLexToCtv3MapDalI {
    @Override
    public TppMultiLexToCtv3Map getMultiLexToCTV3Map(long multiLexProductId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsTppMultilexToCtv3Map c"
                    + " where c.multilexProductId = :multilex_product_id";

            Query query = entityManager.createQuery(sql, RdbmsTppMultilexToCtv3Map.class)
                    .setParameter("multilex_product_id", multiLexProductId);

            try {
                List<RdbmsTppMultilexToCtv3Map> result = (List<RdbmsTppMultilexToCtv3Map>) query.getResultList();

                if (result.size() > 0) {
                    return new TppMultiLexToCtv3Map(result.get(0));
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

    @Override
    public void save(TppMultiLexToCtv3Map mapping) throws Exception {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsTppMultilexToCtv3Map multiLexToCTV3Map = new RdbmsTppMultilexToCtv3Map(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_multilex_to_ctv3_map "
                    + " (row_id, multilex_product_id, ctv3_read_code, ctv3_read_term, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " multilex_product_id = VALUES(multilex_product_id),"
                    + " ctv3_read_code = VALUES(ctv3_read_code),"
                    + " ctv3_read_term = VALUES(ctv3_read_term),"
                    + " audit_json = VALUES(audit_json);";

            ps = connection.prepareStatement(sql);

            ps.setLong(1, multiLexToCTV3Map.getRowId());
            ps.setLong(2, multiLexToCTV3Map.getMultilexProductId());
            ps.setString(3,multiLexToCTV3Map.getCtv3ReadCode());
            ps.setString(4,multiLexToCTV3Map.getCtv3ReadTerm());
            ps.setString(5,multiLexToCTV3Map.getAuditJson());

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }
}
