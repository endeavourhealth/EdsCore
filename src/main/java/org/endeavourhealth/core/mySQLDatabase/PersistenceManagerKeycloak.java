package org.endeavourhealth.core.mySQLDatabase;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class PersistenceManagerKeycloak {
    private static EntityManagerFactory entityManagerFactory;


    public static EntityManager getEntityManager() throws Exception {

        if (entityManagerFactory == null
                || !entityManagerFactory.isOpen()) {
            createEntityManager();
        }

        return entityManagerFactory.createEntityManager();
    }

    private static synchronized void createEntityManager() throws Exception {

        if (entityManagerFactory != null
                && entityManagerFactory.isOpen()) {
            return;
        }

//        JsonNode json = ConfigManager.getConfigurationAsJson("KeycloakDB");
//        String url = json.get("url").asText();
//        String user = json.get("username").asText();
//        String pass = json.get("password").asText();

        //TODO: move to config db entry
        String url = "jdbc:mysql://keycloak-v3-devmysql.csjxcq8rzerp.eu-west-2.rds.amazonaws.com:3306/keycloak";
        String user = "<USERNAME>";
        String pass = "<PASSWORD>";

        Map<String, Object> properties = new HashMap<>();
        //properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("hibernate.connection.url", url);
        properties.put("hibernate.connection.username", user);
        properties.put("hibernate.connection.password", pass);

        entityManagerFactory = Persistence.createEntityManagerFactory("KeycloakDB", properties);
    }
}
