package org.endeavourhealth.core.database.dal.admin.models;

import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsLinkDistributorTaskList;

public class LinkDistributorTaskList {
    private String configName;
    private byte processStatus;

    public LinkDistributorTaskList() {
    }

    public LinkDistributorTaskList(String configName, byte processStatus) {
        this.configName = configName;
        this.processStatus = processStatus;
    }

    public LinkDistributorTaskList(RdbmsLinkDistributorTaskList proxy) throws Exception {
        this.configName = proxy.getConfigName();
        this.processStatus = proxy.getProcessStatus();
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public byte getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(byte processStatus) {
        this.processStatus = processStatus;
    }
}
