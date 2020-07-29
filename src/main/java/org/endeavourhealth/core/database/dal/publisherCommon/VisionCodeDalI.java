package org.endeavourhealth.core.database.dal.publisherCommon;

import java.util.Date;

public interface VisionCodeDalI {

    void updateLookupTable(String s3FilePath, Date dataDate) throws Exception;
}
