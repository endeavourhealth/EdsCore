package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.CTV3HierarchyRefDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.CTV3HierarchyRef;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsCTV3HierarchyRef;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

public class RdbmsCTV3HierarchyRefDal implements CTV3HierarchyRefDalI {

    public boolean isChildCodeUnderParentCode (String childReadCode, String ParentReadCode, UUID serviceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsCTV3HierarchyRef c"
                    + " where c.ctv3ChildReadCode = :ctv3_child_read_code"
                    + " and c.ctv3ParentReadCode = :ctv3_parent_read_code";

            Query query = entityManager.createQuery(sql, RdbmsCTV3HierarchyRef.class)
                    .setParameter("ctv3_child_read_code", childReadCode)
                    .setParameter("ctv3_parent_read_code", ParentReadCode);

            try {
                List<RdbmsCTV3HierarchyRef> result = (List<RdbmsCTV3HierarchyRef>) query.getResultList();
                return  (result.size() > 0);

            } catch (NoResultException ex) {
                return false;
            }

        } finally {
            entityManager.close();
        }
    }

    public void save(CTV3HierarchyRef ref, UUID serviceId) throws Exception
    {
        if (ref == null) {
            throw new IllegalArgumentException("ref is null");
        }

        RdbmsCTV3HierarchyRef ctv3HierarchyRef = new RdbmsCTV3HierarchyRef(ref);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO ctv3_hierarchy_ref "
                    + " (row_id, ctv3_parent_read_code, ctv3_child_read_code, child_level)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " ctv3_parent_read_code = VALUES(ctv3_parent_read_code),"
                    + " ctv3_child_read_code = VALUES(ctv3_child_read_code),"
                    + " child_level = VALUES(child_level)";

            ps = connection.prepareStatement(sql);

            ps.setLong(1, ctv3HierarchyRef.getRowId());
            ps.setString(2, ctv3HierarchyRef.getCTV3ParentReadCode());
            ps.setString(3,ctv3HierarchyRef.getCTV3ChildReadTerm());
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