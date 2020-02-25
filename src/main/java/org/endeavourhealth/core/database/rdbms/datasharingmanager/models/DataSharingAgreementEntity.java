package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.MasterMappingDalI;
import org.endeavourhealth.core.database.dal.datasharingmanager.PurposeDalI;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreMasterMappingDal;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCorePurposeDal;
import org.endeavourhealth.core.database.dal.datasharingmanager.enums.MapType;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonDSA;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "data_sharing_agreement", schema = "data_sharing_manager")
public class DataSharingAgreementEntity {
    private String uuid;
    private String name;
    private String description;
    private String derivation;
    private short dsaStatusId;
    private short consentModelId;
    private Date startDate;
    private Date endDate;
    @Transient private List<PurposeEntity> purposes;
    @Transient private List<PurposeEntity> benefits;
    @Transient private List<String> regions;
    @Transient private List<String> projects;
    @Transient private List<String> publishers;
    @Transient private List<String> subscribers;
    @Transient private List<String> documentations;
    @Transient private List<String> cohorts;
    @Transient private List<String> dataSets;

    public DataSharingAgreementEntity() {    }

    public DataSharingAgreementEntity(JsonDSA dsa) {
        updateFromJson(dsa);
    }

    public void updateFromJson(JsonDSA dsa) {
        this.uuid = dsa.getUuid();
        this.name = dsa.getName();
        this.description = dsa.getDescription();
        this.derivation = dsa.getDerivation();
        this.dsaStatusId = dsa.getDsaStatusId();
        this.consentModelId = dsa.getConsentModelId();

        if (dsa.getStartDate() != null) {
            this.startDate = Date.valueOf(dsa.getStartDate());
        }
        if (dsa.getEndDate() != null) {
            this.endDate = Date.valueOf(dsa.getEndDate());
        }

        this.purposes = new ArrayList<>();
        if (dsa.getPurposes() != null) {
            dsa.getPurposes().forEach((p) -> this.purposes.add(new PurposeEntity(p)));
        }
        this.benefits = new ArrayList<>();
        if (dsa.getBenefits() != null) {
            dsa.getBenefits().forEach((b) -> this.benefits.add(new PurposeEntity(b)));
        }
        this.regions = new ArrayList<>();
        if (dsa.getRegions() != null) {
            dsa.getRegions().forEach((k, v) -> this.regions.add(k.toString()));
        }
        this.projects = new ArrayList<>();
        if (dsa.getProjects() != null) {
            dsa.getProjects().forEach((k, v) -> this.projects.add(k.toString()));
        }
        this.publishers = new ArrayList<>();
        if (dsa.getPublishers() != null) {
            dsa.getPublishers().forEach((k, v) -> this.publishers.add(k.toString()));
        }
        this.subscribers = new ArrayList<>();
        if (dsa.getSubscribers() != null) {
            dsa.getSubscribers().forEach((k, v) -> this.subscribers.add(k.toString()));
        }
        this.documentations = new ArrayList<>();
        if (dsa.getDocumentations() != null) {
            dsa.getDocumentations().forEach((d) -> this.documentations.add(d.getUuid()));
        }
        this.cohorts = new ArrayList<>();
        if (dsa.getCohorts() != null) {
            dsa.getCohorts().forEach((k, v) -> this.cohorts.add(k.toString()));
        }
        this.dataSets = new ArrayList<>();
        if (dsa.getDataSets() != null) {
            dsa.getDataSets().forEach((k, v) -> this.dataSets.add(k.toString()));
        }
    }

    public void setMappingsFromDAL () throws Exception {
        MasterMappingDalI masterMappingRepository = DalProvider.factoryDSMMasterMappingDal();
        PurposeDalI purposeRepository = DalProvider.factoryDSMPurposeDal();
        Short thisMapType = MapType.DATASHARINGAGREEMENT.getMapType();

        List<String> purposes = masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.PURPOSE.getMapType());
        List<String> benefits = masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.BENEFIT.getMapType());
        this.setPurposes(purposeRepository.getPurposesFromList(purposes));
        this.setBenefits(purposeRepository.getPurposesFromList(benefits));

        this.setRegions(masterMappingRepository.getParentMappings(this.uuid, thisMapType, MapType.REGION.getMapType()));
        this.setProjects(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.PROJECT.getMapType()));
        this.setPublishers(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.PUBLISHER.getMapType()));
        this.setSubscribers(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.SUBSCRIBER.getMapType()));
        this.setDocumentations(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.DOCUMENT.getMapType()));
        this.setCohorts(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.COHORT.getMapType()));
        this.setDataSets(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.DATASET.getMapType()));
    }

    @Id
    @Column(name = "uuid", nullable = false, length = 36)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 10000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "derivation", nullable = true, length = 100)
    public String getDerivation() {
        return derivation;
    }

    public void setDerivation(String derivation) {
        this.derivation = derivation;
    }

    @Basic
    @Column(name = "dsa_status_id", nullable = false)
    public short getDsaStatusId() {
        return dsaStatusId;
    }

    public void setDsaStatusId(short dsaStatusId) {
        this.dsaStatusId = dsaStatusId;
    }

    @Basic
    @Column(name = "consent_model_id", nullable = false)
    public short getConsentModelId() {
        return consentModelId;
    }

    public void setConsentModelId(short consentModelId) {
        this.consentModelId = consentModelId;
    }

    @Basic
    @Column(name = "start_date", nullable = true)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Basic
    @Column(name = "end_date", nullable = true)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Transient
    public List<PurposeEntity> getPurposes() {
        return purposes;
    }

    @Transient
    public void setPurposes(List<PurposeEntity> purposes) {
        this.purposes = purposes;
    }

    @Transient
    public List<PurposeEntity> getBenefits() {
        return benefits;
    }

    @Transient
    public void setBenefits(List<PurposeEntity> benefits) {
        this.benefits = benefits;
    }

    @Transient
    public List<String> getRegions() {
        return regions;
    }

    @Transient
    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    @Transient
    public List<String> getProjects() {
        return projects;
    }

    @Transient
    public void setProjects(List<String> projects) {
        this.projects = projects;
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

    @Transient
    public void setSubscribers(List<String> subscribers) {
        this.subscribers = subscribers;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSharingAgreementEntity that = (DataSharingAgreementEntity) o;

        if (dsaStatusId != that.dsaStatusId) return false;
        if (consentModelId != that.consentModelId) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (derivation != null ? !derivation.equals(that.derivation) : that.derivation != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;

        return true;
        // N.B. Ignores linked items for now
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (derivation != null ? derivation.hashCode() : 0);
        result = 31 * result + (int) dsaStatusId;
        result = 31 * result + (int) consentModelId;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
        // N.B. Ignores linked items for now
    }
}
