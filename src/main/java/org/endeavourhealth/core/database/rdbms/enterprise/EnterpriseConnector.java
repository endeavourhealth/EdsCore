package org.endeavourhealth.core.database.rdbms.enterprise;

import com.fasterxml.jackson.databind.JsonNode;
import com.zaxxer.hikari.HikariDataSource;
import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EnterpriseConnector {
    private static final Logger LOG = LoggerFactory.getLogger(EnterpriseConnector.class);

    private static ConcurrentHashMap<String, ConnectionWrapper> cache = new ConcurrentHashMap<>();

    /**
     * returns list of connections to main subscriber DB and any replicas
     */
    public static List<ConnectionWrapper> openConnection(String subscriberConfigName) throws Exception {
        JsonNode config = ConfigManager.getConfigurationAsJson(subscriberConfigName, "db_subscriber");

        List<ConnectionWrapper> ret = new ArrayList<>();

        //see if we can connect using the new-style config records
        ConnectionWrapper mainConnection = null;
        try {
            DataSource dataSource = ConnectionManager.getDataSourceNewWay(ConnectionManager.Db.Subscriber, subscriberConfigName);
            mainConnection = new ConnectionWrapper(dataSource, false);
            LOG.debug("Got enterprise/subscriber dataSource " + subscriberConfigName + " new way");

        } catch (Exception ex) {
            mainConnection = openSingleConnectionOldWay(config, false);
            LOG.debug("Got enterprise/subscriber dataSource " + subscriberConfigName + " old way");
        }

        if (config.has("remote_subscriber_id")) {
            String value = config.get("remote_subscriber_id").asText();
            mainConnection.setRemoteSubscriberId(value);
        }

        ret.add(mainConnection);

        if (config.has("replicas")) {
            JsonNode replicas = config.get("replicas");
            for (int i = 0; i < replicas.size(); i++) {
                JsonNode replica = replicas.get(i);

                //see if we can connect to the replica using the new-style config records
                ConnectionWrapper replicaConnection = null;
                try {
                    String replicaName = replica.asText();
                    DataSource dataSource = ConnectionManager.getDataSourceNewWay(ConnectionManager.Db.Subscriber, replicaName);
                    replicaConnection = new ConnectionWrapper(dataSource, false);
                    LOG.debug("Got replica enterprise/subscriber dataSource " + subscriberConfigName + " new way");

                } catch (Exception ex) {
                    LOG.error("Failed to get replica enterprise connection new way", ex);
                    replicaConnection = openSingleConnectionOldWay(replica, true);
                    LOG.debug("Got replica enterprise/subscriber dataSource " + subscriberConfigName + " old way");
                }

                ret.add(replicaConnection);
            }
        }

        //and set the batch size if specified
        int batchSize = 50;

        if (config.has("batch_size")) {
            batchSize = config.get("batch_size").asInt();
            if (batchSize <= 0) {
                throw new Exception("Invalid batch size");
            }
        }

        for (ConnectionWrapper w : ret) {
            w.setBatchSize(batchSize);
        }

        return ret;
    }

    private static ConnectionWrapper openSingleConnectionOldWay(JsonNode config, boolean isReplica) throws Exception {

        String url = config.get("enterprise_url").asText();

        ConnectionWrapper cached = cache.get(url);
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

                    cached = new ConnectionWrapper(pool, isReplica);
                    cache.put(url, cached);
                }
            }
        }

        return cached;
    }


    public static class ConnectionWrapper {
        private final DataSource dataSource;
        private final String keywordEscapeChar;
        private final boolean isReplica;
        private int batchSize;
        private String remoteSubscriberId;

        public ConnectionWrapper(DataSource dataSource, boolean isReplica) throws Exception {
            this.dataSource = dataSource;
            this.isReplica = isReplica;

            //work out the escape char for whatever DB engine we're connected to
            Connection conn = dataSource.getConnection();
            this.keywordEscapeChar = conn.getMetaData().getIdentifierQuoteString();
            conn.close();
        }


        public boolean isReplica() {
            return isReplica;
        }

        public String getKeywordEscapeChar() {
            return keywordEscapeChar;
        }

        public Connection getConnection() throws SQLException {
            return dataSource.getConnection();
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public String getRemoteSubscriberId() {
            return remoteSubscriberId;
        }

        public void setRemoteSubscriberId(String remoteSubscriberId) {
            this.remoteSubscriberId = remoteSubscriberId;
        }

        public String toString() {

            StringBuilder sb = new StringBuilder();

            //indicate if a replica
            if (isReplica) {
                sb.append("<<REPLICA>> ");
            }

            //the connection pool should ALWAYS be a Hikari pool, but just handle it not being the case
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

            return sb.toString();
        }
    }
}
