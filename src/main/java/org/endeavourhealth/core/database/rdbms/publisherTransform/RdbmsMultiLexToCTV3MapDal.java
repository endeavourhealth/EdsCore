package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.MultiLexToCTV3MapDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.MultiLexToCTV3Map;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsMultiLexToCTV3Map;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

public class RdbmsMultiLexToCTV3MapDal implements MultiLexToCTV3MapDalI {

    public MultiLexToCTV3Map getMultiLexToCTV3Map(long multiLexProductId,  UUID serviceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsMultiLexToCTV3Map c"
                    + " where c.multiLexProductId = :multilex_product_id";

            Query query = entityManager.createQuery(sql, RdbmsMultiLexToCTV3Map.class)
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

    public void save(MultiLexToCTV3Map mapping, UUID serviceId) throws Exception
    {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsMultiLexToCTV3Map multiLexToCTV3Map = new RdbmsMultiLexToCTV3Map(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO multilex_to_ctv3_map "
                    + " (row_id, multilex_product_id, ctv3_read_code, ctv3_read_term)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " multilex_product_id = VALUES(multilex_product_id),"
                    + " ctv3_read_code = VALUES(ctv3_read_code),"
                    + " ctv3_read_term = VALUES(ctv3_read_term)";

            ps = connection.prepareStatement(sql);

            ps.setLong(1, multiLexToCTV3Map.getRowId());
            ps.setLong(2, multiLexToCTV3Map.getMultiLexProductId());
            ps.setString(3,multiLexToCTV3Map.getCTV3ReadCode());
            ps.setString(4,multiLexToCTV3Map.getCTV3ReadTerm());

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