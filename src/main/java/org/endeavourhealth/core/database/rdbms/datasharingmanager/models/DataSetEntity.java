package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;


import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.MasterMappingDalI;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreMasterMappingDal;
import org.endeavourhealth.core.database.dal.datasharingmanager.enums.MapType;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonDataSet;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "dataset", schema = "data_sharing_manager")
public class DataSetEntity {
    private String uuid;
    private String name;
    private String description;
    private String technicalDefinition;
    @Transient private List<String> dpas;
    @Transient private List<String> dsas;
    @Transient private List<String> projects;
    @Transient private List<String> regions;

    public DataSetEntity() {
    }

    public DataSetEntity(JsonDataSet dataSet) {
        updateFromJson(dataSet);
    }

    public void updateFromJson(JsonDataSet dataSet) {
        this.uuid = dataSet.getUuid();
        this.name = dataSet.getName();
        this.description = dataSet.getDescription();
        this.technicalDefinition = dataSet.getTechnicalDefinition();
        this.dpas = new ArrayList<>();
        if (dataSet.getDpas() != null) {
            dataSet.getDpas().forEach((k, v) -> this.dpas.add(k.toString()));
        }
        this.dsas = new ArrayList<>();
        if (dataSet.getDsas() != null) {
            dataSet.getDsas().forEach((k, v) -> this.dsas.add(k.toString()));
        }
        this.projects = new ArrayList<>();
        if (dataSet.getProjects() != null) {
            dataSet.getProjects().forEach((k, v) -> this.projects.add(k.toString()));
        }
        this.regions = new ArrayList<>();
        if (dataSet.getRegions() != null) {
            dataSet.getRegions().forEach((k, v) -> this.regions.add(k.toString()));
        }
    }

    public void setMappingsFromDAL () throws Exception {
        MasterMappingDalI masterMappingRepository = DalProvider.factoryDSMMasterMappingDal();
        Short thisMapType = MapType.DATASET.getMapType();

        this.setDpas(masterMappingRepository.getParentMappings(this.uuid, thisMapType, MapType.DATAPROCESSINGAGREEMENT.getMapType()));

        this.setDsas(masterMappingRepository.getParentMappings(this.uuid, thisMapType, MapType.DATASHARINGAGREEMENT.getMapType()));

        this.setProjects(masterMappingRepository.getParentMappings(this.uuid, thisMapType, MapType.PROJECT.getMapType()));

        this.setRegions(masterMappingRepository.getParentMappings(this.uuid, thisMapType, MapType.REGION.getMapType()));
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
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSetEntity that = (DataSetEntity) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, name, description);
    }

    @Basic
    @Column(name = "technical_definition")
    public String getTechnicalDefinition() {
        return technicalDefinition;
    }

    public void setTechnicalDefinition(String technicalDefinition) {
        this.technicalDefinition = technicalDefinition;
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

    @Transient
    public List<String> getRegions() { return regions; }

    @Transient
    public void setRegions(List<String> regions) { this.regions = regions; }

}
