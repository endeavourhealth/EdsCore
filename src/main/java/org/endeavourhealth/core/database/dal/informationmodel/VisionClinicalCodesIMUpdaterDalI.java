package org.endeavourhealth.core.database.dal.informationmodel;


import org.endeavourhealth.core.database.dal.publisherCommon.models.VisionClinicalCodeForIMUpdate;

import java.util.List;

public interface VisionClinicalCodesIMUpdaterDalI {
    void updateIMForVisionClinicalCodes(List<VisionClinicalCodeForIMUpdate> codeList) throws Exception;

}
