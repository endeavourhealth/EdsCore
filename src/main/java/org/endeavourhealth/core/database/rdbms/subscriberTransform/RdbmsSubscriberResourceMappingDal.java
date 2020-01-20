package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.database.dal.subscriberTransform.SubscriberResourceMappingDalI;
import org.endeavourhealth.core.database.dal.subscriberTransform.models.SubscriberId;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.DeadlockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.*;

public class RdbmsSubscriberResourceMappingDal implements SubscriberResourceMappingDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsSubscriberResourceMappingDal.class);

    private static final String ENTERPRISE_ID_MAP_SMALL = "enterprise_id_map";
    private static final String ENTERPRISE_ID_MAP_LARGE = "enterprise_id_map_3";

    private static final String DUPLICATE_KEY_ERR = "Duplicate entry .* for key 'PRIMARY'";

    private String subscriberConfigName = null;

    public RdbmsSubscriberResourceMappingDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    @Override
    public Long findEnterpriseIdOldWay(String resourceType, String resourceId) throws Exception {

        ResourceWrapper wrapper = new ResourceWrapper();
        wrapper.setResourceType(resourceType);
        wrapper.setResourceId(UUID.fromString(resourceId));
        List<ResourceWrapper> l = new ArrayList<>();
        l.add(wrapper);

        Map<ResourceWrapper, Long> ids = new HashMap<>();

        findEnterpriseIdsOldWay(l, ids);

        return ids.get(wrapper);
    }

    @Override
    public void findEnterpriseIdsOldWay(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception {
        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        try {
            //check BOTH tables for the IDs if necessary
            findEnterpriseIdsOldWayGivenTable(resources, ids, connection, ENTERPRISE_ID_MAP_SMALL);
            if (ids.size() < resources.size()) {
                findEnterpriseIdsOldWayGivenTable(resources, ids, connection, ENTERPRISE_ID_MAP_LARGE);
            }

        } finally {
            connection.close();
        }
    }

    @Override
    public Long findOrCreateEnterpriseIdOldWay(String resourceType, String resourceId) throws Exception {

        ResourceWrapper wrapper = new ResourceWrapper();
        wrapper.setResourceType(resourceType);
        wrapper.setResourceId(UUID.fromString(resourceId));
        List<ResourceWrapper> l = new ArrayList<>();
        l.add(wrapper);

        Map<ResourceWrapper, Long> ids = new HashMap<>();

        findOrCreateEnterpriseIdsOldWay(l, ids);

        return ids.get(wrapper);
    }

    @Override
    public void findOrCreateEnterpriseIdsOldWay(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception {
        //allow several attempts if it fails due to a deadlock
        DeadlockHandler h = new DeadlockHandler();
        h.addOtherErrorMessageToHandler(DUPLICATE_KEY_ERR); //due to multi-threading, we may get duplicate key errors, so just try again
        while (true) {
            try {
                tryFindOrCreateEnterpriseIdsOldWay(resources, ids);
                break;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }

    }

    private void tryFindOrCreateEnterpriseIdsOldWay(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement psInsert = null;
        try {

            //find any IDs over BOTH tables
            findEnterpriseIdsOldWayGivenTable(resources, ids, connection, ENTERPRISE_ID_MAP_SMALL);
            findEnterpriseIdsOldWayGivenTable(resources, ids, connection, ENTERPRISE_ID_MAP_LARGE);

            List<ResourceWrapper> resourcesRemaining = new ArrayList<>();

            for (ResourceWrapper resource : resources) {
                if (!ids.containsKey(resource)) {
                    resourcesRemaining.add(resource);
                }
            }

            if (!resourcesRemaining.isEmpty()) {

                //for any without an ID, insert into the NEW table only
                String sql = "INSERT INTO enterprise_id_map_3 (resource_id, resource_type) "
                        + "VALUES (?, ?)";

                psInsert = connection.prepareStatement(sql);

                for (ResourceWrapper resource : resourcesRemaining) {
                    int col = 1;
                    psInsert.setString(col++, resource.getResourceId().toString());
                    psInsert.setString(col++, resource.getResourceType());

                    psInsert.addBatch();
                }

                psInsert.executeBatch();
                connection.commit();

                //hit the NEW table only for the newly generated IDs
                findEnterpriseIdsOldWayGivenTable(resources, ids, connection, ENTERPRISE_ID_MAP_LARGE);
            }

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (psInsert != null) {
                psInsert.close();
            }
            connection.close();
        }
    }



    @Override
    public SubscriberId findSubscriberId(byte subscriberTable, String sourceId) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);

        try {
            return findSubscriberIdImpl(subscriberTable, sourceId, connection);

        } finally {
            connection.close();
        }
    }


    private SubscriberId findSubscriberIdImpl(byte subscriberTable, String sourceId, Connection connection) throws Exception {
        PreparedStatement ps = null;
        try {

            String sql = "SELECT subscriber_id, dt_previously_sent "
                    + "FROM subscriber_id_map "
                    + "WHERE source_id = ? "
                    + "AND subscriber_table = ?";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, sourceId);
            ps.setInt(col++, subscriberTable);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                col = 1;
                long id = rs.getLong(col++);

                java.util.Date dtUpdated = null;
                java.sql.Timestamp ts = rs.getTimestamp(col++);
                if (ts != null) {
                    dtUpdated = new java.util.Date(ts.getTime());
                }

                return new SubscriberId(subscriberTable, id, sourceId, dtUpdated);

            } else {
                return null;
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    @Override
    public Map<String, SubscriberId> findSubscriberIds(byte subscriberTable, List<String> sourceIds) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        try {
            Map<String, SubscriberId> ret = new HashMap<>();
            findSubscriberIdsImpl(subscriberTable, sourceIds, ret, connection);
            return ret;

        } finally {
            connection.close();
        }
    }

    private void findSubscriberIdsImpl(byte subscriberTable, List<String> sourceIds, Map<String, SubscriberId> map, Connection connection) throws Exception {

        PreparedStatement ps = null;
        try {
            String sql = "SELECT source_id, subscriber_id, dt_previously_sent "
                    + "FROM subscriber_id_map "
                    + "WHERE subscriber_table = ? "
                    + "AND source_id IN (";
            for (int i=0; i<sourceIds.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setInt(col++, subscriberTable);
            for (String sourceId: sourceIds) {
                ps.setString(col++, sourceId);
            }
            //LOG.debug("" + ps);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                col = 1;
                String sourceId = rs.getString(col++);
                long subscriberId = rs.getLong(col++);

                java.util.Date dtUpdated = null;
                java.sql.Timestamp ts = rs.getTimestamp(col++);
                if (ts != null) {
                    dtUpdated = new java.util.Date(ts.getTime());
                }

                SubscriberId o = new SubscriberId(subscriberTable, subscriberId, sourceId, dtUpdated);
                map.put(sourceId, o);
            }

            rs.close();

        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    @Override
    public SubscriberId findOrCreateSubscriberId(byte subscriberTable, String sourceId) throws Exception {

        List<String> l = new ArrayList<>();
        l.add(sourceId);

        //public Map<String, SubscriberId> findOrCreateSubscriberIds(byte subscriberTable, List<String> sourceIds) throws Exception {
        Map<String, SubscriberId> ids = findOrCreateSubscriberIds(subscriberTable, l);
        return ids.get(sourceId);
    }

    private SubscriberId createSubscriberIdImpl(byte subscriberTable, String sourceId, Connection connection) throws Exception {
        PreparedStatement psInsert = null;
        PreparedStatement psLastId = null;
        try {
            String sql = "INSERT INTO subscriber_id_map (subscriber_table, source_id)"
                    + " VALUES (?, ?)";
            psInsert = connection.prepareStatement(sql);

            sql = "SELECT LAST_INSERT_ID()";
            psLastId = connection.prepareStatement(sql);

            int col = 1;
            psInsert.setInt(col++, subscriberTable);
            psInsert.setString(col++, sourceId);

            psInsert.executeUpdate();

            //if the above worked, we can call another query to return us the ID just generated by the auto increment column
            ResultSet rs = psLastId.executeQuery();
            rs.next();
            long lastId = rs.getLong(1);
            rs.close();

            connection.commit();

            return new SubscriberId(subscriberTable, lastId, sourceId, null);

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (psInsert != null) {
                psInsert.close();
            }
            if (psLastId != null) {
                psLastId.close();
            }
        }
    }

    @Override
    public Map<String, SubscriberId> findOrCreateSubscriberIds(byte subscriberTable, List<String> sourceIds) throws Exception {
        //allow several attempts if it fails due to a deadlock
        DeadlockHandler h = new DeadlockHandler();
        h.addOtherErrorMessageToHandler(DUPLICATE_KEY_ERR); //due to multi-threading, we may get duplicate key errors, so just try again
        while (true) {
            try {
                return tryFindOrCreateSubscriberIds(subscriberTable, sourceIds);

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    /**
     * with MySQL rewrite batched inserts turned on, there's seemingly no way to insert (using ON DUPLICATE KEY UPDATE or INSERT IGNORE) and get the
     * the auto-generated keys back properly. We can get the keys back (using either SELECT LAST_INSERT_ID() or Statement.RETURN_GENERATED_KEYS,
     * but there's no actually marry them up to which inserts actually generated those keys. The problem is that the int[]
     * returned from executeBatch() contains "-2" for each insert which means "executed OK but no row count available". So we
     * don't know which ones were successful inserts and which were not.
     *
     * It's possible to get them working if rewrite batched inserts is turned off, but that kills performance.
     *
     * So the quickest solution, given that most calls into this function will result in new keys being assigned
     * rather than existing ones being found, is to simply INSERT IGNORE all them and then just call into the function to find them.
     */
    private Map<String, SubscriberId> tryFindOrCreateSubscriberIds(byte subscriberTable, List<String> sourceIds) throws Exception {

        Map<String, SubscriberId> ret = new HashMap<>();

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement psInsert = null;

        try {
            //look for any inserted already
            findSubscriberIdsImpl(subscriberTable, sourceIds, ret, connection);

            //see which IDs weren't found
            List<String> sourceIdsRemaining = new ArrayList<>();
            for (String sourceId : sourceIds) {
                if (!ret.containsKey(sourceId)) {
                    sourceIdsRemaining.add(sourceId);
                }
            }

            //now generate new IDs for any ones not found
            if (!sourceIdsRemaining.isEmpty()) {

                String sql = "INSERT INTO subscriber_id_map (subscriber_table, source_id) "
                        + "VALUES (?, ?)";

                psInsert = connection.prepareStatement(sql);

                for (String sourceId : sourceIdsRemaining) {
                    int col = 1;
                    psInsert.setInt(col++, subscriberTable);
                    psInsert.setString(col++, sourceId);

                    psInsert.addBatch();
                }

                psInsert.executeBatch();

                connection.commit();

                //now retrieve the IDs we just generated
                findSubscriberIdsImpl(subscriberTable, sourceIdsRemaining, ret, connection);
            }

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (psInsert != null) {
                psInsert.close();
            }
            connection.close();
        }

        return ret;
    }


    @Override
    public void updateDtUpdatedForSubscriber(List<SubscriberId> subscriberIds) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {

            String sql = "UPDATE subscriber_id_map "
                    + "SET dt_previously_sent = ? "
                    + "WHERE subscriber_id = ?";
            ps = connection.prepareStatement(sql);

            for (SubscriberId subscriberId: subscriberIds) {

                int col = 1;
                if (subscriberId.getDtUpdatedPreviouslySent() == null) {
                    ps.setNull(col++, Types.TIMESTAMP);
                } else {
                    ps.setTimestamp(col++, new java.sql.Timestamp(subscriberId.getDtUpdatedPreviouslySent().getTime()));
                }

                ps.setLong(col++, subscriberId.getSubscriberId());

                ps.addBatch();
            }

            ps.executeBatch();

            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }


    private static void findEnterpriseIdsOldWayGivenTable(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids, Connection connection, String tableName) throws Exception {

        if (resources.isEmpty()) {
            return;
        }

        String resourceType = null;
        List<String> resourceIds = new ArrayList<>();
        Map<String, ResourceWrapper> resourceIdMap = new HashMap<>();

        for (ResourceWrapper resource: resources) {

            if (resourceType == null) {
                resourceType = resource.getResourceType();
            } else if (!resourceType.equals(resource.getResourceType())) {
                throw new Exception("Can't find enterprise IDs for different resource types");
            }

            String id = resource.getResourceId().toString();
            resourceIds.add(id);
            resourceIdMap.put(id, resource);
        }

        String sql = "SELECT resource_id, enterprise_id"
                + " FROM " + tableName
                + " WHERE resource_type = ?"
                + " AND resource_id IN (";
        for (int i=0; i<resourceIds.size(); i++) {
            if (i>0) {
                sql += ", ";
            }
            sql += "?";
        }
        sql += ")";

        PreparedStatement ps = connection.prepareStatement(sql);

        try {
            int col = 1;
            ps.setString(col++, resourceType);
            for (int i=0; i<resourceIds.size(); i++) {
                String resourceId = resourceIds.get(i);
                ps.setString(col++, resourceId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                col = 1;
                String resourceId = rs.getString(col++);
                long enterpriseId = rs.getLong(col++);

                ResourceWrapper resource = resourceIdMap.get(resourceId);
                ids.put(resource, new Long(enterpriseId));
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }


}
