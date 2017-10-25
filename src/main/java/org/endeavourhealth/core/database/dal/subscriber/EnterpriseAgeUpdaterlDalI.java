package org.endeavourhealth.core.database.dal.subscriber;

import org.endeavourhealth.core.database.dal.subscriber.models.EnterpriseAge;

import java.util.Date;
import java.util.List;

public interface EnterpriseAgeUpdaterlDalI {

    List<EnterpriseAge> findAgesToUpdate() throws Exception;
    Integer[] calculateAgeValues(long patientId, Date dateOfBirth) throws Exception;
    Integer[] calculateAgeValues(EnterpriseAge map) throws Exception;
    void save(EnterpriseAge obj) throws Exception;

}
