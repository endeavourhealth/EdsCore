package org.endeavourhealth.core.database.dal.datagenerator.models;

public class RemoteFilingSubscriber {

    private Integer id = null;
    private String jsonDefinition = null;
    private boolean isLive;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getJsonDefinition() {
        return jsonDefinition;
    }
    public void setJsonDefinition(String jsonDefinition) {
        this.jsonDefinition = jsonDefinition;
    }

    public boolean isLive() {
        return isLive;
    }
    public void setIsLive(boolean isLive) {
        this.isLive = isLive;
    }
}
