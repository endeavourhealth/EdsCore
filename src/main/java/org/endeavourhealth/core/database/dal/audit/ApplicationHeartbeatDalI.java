package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.ApplicationHeartbeat;

import java.util.List;

public interface ApplicationHeartbeatDalI {

    void saveHeartbeat(ApplicationHeartbeat h) throws Exception;
    List<ApplicationHeartbeat> getLatest() throws Exception;
}
