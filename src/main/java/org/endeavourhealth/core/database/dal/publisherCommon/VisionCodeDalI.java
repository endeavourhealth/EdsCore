package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.VisionClinicalCodeForIMUpdate;

import java.util.Date;
import java.util.List;

public interface VisionCodeDalI {

    void updateRead2TermTable(String sourceFile, Date dataDate) throws Exception;
    void updateRead2ToSnomedMapTable(String sourceFile, Date dataDate) throws Exception;
    List<VisionClinicalCodeForIMUpdate> getClinicalCodesForIMUpdate() throws Exception;

}
