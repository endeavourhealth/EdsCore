package org.endeavourhealth.core.database.rdbms.enterprise;

import com.fasterxml.jackson.databind.JsonNode;
import com.zaxxer.hikari.HikariDataSource;
import org.endeavourhealth.common.config.ConfigManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EnterpriseConnector {

    private static ConcurrentHashMap<String, ConnectionWrapper> cache = new ConcurrentHashMap<>();

    public static List<ConnectionWrapper> openConnection(String subscriberConfigName) throws Exception {
        JsonNode config = ConfigManager.getConfigurationAsJson(subscriberConfigName, "db_subscriber");
        return openConnection(config);
    }

    /**
     * returns list of connections to main subscriber DB and any replicas
     */
    public static List<ConnectionWrapper> openConnection(JsonNode config) throws Exception {

        ConnectionWrapper mainConnection = openSingleConnection(config, false);

        List<ConnectionWrapper> ret = new ArrayList<>();
        ret.add(mainConnection);

        if (config.has("replicas")) {

            JsonNode replicas = (JsonNode)config.get("replicas");
            for (int i=0; i<replicas.size(); i++) {
                JsonNode replica = replicas.get(i);
                ConnectionWrapper replicaConnection = openSingleConnection(replica, true);
                ret.add(replicaConnection);
            }
        }

        return ret;
    }

    private static ConnectionWrapper openSingleConnection(JsonNode config, boolean isReplica) throws Exception {

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

                    //cache the escape string too, since getting the metadata each time is extra load
                    Connection conn = pool.getConnection();
                    String escapeChar = conn.getMetaData().getIdentifierQuoteString();
                    conn.close();

                    //and catch the batch size
                    int batchSize = 50;
                    if (config.has("batch_size")) {
                        batchSize = config.get("batch_size").asInt();
                        if (batchSize <= 0) {
                            throw new Exception("Invalid batch size");
                        }
                    }

                    cached = new ConnectionWrapper(url, pool, escapeChar, batchSize, isReplica);
                    cache.put(url, cached);
                }
            }
        }

        return cached;
    }

    /*public static Connection openConnection(String enterpriseConfigName) throws Exception {

        JsonNode config = ConfigManager.getConfigurationAsJson(enterpriseConfigName, "db_subscriber");

        String driverClass = config.get("driverClass").asText();
        String url = config.get("enterprise_url").asText();
        String username = config.get("enterprise_username").asText();
        String password = config.get("enterprise_password").asText();

        //force the driver to be loaded
        Class.forName(driverClass);

        Connection conn = DriverManager.getConnection(url, username, password);
        conn.setAutoCommit(false);

        return conn;
    }*/

    public static class ConnectionWrapper {
        private String url;
        private HikariDataSource connectionPool;
        private String keywordEscapeChar;
        private int batchSize;
        private boolean isReplica;

        public ConnectionWrapper(String url, HikariDataSource connectionPool, String keywordEscapeChar, int batchSize, boolean isReplica) {
            this.url = url;
            this.connectionPool = connectionPool;
            this.keywordEscapeChar = keywordEscapeChar;
            this.batchSize = batchSize;
            this.isReplica = isReplica;
        }

        public String getUrl() {
            return url;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public boolean isReplica() {
            return isReplica;
        }

        public String getKeywordEscapeChar() {
            return keywordEscapeChar;
        }

        public Connection getConnection() throws SQLException {
            return connectionPool.getConnection();
        }

        public String toString() {
            if (!isReplica) {
                return url;
            } else {
                return "<<REPLICA>> " + url;
            }
        }
    }
}
