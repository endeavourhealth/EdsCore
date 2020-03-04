package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.MasterMappingDalI;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreMasterMappingDal;
import org.endeavourhealth.core.database.dal.datasharingmanager.enums.MapType;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonRegion;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "region", schema = "data_sharing_manager")
public class RegionEntity {
    private String uuid;
    private String name;
    private String description;
    @Transient private List<String> sharingAgreements;
    @Transient private List<String> processingAgreements;
    @Transient private List<String> parentRegions;
    @Transient private List<String> childRegions;
    @Transient private List<String> organisations;

    public RegionEntity() {    }

    public RegionEntity(JsonRegion region) {
        updateFromJson(region);
    }

    public void updateFromJson(JsonRegion region) {
        this.uuid = region.getUuid();
        this.name = region.getName();
        this.description = region.getDescription();

        this.sharingAgreements = new ArrayList<>();
        if (region.getSharingAgreements() != null) {
            region.getSharingAgreements().forEach((k, v) -> this.sharingAgreements.add(k.toString()));
        }
        this.processingAgreements = new ArrayList<>();
        if (region.getProcessingAgreements() != null) {
            region.getProcessingAgreements().forEach((k, v) -> this.processingAgreements.add(k.toString()));
        }
        this.parentRegions = new ArrayList<>();
        if (region.getParentRegions() != null) {
            region.getParentRegions().forEach((k, v) -> this.parentRegions.add(k.toString()));
        }
        this.childRegions = new ArrayList<>();
        if (region.getChildRegions() != null) {
            region.getChildRegions().forEach((k, v) -> this.childRegions.add(k.toString()));
        }
        this.organisations = new ArrayList<>();
        if (region.getOrganisations() != null) {
            region.getOrganisations().forEach((k, v) -> this.organisations.add(k.toString()));
        }
    }

    public void setMappingsFromDAL () throws Exception {
        MasterMappingDalI masterMappingRepository = DalProvider.factoryDSMMasterMappingDal();
        Short thisMapType = MapType.REGION.getMapType();

        this.setSharingAgreements(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.DATASHARINGAGREEMENT.getMapType()));
        this.setProcessingAgreements(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.DATAPROCESSINGAGREEMENT.getMapType()));
        this.setParentRegions(masterMappingRepository.getParentMappings(this.uuid, thisMapType, thisMapType));
        this.setChildRegions(masterMappingRepository.getChildMappings(this.uuid, thisMapType, thisMapType));
        this.setOrganisations(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.ORGANISATION.getMapType()));
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

    @Transient
    public List<String> getSharingAgreements() {
        return this.sharingAgreements;
    }

    @Transient
    public void setSharingAgreements(List<String> sharingAgreements) {
        this.sharingAgreements = sharingAgreements;
    }

    @Transient
    public List<String> getProcessingAgreements() {
        return this.processingAgreements;
    }

    @Transient
    public void setProcessingAgreements(List<String> processingAgreements) {
        this.processingAgreements = processingAgreements;
    }

    @Transient
    public List<String> getParentRegions () {
        return this.parentRegions;
    }

    @Transient
    public void setParentRegions(List<String> parentRegions) {
        this.parentRegions = parentRegions;
    }

    @Transient
    public List<String> getChildRegions () {
        return this.childRegions;
    }

    @Transient
    public void setChildRegions(List<String> childRegions) {
        this.childRegions = childRegions;
    }

    @Transient
    public List<String> getOrganisations () {
        return this.organisations;
    }

    @Transient
    public void setOrganisations(List<String> organisations) {
        this.organisations = organisations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegionEntity that = (RegionEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
