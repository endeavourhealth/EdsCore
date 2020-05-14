package org.endeavourhealth.core.database.rdbms.enterprise;

import com.fasterxml.jackson.databind.JsonNode;
import com.zaxxer.hikari.HikariDataSource;
import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EnterpriseConnector {
    private static final Logger LOG = LoggerFactory.getLogger(EnterpriseConnector.class);

    private static ConcurrentHashMap<String, DataSource> cache = new ConcurrentHashMap<>();

    /**
     * returns list of connection wrappers/remote IDs to subscriber DB and any replicas
     */
    public static List<ConnectionWrapper> openSubscriberConnections(String subscriberConfigName) throws Exception {

        JsonNode config = ConfigManager.getConfigurationAsJson(subscriberConfigName, "db_subscriber");

        //find out if we have a remote subscriber ID
        Integer remoteSubscriberId = null;
        if (config.has("remote_subscriber_id")) {
            int value = config.get("remote_subscriber_id").asInt();
            remoteSubscriberId = new Integer(value);
        }

        //find the batch size if specified
        int batchSize = 50;
        if (config.has("batch_size")) {
            batchSize = config.get("batch_size").asInt();
            if (batchSize <= 0) {
                throw new Exception("Invalid batch size");
            }
        }

        List<ConnectionWrapper> ret = new ArrayList<>();

        //see if we can connect using the new-style config records
        ConnectionWrapper mainConnection = null;
        try {
            //in the new DB connection architecture, the database credentials are held separately, so we just need to pass through the config name
            DataSource dataSource = ConnectionManager.getDataSourceNewWay(ConnectionManager.Db.Subscriber, subscriberConfigName);
            mainConnection = new ConnectionWrapper(dataSource, remoteSubscriberId, false, batchSize);

        } catch (Exception ex) {

            //if no new-style config record can be found, then fall back and check for the database credentials being
            //in the subscriber config JSON itself
            if (config.has("enterprise_url")) {
                DataSource dataSource = openConnectionOldWay(config);
                mainConnection = new ConnectionWrapper(dataSource, remoteSubscriberId, false, batchSize);
                LOG.debug("Got enterprise/subscriber dataSource " + subscriberConfigName + " old way");

            } else if (remoteSubscriberId != null) {
                //if we've not found any DB credentials, but we have a remote subscriber ID, that's fine
                mainConnection = new ConnectionWrapper(null, remoteSubscriberId, false, batchSize);

            } else {
                //if we've no subscriber DB credentials and no remote subscriber ID then something is wrong
                throw new Exception("No subscriber database credentials or remote subscriber ID found for config [" + subscriberConfigName + "]");
            }
        }

        ret.add(mainConnection);

        if (config.has("replicas")) {
            JsonNode replicas = config.get("replicas");
            for (int i = 0; i < replicas.size(); i++) {
                JsonNode replicaNode = replicas.get(i);

                //see if we can connect to the replica using the new-style config records
                ConnectionWrapper replicaConnection = null;
                try {
                    String replicaName = replicaNode.asText();
                    DataSource dataSource = ConnectionManager.getDataSourceNewWay(ConnectionManager.Db.Subscriber, replicaName);
                    replicaConnection = new ConnectionWrapper(dataSource, null, true, batchSize);
                    //LOG.debug("Got replica enterprise/subscriber dataSource " + subscriberConfigName + " new way");

                } catch (Exception ex) {
                    DataSource dataSource = openConnectionOldWay(replicaNode);
                    replicaConnection = new ConnectionWrapper(dataSource, null, true, batchSize);
                    LOG.debug("Got replica enterprise/subscriber dataSource " + subscriberConfigName + " old way");
                }

                ret.add(replicaConnection);
            }
        }

        return ret;
    }

    /**
     * opens a DB connection using the old-style config record where the subscriber config JSON
     * contains the DB credentials directly
     */
    private static DataSource openConnectionOldWay(JsonNode config) throws Exception {

        String url = config.get("enterprise_url").asText();

        DataSource cached = cache.get(url);
        if (cached == null) {

            //sync and check again, just in case
            synchronized (cache) {
                cached = cache.get(url);
                if (cached == null) {

                    String driverClass = config.get("driverClass").asText();
                    String username = config.get("enterprise_username").asText();
                    String password = config.get("enterprise_password").asText();

                    //force the driver to be loaded
                    Class.forName(driverClass);

                    HikariDataSource pool = new HikariDataSource();
                    pool.setJdbcUrl(url);
                    pool.setUsername(username);
                    pool.setPassword(password);
                    pool.setMaximumPoolSize(3);
                    pool.setMinimumIdle(1);
                    pool.setIdleTimeout(60000);
                    pool.setPoolName("EnterpriseFilerConnectionPool" + url);
                    pool.setAutoCommit(false);

                    cached = pool;
                    cache.put(url, pool);
                }
            }
        }

        return cached;
    }


    /**
     * object to wrap up a DB connection source and/or a remote subscriber ID (will always have one or BOTH)
     */
    public static class ConnectionWrapper {
        private final DataSource dataSource;
        private final boolean isReplica;
        private final Integer remoteSubscriberId;
        private final int batchSize;
        private String keywordEscapeChar;

        public ConnectionWrapper(DataSource dataSource, Integer remoteSubscriberId, boolean isReplica, int batchSize) throws Exception {
            this.dataSource = dataSource;
            this.isReplica = isReplica;
            this.remoteSubscriberId = remoteSubscriberId;
            this.batchSize = batchSize;

            //work out the escape char for whatever DB engine we're connected to
            //the dataSource may be null if we have a remote subscriber only
            if (dataSource != null) {
                Connection conn = dataSource.getConnection();
                this.keywordEscapeChar = conn.getMetaData().getIdentifierQuoteString();
                conn.close();
            }
        }


        public boolean isReplica() {
            return isReplica;
        }

        public String getKeywordEscapeChar() {
            return keywordEscapeChar;
        }

        public Connection getConnection() throws Exception {
            if (dataSource == null) {
                throw new Exception("Trying to get connection for remote subscriber");
            }
            return dataSource.getConnection();
        }

        public boolean hasDatabaseConnection() {
            return this.dataSource != null;
        }

        public boolean hasRemoteSubscriberId() {
            return this.remoteSubscriberId != null;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public Integer getRemoteSubscriberId() {
            return remoteSubscriberId;
        }

        public String toString() {

            StringBuilder sb = new StringBuilder();

            //indicate if a replica
            if (isReplica) {
                sb.append("<<REPLICA>> ");
            }

            if (remoteSubscriberId != null) {
                sb.append("[Remote Subscriber ID " + remoteSubscriberId + "] ");
            }

            //the connection pool should ALWAYS be a Hikari pool, but just handle it not being the case
            if (dataSource != null) {
                if (dataSource instanceof HikariDataSource) {
                    HikariDataSource hds = (HikariDataSource) dataSource;
                    String url = hds.getJdbcUrl();
                    //remove the connection string properties, so we're not logging out username, password etc.
                    int paramsIndex = url.indexOf("?");
                    if (paramsIndex == -1) {
                        sb.append(url);
                    } else {
                        sb.append(url.substring(0, paramsIndex));
                    }

                } else {
                    //if a different dataSource, then just log something
                    sb.append("Unexpected data source class " + dataSource.getClass());
                }
            }

            return sb.toString();
        }
    }
}
