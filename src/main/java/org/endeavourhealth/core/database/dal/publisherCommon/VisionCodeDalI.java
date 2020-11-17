package org.endeavourhealth.core.database.dal.publisherCommon;

import java.util.Date;

public interface VisionCodeDalI {

    void updateRead2TermTable(String sourceFile, Date dataDate) throws Exception;
    void updateRead2ToSnomedMapTable(String sourceFile, Date dataDate) throws Exception;

}
