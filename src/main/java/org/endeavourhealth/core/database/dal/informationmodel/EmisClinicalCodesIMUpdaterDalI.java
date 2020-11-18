package org.endeavourhealth.core.database.dal.informationmodel;


import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisClinicalCodeForIMUpdate;

import java.util.List;

public interface EmisClinicalCodesIMUpdaterDalI {
    void updateIMForEmisClinicalCodes(List<EmisClinicalCodeForIMUpdate> codeList) throws Exception;

}
