package org.endeavourhealth.core.database.dal.publisherCommon;

import java.util.Date;

public interface TppCtv3SnomedRefDalI {

    void updateSnomedTable(String s3FilePath, Date dataDate) throws Exception;
}
