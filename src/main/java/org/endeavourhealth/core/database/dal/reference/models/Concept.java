package org.endeavourhealth.core.database.dal.reference.models;

import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsConcept;

public class Concept {

    private long pid;
    private String code;
    private long codesystem_pid;
    private String display;
    private long index_status;

    public Concept() {}

    public Concept(RdbmsConcept proxy) {
        this.pid = proxy.getPid();
        this.code = proxy.getCode();
        this.codesystem_pid = proxy.getCodesystem_pid();
        this.display = proxy.getDisplay();
        this.index_status = proxy.getIndex_status();

    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCodesystem_pid() {
        return codesystem_pid;
    }

    public void setCodesystem_pid(long codesystem_pid) {
        this.codesystem_pid = codesystem_pid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public long getIndex_status() {
        return index_status;
    }

    public void setIndex_status(long index_status) {
        this.index_status = index_status;
    }
}
