package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.TppProfileRoleDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.TppProfileRole;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsTppProfileRole;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.UUID;

public class RdbmsTppProfileRoleDal implements TppProfileRoleDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppProfileRoleDal.class);

    @Override
    public TppProfileRole getContentFromRowId(Long rowId, UUID serviceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select r"
                    + " from "
                    + " RdbmsTppProfileRole r"
                    + " where r.rowId = :row_id";

            Query query = entityManager.createQuery(sql, RdbmsTppProfileRole.class)
                    .setParameter("row_id", rowId)
                    .setMaxResults(1);

            try {
                RdbmsTppProfileRole result = (RdbmsTppProfileRole)query.getSingleResult();
                return new TppProfileRole(result);
            }
            catch (NoResultException e) {
                LOG.error("No code found for rowId " + rowId + ", service " + serviceId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void save(TppProfileRole mapping, UUID serviceId) throws Exception {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsTppProfileRole tppImmunisationContent = new RdbmsTppProfileRole(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_profile_role "
                    + " (row_id, role_description, audit_json)"
                    + " VALUES (?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " row_id = VALUES(row_id), "
                    + " role_description = VALUES(role_description),"
                    + " audit_json = VALUES(audit_json);";

            ps = connection.prepareStatement(sql);
            // Only JSON audit field is nullable
            ps.setLong(1, tppImmunisationContent.getRowId());
            ps.setString(2, tppImmunisationContent.getRoleDescription());
            if (tppImmunisationContent.getAuditJson() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, tppImmunisationContent.getAuditJson());
            }

            ps.executeUpdate();

            //transaction.commit();
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
