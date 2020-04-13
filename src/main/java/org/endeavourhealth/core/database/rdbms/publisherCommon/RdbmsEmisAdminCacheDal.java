package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.EmisAdminCacheDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsEmisAdminResourceCacheApplied;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;
import java.util.UUID;

public class RdbmsEmisAdminCacheDal implements EmisAdminCacheDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsEmisAdminCacheDal.class);





    @Override
    public boolean wasAdminCacheApplied(UUID serviceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsEmisAdminResourceCacheApplied c"
                    + " where c.serviceId = :service_id";

            Query query = entityManager.createQuery(sql, RdbmsEmisAdminResourceCacheApplied.class)
                    .setParameter("service_id", serviceId.toString());

            try {
                RdbmsEmisAdminResourceCacheApplied result = (RdbmsEmisAdminResourceCacheApplied) query.getSingleResult();
                return true;

            } catch (NoResultException ex) {
                return false;
            }

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void adminCacheWasApplied(UUID serviceId, String dataSharingAgreementGuid) throws Exception {

        RdbmsEmisAdminResourceCacheApplied o = new RdbmsEmisAdminResourceCacheApplied();
        o.setServiceId(serviceId.toString());
        o.setDataSharingAgreementGuid(dataSharingAgreementGuid);
        o.setDateApplied(new Date());

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(o);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

/*
public void saveAdminResource(EmisAdminResourceCache resourceCache) throws Exception {
        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache is null");
        }

        RdbmsEmisAdminResourceCache emisObj = new RdbmsEmisAdminResourceCache(resourceCache);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {

            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO emis_admin_resource_cache"
                    + " (data_sharing_agreement_guid, emis_guid, resource_type, resource_data, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " resource_data = VALUES(resource_data),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            ps.setString(1, emisObj.getDataSharingAgreementGuid());
            ps.setString(2, emisObj.getEmisGuid());
            ps.setString(3, emisObj.getResourceType());
            ps.setString(4, emisObj.getResourceData());
            ps.setString(5, emisObj.getAuditJson());
            //entityManager.persist(emisObj);

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

    public void deleteAdminResource(EmisAdminResourceCache resourceCache) throws Exception {
        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache is null");
        }

        RdbmsEmisAdminResourceCache emisObj = new RdbmsEmisAdminResourceCache(resourceCache);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.remove(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "DELETE FROM emis_admin_resource_cache"
                    + " WHERE data_sharing_agreement_guid = ?"
                    + " AND emis_guid = ?"
                    + " AND resource_type = ?";

            ps = connection.prepareStatement(sql);

            ps.setString(1, emisObj.getDataSharingAgreementGuid());
            ps.setString(2, emisObj.getEmisGuid());
            ps.setString(3, emisObj.getResourceType());

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

    @Override
    public void saveAdminResources(List<EmisAdminResourceCache> resources) throws Exception {
        if (resources == null || resources.isEmpty()) {
            throw new IllegalArgumentException("resources is null or empty");
        }

        DeadlockHandler h = new DeadlockHandler();
        while (true) {
            try {
                trySaveAdminResources(resources);
                break;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    public void trySaveAdminResources(List<EmisAdminResourceCache> resources) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO emis_admin_resource_cache"
                    + " (data_sharing_agreement_guid, emis_guid, resource_type, resource_data, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " resource_data = VALUES(resource_data),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (EmisAdminResourceCache resource : resources) {

                int col = 1;
                ps.setString(col++, resource.getDataSharingAgreementGuid());
                ps.setString(col++, resource.getEmisGuid());
                ps.setString(col++, resource.getResourceType());
                ps.setString(col++, resource.getResourceData());
                if (resource.getAudit() != null) {
                    ps.setString(col++, resource.getAudit().writeToJson());
                } else {
                    ps.setNull(col++, Types.VARCHAR);
                }

                ps.addBatch();
            }

            ps.executeBatch();

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
    public void deleteAdminResources(List<EmisAdminResourceCache> resources) throws Exception {
        if (resources == null || resources.isEmpty()) {
            throw new IllegalArgumentException("resources is null or empty");
        }

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;
        try {

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.remove(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "DELETE FROM emis_admin_resource_cache"
                    + " WHERE data_sharing_agreement_guid = ?"
                    + " AND emis_guid = ?"
                    + " AND resource_type = ?";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (EmisAdminResourceCache resource : resources) {

                int col = 1;
                ps.setString(col++, resource.getDataSharingAgreementGuid());
                ps.setString(col++, resource.getEmisGuid());
                ps.setString(col++, resource.getResourceType());

                ps.addBatch();
            }

            ps.executeBatch();

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
    public EmisAdminResourceCache getAdminResource(String dataSharingAgreementGuid, ResourceType resourceType, String sourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsEmisAdminResourceCache c"
                    + " where c.dataSharingAgreementGuid = :data_sharing_agreement_guid"
                    + " and c.resourceType = :resource_type"
                    + " and c.emisGuid = :emis_guid";

            Query query = entityManager.createQuery(sql, RdbmsEmisAdminResourceCache.class)
                    .setParameter("data_sharing_agreement_guid", dataSharingAgreementGuid)
                    .setParameter("resource_type", resourceType.toString())
                    .setParameter("emis_guid", sourceId);

            try {
                RdbmsEmisAdminResourceCache result = (RdbmsEmisAdminResourceCache) query.getSingleResult();
                return new EmisAdminResourceCache(result);

            } catch (NoResultException ex) {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    @Override
    public Map<String, EmisAdminResourceCache> getAdminResources(String dataSharingAgreementGuid,
                                                                 ResourceType resourceType, List<String> sourceIds) throws Exception {

        if (sourceIds.isEmpty()) {
            throw new Exception("Source IDs cannot be empty");
        }

        Connection connection = ConnectionManager.getPublisherCommonConnection();

        PreparedStatement ps = null;
        try {
            String sql = "SELECT data_sharing_agreement_guid, emis_guid, resource_type, resource_data, audit_json"
                    + " FROM emis_admin_resource_cache"
                    + " WHERE data_sharing_agreement_guid = ?"
                    + " AND resource_type = ?"
                    + " AND emis_guid IN (";
            for (int i = 0; i < sourceIds.size(); i++) {
                if (i > 0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, dataSharingAgreementGuid);
            ps.setString(col++, resourceType.toString());
            for (int i = 0; i < sourceIds.size(); i++) {
                ps.setString(col++, sourceIds.get(i));
            }

            Map<String, EmisAdminResourceCache> ret = new HashMap<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                col = 1;
                String sharingGuid = rs.getString(col++);
                String emisGuid = rs.getString(col++);
                String type = rs.getString(col++);
                String resourceData = rs.getString(col++);
                String auditJson = rs.getString(col++);

                EmisAdminResourceCache o = new EmisAdminResourceCache();
                o.setDataSharingAgreementGuid(sharingGuid);
                o.setEmisGuid(emisGuid);
                o.setResourceType(type);
                o.setResourceData(resourceData);
                if (!Strings.isNullOrEmpty(auditJson)) {
                    o.setAudit(ResourceFieldMappingAudit.readFromJson(auditJson));
                }

                ret.put(emisGuid, o);
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    private EntityManager adminCacheRetrieveEntityManager;
    private PreparedStatement adminCacheRetrievePreparedStatement;
    private ResultSet adminCacheRetrieveResultSet;


    @Override
    public void startRetrievingAdminResources(String dataSharingAgreementGuid) throws Exception {

        if (adminCacheRetrieveEntityManager != null) {
            throw new Exception("Already retrieving admin resources");
        }

        adminCacheRetrieveEntityManager = ConnectionManager.getPublisherCommonEntityManager();

        SessionImpl session = (SessionImpl) adminCacheRetrieveEntityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "SELECT data_sharing_agreement_guid, emis_guid, resource_type, resource_data, audit_json"
                + " FROM emis_admin_resource_cache"
                + " WHERE data_sharing_agreement_guid = ?";

        adminCacheRetrievePreparedStatement = connection.prepareStatement(sql);
        adminCacheRetrievePreparedStatement.setFetchSize(10000); //only retrieve a limited amount at a time
        adminCacheRetrievePreparedStatement.setString(1, dataSharingAgreementGuid);

        adminCacheRetrieveResultSet = adminCacheRetrievePreparedStatement.executeQuery();
    }

    @Override
    public EmisAdminResourceCache getNextAdminResource() throws Exception {

        if (adminCacheRetrieveResultSet == null) {
            throw new Exception("Haven't started retrieving admin resources");
        }

        if (adminCacheRetrieveResultSet.next()) {

            int col = 1;

            EmisAdminResourceCache ret = new EmisAdminResourceCache();
            ret.setDataSharingAgreementGuid(adminCacheRetrieveResultSet.getString(col++));
            ret.setEmisGuid(adminCacheRetrieveResultSet.getString(col++));
            ret.setResourceType(adminCacheRetrieveResultSet.getString(col++));
            ret.setResourceData(adminCacheRetrieveResultSet.getString(col++));

            String auditJson = adminCacheRetrieveResultSet.getString(col++);
            if (auditJson != null) {
                ret.setAudit(ResourceFieldMappingAudit.readFromJson(auditJson));
            }

            return ret;

        } else {

            //if we've reeached the end, then close everything down
            adminCacheRetrievePreparedStatement.close();
            adminCacheRetrieveEntityManager.close();

            //and null everything out
            adminCacheRetrievePreparedStatement = null;
            adminCacheRetrieveEntityManager = null;
            adminCacheRetrieveResultSet = null;

            return null;
        }
    }
 */
}
