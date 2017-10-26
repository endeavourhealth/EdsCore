package org.endeavourhealth.core.database.dal.logback;

import org.endeavourhealth.core.database.dal.logback.models.LoggingEvent;

import java.util.List;

public interface LogbackDalI {

    public List<LoggingEvent> getLoggingEvents(int page, String serviceId, String level) throws Exception;
    public String getStackTrace(Long eventId) throws Exception;
}
