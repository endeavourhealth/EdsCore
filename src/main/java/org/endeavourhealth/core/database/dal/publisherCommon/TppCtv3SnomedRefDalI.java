package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppClinicalCodeForIMUpdate;

import java.util.Date;
import java.util.List;

public interface TppCtv3SnomedRefDalI {

    void updateSnomedTable(String s3FilePath, Date dataDate) throws Exception;
    List<TppClinicalCodeForIMUpdate> getClinicalCodesForIMUpdate() throws Exception;

}

