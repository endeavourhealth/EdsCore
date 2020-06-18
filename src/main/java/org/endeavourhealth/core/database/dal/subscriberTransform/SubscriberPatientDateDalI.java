package org.endeavourhealth.core.database.dal.subscriberTransform;

import java.util.Date;
import java.util.UUID;

/**
 * tracks what version of a patient was last sent to a subscriber so the transform can work out what's changed
 */
public interface SubscriberPatientDateDalI {

    Date getDateLastTransformedPatient(String subscriberConfigName, UUID patientId) throws Exception;
    void saveDateLastTransformedPatient(String subscriberConfigName, UUID patientId, long subscriberId, Date dtVersion) throws Exception;
}
