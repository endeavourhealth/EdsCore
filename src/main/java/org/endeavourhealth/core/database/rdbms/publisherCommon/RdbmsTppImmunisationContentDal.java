package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppImmunisationContent;
import org.endeavourhealth.core.database.dal.publisherCommon.TppImmunisationContentDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsTppImmunisationContent;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

public class RdbmsTppImmunisationContentDal implements TppImmunisationContentDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppImmunisationContentDal.class);

    @Override
    public TppImmunisationContent getContentFromRowId(Long rowId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsTppImmunisationContent c"
                    + " where c.rowId = :row_id";

            Query query = entityManager.createQuery(sql, RdbmsTppImmunisationContent.class)
                    .setParameter("row_id", rowId)
                    .setMaxResults(1);

            try {
                RdbmsTppImmunisationContent result = (RdbmsTppImmunisationContent)query.getSingleResult();
                return new TppImmunisationContent(result);
            }
            catch (NoResultException e) {
                LOG.error("No code found for rowId " + rowId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void save(TppImmunisationContent mapping) throws Exception {

        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsTppImmunisationContent tppImmunisationContent = new RdbmsTppImmunisationContent(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
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
