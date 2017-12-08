package org.endeavourhealth.core.database.rdbms.jdbcreader.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "instance")
public class RdbmsInstance implements Serializable {

    private String instanceName = null;
    private String hostname = null;
    private int httpManagementPort;
    private Date lastConfigGetDate;

    public RdbmsInstance() {}

    @Id
    @Column(name = "instance_name", nullable = false)
    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    @Column(name = "hostname", nullable = true)
    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Column(name = "http_management_port", nullable = true)
    public int getHttpManagementPort() {
        return httpManagementPort;
    }

    public void setHttpManagementPort(int httpManagementPort) {
        this.httpManagementPort = httpManagementPort;
    }

    @Column(name = "last_config_get_date", nullable = true)
    public Date getLastConfigGetDate() {
        return lastConfigGetDate;
    }

    public void setLastConfigGetDate(Date lastConfigGetDate) {
        this.lastConfigGetDate = lastConfigGetDate;
    }
}
