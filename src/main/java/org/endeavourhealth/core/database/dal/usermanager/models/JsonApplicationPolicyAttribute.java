package org.endeavourhealth.core.database.dal.usermanager.models;


import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationPolicyAttributeEntity;

public class JsonApplicationPolicyAttribute {
    private String id = null;
    private String applicationPolicyId = null;
    private String name = null;
    private String application = null;
    private String applicationId = null;
    private String applicationAccessProfileId = null;
    private String applicationAccessProfileName = null;
    private String applicationAccessProfileDescription = null;
    private boolean applicationAccessProfileSuperUser = false;
    private boolean isDeleted;

    public JsonApplicationPolicyAttribute() {
    }

    public JsonApplicationPolicyAttribute(ApplicationPolicyAttributeEntity applicationPolicyAttributeEntity) {
        this.id = applicationPolicyAttributeEntity.getId();
        this.applicationPolicyId = applicationPolicyAttributeEntity.getApplicationPolicyId();
        this.applicationAccessProfileId = applicationPolicyAttributeEntity.getApplicationAccessProfileId();
        this.isDeleted = applicationPolicyAttributeEntity.getIsDeleted() == 1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationPolicyId() {
        return applicationPolicyId;
    }

    public void setApplicationPolicyId(String applicationPolicyId) {
        this.applicationPolicyId = applicationPolicyId;
    }

    public String getApplicationAccessProfileId() {
        return applicationAccessProfileId;
    }

    public void setApplicationAccessProfileId(String applicationAccessProfileId) {
        this.applicationAccessProfileId = applicationAccessProfileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationAccessProfileName() {
        return applicationAccessProfileName;
    }

    public void setApplicationAccessProfileName(String applicationAccessProfileName) {
        this.applicationAccessProfileName = applicationAccessProfileName;
    }

    public String getApplicationAccessProfileDescription() {
        return applicationAccessProfileDescription;
    }

    public void setApplicationAccessProfileDescription(String applicationAccessProfileDescription) {
        this.applicationAccessProfileDescription = applicationAccessProfileDescription;
    }

    public boolean getApplicationAccessProfileSuperUser() {
        return applicationAccessProfileSuperUser;
    }

    public void setApplicationAccessProfileSuperUser(boolean applicationAccessProfileSuperUser) {
        this.applicationAccessProfileSuperUser = applicationAccessProfileSuperUser;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
