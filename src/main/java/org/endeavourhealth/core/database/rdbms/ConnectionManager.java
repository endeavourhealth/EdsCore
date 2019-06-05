package org.endeavourhealth.core.database.rdbms;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;
import com.zaxxer.hikari.pool.HikariProxyConnection;
import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.admin.ServiceDalI;
import org.endeavourhealth.core.database.dal.admin.models.Service;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {



    public static enum Db {
        Eds,
        Reference,
        Hl7Receiver,
        Admin,
        Audit,
        PublisherTransform,
        SubscriberTransform, //note that there are multiple subscriber transform DBs (one for each subscriber)
        Ehr,
        Logback,
        JdbcReader,
        Coding, //once fully moved to MySQL, this can go as it will be the same as Reference
        PublisherCommon,
        FhirAudit,
        PublisherStaging,
        DataGenerator;
    }
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);
    private static Map<String, EntityManagerFactory> entityManagerFactoryMap = new ConcurrentHashMap<>();
    private static Map<UUID, String> publisherServiceToConfigMap = new ConcurrentHashMap<>();
    private static Map<String, Integer> connectionMaxPoolSize = new ConcurrentHashMap<>();

    public static EntityManager getEntityManager(Db dbName) throws Exception {
        return getEntityManager(dbName, null);
    }

    public static EntityManager getEntityManager(Db dbName, String explicitConfigName) throws Exception {

        String cacheKey = "" + dbName + "/" + explicitConfigName;
        EntityManagerFactory factory = entityManagerFactoryMap.get(cacheKey);

        if (factory == null
                || !factory.isOpen()) {

            synchronized (entityManagerFactoryMap) {

                //once in the sync block, repeat the check
                factory = entityManagerFactoryMap.get(cacheKey);
                if (factory == null
                        || !factory.isOpen()) {

                    factory = createEntityManager(dbName, explicitConfigName);
                    entityManagerFactoryMap.put(cacheKey, factory);
                }
            }
        }

        return factory.createEntityManager();
    }

    /*public static void shutdown(Db dbName) {

        EntityManagerFactory factory = entityManagerFactoryMap.get(dbName);

        if (factory != null) {
            factory.close();
            entityManagerFactoryMap.remove(dbName);
        }
    }*/

    private static synchronized EntityManagerFactory createEntityManager(Db dbName, String explicitConfigName) throws Exception {

        //adding this line to force compile-time checking for this class. Spent far too long investigating
        //why this wasn't being found when it turned out to be that it had been removed from POM.xml,
        //so adding this to ensure it's picked up during compile-time rather than run-time
        org.hibernate.hikaricp.internal.HikariCPConnectionProvider p = null;

        JsonNode json = findDatabaseConfigJson(dbName, explicitConfigName);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false"); //always turn this off (https://stackoverflow.com/questions/10075081/hibernate-slow-to-acquire-postgres-connection)

        Iterator<String> fieldNames = json.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode child = json.get(fieldName);

            if (fieldName.equals("url")) {
                String url = child.asText();
                properties.put("hibernate.hikari.dataSource.url", url);

            } else if (fieldName.equals("username")) {
                String user = child.asText();
                properties.put("hibernate.hikari.dataSource.user", user);

            } else if (fieldName.equals("password")) {
                String pass = child.asText();
                properties.put("hibernate.hikari.dataSource.password", pass);

            } else if (fieldName.equals("class")) {
                String cls = child.asText();
                properties.put("hibernate.hikari.dataSourceClassName", cls);

            } else if (fieldName.equals("dialect")) {
                String dialect = child.asText();
                properties.put("hibernate.dialect", dialect);

            } else if (fieldName.equals("connection_properties")) {
                populateConnectionProperties(child, properties, dbName, explicitConfigName);

            } else {
                //ignore it, as it's nothing to do with the DB connection
            }
        }

        String hibernatePersistenceUnit = getPersistenceUnitName(dbName);
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(hibernatePersistenceUnit, properties);

        return factory;
    }

    private static void populateConnectionProperties(JsonNode connectionPropertiesRoot, Map<String, Object> properties, Db dbName, String explicitConfigName) {

        if (!connectionPropertiesRoot.isObject()) {
            throw new IllegalArgumentException("connection_properties should be an object");
        }

        Iterator<String> fieldNames = connectionPropertiesRoot.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode child = connectionPropertiesRoot.get(fieldName);

            //if not one of the generally-used fields above, then just interpret as a
            //properly named property, and just set in according to its type
            if (child.isTextual()) {
                String value = child.asText();
                properties.put(fieldName, value);

            } else if (child.isInt()) {
                int value = child.asInt();
                properties.put(fieldName, new Integer(value));

            } else if (child.isBigInteger()) {
                long value = child.asLong();
                properties.put(fieldName, new Long(value));

            } else if (child.isBoolean()) {
                boolean value = child.asBoolean();
                properties.put(fieldName, new Boolean(value));

            } else {
                throw new IllegalArgumentException("Unsupported JSON element type for database " + dbName + " " + explicitConfigName + ": " + child.getNodeType());
            }
        }

    }

    /*private static synchronized EntityManagerFactory createEntityManager(Db dbName, String explicitConfigName) throws Exception {

        //adding this line to force compile-time checking for this class. Spent far too long investigating
        //why this wasn't being found when it turned out to be that it had been removed from POM.xml,
        //so adding this to ensure it's picked up during compile-time rather than run-time
        org.hibernate.hikaricp.internal.HikariCPConnectionProvider p = null;

        JsonNode json = findDatabaseConfigJson(dbName, explicitConfigName);

        String url = json.get("url").asText();
        String user = json.get("username").asText();
        String pass = json.get("password").asText();

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("hibernate.hikari.dataSource.url", url);
        properties.put("hibernate.hikari.dataSource.user", user);
        properties.put("hibernate.hikari.dataSource.password", pass);

        if (json.has("class")) {
            properties.put("hibernate.hikari.dataSourceClassName", json.get("class").asText());
        }

        if (json.has("dialect")) {
            properties.put("hibernate.dialect", json.get("dialect").asText());
        }

        String hibernatePersistenceUnit = getPersistenceUnitName(dbName);
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(hibernatePersistenceUnit, properties);

        return factory;
    }*/

    private static JsonNode findDatabaseConfigJson(Db dbName, String configName) throws Exception {

        JsonNode json = null;
        if (dbName == Db.SubscriberTransform) {
            json = ConfigManager.getConfigurationAsJson(configName, "db_subscriber");
            /*if (json != null) {
                json = json.get("transform");
            }*/

        } else if (dbName == Db.PublisherTransform) {
            json = ConfigManager.getConfigurationAsJson(configName, "db_publisher");
            if (json != null) {
                json = json.get("transform");
            }

        } else if (dbName == Db.Ehr) {
            json = ConfigManager.getConfigurationAsJson(configName, "db_publisher");
            if (json != null) {
                json = json.get("core");
            }

        }  else if (dbName == Db.PublisherStaging) {
            json = ConfigManager.getConfigurationAsJson(configName, "db_publisher");
            if (json != null) {
                json = json.get("staging");
            }

        } else {

            if (dbName == Db.Eds) {
                configName = "eds";
            } else if (dbName == Db.Reference) {
                configName = "reference";
            } else if (dbName == Db.Hl7Receiver) {
                configName = "hl7_receiver_db";
            } else if (dbName == Db.Admin) {
                configName = "admin";
            } else if (dbName == Db.Audit) {
                configName = "audit";
            } else if (dbName == Db.Logback) {
                configName = "logback";
            } else if (dbName == Db.Coding) {
                configName = "coding";
            } else if (dbName == Db.JdbcReader) {
                configName = "jdbcreader";
            } else if (dbName == Db.PublisherCommon) {
                configName = "publisher_common";
            } else if (dbName == Db.FhirAudit) {
                configName = "fhir_audit";
            } else if (dbName == Db.PublisherStaging) {
                configName = "staging";
            } else if (dbName == Db.DataGenerator) {
                configName = "data_generator";
            }
            else {
                throw new RuntimeException("Unknown database " + dbName);
            }

            json = ConfigManager.getConfigurationAsJson(configName, "db_common");
        }

        if (json == null) {
            throw new Exception("No config JSON for " + dbName + " and config " + configName);
        }

        return json;
    }

    private static String getPersistenceUnitName(Db dbName) {
        if (dbName == Db.Eds) {
            return "EdsDb";
        } else if (dbName == Db.Reference) {
            return "ReferenceDB";
        } else if (dbName == Db.Hl7Receiver) {
            return "HL7ReceiverDb";
        } else if (dbName == Db.Admin) {
            return "AdminDb";
        } else if (dbName == Db.Audit) {
            return "AuditDb";
        } else if (dbName == Db.PublisherTransform) {
            return "PublisherTransformDb";
        } else if (dbName == Db.SubscriberTransform) {
            return "SubscriberTransformDb";
        } else if (dbName == Db.Ehr) {
            return "EhrDb";
        } else if (dbName == Db.Logback) {
            return "LogbackDb";
        } else if (dbName == Db.Coding) {
            return "CodingDb";
        } else if (dbName == Db.JdbcReader) {
            return "JDBCReaderDb";
        } else if (dbName == Db.PublisherCommon) {
            return "PublisherCommonDb";
        } else if (dbName == Db.FhirAudit) {
            return "FhirAuditDb";
        } else if (dbName == Db.PublisherStaging) {
            return "PublisherStagingDb";
        } else if (dbName == Db.DataGenerator) {
            return "DataGeneratorDb";
        }
        else {
            throw new RuntimeException("Unknown database " + dbName);
        }
    }

    public static EntityManager getEdsEntityManager() throws Exception {
        return getEntityManager(Db.Eds);
    }

    public static EntityManager getReferenceEntityManager() throws Exception {
        return getEntityManager(Db.Reference);
    }

    public static EntityManager getHl7ReceiverEntityManager() throws Exception {
        return getEntityManager(Db.Hl7Receiver);
    }

    public static EntityManager getAdminEntityManager() throws Exception {
        return getEntityManager(Db.Admin);
    }

    public static EntityManager getAuditEntityManager() throws Exception {
        return getEntityManager(Db.Audit);
    }

    public static EntityManager getFhirAuditEntityManager() throws Exception {
        return getEntityManager(Db.FhirAudit);
    }

    public static EntityManager getPublisherTransformEntityManager(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getEntityManager(Db.PublisherTransform, configName);
    }

    public static EntityManager getSubscriberTransformEntityManager(String configName) throws Exception {
        return getEntityManager(Db.SubscriberTransform, configName);
    }

    public static EntityManager getEhrEntityManager(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getEntityManager(Db.Ehr, configName);
    }

    public static EntityManager getLogbackEntityManager() throws Exception {
        return getEntityManager(Db.Logback);
    }

    public static EntityManager getCodingEntityManager() throws Exception {
        return getEntityManager(Db.Coding);
    }

    public static EntityManager getJDBCReaderEntityManager() throws Exception {
        return getEntityManager(Db.JdbcReader);
    }

    public static EntityManager getPublisherCommonEntityManager() throws Exception {
        return getEntityManager(Db.PublisherCommon);
    }

    public static EntityManager getPublisherStagingEntityMananger(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getEntityManager(Db.PublisherStaging, configName);
    }

    public static EntityManager getDataGeneratorEntityManager () throws Exception {
        return getEntityManager(Db.DataGenerator);
    }

    /**
     * there's a couple of places where we need to know if connection is to postgreSQL rather than MySQL
     */
    public static boolean isPostgreSQL(Connection connection) {

        if (connection instanceof org.postgresql.jdbc.PgConnection) {
            return true;

        } else {
            try {
                connection.unwrap(org.postgresql.jdbc.PgConnection.class);
                return true;
            } catch (SQLException ex) {
                return false;
            }
        }
    }

    /**
     * need to detect if SQL Server as the syntax is different for some things
     */
    public static boolean isSqlServer(Connection connection) {

        if (connection instanceof com.microsoft.sqlserver.jdbc.SQLServerConnection) {
            return true;

        } else {
            try {
                connection.unwrap(com.microsoft.sqlserver.jdbc.SQLServerConnection.class);
                return true;
            } catch (SQLException ex) {
                return false;
            }
        }
    }

    private static String findConfigNameForPublisherService(UUID serviceId) throws Exception {

        String ret = publisherServiceToConfigMap.get(serviceId);
        if (ret == null) {
            ServiceDalI serviceDalI = DalProvider.factoryServiceDal();
            Service service = serviceDalI.getById(serviceId);
            UUID id = service.getId();
            String configName = service.getPublisherConfigName();

            if (Strings.isNullOrEmpty(configName)) {
                throw new Exception("Unknown publisher config name for service " + serviceId + " - " + service.getName());
            }

            publisherServiceToConfigMap.put(id, configName);
            ret = configName;
        }

        return ret;
    }

    public static int getEhrConnectionPoolMaxSize(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getConnectionPoolMaxSize(Db.Ehr, configName);
    }

    public static int getPublisherCommonConnectionPoolMaxSize() throws Exception {
        return getConnectionPoolMaxSize(Db.PublisherCommon, null);
    }

    public static int getPublisherTransformConnectionPoolMaxSize(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getConnectionPoolMaxSize(Db.PublisherTransform, configName);
    }

    private static int getConnectionPoolMaxSize(Db dbName, String explicitConfigName) throws Exception {
        String cacheKey = "" + dbName + "/" + explicitConfigName;
        Integer maxPoolSize = connectionMaxPoolSize.get(cacheKey);

        if (maxPoolSize == null) {

            synchronized (connectionMaxPoolSize) {

                EntityManager entityManager = getEntityManager(dbName, explicitConfigName);
                try {
                    maxPoolSize = getConnectionPoolMaxSize(entityManager);
                } finally {
                    entityManager.close();
                }

                connectionMaxPoolSize.put(cacheKey, maxPoolSize);
            }
        }

        return maxPoolSize.intValue();
    }

    /**
     * returns the HikariCP max pool size for an EntityManager. Note this function uses
     * reflection to access private variables, so it potentially ask risk of breaking
     * if HikariCP changes, but allows us to base things such as thread pool size on
     * DB connection pool sizes, reducing the amount of config needed
     */
    private static Integer getConnectionPoolMaxSize(EntityManager entityManager) throws Exception {
        SessionImpl session = (SessionImpl) entityManager.getDelegate();
        Connection connection = session.connection();

        HikariProxyConnection hikariProxyConnection = (HikariProxyConnection)connection;

        Field field = hikariProxyConnection.getClass().getSuperclass().getDeclaredField("poolEntry");
        field.setAccessible(true);
        Object poolEntry = field.get(hikariProxyConnection);

        field = poolEntry.getClass().getDeclaredField("hikariPool");
        field.setAccessible(true);
        HikariPool hikariPool = (HikariPool)field.get(poolEntry);

        field = hikariPool.getClass().getSuperclass().getDeclaredField("config");
        field.setAccessible(true);
        HikariConfig hikariConfig = (HikariConfig)field.get(hikariPool);

        return new Integer(hikariConfig.getMaximumPoolSize());
    }

    public static void shutdown() {
        LOG.debug("Shutting down Core connection factories");
        for(String factoryName : entityManagerFactoryMap.keySet()) {
            LOG.debug("Closing " + factoryName);
            EntityManagerFactory factory = entityManagerFactoryMap.get(factoryName);
            factory.close();
        }
        LOG.debug("Core connection factory shutdown complete");
    }

    /**
     * MySQL "SELECT LAST_INSERT_ID()" works differently depending on whether the rewriteBatchedStatements
     * option is on or not. By default it's off but things are faster with it on.
     */
    public static boolean isMysqlRewriteBatchedStatementsEnabled(Connection connection) throws SQLException {
        String connectionUrl = connection.getMetaData().getURL().toLowerCase();
        String checkFor = "rewritebatchedstatements=true";
        return connectionUrl.indexOf(checkFor) > -1;
    }
}
