package org.endeavourhealth.core.rdbms.audit.models;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_event", schema = "public", catalog = "audit")
public class UserEvent {

    private String userId = null;
    private String organisationId = null;
    private String module = null;
    private String subModule = null;
    private String action = null;
    private DateTime timestamp = null;
    private String data = null;

    @Id
    @Column(name = "user_id", nullable = false)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "organisation_id", nullable = false)
    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    @Id
    @Column(name = "module", nullable = false)
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Id
    @Column(name = "sub_module", nullable = false)
    public String getSubModule() {
        return subModule;
    }

    public void setSubModule(String subModule) {
        this.subModule = subModule;
    }

    @Id
    @Column(name = "action", nullable = false)
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Id
    @Column(name = "timestamp", nullable = false)
    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "data", nullable = false)
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
