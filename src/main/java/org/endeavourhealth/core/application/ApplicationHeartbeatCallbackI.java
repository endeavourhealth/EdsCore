package org.endeavourhealth.core.application;

import org.endeavourhealth.core.database.dal.audit.models.ApplicationHeartbeat;

public interface ApplicationHeartbeatCallbackI {

    /**
     * callback function to provide the application-specific "busy" state
     */
    void populateIsBusy(ApplicationHeartbeat h);
}
