package org.endeavourhealth.core.database.dal.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.models.PcrAge;

import java.util.Date;
import java.util.List;

public interface PcrAgeUpdaterlDalI {

    List<PcrAge> findAgesToUpdate() throws Exception;
    Integer[] calculateAgeValues(long patientId, Date dateOfBirth) throws Exception;
    Integer[] calculateAgeValues(PcrAge map) throws Exception;
    void save(PcrAge obj) throws Exception;

}
