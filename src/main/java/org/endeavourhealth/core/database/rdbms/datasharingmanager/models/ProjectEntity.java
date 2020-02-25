package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.MasterMappingDalI;
import org.endeavourhealth.core.database.dal.datasharingmanager.ProjectDalI;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreMasterMappingDal;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreProjectDal;
import org.endeavourhealth.core.database.dal.datasharingmanager.enums.MapType;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonProject;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "project", schema = "data_sharing_manager")
public class ProjectEntity {
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
    private String objectives;
    private short securityArchitectureId;
    private short storageProtocolId;
    private Short businessCaseStatus;
    private Short flowScheduleId;
    private Short projectStatusId;
    private Date startDate;
    private Date endDate;
    @Transient private List<String> publishers;
    @Transient private List<String> subscribers;
    @Transient private List<String> cohorts;
    @Transient private List<String> dataSets;
    @Transient private List<String> dsas;
    @Transient private List<String> documentations;
    @Transient private ProjectScheduleEntity schedule;
    @Transient private ExtractTechnicalDetailsEntity extractTechnicalDetails;

    public ProjectEntity() {
    }

    public ProjectEntity(JsonProject project) {   updateFromJson(project);   }

    public void updateFromJson(JsonProject project) {
        this.uuid = project.getUuid();
        this.name = project.getName();
        this.leadUser = project.getLeadUser();
        this.technicalLeadUser = project.getTechnicalLeadUser();
        this.consentModelId = project.getConsentModelId();
        this.deidentificationLevel = project.getDeidentificationLevel();
        this.projectTypeId = project.getProjectTypeId();
        this.securityInfrastructureId = project.getSecurityInfrastructureId();
        this.ipAddress = project.getIpAddress();
        this.summary = project.getSummary();
        this.businessCase = project.getBusinessCase();
        this.objectives = project.getObjectives();
        this.securityArchitectureId = project.getSecurityArchitectureId();
        this.storageProtocolId = project.getStorageProtocolId();
        this.businessCaseStatus = project.getBusinessCaseStatus();
        this.flowScheduleId = project.getFlowScheduleId();
        this.projectStatusId = project.getProjectStatusId();
        if (project.getStartDate() != null) {
            this.startDate = Date.valueOf(project.getStartDate());
        }
        if (project.getEndDate() != null) {
            this.endDate = Date.valueOf(project.getEndDate());
        }

        this.dsas = new ArrayList<>();
        if (project.getDsas() != null) {
            project.getDsas().forEach((k, v) -> this.dsas.add(k.toString()));
        }

        this.publishers = new ArrayList<>();
        if (project.getPublishers() != null) {
            project.getPublishers().forEach((k, v) -> this.publishers.add(k.toString()));
        }

        this.subscribers = new ArrayList<>();
        if (project.getSubscribers() != null) {
            project.getSubscribers().forEach((k, v) -> this.subscribers.add(k.toString()));
        }

        this.documentations = new ArrayList<>();
        if (project.getDocumentations() != null) {
            project.getDocumentations().forEach((k) -> this.documentations.add(k.getUuid()));
        }

        this.cohorts = new ArrayList<>();
        if (project.getCohorts() != null) {
            project.getCohorts().forEach((k, v) -> this.cohorts.add(k.toString()));
        }

        this.dataSets = new ArrayList<>();
        if (project.getDataSets() != null) {
            project.getDataSets().forEach((k, v) -> this.dataSets.add(k.toString()));
        }

        if (project.getSchedule() != null) {
            this.schedule = new ProjectScheduleEntity();
            this.schedule.setUuid(project.getSchedule().getUuid());
            this.schedule.setCronExpression(project.getSchedule().getCronExpression());
            this.schedule.setCronDescription(project.getSchedule().getCronDescription());
            this.schedule.setCronSettings(project.getSchedule().getCronSettings());
        }

        if (project.getExtractTechnicalDetails() != null) {

            this.extractTechnicalDetails = new ExtractTechnicalDetailsEntity();

            if (project.getExtractTechnicalDetails().getUuid() != null) {
                this.extractTechnicalDetails.setUuid(project.getExtractTechnicalDetails().getUuid());
            }
            if (project.getExtractTechnicalDetails().getName() != null) {
                this.extractTechnicalDetails.setName(project.getExtractTechnicalDetails().getName());
            }
            if (project.getExtractTechnicalDetails().getSftpHostName() != null) {
                this.extractTechnicalDetails.setSftpHostName(project.getExtractTechnicalDetails().getSftpHostName());
            }
            if (project.getExtractTechnicalDetails().getSftpHostDirectory() != null) {
                this.extractTechnicalDetails.setSftpHostDirectory(project.getExtractTechnicalDetails().getSftpHostDirectory());
            }
            if (project.getExtractTechnicalDetails().getSftpHostPort() != null) {
                this.extractTechnicalDetails.setSftpHostPort(project.getExtractTechnicalDetails().getSftpHostPort());
            }
            if (project.getExtractTechnicalDetails().getSftpClientUsername() != null) {
                this.extractTechnicalDetails.setSftpClientUsername(project.getExtractTechnicalDetails().getSftpClientUsername());
            }
            if (project.getExtractTechnicalDetails().getSftpClientPrivateKeyPassword() != null) {
                this.extractTechnicalDetails.setSftpClientPrivateKeyPassword(project.getExtractTechnicalDetails().getSftpClientPrivateKeyPassword());
            }
            if (project.getExtractTechnicalDetails().getSftpHostPublicKeyFilename() != null) {
                this.extractTechnicalDetails.setSftpHostPublicKeyFilename(project.getExtractTechnicalDetails().getSftpHostPublicKeyFilename());
            }
            if (project.getExtractTechnicalDetails().getSftpHostPublicKeyFileData() != null) {
                this.extractTechnicalDetails.setSftpHostPublicKeyFileData(project.getExtractTechnicalDetails().getSftpHostPublicKeyFileData());
            }
            if (project.getExtractTechnicalDetails().getSftpClientPrivateKeyFilename() != null) {
                this.extractTechnicalDetails.setSftpClientPrivateKeyFilename(project.getExtractTechnicalDetails().getSftpClientPrivateKeyFilename());
            }
            if (project.getExtractTechnicalDetails().getSftpClientPrivateKeyFileData() != null) {
                this.extractTechnicalDetails.setSftpClientPrivateKeyFileData(project.getExtractTechnicalDetails().getSftpClientPrivateKeyFileData());
            }
            if (project.getExtractTechnicalDetails().getPgpCustomerPublicKeyFilename() != null) {
                this.extractTechnicalDetails.setPgpCustomerPublicKeyFilename(project.getExtractTechnicalDetails().getPgpCustomerPublicKeyFilename());
            }
            if (project.getExtractTechnicalDetails().getPgpCustomerPublicKeyFileData() != null) {
                this.extractTechnicalDetails.setPgpCustomerPublicKeyFileData(project.getExtractTechnicalDetails().getPgpCustomerPublicKeyFileData());
            }
            if (project.getExtractTechnicalDetails().getPgpInternalPublicKeyFilename() != null) {
                this.extractTechnicalDetails.setPgpInternalPublicKeyFilename(project.getExtractTechnicalDetails().getPgpInternalPublicKeyFilename());
            }
            if (project.getExtractTechnicalDetails().getPgpInternalPublicKeyFileData() != null) {
                this.extractTechnicalDetails.setPgpInternalPublicKeyFileData(project.getExtractTechnicalDetails().getPgpInternalPublicKeyFileData());
            }
        }
    }
    
