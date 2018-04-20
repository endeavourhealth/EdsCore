package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.TppMappingRefDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.TppMappingRef;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsTppMappingRef;
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

public class RdbmsTppMappingRefDal implements TppMappingRefDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppMappingRefDal.class);

    @Override
    public TppMappingRef getMappingFromRowId(Long rowId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsTppMappingRef c"
                    + " where c.serviceId = :service_id"
                    + " and c.rowId = :row_id";

            Query query = entityManager.createQuery(sql, RdbmsTppMappingRef.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("row_id", rowId)
                    .setMaxResults(1);

            try {
                RdbmsTppMappingRef result = (RdbmsTppMappingRef)query.getSingleResult();
                return new TppMappingRef(result);
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
    public TppMappingRef getMappingFromRowAndGroupId(Long rowId, Long groupId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsTppMappingRef c"
                    + " where c.serviceId = :service_id"
                    + " and c.groupId = :group_id"
                    + " and c.rowId = :row_id";

            Query query = entityManager.createQuery(sql, RdbmsTppMappingRef.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("row_id", rowId)
                    .setParameter("group_id", groupId)
                    .setMaxResults(1);

            try {
                RdbmsTppMappingRef result = (RdbmsTppMappingRef)query.getSingleResult();
                return new TppMappingRef(result);
            }
            catch (NoResultException e) {
                LOG.error("No code found for rowId " + rowId + ", groupId " + groupId + ", service " + serviceId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void save(TppMappingRef mapping, UUID serviceId) throws Exception
    {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsTppMappingRef tppMapping = new RdbmsTppMappingRef(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_mapping_ref "
                    + " (row_id, group_id, mapped_term, service_id, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " mapped_term = VALUES(mapped_term),"
                    + " audit_json = VALUES(audit_json);";

            ps = connection.prepareStatement(sql);
            // Only JSON audit field is nullable
            ps.setLong(1, tppMapping.getRowId());
            ps.setLong(2, tppMapping.getGroupId());
            ps.setString(3,tppMapping.getMappedTerm());
            ps.setString(4,tppMapping.getServiceId());
            if (tppMapping.getAuditJson() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, tppMapping.getAuditJson());
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
