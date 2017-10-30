package org.endeavourhealth.core.database.rdbms;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import org.endeavourhealth.common.config.ConfigManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
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
        Coding; //once fully moved to MySQL, this can go as it will be the same as Reference
    }

    private static Map<String, EntityManagerFactory> entityManagerFactoryMap = new ConcurrentHashMap<>();

    public static EntityManager getEntityManager(Db dbName) throws Exception {
        return getEntityManager(dbName, null);
    }

    public static EntityManager getEntityManager(Db dbName, String explicitConfigName) throws Exception {

        String configNameToUse = getConfigNameForDb(dbName, explicitConfigName);

        EntityManagerFactory factory = entityManagerFactoryMap.get(configNameToUse);

        if (factory == null
                || !factory.isOpen()) {
            String hibernatePersistenceUnit = getPersistenceUnitName(dbName);
            factory = createEntityManager(dbName, configNameToUse, hibernatePersistenceUnit);
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

    private static synchronized EntityManagerFactory createEntityManager(Db dbName, String configName, String hibernatePersistenceUnit) throws Exception {

        EntityManagerFactory factory = entityManagerFactoryMap.get(configName);

        if (factory != null
                && factory.isOpen()) {
            return factory;
        }

        //adding this line to force compile-time checking for this class. Spent far too long investigating
        //why this wasn't being found when it turned out to be that it had been removed from POM.xml,
        //so adding this to ensure it's picked up during compile-time rather than run-time
        org.hibernate.hikaricp.internal.HikariCPConnectionProvider p = null;

        JsonNode json = null;
        if (dbName == Db.SubscriberTransform) {
            json = ConfigManager.getConfigurationAsJson(configName, "subscriber");
        } else {
            json = ConfigManager.getConfigurationAsJson(configName);
        }

        if (json == null) {
            throw new Exception("No config JSON for " + dbName + " and config " + configName);
        }

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

        factory = Persistence.createEntityManagerFactory(hibernatePersistenceUnit, properties);

        entityManagerFactoryMap.put(configName, factory);

        return factory;
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
        } else {
            throw new RuntimeException("Unknown database " + dbName);
        }
    }


    private static String getConfigNameForDb(Db dbName, String explicitConfigName) {

        //the subuscriber transform DB always must have the config name supplied too
        if (dbName == Db.SubscriberTransform
                && Strings.isNullOrEmpty(explicitConfigName)) {
            throw new IllegalArgumentException("Config name should be supplied for connections to subscriber transform DBs");

        } else if (dbName != Db.SubscriberTransform
                && !Strings.isNullOrEmpty(explicitConfigName)) {
            throw new IllegalArgumentException("Config name should not be supplied for connections to the " + dbName + " DB");
        }

        if (dbName == Db.Eds) {
            return "eds_db";
        } else if (dbName == Db.Reference) {
            return "reference_db";
        } else if (dbName == Db.Hl7Receiver) {
            return "hl7_receiver_db";
        } else if (dbName == Db.Admin) {
            return "db_admin";
        } else if (dbName == Db.Audit) {
            return "db_audit";
        } else if (dbName == Db.PublisherTransform) {
            return "db_publisher_transform";
        } else if (dbName == Db.SubscriberTransform) {
            return explicitConfigName;
        } else if (dbName == Db.Ehr) {
            return "db_ehr";
        } else if (dbName == Db.Logback) {
            return "logbackDb";
        } else if (dbName == Db.Coding) {
            return "coding";
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

    public static EntityManager getPublisherTransformEntityManager() throws Exception {
        return getEntityManager(Db.PublisherTransform);
    }

    public static EntityManager getSubscriberTransformEntityManager(String configName) throws Exception {
        return getEntityManager(Db.SubscriberTransform, configName);
    }

    public static EntityManager getEhrEntityManager() throws Exception {
        return getEntityManager(Db.Ehr);
    }

    public static EntityManager getLogbackEntityManager() throws Exception {
        return getEntityManager(Db.Logback);
    }

    public static EntityManager getCodingEntityManager() throws Exception {
        return getEntityManager(Db.Coding);
    }
}
