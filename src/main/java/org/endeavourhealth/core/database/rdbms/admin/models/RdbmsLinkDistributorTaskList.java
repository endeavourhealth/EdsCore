package org.endeavourhealth.core.database.rdbms.admin.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "link_distributor_task_list", schema = "admin", catalog = "")
public class RdbmsLinkDistributorTaskList {
    private String configName;
    private byte processStatus;

    public RdbmsLinkDistributorTaskList() {
    }

    public RdbmsLinkDistributorTaskList(String configName, byte processStatus) {
        this.configName = configName;
        this.processStatus = processStatus;
    }

    @Id
    @Column(name = "config_name")
    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @Basic
    @Column(name = "process_status")
    public byte getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(byte processStatus) {
        this.processStatus = processStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsLinkDistributorTaskList that = (RdbmsLinkDistributorTaskList) o;
        return processStatus == that.processStatus &&
                Objects.equals(configName, that.configName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(configName, processStatus);
    }
}
