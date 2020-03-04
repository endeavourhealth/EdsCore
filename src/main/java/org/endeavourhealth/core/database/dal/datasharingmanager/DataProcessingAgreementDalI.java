package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.DataProcessingAgreementEntity;

import java.util.List;

public interface DataProcessingAgreementDalI {

    public DataProcessingAgreementEntity getDPA(String uuid) throws Exception;
    public List<DataProcessingAgreementEntity> getDPAsFromList(List<String> dpas) throws Exception;
    public List<DataProcessingAgreementEntity> getDataProcessingAgreementsForOrganisation(String odsCode) throws Exception;
    public List<DataProcessingAgreementEntity> getAllDPAsForAllChildRegions(String regionUUID) throws Exception;
}
