package org.endeavourhealth.core.database.rdbms;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.admin.ServiceDalI;
import org.endeavourhealth.core.database.dal.admin.models.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
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
        PublisherCommon;
    }

    private static Map<String, EntityManagerFactory> entityManagerFactoryMap = new ConcurrentHashMap<>();
    private static Map<UUID, String> publisherServiceToConfigMap = new ConcurrentHashMap<>();

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
    }

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
            } else {
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
        } else {
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
}
