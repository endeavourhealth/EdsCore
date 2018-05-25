package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.TppMappingRefDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppMappingRef;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsTppMappingRef;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

public class RdbmsTppMappingRefDal implements TppMappingRefDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppMappingRefDal.class);

    @Override
    public TppMappingRef getMappingFromRowId(Long rowId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsTppMappingRef c"
                    + " where c.rowId = :row_id";

            Query query = entityManager.createQuery(sql, RdbmsTppMappingRef.class)
                    .setParameter("row_id", rowId)
                    .setMaxResults(1);

            try {
                RdbmsTppMappingRef result = (RdbmsTppMappingRef)query.getSingleResult();
                return new TppMappingRef(result);
            }
            catch (NoResultException e) {
                LOG.error("No mapping code found for rowId " + rowId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public TppMappingRef getMappingFromRowAndGroupId(Long rowId, Long groupId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsTppMappingRef c"
                    + " where c.groupId = :group_id"
                    + " and c.rowId = :row_id";

            Query query = entityManager.createQuery(sql, RdbmsTppMappingRef.class)
                    .setParameter("row_id", rowId)
                    .setParameter("group_id", groupId)
                    .setMaxResults(1);

            try {
                RdbmsTppMappingRef result = (RdbmsTppMappingRef)query.getSingleResult();
                return new TppMappingRef(result);
            }
            catch (NoResultException e) {
                LOG.error("No mapping code found for rowId " + rowId + ", groupId " + groupId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void save(TppMappingRef mapping) throws Exception
    {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsTppMappingRef tppMapping = new RdbmsTppMappingRef(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_mapping_ref "
                    + " (row_id, group_id, mapped_term, audit_json)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " row_id = VALUES(row_id),"
                    + " group_id = VALUES(group_id),"
                    + " mapped_term = VALUES(mapped_term),"
                    + " audit_json = VALUES(audit_json);";

            ps = connection.prepareStatement(sql);
            // Only JSON audit field is nullable
            ps.setLong(1, tppMapping.getRowId());
            ps.setLong(2, tppMapping.getGroupId());
            ps.setString(3,tppMapping.getMappedTerm());
            if (tppMapping.getAuditJson() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, tppMapping.getAuditJson());
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
