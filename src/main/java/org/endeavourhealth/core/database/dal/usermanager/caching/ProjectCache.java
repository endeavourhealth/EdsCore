package org.endeavourhealth.core.database.dal.usermanager.caching;


import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.ProjectApplicationPolicyDalI;
import org.endeavourhealth.core.database.dal.datasharingmanager.ProjectDalI;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreProjectApplicationPolicyDal;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreProjectDal;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ProjectApplicationPolicyEntity;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ProjectEntity;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonProject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectCache {

    private static Map<String, ProjectEntity> projectMap = new ConcurrentHashMap<>();
    private static Map<String, JsonProject> jsonProjectMap = new ConcurrentHashMap<>();
    private static Map<String, String> projectApplicationPolicyMap = new ConcurrentHashMap<>();
    private static Map<String, List<ProjectEntity>> allProjectsForAllChildRegion = new ConcurrentHashMap<>();
    private static Map<String, List<String>> allPublishersForProjectWithSubCheck = new ConcurrentHashMap<>();
    private static Map<String, List<ProjectEntity>> allProjectsForSubscriberODS = new ConcurrentHashMap<>();

    private static ProjectDalI repository = DalProvider.factoryDSMProjectDal();
    private static ProjectApplicationPolicyDalI ProjectAppPolicyRepository = DalProvider.factoryDSMProjectApplicationPolicyDal();

    public static List<ProjectEntity> getProjectDetails(List<String> projects) throws Exception {
        List<ProjectEntity> projectEntities = new ArrayList<>();
        List<String> missingProjects = new ArrayList<>();

        for (String org : projects) {
            ProjectEntity projInMap = projectMap.get(org);
            if (projInMap != null) {
                projectEntities.add(projInMap);
            } else {
                missingProjects.add(org);
            }
        }

        if (missingProjects.size() > 0) {
            List<ProjectEntity> entities = repository.getProjectsFromList(missingProjects);

            for (ProjectEntity org : entities) {
                projectMap.put(org.getUuid(), org);
                projectEntities.add(org);
            }
        }

        CacheManager.startScheduler();

        return projectEntities;

    }

    public static ProjectEntity getProjectDetails(String projectId) throws Exception {

        ProjectEntity projectEntity = projectMap.get(projectId);
        if (projectEntity == null) {
            projectEntity = repository.getProject(projectId);
            projectMap.put(projectEntity.getUuid(), projectEntity);
        }

        CacheManager.startScheduler();

        return projectEntity;

    }

    public static JsonProject getJsonProjectDetails(String projectId) throws Exception {

        JsonProject project = jsonProjectMap.get(projectId);
        if (project == null) {
            project = repository.getFullProjectJson(projectId);
            jsonProjectMap.put(project.getUuid(), project);
        }

        CacheManager.startScheduler();

        return project;

    }

    public static String getProjectApplicationPolicy(String projectId) throws Exception {

        String foundPolicy = projectApplicationPolicyMap.get(projectId);
        if (foundPolicy == null) {
            ProjectApplicationPolicyEntity policyApp = ProjectAppPolicyRepository.getProjectApplicationPolicyId(projectId);
            foundPolicy = policyApp.getApplicationPolicyId();
            projectApplicationPolicyMap.put(projectId, foundPolicy);
        }

        CacheManager.startScheduler();

        return foundPolicy;
    }

    public static List<ProjectEntity> getAllProjectsForAllChildRegions(String regionId) throws Exception {

        List<ProjectEntity> allProjects = allProjectsForAllChildRegion.get(regionId);
        if (allProjects == null) {
            allProjects = repository.getProjectsForRegion(regionId);
            allProjectsForAllChildRegion.put(regionId, allProjects);
        }

        CacheManager.startScheduler();

        return allProjects;
    }

    public static List<String> getAllPublishersForProjectWithSubscriberCheck(String projectId, String requesterOdsCode) throws Exception {
        String key = projectId + ":" + requesterOdsCode;

        List<String> pubOdsCodes = allPublishersForProjectWithSubCheck.get(key);
        if (pubOdsCodes == null) {
            pubOdsCodes = repository.getPublishersForProject(projectId, requesterOdsCode);
            allPublishersForProjectWithSubCheck.put(key, pubOdsCodes);
        }

        CacheManager.startScheduler();

        return pubOdsCodes;
    }

    public static List<ProjectEntity> getAllProjectsForSubscriberOrg(String odsCode) throws Exception {

        List<ProjectEntity> projects = allProjectsForSubscriberODS.get(odsCode);
        if (projects == null) {
            projects = repository.getAllProjectsForSubscriber(odsCode);
            allProjectsForSubscriberODS.put(odsCode, projects);
        }

        CacheManager.startScheduler();

        return projects;
    }

    public static void clearProjectCache(String projectId) throws Exception {
        projectMap.remove(projectId);

        jsonProjectMap.remove(projectId);

        projectApplicationPolicyMap.remove(projectId);

        allProjectsForAllChildRegion.clear();
        allPublishersForProjectWithSubCheck.clear();
    }

    public static void flushCache() throws Exception {
        projectMap.clear();
        jsonProjectMap.clear();
        projectApplicationPolicyMap.clear();
        allProjectsForAllChildRegion.clear();
    }
}
