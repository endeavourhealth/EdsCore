package org.endeavourhealth.core.database.dal.audit.models;

import org.endeavourhealth.core.database.cassandra.audit.models.CassandraUserEvent;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsUserEvent;

import java.util.Date;
import java.util.UUID;

public class UserEvent {

    private UUID userId = null;
    private UUID organisationId = null;
    private String module = null;
    private String subModule = null;
    private String action = null;
    private Date timestamp = null;
    private String data = null;

    public UserEvent() {}

    public UserEvent(CassandraUserEvent proxy) {
        this.userId = proxy.getUserId();
        this.organisationId = proxy.getOrganisationId();
        this.module = proxy.getModule();
        this.subModule = proxy.getSubModule();
        this.action = proxy.getAction();
        this.timestamp = proxy.getTimestamp();
        this.data = proxy.getData();
    }

    public UserEvent(RdbmsUserEvent proxy) {
        this.userId = UUID.fromString(proxy.getUserId());
        this.organisationId = UUID.fromString(proxy.getOrganisationId());
        this.module = proxy.getModule();
        this.subModule = proxy.getSubModule();
        this.action = proxy.getAction();
        this.timestamp = proxy.getTimestamp();
        this.data = proxy.getData();
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(UUID organisationId) {
        this.organisationId = organisationId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSubModule() {
        return subModule;
    }

    public void setSubModule(String subModule) {
        this.subModule = subModule;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