    public void setMappingsFromDAL () throws Exception {

        MasterMappingDalI masterMappingRepository = DalProvider.factoryDSMMasterMappingDal();
        ProjectDalI projectRepository = DalProvider.factoryDSMProjectDal();
        Short thisMapType = MapType.PROJECT.getMapType();

        this.setDsas(masterMappingRepository.getParentMappings(this.uuid, thisMapType, MapType.DATASHARINGAGREEMENT.getMapType()));
        this.setPublishers(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.PUBLISHER.getMapType()));
        this.setSubscribers(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.SUBSCRIBER.getMapType()));
        this.setDocumentations(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.DOCUMENT.getMapType()));
        this.setCohorts(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.COHORT.getMapType()));
        this.setDataSets(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.DATASET.getMapType()));
        this.setSchedule(projectRepository.getLinkedSchedule(this.uuid, MapType.SCHEDULE.getMapType()));
        this.setExtractTechnicalDetails(projectRepository.getLinkedExtractTechnicalDetails(this.uuid, MapType.EXTRACTTECHNICALDETAILS.getMapType()));
    }

    @Id
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "lead_user")
    public String getLeadUser() {
        return leadUser;
    }

    public void setLeadUser(String leadUser) {
        this.leadUser = leadUser;
    }

    @Basic
    @Column(name = "technical_lead_user")
    public String getTechnicalLeadUser() {
        return technicalLeadUser;
    }

    public void setTechnicalLeadUser(String technicalLeadUser) {
        this.technicalLeadUser = technicalLeadUser;
    }

    @Basic
    @Column(name = "consent_model_id")
    public Short getConsentModelId() {
        return consentModelId;
    }

    public void setConsentModelId(Short consentModelId) {
        this.consentModelId = consentModelId;
    }

    @Basic
    @Column(name = "deidentification_level")
    public Short getDeidentificationLevel() {
        return deidentificationLevel;
    }

    public void setDeidentificationLevel(Short deidentificationLevel) {
        this.deidentificationLevel = deidentificationLevel;
    }

    @Basic
    @Column(name = "project_type_id")
    public Short getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(Short projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    @Basic
    @Column(name = "security_infrastructure_id")
    public Short getSecurityInfrastructureId() {
        return securityInfrastructureId;
    }

    public void setSecurityInfrastructureId(Short securityInfrastructureId) {
        this.securityInfrastructureId = securityInfrastructureId;
    }

    @Basic
    @Column(name = "ip_address")
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Basic
    @Column(name = "summary")
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Basic
    @Column(name = "business_case")
    public String getBusinessCase() {
        return businessCase;
    }

    public void setBusinessCase(String businessCase) {
        this.businessCase = businessCase;
    }

    @Basic
    @Column(name = "objectives")
    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    @Basic
    @Column(name = "security_architecture_id")
    public short getSecurityArchitectureId() {
        return securityArchitectureId;
    }

    public void setSecurityArchitectureId(short securityArchitectureId) {
        this.securityArchitectureId = securityArchitectureId;
    }

    @Basic
    @Column(name = "storage_protocol_id")
    public short getStorageProtocolId() {
        return storageProtocolId;
    }

    public void setStorageProtocolId(short storageProtocolId) {
        this.storageProtocolId = storageProtocolId;
    }

    @Transient
    public List<String> getPublishers() {
        return publishers;
    }

    @Transient
    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    @Transient
    public List<String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<String> subscribers) {
        this.subscribers = subscribers;
    }

    @Transient
    public List<String> getCohorts() {
        return cohorts;
    }

    @Transient
    public void setCohorts(List<String> cohorts) {
        this.cohorts = cohorts;
    }

    @Transient
    public List<String> getDataSets() {
        return dataSets;
    }

    @Transient
    public void setDataSets(List<String> dataSets) {
        this.dataSets = dataSets;
    }

    @Transient
    public List<String> getDsas() {
        return dsas;
    }

    @Transient
    public void setDsas(List<String> dsas) {
        this.dsas = dsas;
    }

    @Transient
    public List<String> getDocumentations() {
        return documentations;
    }

    @Transient
    public void setDocumentations(List<String> documentations) {
        this.documentations = documentations;
    }

    @Transient
    public ProjectScheduleEntity getSchedule() { return schedule; }

    @Transient
    public void setSchedule(ProjectScheduleEntity schedule) { this.schedule = schedule; }

    @Transient
    public ExtractTechnicalDetailsEntity getExtractTechnicalDetails() {
        return extractTechnicalDetails;
    }

    @Transient
    public void setExtractTechnicalDetails(ExtractTechnicalDetailsEntity extractTechnicalDetails) {
        this.extractTechnicalDetails = extractTechnicalDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectEntity that = (ProjectEntity) o;
        return securityArchitectureId == that.securityArchitectureId &&
                storageProtocolId == that.storageProtocolId &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(leadUser, that.leadUser) &&
                Objects.equals(technicalLeadUser, that.technicalLeadUser) &&
                Objects.equals(consentModelId, that.consentModelId) &&
                Objects.equals(deidentificationLevel, that.deidentificationLevel) &&
                Objects.equals(projectTypeId, that.projectTypeId) &&
                Objects.equals(securityInfrastructureId, that.securityInfrastructureId) &&
                Objects.equals(ipAddress, that.ipAddress) &&
                Objects.equals(summary, that.summary) &&
                Objects.equals(businessCase, that.businessCase) &&
                Objects.equals(objectives, that.objectives);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, name, leadUser, technicalLeadUser, consentModelId, deidentificationLevel, projectTypeId, securityInfrastructureId, ipAddress, summary, businessCase, objectives, securityArchitectureId, storageProtocolId);
    }

    @Basic
    @Column(name = "business_case_status")
    public Short getBusinessCaseStatus() {
        return businessCaseStatus;
    }

    public void setBusinessCaseStatus(Short businessCaseStatus) {
        this.businessCaseStatus = businessCaseStatus;
    }

    @Basic
    @Column(name = "flow_schedule_id")
    public Short getFlowScheduleId() {
        return flowScheduleId;
    }

    public void setFlowScheduleId(Short flowScheduleId) {
        this.flowScheduleId = flowScheduleId;
    }

    @Basic
    @Column(name = "project_status_id")
    public Short getProjectStatusId() {
        return projectStatusId;
    }

    public void setProjectStatusId(Short projectStatusId) {
        this.projectStatusId = projectStatusId;
    }

    @Basic
    @Column(name = "start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Basic
    @Column(name = "end_date")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
