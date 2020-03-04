package org.endeavourhealth.core.database.dal.datasharingmanager.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonCohort {
    private String uuid = null;
    private String name = null;
    private Short ConsentModelId = null;
    private String description = null;
    private String technicalDefinition = null;
    private Map<UUID, String> dpas = null;
    private Map<UUID, String> dsas = null;
    private Map<UUID, String> projects = null;
    private Map<UUID, String> regions = null;

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

    public Short getConsentModelId() {
        return ConsentModelId;
    }

    public void setConsentModelId(Short consentModelId) {
        ConsentModelId = consentModelId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnicalDefinition() {
        return technicalDefinition;
    }

    public void setTechnicalDefinition(String technicalDefinition) {
        this.technicalDefinition = technicalDefinition;
    }

    public Map<UUID, String> getDpas() {
        return dpas;
    }

    public void setDpas(Map<UUID, String> dpas) {
        this.dpas = dpas;
    }

    public Map<UUID, String> getDsas() {
        return dsas;
    }

    public void setDsas(Map<UUID, String> dsas) {
        this.dsas = dsas;
    }

    public Map<UUID, String> getProjects() {
        return projects;
    }

    public void setProjects(Map<UUID, String> projects) {
        this.projects = projects;
    }

    public Map<UUID, String> getRegions() {
    	 return regions; 
    }

    public void setRegions(Map<UUID, String> regions) {
        this.regions = regions;
    }
}
