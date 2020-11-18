package org.endeavourhealth.core.database.dal.informationmodel;


import org.endeavourhealth.core.database.dal.publisherCommon.models.TppClinicalCodeForIMUpdate;

import java.util.List;

public interface TppClinicalCodesIMUpdaterDalI {
    void updateIMForTppClinicalCodes(List<TppClinicalCodeForIMUpdate> codeList) throws Exception;

}
