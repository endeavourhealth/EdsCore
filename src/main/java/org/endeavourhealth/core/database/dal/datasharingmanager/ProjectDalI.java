package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonAuthorityToShare;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonExtractTechnicalDetails;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonProject;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonProjectSchedule;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.*;

import java.util.List;

public interface ProjectDalI {

    public List<ProjectEntity> getProjectsFromList(List<String> projects) throws Exception;
    public ProjectEntity getProject(String uuid) throws Exception;
    public List<JsonAuthorityToShare> getUsersAssignedToProject(String projectUuid) throws Exception;
    public JsonProject getFullProjectJson(String projectId) throws Exception;
    public JsonExtractTechnicalDetails setJsonExtractTechnicalDetails(ExtractTechnicalDetailsEntity detailsEntity) throws Exception;
    public JsonProjectSchedule setJsonProjectSchedule(ProjectScheduleEntity scheduleEntity) throws Exception;
    public List<DataSharingAgreementEntity> getLinkedDsas(String projectId) throws Exception;
    public List<CohortEntity> getBasePopulations(String projectId) throws Exception;
    public List<DataSetEntity> getDataSets(String projectId) throws Exception;
    public List<OrganisationEntity> getLinkedOrganisations(String projectId, Short mapType) throws Exception;
    public ExtractTechnicalDetailsEntity getLinkedExtractTechnicalDetails(String projectId, Short mapType) throws Exception;
    public ProjectScheduleEntity getLinkedSchedule(String projectId, Short mapType) throws Exception;
    public List<ProjectEntity> getProjectsForOrganisation(String organisationId) throws Exception;
    public List<ProjectEntity> getProjectsForRegion(String regionUUID) throws Exception;
    public List<String> getPublishersForProject(String projectId, String requesterOdsCode) throws Exception;
    public List<ProjectEntity> getAllProjectsForSubscriber(String odsCode) throws Exception;
    public List<ProjectEntity> getValidDistributionProjectsForPublisher(String publisherOdsCode) throws Exception;
    public boolean isProjectActive(String projectUUID) throws Exception;
    public List<String> getPublishersForProjectWithActiveCheck(String projectId, boolean checkActive) throws Exception;
}
