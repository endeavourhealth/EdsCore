package org.endeavourhealth.core.database.rdbms;

import com.fasterxml.jackson.databind.JsonNode;
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
        //Subscriber, there are multiple subscriber DBs, so their connections are managed by SubscriberConnectionMananger
        Admin,
        Audit,
        Transform,
        Ehr
    }

    private static Map<Db, EntityManagerFactory> entityManagerFactoryMap = new ConcurrentHashMap<>();

    public static EntityManager getEntityManager(Db dbName) throws Exception {

        EntityManagerFactory factory = entityManagerFactoryMap.get(dbName);

        if (factory == null
                || !factory.isOpen()) {
            factory = createEntityManager(dbName);
        }

        return factory.createEntityManager();
    }

    public static void shutdown(Db dbName) {

        EntityManagerFactory factory = entityManagerFactoryMap.get(dbName);

        if (factory != null) {
            factory.close();
            entityManagerFactoryMap.remove(dbName);
        }
    }

    private static synchronized EntityManagerFactory createEntityManager(Db dbName) throws Exception {

        EntityManagerFactory factory = entityManagerFactoryMap.get(dbName);

        if (factory != null
                && factory.isOpen()) {
            return factory;
        }

        //adding this line to force compile-time checking for this class. Spent far too long investigating
        //why this wasn't being found when it turned out to be that it had been removed from POM.xml,
        //so adding this to ensure it's picked up during compile-time rather than run-time
        org.hibernate.hikaricp.internal.HikariCPConnectionProvider p = null;

        String configName = getConfigNameForDb(dbName);
        JsonNode json = ConfigManager.getConfigurationAsJson(configName);
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

        String persistanceUnitName = getPersistenceUnitName(dbName);
        factory = Persistence.createEntityManagerFactory(persistanceUnitName, properties);

        entityManagerFactoryMap.put(dbName, factory);

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
        } else if (dbName == Db.Transform) {
            return "TransformDb";
        } else if (dbName == Db.Ehr) {
            return "EhrDb";
        } else {
            throw new RuntimeException("Unknown database " + dbName);
        }
    }


    private static String getConfigNameForDb(Db dbName) {
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
        } else if (dbName == Db.Transform) {
            return "db_transform";
        } else if (dbName == Db.Ehr) {
            return "db_ehr";
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

    public static EntityManager getTransformEntityManager() throws Exception {
        return getEntityManager(Db.Transform);
    }

    public static EntityManager getEhrEntityManager() throws Exception {
        return getEntityManager(Db.Ehr);
    }
}
