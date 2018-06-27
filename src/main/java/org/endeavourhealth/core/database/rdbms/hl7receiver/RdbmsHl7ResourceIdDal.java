package org.endeavourhealth.core.database.rdbms.hl7receiver;

import org.endeavourhealth.core.database.dal.hl7receiver.Hl7ResourceIdDalI;
import org.endeavourhealth.core.database.dal.hl7receiver.models.ResourceId;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class RdbmsHl7ResourceIdDal implements Hl7ResourceIdDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsHl7ResourceIdDal.class);

    /*
     *
     */
    public ResourceId getResourceId(String scope, String resourceType, String uniqueId) throws Exception {
        EntityManager entityManager = ConnectionManager.getHl7ReceiverEntityManager();

        PreparedStatement ps = null;

        try {
            if (!entityManager.isOpen())
                throw new IllegalStateException("No connection to HL7 DB");

            SessionImpl session = (SessionImpl) entityManager.getDelegate();

            Connection connection = session.connection();

            //syntax for postreSQL is slightly different
            String sql = null;
            if (ConnectionManager.isPostgreSQL(connection)) {
                sql = "SELECT resource_uuid FROM mapping.resource_uuid WHERE scope_id=? and resource_type=? and unique_identifier=?;";
                ps = connection.prepareStatement(sql);
                ps.setString(1, scope);
                ps.setString(2, resourceType);
                ps.setString(3, uniqueId);
            } else {
                sql = "SELECT resource_uuid FROM resource_uuid WHERE scope_id=? and resource_type=? and unique_identifier=?;";
                ps = connection.prepareStatement(sql);
                ps.setString(1, scope);
                ps.setString(2, resourceType);
                ps.setString(3, uniqueId);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ResourceId resourceId = new ResourceId();
                resourceId.setScopeId(scope);
                resourceId.setResourceType(resourceType);
                resourceId.setUniqueId(uniqueId);
                if (ConnectionManager.isPostgreSQL(connection)) {
                    resourceId.setResourceId((UUID) rs.getObject(1));
                } else {
                    resourceId.setResourceId(UUID.fromString(rs.getString(1)));
                }

                return resourceId;

            } else {
                return null;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    public void saveResourceId(ResourceId resourceId)  throws Exception {

        //RdbmsResourceId dbObj = new RdbmsResourceId(resourceId);

        EntityManager entityManager = ConnectionManager.getHl7ReceiverEntityManager();

        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            //syntax for postreSQL is slightly different
            String sql = null;
            if (ConnectionManager.isPostgreSQL(connection)) {
                sql = "INSERT INTO mapping.resource_uuid (scope_id, resource_type, unique_identifier, resource_uuid) VALUES (?, ?, ?, ?);";
                ps = connection.prepareStatement(sql);
                ps.setString(1, resourceId.getScopeId());
                ps.setString(2, resourceId.getResourceType());
                ps.setString(3, resourceId.getUniqueId());
                ps.setObject(4, resourceId.getResourceId());
            } else {
                sql = "INSERT INTO resource_uuid (scope_id, resource_type, unique_identifier, resource_uuid) VALUES (?, ?, ?, ?);";
                ps = connection.prepareStatement(sql);
                ps.setString(1, resourceId.getScopeId());
                ps.setString(2, resourceId.getResourceType());
                ps.setString(3, resourceId.getUniqueId());
                ps.setString(4, resourceId.getResourceId().toString());
            }

            ps.executeUpdate();

            entityManager.getTransaction().commit();

            //LOG.trace("Saved recourceId:" + resourceId.getUniqueId() + "==>" + resourceId.getResourceId());

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

    /**
     * unlike other DALs we want explicit separate insert and update functions, rather than a general-purpose upsert one
     */
    public void updateResourceId(ResourceId resourceId)  throws Exception {

        EntityManager entityManager = ConnectionManager.getHl7ReceiverEntityManager();

        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            //syntax for postreSQL is slightly different
            String sql = null;
            if (ConnectionManager.isPostgreSQL(connection)) {
                sql = "UPDATE mapping.resource_uuid SET resource_uuid = ? WHERE scope_id = ? AND resource_type = ? AND unique_identifier = ?;";
                ps = connection.prepareStatement(sql);
                ps.setObject(1, resourceId.getResourceId());
                ps.setString(2, resourceId.getScopeId());
                ps.setString(3, resourceId.getResourceType());
                ps.setString(4, resourceId.getUniqueId());

            } else {
                sql = "UPDATE resource_uuid SET resource_uuid = ? WHERE scope_id = ? AND resource_type = ? AND unique_identifier = ?;";
                ps = connection.prepareStatement(sql);
                ps.setString(1, resourceId.getResourceId().toString());
                ps.setString(2, resourceId.getScopeId());
                ps.setString(3, resourceId.getResourceType());
                ps.setString(4, resourceId.getUniqueId());
            }

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
