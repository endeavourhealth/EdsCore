package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;


import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.MasterMappingDalI;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreMasterMappingDal;
import org.endeavourhealth.core.database.dal.datasharingmanager.enums.MapType;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonCohort;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cohort", schema = "data_sharing_manager")
public class CohortEntity {
    private String uuid;
    private String name;
    private Short consentModelId;
    private String description;
    private String technicalDefinition;
    @Transient private List<String> dpas;
    @Transient private List<String> dsas;
    @Transient private List<String> projects;

    public CohortEntity(JsonCohort cohort) {
        updateFromJson(cohort);
    }

    public void updateFromJson(JsonCohort cohort) {
        this.uuid = cohort.getUuid();
        this.name = cohort.getName();
        this.consentModelId = cohort.getConsentModelId();
        this.description = cohort.getDescription();
        this.technicalDefinition = cohort.getTechnicalDefinition();
        this.dpas = new ArrayList<>();
        if (cohort.getDpas() != null) {
            cohort.getDpas().forEach((k, v) -> this.dpas.add(k.toString()));
        }
        this.dsas = new ArrayList<>();
        if (cohort.getDsas() != null) {
            cohort.getDsas().forEach((k, v) -> this.dsas.add(k.toString()));
        }
        this.projects = new ArrayList<>();
        if (cohort.getProjects() != null) {
            cohort.getProjects().forEach((k, v) -> this.projects.add(k.toString()));
        }
    }

    public CohortEntity() {
    }

    public void setMappingsFromDAL () throws Exception {
        MasterMappingDalI masterMappingRepository = DalProvider.factoryDSMMasterMappingDal();
        Short thisMapType = MapType.COHORT.getMapType();

        this.setDpas(masterMappingRepository.getParentMappings(this.uuid, thisMapType, MapType.DATAPROCESSINGAGREEMENT.getMapType()));

        this.setDsas(masterMappingRepository.getParentMappings(this.uuid, thisMapType, MapType.DATASHARINGAGREEMENT.getMapType()));

        this.setProjects(masterMappingRepository.getParentMappings(this.uuid, thisMapType, MapType.PROJECT.getMapType()));


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
    @Column(name = "consent_model_id")
    public Short getConsentModelId() {
        return consentModelId;
    }

    public void setConsentModelId(Short consentModelId) {
        this.consentModelId = consentModelId;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Transient
    public List<String> getDpas() {
        return dpas;
    }

    @Transient
    public void setDpas(List<String> dpas) {
        this.dpas = dpas;
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
    public List<String> getProjects() {
        return projects;
    }

    @Transient
    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CohortEntity that = (CohortEntity) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(consentModelId, that.consentModelId) &&
                Objects.equals(description, that.description);
        // N.B. Definition excludes DPAs for now
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, name, consentModelId, description);
        // N.B. Definition excludes DPAs for now
    }

    @Basic
    @Column(name = "technical_definition")
    public String getTechnicalDefinition() {
        return technicalDefinition;
    }

    public void setTechnicalDefinition(String technicalDefinition) {
        this.technicalDefinition = technicalDefinition;
    }
}
