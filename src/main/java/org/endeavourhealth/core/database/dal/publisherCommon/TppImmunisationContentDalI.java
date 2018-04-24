package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppImmunisationContent;

public interface TppImmunisationContentDalI {

    TppImmunisationContent getContentFromRowId(Long rowId) throws Exception;

    void save(TppImmunisationContent mapping) throws Exception;
}
