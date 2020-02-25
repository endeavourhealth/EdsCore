package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.DataSharingAgreementEntity;

import java.util.List;

public interface DataSharingAgreementDalI {

    public DataSharingAgreementEntity getDSA(String uuid) throws Exception;
    public List<DataSharingAgreementEntity> getDSAsFromList(List<String> dsas) throws Exception;
    public List<DataSharingAgreementEntity> getAllDSAsForAllChildRegions(String regionUUID) throws Exception;
    public List<DataSharingAgreementEntity> getAllDSAsForPublisherOrganisation(String odsCode) throws Exception;
    public List<DataSharingAgreementEntity> getDSAsWithMatchingPublisherAndSubscriber(String publisherOds, String subscriberOds) throws Exception;

}
