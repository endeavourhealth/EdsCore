package org.endeavourhealth.core.database.dal.audit.models;

import java.util.Date;

public class ApplicationHeartbeat {
    private String applicationName;
    private String applicationInstanceName;
    private int applicationInstanceNumber;
    private Date timestmp;
    private String hostName;
    private Boolean isBusy;
    private Integer maxHeapMb;
    private Integer currentHeapMb;
    private Integer serverMemoryMb;
    private Integer serverCpuUsagePercent;
    private String isBusyDetail;
    private Date dtStarted;
    private Date dtJar;

    public ApplicationHeartbeat() {
        this.applicationInstanceNumber = 1; //default to one
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationInstanceName() {
        return applicationInstanceName;
    }

    public void setApplicationInstanceName(String applicationInstanceName) {
        this.applicationInstanceName = applicationInstanceName;
    }

    public Integer getApplicationInstanceNumber() {
        return applicationInstanceNumber;
    }

    public void setApplicationInstanceNumber(Integer applicationInstanceNumber) {
        this.applicationInstanceNumber = applicationInstanceNumber;
    }

    public Date getTimestmp() {
        return timestmp;
    }

    public void setTimestmp(Date timestmp) {
        this.timestmp = timestmp;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Boolean getBusy() {
        return isBusy;
    }

    public void setBusy(Boolean busy) {
        isBusy = busy;
    }

    public Integer getMaxHeapMb() {
        return maxHeapMb;
    }

    public void setMaxHeapMb(Integer maxHeapMb) {
        this.maxHeapMb = maxHeapMb;
    }

    public Integer getCurrentHeapMb() {
        return currentHeapMb;
    }

    public void setCurrentHeapMb(Integer currentHeapMb) {
        this.currentHeapMb = currentHeapMb;
    }

    public Integer getServerMemoryMb() {
        return serverMemoryMb;
    }

    public void setServerMemoryMb(Integer serverMemoryMb) {
        this.serverMemoryMb = serverMemoryMb;
    }

    public Integer getServerCpuUsagePercent() {
        return serverCpuUsagePercent;
    }

    public void setServerCpuUsagePercent(Integer serverCpuUsagePercent) {
        this.serverCpuUsagePercent = serverCpuUsagePercent;
    }

    public String getIsBusyDetail() {
        return isBusyDetail;
    }

    public void setIsBusyDetail(String isBusyDetail) {
        this.isBusyDetail = isBusyDetail;
    }

    public Date getDtStarted() {
        return dtStarted;
    }

    public void setDtStarted(Date dtStarted) {
        this.dtStarted = dtStarted;
    }

    public Date getDtJar() {
        return dtJar;
    }

    public void setDtJar(Date dtJar) {
        this.dtJar = dtJar;
    }

    @Override
    public String toString() {
        return "applicationName [" + applicationName + "], "
                + "applicationInstanceName [" + applicationInstanceName + "], "
                + "timestmp [" + timestmp + "], "
                + "hostName [" + hostName + "], "
                + "isBusy [" + isBusy + "], "
                + "isBusyDetail [" + isBusyDetail + "], "
                + "maxHeapMb [" + maxHeapMb + "], "
                + "currentHeapMb [" + currentHeapMb + "], "
                + "serverMemoryMb [" + serverMemoryMb + "], "
                + "serverCpuUsagePercent [" + serverCpuUsagePercent + "], "
                + "dtStarted [" + dtStarted + "], "
                + "dtJar [" + dtJar + "]";
    }


}
