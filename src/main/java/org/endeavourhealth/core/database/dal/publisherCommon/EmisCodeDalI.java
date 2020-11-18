package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisClinicalCode;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisClinicalCodeForIMUpdate;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisDrugCode;

import java.util.Date;
import java.util.List;

public interface EmisCodeDalI {

    void updateClinicalCodeTable(String s3FilePath, String validReadCodesFile, Date dataDate) throws Exception;
    void updateDrugCodeTable(String s3FilePath, Date dataDate) throws Exception;

    EmisDrugCode getDrugCode(long codeId) throws Exception;
    EmisClinicalCode getClinicalCode(long codeId) throws Exception;
    List<EmisClinicalCodeForIMUpdate> getClinicalCodesForIMUpdate() throws Exception;

/*
    void saveCodeMappings(List<EmisCsvCodeMap> mappings) throws Exception;
    void saveCodeMapping(EmisCsvCodeMap mapping) throws Exception;
    EmisCsvCodeMap getCodeMapping(boolean medication, Long codeId) throws Exception;
*/

}
