package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.QueuedMessageType;

import java.util.UUID;

public interface QueuedMessageDalI {

    void save(UUID messageId, String messageBody, QueuedMessageType type) throws Exception;
    String getById(UUID id) throws Exception;
    void delete(UUID id) throws Exception;
}
