package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.TppImmunisationContentDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.TppImmunisationContent;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsTppImmunisationContent;
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

public class RdbmsTppImmunisationContentDal implements TppImmunisationContentDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppImmunisationContentDal.class);

    @Override
    public TppImmunisationContent getContentFromRowId(Long rowId, UUID serviceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsTppImmunisationContent c"
                    + " where c.serviceId = :service_id"
                    + " and c.rowId = :row_id";

            Query query = entityManager.createQuery(sql, RdbmsTppImmunisationContent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("row_id", rowId)
                    .setMaxResults(1);

            try {
                RdbmsTppImmunisationContent result = (RdbmsTppImmunisationContent)query.getSingleResult();
                return new TppImmunisationContent(result);
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
    public void save(TppImmunisationContent mapping, UUID serviceId) throws Exception {

        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsTppImmunisationContent tppImmunisationContent = new RdbmsTppImmunisationContent(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_immunisation_content "
                    + " (row_id, name, content, service_id, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " row_id = VALUES(row_id), "
                    + " name = VALUES(name),"
                    + " content = VALUES(content),"
                    + " service_id = VALUES(service_id),"
                    + " audit_json = VALUES(audit_json);";

            ps = connection.prepareStatement(sql);
            // Only JSON audit field is nullable
            ps.setLong(1, tppImmunisationContent.getRowId());
            ps.setString(2, tppImmunisationContent.getName());
            ps.setString(3,tppImmunisationContent.getContent());
            ps.setString(4,tppImmunisationContent.getServiceId());
            if (tppImmunisationContent.getAuditJson() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, tppImmunisationContent.getAuditJson());
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
