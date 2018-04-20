package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.TppConfigListOptionDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.TppConfigListOption;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsTppConfigListOption;
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

public class RdbmsTppConfigListOptionDal implements TppConfigListOptionDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppConfigListOptionDal.class);

    @Override
    public TppConfigListOption getListOptionFromRowId(Long rowId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsTppConfigListOption c"
                    + " where c.serviceId = :service_id"
                    + " and c.rowId = :row_id";

            Query query = entityManager.createQuery(sql, RdbmsTppConfigListOption.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("row_id", rowId)
                    .setMaxResults(1);

            try {
                RdbmsTppConfigListOption result = (RdbmsTppConfigListOption)query.getSingleResult();
                return new TppConfigListOption(result);
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
    public TppConfigListOption getListOptionFromRowAndListId(Long rowId, Long configListId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsTppConfigListOption c"
                    + " where c.serviceId = :service_id"
                    + " and c.configListId = :config_list_id"
                    + " and c.rowId = :row_id";

            Query query = entityManager.createQuery(sql, RdbmsTppConfigListOption.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("config_list_id", configListId)
                    .setParameter("row_id", rowId)
                    .setMaxResults(1);

            try {
                RdbmsTppConfigListOption result = (RdbmsTppConfigListOption)query.getSingleResult();
                return new TppConfigListOption(result);
            }
            catch (NoResultException e) {
                LOG.error("No code found for rowId " + rowId + ", configListId " + configListId + ", service " + serviceId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void save(TppConfigListOption configListOption, UUID serviceId) throws Exception
    {
        if (configListOption == null) {
            throw new IllegalArgumentException("configListOption is null");
        }

        RdbmsTppConfigListOption tppConfigListOption = new RdbmsTppConfigListOption(configListOption);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO tpp_config_list_option "
                    + " (row_id, config_list_id, list_option_name, service_id, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " list_option_name = VALUES(list_option_name),"
                    + " audit_json = VALUES(audit_json);";

            ps = connection.prepareStatement(sql);
            // Only JSON audit field is nullable
            ps.setLong(1, tppConfigListOption.getRowId());
            ps.setLong(2, tppConfigListOption.getConfigListId());
            ps.setString(3,tppConfigListOption.getListOptionName());
            ps.setString(4,tppConfigListOption.getServiceId());
            if (tppConfigListOption.getAuditJson() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, tppConfigListOption.getAuditJson());
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
