package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.TppCtv3LookupDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppCtv3Lookup;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsTppCtv3Lookup;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;

public class RdbmsTppCtv3LookupDal implements TppCtv3LookupDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppCtv3LookupDal.class);

    @Override
    public TppCtv3Lookup getContentFromRowId(Long rowId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsTppCtv3Lookup c"
                    + " where c.rowId = :row_id";

            Query query = entityManager.createQuery(sql, RdbmsTppCtv3Lookup.class)
                    .setParameter("row_id", rowId)
                    .setMaxResults(1);

            try {
                RdbmsTppCtv3Lookup result = (RdbmsTppCtv3Lookup) query.getSingleResult();
                return new TppCtv3Lookup(result);
            } catch (NoResultException e) {
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
    public TppCtv3Lookup getContentFromCtv3Code(String ctv3Code) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsTppCtv3Lookup c"
                    + " where c.ctv3Code = :code";

            Query query = entityManager.createQuery(sql, RdbmsTppCtv3Lookup.class)
                    .setParameter("code", ctv3Code)
                    .setMaxResults(1);

            try {
                RdbmsTppCtv3Lookup result = (RdbmsTppCtv3Lookup) query.getSingleResult();
                return new TppCtv3Lookup(result);
            } catch (NoResultException e) {
                LOG.error("No code found for ctv3 code " + ctv3Code);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void save(TppCtv3Lookup ctv3Lookup) throws Exception {
        if (ctv3Lookup == null) {
            throw new IllegalArgumentException("ctv3 lookup is null");
        }

        RdbmsTppCtv3Lookup tppCtv3Lookup = new RdbmsTppCtv3Lookup(ctv3Lookup);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_ctv3_lookup "
                    + " (row_id, ctv3_code, ctv3_text, audit_json)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " row_id = VALUES(row_id), "
                    + " ctv3_code = VALUES(ctv3_code),"
                    + " ctv3_text = VALUES(ctv3_text),"
                    + " audit_json = VALUES(audit_json);";

            ps = connection.prepareStatement(sql);
            // Only JSON audit field is nullable
            ps.setLong(1, tppCtv3Lookup.getRowId());
            ps.setString(2, tppCtv3Lookup.getCtv3Code());
            ps.setString(3, tppCtv3Lookup.getCtv3Text());
            if (tppCtv3Lookup.getAuditJson() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, tppCtv3Lookup.getAuditJson());
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

    @Override
    public void save(List<TppCtv3Lookup> ctv3Lookups) throws Exception {
        if (ctv3Lookups == null || ctv3Lookups.isEmpty()) {
            throw new IllegalArgumentException("ctv3 lookup is null or empty");
        }

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;
        try {
            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_ctv3_lookup "
                    + " (row_id, ctv3_code, ctv3_text, audit_json)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " row_id = VALUES(row_id), "
                    + " ctv3_code = VALUES(ctv3_code),"
                    + " ctv3_text = VALUES(ctv3_text),"
                    + " audit_json = VALUES(audit_json);";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (TppCtv3Lookup lookup : ctv3Lookups) {

                int col = 1;
                // Only JSON audit field is nullable
                ps.setLong(col++, lookup.getRowId());
                ps.setString(col++, lookup.getCtv3Code());
                ps.setString(col++, lookup.getCtv3Text());
                if (lookup.getAudit() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, lookup.getAudit().writeToJson());
                }

                ps.addBatch();
            }

            ps.executeBatch();

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
