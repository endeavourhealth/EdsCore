package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.TppProfileRole;

import java.util.UUID;

public interface TppProfileRoleDalI {

    TppProfileRole getContentFromRowId(Long rowId, UUID serviceId) throws Exception;

    void save(TppProfileRole mapping, UUID serviceId) throws Exception;
}
