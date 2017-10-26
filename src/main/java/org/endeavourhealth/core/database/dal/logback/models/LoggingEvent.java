package org.endeavourhealth.core.database.dal.logback.models;

import org.endeavourhealth.core.database.rdbms.logback.models.RdbmsLoggingEvent;

public class LoggingEvent {
    private long timestmp;
    private String formattedMessage;
    private String loggerName;
    private String levelString;
    private String threadName;
    private Short referenceFlag;
    private String arg0;
    private String arg1;
    private String arg2;
    private String arg3;
    private String callerFilename;
    private String callerClass;
    private String callerMethod;
    private String callerLine;
    private long eventId;
    
    public LoggingEvent() {}
    
    public LoggingEvent(RdbmsLoggingEvent proxy) {
        this.timestmp = proxy.getTimestmp();
        this.formattedMessage = proxy.getFormattedMessage();
        this.loggerName = proxy.getLoggerName();
        this.levelString = proxy.getLevelString();
        this.threadName = proxy.getThreadName();
        this.referenceFlag = proxy.getReferenceFlag();
        this.arg0 = proxy.getArg0();
        this.arg1 = proxy.getArg1();
        this.arg2 = proxy.getArg2();
        this.arg3 = proxy.getArg3();
        this.callerFilename = proxy.getCallerFilename();
        this.callerClass = proxy.getCallerClass();
        this.callerMethod = proxy.getCallerMethod();
        this.callerLine = proxy.getCallerLine();
        this.eventId = proxy.getEventId();
    }

    public long getTimestmp() {
        return timestmp;
    }

    public void setTimestmp(long timestmp) {
        this.timestmp = timestmp;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public void setFormattedMessage(String formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getLevelString() {
        return levelString;
    }

    public void setLevelString(String levelString) {
        this.levelString = levelString;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public Short getReferenceFlag() {
        return referenceFlag;
    }

    public void setReferenceFlag(Short referenceFlag) {
        this.referenceFlag = referenceFlag;
    }

    public String getArg0() {
        return arg0;
    }

    public void setArg0(String arg0) {
        this.arg0 = arg0;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public String getArg3() {
        return arg3;
    }

    public void setArg3(String arg3) {
        this.arg3 = arg3;
    }

    public String getCallerFilename() {
        return callerFilename;
    }

    public void setCallerFilename(String callerFilename) {
        this.callerFilename = callerFilename;
    }

    public String getCallerClass() {
        return callerClass;
    }

    public void setCallerClass(String callerClass) {
        this.callerClass = callerClass;
    }

    public String getCallerMethod() {
        return callerMethod;
    }

    public void setCallerMethod(String callerMethod) {
        this.callerMethod = callerMethod;
    }

    public String getCallerLine() {
        return callerLine;
    }

    public void setCallerLine(String callerLine) {
        this.callerLine = callerLine;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }
}
