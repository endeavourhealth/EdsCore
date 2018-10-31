package org.endeavourhealth.core.database.dal.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.models.EnterpriseAge;

import java.util.Date;
import java.util.List;

public interface EnterpriseAgeUpdaterlDalI {

    List<EnterpriseAge> findAgesToUpdate() throws Exception;
    Integer[] calculateAgeValuesAndUpdateTable(long patientId, Date dateOfBirth, Date dateOfDeath) throws Exception;
    Integer[] reCalculateAgeValues(EnterpriseAge map) throws Exception;
    void save(EnterpriseAge obj) throws Exception;

}
