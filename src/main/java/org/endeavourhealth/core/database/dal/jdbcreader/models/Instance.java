package org.endeavourhealth.core.database.dal.jdbcreader.models;

import org.endeavourhealth.core.database.rdbms.jdbcreader.models.RdbmsBatch;
import org.endeavourhealth.core.database.rdbms.jdbcreader.models.RdbmsInstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Instance {

    private String instanceName = null;
    private String hostname = null;
    private int httpManagementPort;
    private Date lastConfigGetDate;

    public Instance() {}

    public Instance(RdbmsInstance proxy) {
        this.instanceName = proxy.getInstanceName();
        this.hostname = proxy.getHostname();
        this.httpManagementPort = proxy.getHttpManagementPort();
        this.lastConfigGetDate = proxy.getLastConfigGetDate();
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getHttpManagementPort() {
        return httpManagementPort;
    }

    public void setHttpManagementPort(int httpManagementPort) {
        this.httpManagementPort = httpManagementPort;
    }

    public Date getLastConfigGetDate() {
        return lastConfigGetDate;
    }

    public void setLastConfigGetDate(Date lastConfigGetDate) {
        this.lastConfigGetDate = lastConfigGetDate;
    }
}
