package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppImmunisationContent;

import java.util.Date;

public interface TppImmunisationContentDalI {

    TppImmunisationContent getContentFromRowId(int rowId) throws Exception;

    void updateLookupTable(String s3FilePath, Date dataDate) throws Exception;
}
