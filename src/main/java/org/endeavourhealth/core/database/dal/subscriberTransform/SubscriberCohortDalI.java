package org.endeavourhealth.core.database.dal.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.models.SubscriberCohortRecord;

import java.util.UUID;

public interface SubscriberCohortDalI {

    SubscriberCohortRecord getCohortRecord(String subscriberConfigName, UUID patientId) throws Exception;
    void saveCohortRecord(SubscriberCohortRecord record) throws Exception;
}
