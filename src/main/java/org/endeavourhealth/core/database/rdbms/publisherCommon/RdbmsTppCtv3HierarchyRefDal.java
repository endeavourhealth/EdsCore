package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.TppCtv3HierarchyRefDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppCtv3HierarchyRef;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsTppCtv3HierarchyRef;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class RdbmsTppCtv3HierarchyRefDal implements TppCtv3HierarchyRefDalI {
    @Override
    public boolean isChildCodeUnderParentCode(String childReadCode, String ParentReadCode) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsTppCtv3HierarchyRef c"
                    + " where c.ctv3ChildReadCode = :ctv3_child_read_code"
                    + " and c.ctv3ParentReadCode = :ctv3_parent_read_code";

            Query query = entityManager.createQuery(sql, RdbmsTppCtv3HierarchyRef.class)
                    .setParameter("ctv3_child_read_code", childReadCode)
                    .setParameter("ctv3_parent_read_code", ParentReadCode);

            try {
                List<RdbmsTppCtv3HierarchyRef> result = (List<RdbmsTppCtv3HierarchyRef>) query.getResultList();
                return  (result.size() > 0);

            } catch (NoResultException ex) {
                return false;
            }

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void save(TppCtv3HierarchyRef ref) throws Exception {
        if (ref == null) {
            throw new IllegalArgumentException("ref is null");
        }

        RdbmsTppCtv3HierarchyRef ctv3HierarchyRef = new RdbmsTppCtv3HierarchyRef(ref);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_ctv3_hierarchy_ref "
                    + " (row_id, ctv3_parent_read_code, ctv3_child_read_code, child_level)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " ctv3_parent_read_code = VALUES(ctv3_parent_read_code),"
                    + " ctv3_child_read_code = VALUES(ctv3_child_read_code),"
                    + " child_level = VALUES(child_level)";

            ps = connection.prepareStatement(sql);

            ps.setLong(1, ctv3HierarchyRef.getRowId());
            ps.setString(2, ctv3HierarchyRef.getCtv3ParentReadCode());
            ps.setString(3,ctv3HierarchyRef.getCtv3ChildReadCode());
            ps.setInt(4,ctv3HierarchyRef.getChildLevel());

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
