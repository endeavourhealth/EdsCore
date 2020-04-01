package org.endeavourhealth.core.database.rdbms;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import com.zaxxer.hikari.pool.HikariProxyConnection;
import com.zaxxer.hikari.util.PropertyElf;
import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.admin.ServiceDalI;
import org.endeavourhealth.core.database.dal.admin.models.Service;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    /**
     * these enums define the known database schemas
     */
    public static enum Db {
        Eds("db_eds", true, "EdsDb"),
        Reference("db_reference", true, "ReferenceDB"),
        Hl7Receiver("db_hl7_receiver", true, "HL7ReceiverDb"),
        Admin("db_admin", true, "AdminDb"),
        Audit("db_audit", true, "AuditDb"),
        PublisherTransform("db_publisher_transform", false, "PublisherTransformDb"),
        SubscriberTransform("db_subscriber_transform", false, "SubscriberTransformDb"),
        Ehr("db_ehr", false, "EhrDb"),
        Logback("db_logback", false, "LogbackDb"),
        JdbcReader("db_jdbc_reader", true, "JDBCReaderDb"),
        PublisherCommon("db_publisher_common", true, "PublisherCommonDb"),
        FhirAudit("db_fhir_audit", true, "FhirAuditDb"),
        PublisherStaging("db_publisher_staging", false, "PublisherStagingDb"),
        DataGenerator("db_data_generator", true, "DataGeneratorDb"),
        SftpReader("db_sftp_reader", true, "SftpReaderDb"),
        Subscriber("db_subscriber", false, "SubscriberDb"), //this is used for Enterprise and Subscriber DBs
        KeyCloak("keycloak_db", true, "KeycloakDB"),
        UserManager("db_user_manager", true, "UserManager"),
        DataSharingManager("db_data_sharing_manager", true, "DataSharingManager");

        private String configName;
        private boolean singleInstance;
        private String hibernatePersistenceUnitName;

        Db(String configName, boolean singleInstance, String hibernatePersistenceUnitName) {
            this.configName = configName;
            this.singleInstance = singleInstance;
            this.hibernatePersistenceUnitName = hibernatePersistenceUnitName;
        }

        public String getConfigName(String instanceName) {
            //validate we have an instance name when we should and don't when we shouldn't
            if (singleInstance) {
                if (!Strings.isNullOrEmpty(instanceName)) {
                    throw new RuntimeException("No instance name should be supplied for single-instance DB " + this + " (" + instanceName + ")");
                }
                return configName;

            } else {
                if (Strings.isNullOrEmpty(instanceName)) {
                    throw new RuntimeException("No instance name supplied for multi-instance DB " + this);
                }
                return configName + ":" + instanceName;
            }
        }

        public String getHibernatePersistenceUnitName() {
            return hibernatePersistenceUnitName;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);

    private static Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();
    private static Map<String, EntityManagerFactory> entityManagerFactoryMap = new ConcurrentHashMap<>();
    private static Map<UUID, String> publisherServiceToConfigMap = new ConcurrentHashMap<>();
    private static Map<String, Integer> connectionMaxPoolSize = new ConcurrentHashMap<>();

    private static EntityManager getEntityManager(Db dbName) throws Exception {
        return getEntityManager(dbName, null);
    }

    private static EntityManager getEntityManager(Db dbName, String instanceName) throws Exception {

        EntityManagerFactory factory = getEntityManagerFactory(dbName, instanceName);
        return factory.createEntityManager();
    }

    private static Connection getConnection(Db dbName) throws Exception {
        return getConnection(dbName, null);
    }

    /**
     * in a lot of places we've moved away from Hibernate and use raw DB connections, so this function allows us
     * to easily get them but using the same Hikari connection pool as used by Hibernate. End result is we
     * don't have any more connections open but don't have to keep unwrapping the connection out of EntityManagers
     */
    private static Connection getConnection(Db dbName, String instanceName) throws Exception {
        try {
            DataSource dataSource = getDataSourceNewWay(dbName, instanceName);
            return dataSource.getConnection();

        } catch (Exception e) {
            //if using the old-style config, we need to get a connection by pulling the connection provider from the entityManagerFactory
            String msg = e.getMessage();
            LOG.error("Failed to get DB connection new way for " + dbName + " " + instanceName + " so will get old way: " + msg);
            EntityManagerFactory factory = getEntityManagerFactory(dbName, instanceName);
            SessionFactoryImpl sessionFactory = (SessionFactoryImpl)factory;
            ConnectionProvider provider = sessionFactory.getServiceRegistry().getService(ConnectionProvider.class);
            return provider.getConnection();
        }
    }




    public static EntityManagerFactory getEntityManagerFactory(Db dbName, String instanceName) throws Exception {

        String cacheKey = dbName.getConfigName(instanceName);
        EntityManagerFactory factory = entityManagerFactoryMap.get(cacheKey);

        if (factory == null
                || !factory.isOpen()) {

            synchronized (entityManagerFactoryMap) {

                //once in the sync block, repeat the check
                factory = entityManagerFactoryMap.get(cacheKey);
                if (factory == null
                        || !factory.isOpen()) {

                    try {
                        factory = createEntityManagerFactoryNewWay(dbName, instanceName);
                    } catch (Exception e) {
                        String msg = e.getMessage();
                        LOG.error("Failed to get DB credentials new way for " + dbName + " " + instanceName + " so will get old way: " + msg);
                        factory = createEntityManagerFactoryOldWay(dbName, instanceName);
                    }

                    entityManagerFactoryMap.put(cacheKey, factory);
                }
            }
        }

        return factory;
    }

    /**
     * new way of connecting to DB using new-style config records to allow different credentials per application
     */
    private static EntityManagerFactory createEntityManagerFactoryNewWay(Db dbName, String instanceName) throws Exception {

        //adding this line to force compile-time checking for this class. Spent far too long investigating
        //why this wasn't being found when it turned out to be that it had been removed from POM.xml,
        //so adding this to ensure it's picked up during compile-time rather than run-time
        org.hibernate.hikaricp.internal.HikariCPConnectionProvider p = null;

        /*String configName = dbName.getConfigName(instanceName);
        JsonNode json = ConfigManager.getConfigurationAsJson(configName);
        if (json == null) {
            throw new RuntimeException("No new-style config record found for [" + configName + "]");
        }*/

        Map<String, Object> properties = new HashMap<>();

        //always turn this off (https://stackoverflow.com/questions/10075081/hibernate-slow-to-acquire-postgres-connection)
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");

        DataSource dataSource = getDataSourceNewWay(dbName, instanceName);
        properties.put("hibernate.connection.datasource", dataSource);

        String hibernatePersistenceUnit = dbName.getHibernatePersistenceUnitName();
        return Persistence.createEntityManagerFactory(hibernatePersistenceUnit, properties);
    }

    public static DataSource getDataSourceNewWay(Db dbName, String instanceName) throws Exception {


        String cacheKey = dbName.getConfigName(instanceName);
        DataSource ret = dataSourceMap.get(cacheKey);
        if (ret == null) {
            synchronized (dataSourceMap) {
                ret = dataSourceMap.get(cacheKey);
                if (ret == null) {
                    ret = createDataSourceNewWay(dbName, instanceName);
                    dataSourceMap.put(cacheKey, ret);
                }
            }
        }

        return ret;
    }

    /**
     * new way of connecting to DB using new-style config records to allow different credentials per application
     */
    private static DataSource createDataSourceNewWay(Db dbName, String instanceName) throws Exception {

        String configName = dbName.getConfigName(instanceName);

        //adding this line to force compile-time checking for this class. Spent far too long investigating
        //why this wasn't being found when it turned out to be that it had been removed from POM.xml,
        //so adding this to ensure it's picked up during compile-time rather than run-time
        org.hibernate.hikaricp.internal.HikariCPConnectionProvider p = null;

        //create the pool and set some standard defaults for the connection pool
        HikariDataSource pool = new HikariDataSource();
        pool.setMinimumIdle(1);
        pool.setIdleTimeout(60000);
        pool.setPoolName(configName);
        pool.setAutoCommit(false);
        pool.setMaximumPoolSize(3);
        pool.setConnectionTimeout(300000L);
        pool.setLeakDetectionThreshold(5000L);

        /*
            <property name="hibernate.hikari.maximumPoolSize" value="10" />
         */

        //load the config record from the DB (including URL, username and password) into a properties map, then apply to the pool
        Properties properties = new Properties();
        populateConnectionPropertiesNewWay(configName, properties);
        PropertyElf.setTargetFromProperties(pool, properties);

        return pool;
    }


    private static void populateConnectionPropertiesNewWay(String configName, Properties properties) throws Exception {

        JsonNode json = ConfigManager.getConfigurationAsJson(configName);
        if (json == null) {
            throw new Exception("Failed to find config record [" + configName + "] for app ID " + ConfigManager.getAppId());
        }

        Iterator<String> fieldNames = json.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode child = json.get(fieldName);

            if (fieldName.equals("url")) {
                String url = child.asText();
                properties.put("jdbcUrl", url);

            } else if (fieldName.equals("credentials")
                    || fieldName.equals("nested_config")) {
                //if we find a "nested_config" or "credentials" element, this gives us the name of another config record, so recurse
                String credentialsName = child.asText();
                populateConnectionPropertiesNewWay(credentialsName, properties);

            } else if (fieldName.equals("class")) {
                String cls = child.asText();

                //although this property works when used through the Hibernate Hikari properties,
                //if used directly on a HikariCP pool, it overrides the jdbcUrl setting and will
                //connect but will lose the connection properties. So just ensure the class is loaded
                //but don't set the property (see https://github.com/brettwooldridge/HikariCP)
                //properties.put("dataSourceClassName", cls);

                //force the driver to be loaded
                Class.forName(cls);

            } else if (child.isTextual()) {
                //e.g. username, password
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
                throw new IllegalArgumentException("Unsupported JSON element type for database " + configName + ": " + child.getNodeType());
            }
        }
    }


    /**
     * legacy support for old-style config records for DB credentials, to be phased out on DDS Live
     */
    private static synchronized EntityManagerFactory createEntityManagerFactoryOldWay(Db dbName, String instanceName) throws Exception {

        //adding this line to force compile-time checking for this class. Spent far too long investigating
        //why this wasn't being found when it turned out to be that it had been removed from POM.xml,
        //so adding this to ensure it's picked up during compile-time rather than run-time
        org.hibernate.hikaricp.internal.HikariCPConnectionProvider p = null;

        JsonNode json = findDatabaseConfigJsonOldWay(dbName, instanceName);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false"); //always turn this off (https://stackoverflow.com/questions/10075081/hibernate-slow-to-acquire-postgres-connection)

        //moved this from persistence.xml since this property overrides the explicitly provided data source used in the new way
        properties.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

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
                populateConnectionPropertiesOldWay(child, properties, dbName, instanceName);

            } else {
                //ignore it, as it's nothing to do with the DB connection
            }
        }

        String hibernatePersistenceUnit = dbName.getHibernatePersistenceUnitName();
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(hibernatePersistenceUnit, properties);

        return factory;
    }

    private static void populateConnectionPropertiesOldWay(JsonNode connectionPropertiesRoot, Map<String, Object> properties, Db dbName, String instanceName) {

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
                throw new IllegalArgumentException("Unsupported JSON element type for database " + dbName + " " + instanceName + ": " + child.getNodeType());
            }
        }

    }


    private static JsonNode findDatabaseConfigJsonOldWay(Db dbName, String configName) throws Exception {

        JsonNode json = null;
        if (dbName == Db.SubscriberTransform) {
            json = ConfigManager.getConfigurationAsJson(configName, "db_subscriber");

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

        } else if (dbName == Db.DataSharingManager) {
            json = ConfigManager.getConfigurationAsJson("database", "data-sharing-manager");
        } else if (dbName == Db.UserManager) {
            json = ConfigManager.getConfigurationAsJson("database", "user-manager");
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
            } else if (dbName == Db.SftpReader) {
                configName = "sftp_reader";
            }
            else {
                throw new RuntimeException("Unknown database " + dbName);
            }

            /**

             Enterprise("db_enterprise", false, "EnterpriseDb"),
             Subscriber("db_subscriber", false, "SubscriberDb"),
             */

            json = ConfigManager.getConfigurationAsJson(configName, "db_common");
        }

        if (json == null) {
            throw new Exception("No config JSON for " + dbName + " and config " + configName);
        }

        return json;
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

    private static int getConnectionPoolMaxSize(Db dbName, String instanceName) throws Exception {
        String cacheKey = "" + dbName + "/" + instanceName;
        Integer maxPoolSize = connectionMaxPoolSize.get(cacheKey);

        if (maxPoolSize == null) {

            synchronized (connectionMaxPoolSize) {

                EntityManager entityManager = getEntityManager(dbName, instanceName);
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


    public static EntityManager getEdsEntityManager() throws Exception {
        return getEntityManager(Db.Eds);
    }

    public static EntityManager getReferenceEntityManager() throws Exception {
        return getEntityManager(Db.Reference);
    }

    public static EntityManager getHl7ReceiverEntityManager() throws Exception {
        return getEntityManager(Db.Hl7Receiver);
    }

    public static EntityManager getSftpReaderEntityManager() throws Exception {
        return getEntityManager(Db.SftpReader);
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

    public static EntityManager getDsmEntityManager() throws Exception {
        return getEntityManager(Db.DataSharingManager);
    }

    public static EntityManager getUmEntityManager() throws Exception {
        return getEntityManager(Db.UserManager);
    }

    /**
     * no hibernate entities on subscriber/enterprise DBs
     */
    /*public static EntityManager getSubscriberEntityManager(String configName) throws Exception {
        return getEntityManager(Db.Subscriber, configName);
    }*/

    /**
     * no hibernate entities in EHR DB now
     */
    /*public static EntityManager getEhrEntityManager(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getEntityManager(Db.Ehr, configName);
    }*/

    public static EntityManager getLogbackEntityManager() throws Exception {
        return getEntityManager(Db.Logback);
    }

    public static EntityManager getJDBCReaderEntityManager() throws Exception {
        return getEntityManager(Db.JdbcReader);
    }

    public static EntityManager getPublisherCommonEntityManager() throws Exception {
        return getEntityManager(Db.PublisherCommon);
    }

    public static EntityManager getPublisherStagingEntityManager(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getEntityManager(Db.PublisherStaging, configName);
    }

    public static EntityManager getDataGeneratorEntityManager () throws Exception {
        return getEntityManager(Db.DataGenerator);
    }

    public static EntityManager getKeyCloakEntityManager() throws Exception {
        return getEntityManager(Db.KeyCloak);
    }

    public static Connection getEdsConnection() throws Exception {
        return getConnection(Db.Eds);
    }

    public static Connection getReferenceConnection() throws Exception {
        return getConnection(Db.Reference);
    }

    public static Connection getHl7ReceiverConnection() throws Exception {
        return getConnection(Db.Hl7Receiver);
    }

    public static Connection getSftpReaderConnection() throws Exception {
        return getConnection(Db.SftpReader);
    }

    public static Connection getAdminConnection() throws Exception {
        return getConnection(Db.Admin);
    }

    public static Connection getAuditConnection() throws Exception {
        return getConnection(Db.Audit);
    }

    public static Connection getFhirAuditConnection() throws Exception {
        return getConnection(Db.FhirAudit);
    }

    public static Connection getPublisherTransformConnection(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getConnection(Db.PublisherTransform, configName);
    }

    public static Connection getSubscriberTransformConnection(String configName) throws Exception {
        return getConnection(Db.SubscriberTransform, configName);
    }

    public static Connection getSubscriberConnection(String configName) throws Exception {
        return getConnection(Db.Subscriber, configName);
    }

    public static Connection getEhrConnection(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getConnection(Db.Ehr, configName);
    }

    public static Connection getLogbackConnection() throws Exception {
        return getConnection(Db.Logback);
    }

    public static Connection getJDBCReaderConnection() throws Exception {
        return getConnection(Db.JdbcReader);
    }

    public static Connection getPublisherCommonConnection() throws Exception {
        return getConnection(Db.PublisherCommon);
    }

    public static Connection getPublisherStagingConnection(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getConnection(Db.PublisherStaging, configName);
    }

    public static Connection getDataGeneratorConnection() throws Exception {
        return getConnection(Db.DataGenerator);
    }

    public static Connection getDataSharingManagerConnection() throws Exception {
        return getConnection(Db.DataSharingManager);
    }

    public static Connection getUserManagerConnection() throws Exception {
        return getConnection(Db.UserManager);
    }



    /**
     * returns a DB connection that isn't from a connection pool. Useful if the connection will be kept open
     * for an extended period
     */
    private static Connection getConnectionNonPooled(Db dbName) throws Exception {
        return getConnectionNonPooled(dbName, null);
    }

    private static Connection getConnectionNonPooled(Db dbName, String instanceName) throws Exception {
        try {
            return openNonPooledConnectionNewWay(dbName, instanceName);

        } catch (Exception e) {
            return openNonPooledConnectionOldWay(dbName, instanceName);
        }
    }



    private static Connection openNonPooledConnectionNewWay(Db dbName, String instanceName) throws Exception {
        //load the config record from the DB (including URL, username and password) into a properties map, then apply to the pool
        String configName = dbName.getConfigName(instanceName);
        Properties properties = new Properties();
        populateConnectionPropertiesNewWay(configName, properties);

        String url = (String)properties.get("jdbcUrl");
        String username = (String)properties.get("username");
        String password = (String)properties.get("password");

        Connection connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(false); //so this matches the pooled connections
        return connection;
    }

    private static Connection openNonPooledConnectionOldWay(Db dbName, String instanceName) throws Exception {
        JsonNode json = findDatabaseConfigJsonOldWay(dbName, instanceName);

        String url = null;
        String username = null;
        String password = null;

        Iterator<String> fieldNames = json.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode child = json.get(fieldName);

            if (fieldName.equals("url")) {
                url = child.asText();

            } else if (fieldName.equals("username")) {
                username = child.asText();

            } else if (fieldName.equals("password")) {
                password = child.asText();

            } else {
                //ignore it, as it's nothing to do with the DB connection
            }
        }

        Connection connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(false); //so this matches the pooled connections
        return connection;
    }

    public static Connection getEdsNonPooledConnection() throws Exception {
        return getConnectionNonPooled(Db.Eds);
    }

    public static Connection getReferenceNonPooledConnection() throws Exception {
        return getConnectionNonPooled(Db.Reference);
    }

    public static Connection getHl7ReceiverNonPooledConnection() throws Exception {
        return getConnectionNonPooled(Db.Hl7Receiver);
    }

    public static Connection getSftpReaderNonPooledConnection() throws Exception {
        return getConnectionNonPooled(Db.SftpReader);
    }

    public static Connection getAdminNonPooledConnection() throws Exception {
        return getConnectionNonPooled(Db.Admin);
    }

    public static Connection getAuditNonPooledConnection() throws Exception {
        return getConnectionNonPooled(Db.Audit);
    }

    public static Connection getFhirAuditNonPooledConnection() throws Exception {
        return getConnectionNonPooled(Db.FhirAudit);
    }

    public static Connection getPublisherTransformNonPooledConnection(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getConnectionNonPooled(Db.PublisherTransform, configName);
    }

    public static Connection getSubscriberTransformNonPooledConnection(String configName) throws Exception {
        return getConnectionNonPooled(Db.SubscriberTransform, configName);
    }

    public static Connection getSubscriberNonPooledConnection(String configName) throws Exception {
        return getConnectionNonPooled(Db.Subscriber, configName);
    }

    public static Connection getEhrNonPooledConnection(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getConnectionNonPooled(Db.Ehr, configName);
    }

    public static Connection getLogbackNonPooledConnection() throws Exception {
        return getConnectionNonPooled(Db.Logback);
    }

    public static Connection getJDBCReaderNonPooledConnection() throws Exception {
        return getConnectionNonPooled(Db.JdbcReader);
    }

    public static Connection getPublisherCommonNonPooledConnection() throws Exception {
        return getConnectionNonPooled(Db.PublisherCommon);
    }

    public static Connection getPublisherStagingNonPooledConnection(UUID serviceId) throws Exception {
        String configName = findConfigNameForPublisherService(serviceId);
        return getConnectionNonPooled(Db.PublisherStaging, configName);
    }

    public static Connection getDataGeneratorNonPooledConnection() throws Exception {
        return getConnectionNonPooled(Db.DataGenerator);
    }

}
