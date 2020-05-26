package org.endeavourhealth.core.database.rdbms.datasharingmanager;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.*;
import org.endeavourhealth.core.database.dal.datasharingmanager.enums.MapType;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonAuthorityToShare;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonExtractTechnicalDetails;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonProject;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonProjectSchedule;
import org.endeavourhealth.core.database.dal.usermanager.UserProjectDalI;
import org.endeavourhealth.core.database.dal.usermanager.caching.*;
import org.endeavourhealth.core.database.dal.usermanager.models.JsonUser;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.*;
import org.endeavourhealth.core.database.rdbms.usermanager.models.UserProjectEntity;
import org.keycloak.representations.idm.UserRepresentation;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class RdbmsCoreProjectDal implements ProjectDalI {
    private static MasterMappingDalI masterMappingRepository = DalProvider.factoryDSMMasterMappingDal();
    private static UserProjectDalI userProjectRepository = DalProvider.factoryUMUserProjectDal();
    private static ProjectApplicationPolicyDalI projectAppPolicyRepository = DalProvider.factoryDSMProjectApplicationPolicyDal();
    private static ExtractTechnicalDetailsDalI extractRepository = DalProvider.factoryDSMExtractTechnicalDetailsDal();
    private static ProjectScheduleDalI scheduleRepository = DalProvider.factoryDSMProjectScheduleDal();
    private static OrganisationDalI organisationRepository = DalProvider.factoryDSMOrganisationDal();

    public List<ProjectEntity> getProjectsFromList(List<String> projects) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ProjectEntity> cq = cb.createQuery(ProjectEntity.class);
            Root<ProjectEntity> rootEntry = cq.from(ProjectEntity.class);

            Predicate predicate = rootEntry.get("uuid").in(projects);

            cq.where(predicate);
            TypedQuery<ProjectEntity> query = entityManager.createQuery(cq);

            List<ProjectEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public ProjectEntity getProject(String uuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            ProjectEntity ret = entityManager.find(ProjectEntity.class, uuid);

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public List<JsonAuthorityToShare> getUsersAssignedToProject(String projectUuid) throws Exception {

        List<UserProjectEntity> userProjects = userProjectRepository.getUserProjectEntitiesForProject(projectUuid);

        List<JsonAuthorityToShare> authorities = new ArrayList<>();

        for (UserProjectEntity userProject : userProjects) {
            JsonAuthorityToShare auth = authorities.stream().filter(a -> a.getOrganisationId().equals(userProject.getOrganisationId())).findFirst().orElse(new JsonAuthorityToShare());
            if (auth.getOrganisationId() == null) {
                OrganisationEntity org = OrganisationCache.getOrganisationDetails(userProject.getOrganisationId());
                auth.setOrganisationId(org.getUuid());
                auth.setOrganisationName(org.getName());
                auth.setOrganisationOdsCode(org.getOdsCode());

                authorities.add(auth);
            }
            UserRepresentation u = UserCache.getUserDetails(userProject.getUserId());
            if (u != null){
                JsonUser jsonUser = new JsonUser(u);
                auth.addUser(jsonUser);
            }

        }
        return authorities;
    }

    public JsonProject getFullProjectJson(String projectId) throws Exception {
        JsonProject project = new JsonProject(getProject(projectId));

        List<DataSharingAgreementEntity> dsas = getLinkedDsas(projectId);
        List<CohortEntity> basePopulations = getBasePopulations(projectId);
        List<DataSetEntity> dataSets = getDataSets(projectId);
        List<OrganisationEntity> publishers = getLinkedOrganisations(projectId, MapType.PUBLISHER.getMapType());
        List<OrganisationEntity> subscribers = getLinkedOrganisations(projectId, MapType.SUBSCRIBER.getMapType());
        ProjectApplicationPolicyEntity applicationPolicy = projectAppPolicyRepository.getProjectApplicationPolicyId(projectId);
        ExtractTechnicalDetailsEntity extractTechnicalDetailsEntity = getLinkedExtractTechnicalDetails(projectId, MapType.EXTRACTTECHNICALDETAILS.getMapType());
        ProjectScheduleEntity scheduleEntity = getLinkedSchedule(projectId, MapType.SCHEDULE.getMapType());

        if (dsas != null) {
            Map<UUID, String> sharingAgreements = new HashMap<>();

            for (DataSharingAgreementEntity dsa : dsas) {
                sharingAgreements.put(UUID.fromString(dsa.getUuid()), dsa.getName());
            }
            project.setDsas(sharingAgreements);
        }

        if (basePopulations != null) {
            Map<UUID, String> populations = new HashMap<>();

            for (CohortEntity pop : basePopulations) {
                populations.put(UUID.fromString(pop.getUuid()), pop.getName());
            }
            project.setCohorts(populations);
        }

        if (dataSets != null) {
            Map<UUID, String> data = new HashMap<>();

            for (DataSetEntity ds : dataSets) {
                data.put(UUID.fromString(ds.getUuid()), ds.getName());
            }
            project.setDataSets(data);
        }

        if (publishers != null) {
            Map<UUID, String> pubs = new HashMap<>();

            for (OrganisationEntity pub : publishers) {
                pubs.put(UUID.fromString(pub.getUuid()), pub.getName());
            }
            project.setPublishers(pubs);
        }

        if (subscribers != null) {
            Map<UUID, String> subs = new HashMap<>();

            for (OrganisationEntity sub : subscribers) {
                subs.put(UUID.fromString(sub.getUuid()), sub.getName());
            }
            project.setSubscribers(subs);
        }

        if (applicationPolicy != null) {
            project.setApplicationPolicy(applicationPolicy.getApplicationPolicyId());
        }

        if (extractTechnicalDetailsEntity != null) {
            project.setExtractTechnicalDetails(setJsonExtractTechnicalDetails(extractTechnicalDetailsEntity));
        }

        if (scheduleEntity != null) {
            project.setSchedule(setJsonProjectSchedule(scheduleEntity));
            Map<UUID, String> scheds = new HashMap<>();
            scheds.put(UUID.fromString(scheduleEntity.getUuid()), scheduleEntity.getCronDescription());
            project.setSchedules(scheds);
        }

        return project;
    }

    public JsonExtractTechnicalDetails setJsonExtractTechnicalDetails(ExtractTechnicalDetailsEntity detailsEntity) throws Exception {
        JsonExtractTechnicalDetails jsonDetails = new JsonExtractTechnicalDetails();

        jsonDetails.setUuid(detailsEntity.getUuid());
        jsonDetails.setName(detailsEntity.getName());
        jsonDetails.setSftpHostName(detailsEntity.getSftpHostName());
        jsonDetails.setSftpHostDirectory(detailsEntity.getSftpHostDirectory());
        jsonDetails.setSftpHostPort(detailsEntity.getSftpHostPort());
        jsonDetails.setSftpClientUsername(detailsEntity.getSftpClientUsername());
        jsonDetails.setSftpClientPrivateKeyPassword(detailsEntity.getSftpClientPrivateKeyPassword());
        jsonDetails.setSftpHostPublicKeyFilename(detailsEntity.getSftpHostPublicKeyFilename());
        jsonDetails.setSftpHostPublicKeyFileData(detailsEntity.getSftpHostPublicKeyFileData());
        jsonDetails.setSftpClientPrivateKeyFilename(detailsEntity.getSftpClientPrivateKeyFilename());
        jsonDetails.setSftpClientPrivateKeyFileData(detailsEntity.getSftpClientPrivateKeyFileData());
        jsonDetails.setPgpCustomerPublicKeyFilename(detailsEntity.getPgpCustomerPublicKeyFilename());
        jsonDetails.setPgpCustomerPublicKeyFileData(detailsEntity.getPgpCustomerPublicKeyFileData());
        jsonDetails.setPgpInternalPublicKeyFilename(detailsEntity.getPgpInternalPublicKeyFilename());
        jsonDetails.setPgpInternalPublicKeyFileData(detailsEntity.getPgpInternalPublicKeyFileData());

        return jsonDetails;
    }

    public JsonProjectSchedule setJsonProjectSchedule(ProjectScheduleEntity scheduleEntity) throws Exception {
        JsonProjectSchedule schedule = new JsonProjectSchedule();
        schedule.setUuid(scheduleEntity.getUuid());
        schedule.setCronExpression(scheduleEntity.getCronExpression());
        schedule.setCronDescription(scheduleEntity.getCronDescription());
        schedule.setCronSettings(scheduleEntity.getCronSettings());
        return schedule;
    }

    public List<DataSharingAgreementEntity> getLinkedDsas(String projectId) throws Exception {

        List<String> dsaUuids = masterMappingRepository.getParentMappings(projectId, MapType.PROJECT.getMapType(), MapType.DATASHARINGAGREEMENT.getMapType());
        List<DataSharingAgreementEntity> ret = new ArrayList<>();

        if (!dsaUuids.isEmpty())
            ret = DataSharingAgreementCache.getDSADetails(dsaUuids);

        return ret;
    }

    public List<CohortEntity> getBasePopulations(String projectId) throws Exception {

        List<String> cohortIds = masterMappingRepository.getChildMappings(projectId, MapType.PROJECT.getMapType(), MapType.COHORT.getMapType());
        List<CohortEntity> ret = new ArrayList<>();

        if (!cohortIds.isEmpty())
            ret = CohortCache.getCohortDetails(cohortIds);

        return ret;
    }

    public List<DataSetEntity> getDataSets(String projectId) throws Exception {

        List<String> dataSetIds = masterMappingRepository.getChildMappings(projectId, MapType.PROJECT.getMapType(), MapType.DATASET.getMapType());
        List<DataSetEntity> ret = new ArrayList<>();

        if (!dataSetIds.isEmpty())
            ret = DataSetCache.getDataSetDetails(dataSetIds);

        return ret;
    }

    public List<OrganisationEntity> getLinkedOrganisations(String projectId, Short mapType) throws Exception {

        List<String> orgUUIDs = masterMappingRepository.getChildMappings(projectId, MapType.PROJECT.getMapType(), mapType);
        List<OrganisationEntity> ret = new ArrayList<>();

        if (!orgUUIDs.isEmpty())
            ret = OrganisationCache.getOrganisationDetails(orgUUIDs);

        return ret;
    }

    public ExtractTechnicalDetailsEntity getLinkedExtractTechnicalDetails(String projectId, Short mapType) throws Exception {

        List <String> detailsUUIDs = masterMappingRepository.getChildMappings(projectId, MapType.PROJECT.getMapType(), mapType);
        ExtractTechnicalDetailsEntity ret = null;

        if (!detailsUUIDs.isEmpty()) {
            ret = extractRepository.getExtractTechnicalDetails(detailsUUIDs.get(0));
        }
        return ret;
    }

    public ProjectScheduleEntity getLinkedSchedule(String projectId, Short mapType) throws Exception {

        ProjectScheduleEntity schedule = null;
        List<String> schedUUIDs = masterMappingRepository.getChildMappings(projectId, MapType.PROJECT.getMapType(), mapType);
        if (schedUUIDs.size() > 0) {
            schedule = scheduleRepository.get(schedUUIDs.get(0));
        }
        return schedule;
    }

    public List<ProjectEntity> getProjectsForOrganisation(String organisationId) throws Exception {

        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            Query query = entityManager.createQuery(
                    "select p from ProjectEntity p " +
                            "inner join MasterMappingEntity mm on mm.parentUuid = p.uuid and mm.parentMapTypeId = :projectType " +
                            "inner join OrganisationEntity o on o.uuid = mm.childUuid " +
                            "where o.uuid = :orgUuid " +
                            "and mm.childMapTypeId = :subscriberType ");
            query.setParameter("projectType", MapType.PROJECT.getMapType());
            query.setParameter("orgUuid", organisationId);
            query.setParameter("subscriberType", MapType.SUBSCRIBER.getMapType());

            List<ProjectEntity> result = query.getResultList();

            return result;
        } finally {
            entityManager.close();
        }
    }

    public List<ProjectEntity> getProjectsForRegion(String regionUUID) throws Exception {

        List<DataSharingAgreementEntity> dsaUUIDs = DataSharingAgreementCache.getAllDSAsForAllChildRegions(regionUUID);

        List<String> projectUUIDs = new ArrayList<>();

        for (DataSharingAgreementEntity dsa : dsaUUIDs) {
            projectUUIDs.addAll(masterMappingRepository.getChildMappings(dsa.getUuid(), MapType.DATASHARINGAGREEMENT.getMapType(), MapType.PROJECT.getMapType()));
        }

        List<ProjectEntity> ret = new ArrayList<>();

        if (!projectUUIDs.isEmpty())
            ret = ProjectCache.getProjectDetails(projectUUIDs);

        return ret;
    }

    public List<String> getPublishersForProject(String projectId, String requesterOdsCode) throws Exception {

        List<String> pubOdsCodes = new ArrayList<>();
        // get subscribers for specified project
        List<String> projectSubscribers = masterMappingRepository.getChildMappings(projectId,
                MapType.PROJECT.getMapType(), MapType.SUBSCRIBER.getMapType());

        // get org details for all subscribers
        List<OrganisationEntity> subsInProject = OrganisationCache.getOrganisationDetails(projectSubscribers);

        // check if org is a subscriber in a project
        Boolean orgIsSubInProject = subsInProject.stream()
                .anyMatch(org -> org.getOdsCode().equals(requesterOdsCode));

        // if not just return an empty list
        if (!orgIsSubInProject) {
            return pubOdsCodes;
        }

        // get all the publishers for a project
        List<String> projectPublishers = masterMappingRepository.getChildMappings(projectId,
                MapType.PROJECT.getMapType(), MapType.PUBLISHER.getMapType());

        // get publisher details
        List<OrganisationEntity> pubsInProject = OrganisationCache.getOrganisationDetails(projectPublishers);

        // return the ods codes
        pubOdsCodes = pubsInProject.stream().map(OrganisationEntity::getOdsCode).collect(Collectors.toList());

        return pubOdsCodes;
    }

    @Override
    public List<ProjectEntity> getAllProjectsForSubscriber(String odsCode) throws Exception {

        OrganisationEntity org = organisationRepository.getOrganisationsFromOdsCode(odsCode);

        List<String> projectSubscribers = masterMappingRepository.getParentMappings(org.getUuid(),
                MapType.SUBSCRIBER.getMapType(), MapType.PROJECT.getMapType());

        List<ProjectEntity> projects = ProjectCache.getProjectDetails(projectSubscribers);

        return projects;
    }

    @Override
    public List<ProjectEntity> getValidDistributionProjectsForPublisher(String publisherOdsCode) throws Exception {

        OrganisationEntity org = organisationRepository.getOrganisationsFromOdsCode(publisherOdsCode);
        if (org == null) {
            return null;
        }

        List<String> projectPublishers = masterMappingRepository.getParentMappings(org.getUuid(),
                MapType.PUBLISHER.getMapType(), MapType.PROJECT.getMapType());

        List<ProjectEntity> projects = ProjectCache.getProjectDetails(projectPublishers);

        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        java.sql.Date sqlTomorrow = java.sql.Date.valueOf(today.plusDays(1)); // for before comparison below
        java.sql.Date sqlToday = java.sql.Date.valueOf(today);

        List<ProjectEntity> validProjects = projects.stream()
                .filter(p -> (p.getProjectTypeId() != null && p.getProjectTypeId() == 4) // Distribution project
                    && (p.getProjectStatusId() != null && p.getProjectStatusId() == 0) // Active
                    && (p.getStartDate() != null && p.getStartDate().before(sqlTomorrow) && (p.getEndDate() == null || p.getEndDate().after(sqlToday)))
                    && p.getAuthorisedBy() != null && p.getAuthorisedDate() != null)
                .collect(Collectors.toList());

        return validProjects;
    }

}
