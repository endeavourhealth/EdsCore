package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.TppImmunisationContent;

import java.util.UUID;

public interface TppImmunisationContentDalI {

    TppImmunisationContent getContentFromRowId(Long rowId, UUID serviceId) throws Exception;

    void save(TppImmunisationContent mapping, UUID serviceId) throws Exception;
}
