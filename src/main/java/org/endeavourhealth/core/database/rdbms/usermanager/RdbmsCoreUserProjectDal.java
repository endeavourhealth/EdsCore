package org.endeavourhealth.core.database.rdbms.usermanager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.endeavourhealth.core.database.dal.usermanager.UserProjectDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.OrganisationEntity;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ProjectEntity;
import org.endeavourhealth.core.database.dal.usermanager.caching.ApplicationPolicyCache;
import org.endeavourhealth.core.database.dal.usermanager.caching.OrganisationCache;
import org.endeavourhealth.core.database.dal.usermanager.caching.ProjectCache;
import org.endeavourhealth.core.database.dal.usermanager.caching.UserCache;
import org.endeavourhealth.core.database.rdbms.usermanager.models.UserProjectEntity;
import org.endeavourhealth.core.database.dal.usermanager.models.JsonApplicationPolicyAttribute;
import org.keycloak.representations.idm.UserRepresentation;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class RdbmsCoreUserProjectDal implements UserProjectDalI {

    public Boolean checkUserProjectApplicationAccess(String userId,
                                                     String projectId,
                                                     String applicationName) throws Exception {

        List<JsonApplicationPolicyAttribute> mergedAttributes = new ArrayList<>();

        String userAppPolicy = UserCache.getUserApplicationPolicyId(userId);
        String projectAppPolicy = ProjectCache.getProjectApplicationPolicy(projectId);

        if (userAppPolicy.equals(projectAppPolicy)) {
            // policies are the same so just look through one of them
            mergedAttributes = ApplicationPolicyCache.getApplicationPolicyAttributes(userAppPolicy);
        } else {
            List<JsonApplicationPolicyAttribute> userAttributes = ApplicationPolicyCache.getApplicationPolicyAttributes(userAppPolicy);
            List<JsonApplicationPolicyAttribute> projectAttributes = ApplicationPolicyCache.getApplicationPolicyAttributes(userAppPolicy);

            for (JsonApplicationPolicyAttribute attribute : projectAttributes) {
                if (userAttributes.stream().filter(a -> a.getApplicationAccessProfileId().equals(attribute.getApplicationAccessProfileId())).findFirst().isPresent()) {
                    //user policy has the project attribute so add it to the list
                    mergedAttributes.add(attribute);
                }
            }
        }

        if (mergedAttributes.stream().anyMatch(a -> a.getApplication().equals(applicationName))) {
            return true;
        }

        return false;
    }

    @Override
    public Boolean checkExternalUserApplicationAccess(String userId, String applicationName) throws Exception {

        String userAppPolicy = UserCache.getUserApplicationPolicyId(userId);
        List<JsonApplicationPolicyAttribute> attributeList = ApplicationPolicyCache.getApplicationPolicyAttributes(userAppPolicy);

        if (attributeList.stream().anyMatch(a -> a.getApplication().equals(applicationName))) {
            return true;
        }

        return false;
    }

    public List<UserProjectEntity> getUserProjectEntitiesForProject(String projectId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            String sql = "select up" +
                    " from UserProjectEntity up" +
                    " where up.projectId = :projectId" +
                    " and up.isDeleted = 0";

            Query query = entityManager.createQuery(sql);

            query.setParameter("projectId", projectId);

            List<UserProjectEntity> results = query.getResultList();

            return results;
        } finally {
            entityManager.close();
        }

    }

    public List<Object[]> getUserProjects(String userId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            String sql = "select " +
                    " up.id," +
                    " up.userId," +
                    " up.projectId," +
                    " up.organisationId," +
                    " up.isDeleted," +
                    " up.isDefault" +
                    " from UserProjectEntity up" +
                    " where up.userId = :userId" +
                    " and up.isDeleted = 0";

            Query query = entityManager.createQuery(sql);

            query.setParameter("userId", userId);

            List<Object[]> results = query.getResultList();

            return results;
        } finally {
            entityManager.close();
        }

    }

    public void setCurrentDefaultProject(String userId, String userProjectId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update UserProjectEntity up" +
                    " set up.isDefault = 1 " +
                    " where up.userId = :user" +
                    " and up.id = :projectId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("user", userId)
                    .setParameter("projectId", userProjectId);

            query.executeUpdate();
            entityManager.getTransaction().commit();

            UserCache.clearUserCache(userId);


        } finally {
            entityManager.close();
        }
    }

    public void changeDefaultProject(String userId, String defaultRoleId, String userProjectId) throws Exception {
        UserProjectEntity oldDefaultProject = getDefaultProject(userId);
        UserProjectEntity newDefaultRole = getUserProject(defaultRoleId);

        removeCurrentDefaultProject(userId);

        setCurrentDefaultProject(userId, defaultRoleId);

        String auditJson = getAuditJsonForDefaultRoleChange(oldDefaultProject, newDefaultRole);

        UserCache.clearUserCache(userId);

    }

    public void removeCurrentDefaultProject(String userId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update UserProjectEntity up" +
                    " set up.isDefault = 0 " +
                    " where up.userId = :user";

            Query query = entityManager.createQuery(sql)
                    .setParameter("user", userId);

            query.executeUpdate();
            entityManager.getTransaction().commit();

            UserCache.clearUserCache(userId);


        } finally {
            entityManager.close();
        }
    }

    public UserProjectEntity getDefaultProject(String userId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<UserProjectEntity> cq = cb.createQuery(UserProjectEntity.class);
            Root<UserProjectEntity> rootEntry = cq.from(UserProjectEntity.class);

            Predicate predicate = cb.and(cb.equal(rootEntry.get("userId"), userId),
                    (cb.equal(rootEntry.get("isDefault"), 1)),
                    (cb.equal(rootEntry.get("isDeleted"), 0)));

            cq.where(predicate);
            TypedQuery<UserProjectEntity> query = entityManager.createQuery(cq);
            UserProjectEntity ret = query.getSingleResult();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public UserProjectEntity getUserProject(String userProjectId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<UserProjectEntity> cq = cb.createQuery(UserProjectEntity.class);
            Root<UserProjectEntity> rootEntry = cq.from(UserProjectEntity.class);

            Predicate predicate = cb.equal(rootEntry.get("id"), userProjectId);

            cq.where(predicate);
            TypedQuery<UserProjectEntity> query = entityManager.createQuery(cq);
            UserProjectEntity ret = query.getSingleResult();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    private String getAuditJsonForDefaultRoleChange(UserProjectEntity oldDefault, UserProjectEntity newDefault) throws Exception {

        JsonNode beforeJson = generateDefaultRoleJson(oldDefault);
        JsonNode afterJson = generateDefaultRoleJson(newDefault);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode)rootNode).put("title", "Default project changed");

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        if (beforeJson != null) {
            ((ObjectNode) rootNode).set("before", beforeJson);
        }

        return prettyPrintJsonString(rootNode);
    }

    private JsonNode generateDefaultRoleJson(UserProjectEntity userProject) throws Exception {
        UserRepresentation user = UserCache.getUserDetails(userProject.getUserId());
        OrganisationEntity organisation = OrganisationCache.getOrganisationDetails(userProject.getOrganisationId());
        ProjectEntity projectEntity = ProjectCache.getProjectDetails(userProject.getProjectId());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();

        ((ObjectNode)auditJson).put("id", userProject.getId());
        ((ObjectNode)auditJson).put("user", user.getUsername());
        ((ObjectNode)auditJson).put("project", projectEntity.getName());
        ((ObjectNode)auditJson).put("organisation", organisation.getName() + " (" + organisation.getOdsCode() + ")");

        return auditJson;
    }

    private static String prettyPrintJsonString(JsonNode jsonNode) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonNode.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            throw new Exception("Converting Json to String failed : " + e.getMessage() );
        }
    }

    public List<UserProjectEntity> getUserProjectEntities(String userId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            String sql = "select up" +
                    " from UserProjectEntity up" +
                    " where up.userId = :userId" +
                    " and up.isDeleted = 0";

            Query query = entityManager.createQuery(sql);

            query.setParameter("userId", userId);

            List<UserProjectEntity> results = query.getResultList();

            return results;
        } finally {
            entityManager.close();
        }

    }
}
