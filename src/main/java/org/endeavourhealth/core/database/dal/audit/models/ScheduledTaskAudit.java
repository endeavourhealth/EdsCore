package org.endeavourhealth.core.database.dal.audit.models;

import java.util.Date;

public class ScheduledTaskAudit {

    private String applicationName;
    private String taskName;
    private String taskParameters;
    private Date timestamp;
    private String hostName;
    private boolean success;
    private String errorMessage;


    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskParameters() {
        return taskParameters;
    }

    public void setTaskParameters(String taskParameters) {
        this.taskParameters = taskParameters;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("App [" + applicationName + "], ");
        sb.append("Task [" + taskName + "], ");
        sb.append("Params [" + taskParameters + "], ");
        sb.append("Dt [" + timestamp + "], ");
        sb.append("Host [" + hostName + "], ");
        sb.append("Success [" + success + "], ");
        sb.append("Err [" + errorMessage + "]");
        return sb.toString();
    }
}
