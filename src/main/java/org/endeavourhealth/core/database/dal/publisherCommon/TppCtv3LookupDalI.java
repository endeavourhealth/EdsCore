package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppCtv3Lookup;

import java.util.UUID;

public interface TppCtv3LookupDalI {

    TppCtv3Lookup getContentFromRowId(Long rowId) throws Exception;

    TppCtv3Lookup getContentFromCtv3Code(String ctv3Code) throws Exception;

    void save(TppCtv3Lookup ctv3Lookup) throws Exception;
}
