package org.endeavourhealth.core.database.dal.usermanager.models;

public class JsonUserRegion {
    private String userId = null;
    private String regionId = null;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
}
