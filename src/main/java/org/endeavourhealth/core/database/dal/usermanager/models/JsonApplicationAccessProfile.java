package org.endeavourhealth.core.database.dal.usermanager.models;


import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationAccessProfileEntity;

public class JsonApplicationAccessProfile {
    private String id = null;
    private String name = null;
    private String applicationId = null;
    private String description = null;
    private boolean isDeleted;
    private boolean superUser;

    public JsonApplicationAccessProfile() {
    }

    public JsonApplicationAccessProfile(ApplicationAccessProfileEntity applicationAccessProfileEntity) {
        this.id = applicationAccessProfileEntity.getId();
        this.name = applicationAccessProfileEntity.getName();
        this.description = applicationAccessProfileEntity.getDescription();
        this.isDeleted = applicationAccessProfileEntity.getIsDeleted() == 1;
        this.superUser = applicationAccessProfileEntity.getSuperUser() == 1;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean getIsSuperUser() {
        return superUser;
    }

    public void setSuperUser(boolean superUser) {
        this.superUser = superUser;
    }
}
