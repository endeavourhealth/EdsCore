package org.endeavourhealth.core.database.dal.datasharingmanager.models;

import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ProjectEntity;
import org.endeavourhealth.core.database.dal.usermanager.models.JsonApplicationPolicyAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JsonProject {
    private String uuid;
    private String name;
    private String leadUser;
    private String technicalLeadUser;
    private Short consentModelId;
    private Short deidentificationLevel;
    private Short projectTypeId;
    private Short securityInfrastructureId;
    private String ipAddress;
    private String summary;
    private String businessCase;
    private short outputFormat;
    private String objectives;
    private short securityArchitectureId;
    private short storageProtocolId;
    private short businessCaseStatus;
    private short flowScheduleId;
    private Short projectStatusId = null;
    private String startDate = null;
    private String endDate = null;
    private String configName = null;
    private String authorisedBy;
    private String authorisedDate;
    private Map<UUID, String> publishers = null;
    private Map<UUID, String> subscribers = null;
    private Map<UUID, String> cohorts = null;
    private Map<UUID, String> dataSets = null;
    private Map<UUID, String> projectConfiguration = null;
    private Map<UUID, String> dsas = null;
    private String applicationPolicy = null;
    private List<JsonDocumentation> documentations = null;
    private List<JsonApplicationPolicyAttribute> applicationPolicyAttributes = new ArrayList<>();
    private JsonExtractTechnicalDetails extractTechnicalDetails = null;
    private JsonProjectSchedule schedule = null;
    private Map<UUID, String> schedules = null;

    public JsonProject() {
    }

    public JsonProject(ProjectEntity projectEntity) {
        this.uuid = projectEntity.getUuid();
        this.name = projectEntity.getName();
        this.leadUser = projectEntity.getLeadUser();
        this.technicalLeadUser = projectEntity.getTechnicalLeadUser();
        this.consentModelId = projectEntity.getConsentModelId();
        this.deidentificationLevel = projectEntity.getDeidentificationLevel();
        this.projectTypeId = projectEntity.getProjectTypeId();
        this.securityInfrastructureId = projectEntity.getSecurityInfrastructureId();
        this.ipAddress = projectEntity.getIpAddress();
        this.summary = projectEntity.getSummary();
        this.businessCase = projectEntity.getBusinessCase();
        this.objectives = projectEntity.getObjectives();
        this.securityArchitectureId = projectEntity.getSecurityArchitectureId();
        this.storageProtocolId = projectEntity.getStorageProtocolId();
        this.projectStatusId = projectEntity.getProjectStatusId();
        this.configName = projectEntity.getConfigName();
        this.authorisedBy = projectEntity.getAuthorisedBy();
        if (projectEntity.getAuthorisedDate() != null) {
            this.authorisedDate = projectEntity.getAuthorisedDate().toString();
        }
        if (projectEntity.getStartDate() != null) {
            this.startDate = projectEntity.getStartDate().toString();
        }
        if (projectEntity.getEndDate() != null) {
            this.endDate = projectEntity.getEndDate().toString();
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeadUser() {
        return leadUser;
    }

    public void setLeadUser(String leadUser) {
        this.leadUser = leadUser;
    }

    public String getTechnicalLeadUser() {
        return technicalLeadUser;
    }

    public void setTechnicalLeadUser(String technicalLeadUser) {
        this.technicalLeadUser = technicalLeadUser;
    }

    public Short getConsentModelId() {
        return consentModelId;
    }

    public void setConsentModelId(Short consentModelId) {
        this.consentModelId = consentModelId;
    }

    public Short getDeidentificationLevel() {
        return deidentificationLevel;
    }

    public void setDeidentificationLevel(Short deidentificationLevel) {
        this.deidentificationLevel = deidentificationLevel;
    }

    public Short getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(Short projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    public Short getSecurityInfrastructureId() {
        return securityInfrastructureId;
    }

    public void setSecurityInfrastructureId(Short securityInfrastructureId) {
        this.securityInfrastructureId = securityInfrastructureId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBusinessCase() {
        return businessCase;
    }

    public void setBusinessCase(String businessCase) {
        this.businessCase = businessCase;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public short getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(short outputFormat) {
        this.outputFormat = outputFormat;
    }

    public short getSecurityArchitectureId() {
        return securityArchitectureId;
    }

    public void setSecurityArchitectureId(short securityArchitectureId) {
        this.securityArchitectureId = securityArchitectureId;
    }

    public short getStorageProtocolId() {
        return storageProtocolId;
    }

    public void setStorageProtocolId(short storageProtocolId) {
        this.storageProtocolId = storageProtocolId;
    }

    public Short getProjectStatusId() {
        return projectStatusId;
    }

    public void setProjectStatusId(Short projectStatusId) {
        this.projectStatusId = projectStatusId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Map<UUID, String> getPublishers() {
        return publishers;
    }

    public void setPublishers(Map<UUID, String> publishers) {
        this.publishers = publishers;
    }

    public Map<UUID, String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Map<UUID, String> subscribers) {
        this.subscribers = subscribers;
    }

    public Map<UUID, String> getCohorts() {
        return cohorts;
    }

    public void setCohorts(Map<UUID, String> cohorts) {
        this.cohorts = cohorts;
    }

    public Map<UUID, String> getDataSets() {
        return dataSets;
    }

    public void setDataSets(Map<UUID, String> dataSets) {
        this.dataSets = dataSets;
    }

    public Map<UUID, String> getProjectConfiguration() {
        return projectConfiguration;
    }

    public void setProjectConfiguration(Map<UUID, String> projectConfiguration) {
        this.projectConfiguration = projectConfiguration;
    }

    public Map<UUID, String> getDsas() {
        return dsas;
    }

    public void setDsas(Map<UUID, String> dsas) {
        this.dsas = dsas;
    }

    public String getApplicationPolicy() {
        return applicationPolicy;
    }

    public void setApplicationPolicy(String applicationPolicy) {
        this.applicationPolicy = applicationPolicy;
    }

    public List<JsonApplicationPolicyAttribute> getApplicationPolicyAttributes() {
        return applicationPolicyAttributes;
    }

    public void setApplicationPolicyAttributes(List<JsonApplicationPolicyAttribute> applicationPolicyAttributes) {
        this.applicationPolicyAttributes = applicationPolicyAttributes;
    }

    public short getBusinessCaseStatus() {
        return businessCaseStatus;
    }

    public void setBusinessCaseStatus(short businessCaseStatus) {
        this.businessCaseStatus = businessCaseStatus;
    }

    public short getFlowScheduleId() {
        return flowScheduleId;
    }

    public void setFlowScheduleId(short flowScheduleId) {
        this.flowScheduleId = flowScheduleId;
    }

    public List<JsonDocumentation> getDocumentations() {
        return documentations;
    }

    public void setDocumentations(List<JsonDocumentation> documentations) {
        this.documentations = documentations;
    }

    public JsonExtractTechnicalDetails getExtractTechnicalDetails() {
        return extractTechnicalDetails;
    }

    public void setExtractTechnicalDetails(JsonExtractTechnicalDetails extractTechnicalDetails) {
        this.extractTechnicalDetails = extractTechnicalDetails;
    }

    public JsonProjectSchedule getSchedule() { return schedule; }

    public void setSchedule(JsonProjectSchedule projectSchedule) { this.schedule = projectSchedule; }

    public Map<UUID, String> getSchedules() { return schedules; }

    public void setSchedules(Map<UUID, String> schedules) { this.schedules = schedules; }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getAuthorisedBy() {
        return authorisedBy;
    }

    public void setAuthorisedBy(String authorisedBy) {
        this.authorisedBy = authorisedBy;
    }

    public String getAuthorisedDate() {
        return authorisedDate;
    }

    public void setAuthorisedDate(String authorisedDate) {
        this.authorisedDate = authorisedDate;
    }
}
