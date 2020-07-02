package org.endeavourhealth.core.database.dal.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.models.SubscriberCohortRecord;

import java.util.UUID;

public interface SubscriberCohortDalI {

    void saveCohortRecord(SubscriberCohortRecord record) throws Exception;
    SubscriberCohortRecord getLatestCohortRecord(String subscriberConfigName, UUID patientId, UUID excludeBatchId) throws Exception;
    boolean wasEverInCohort(String subscriberConfigName, UUID patientId) throws Exception;

    void saveInExplicitCohort(String subscriberConfigName, String nhsNumber, boolean inCohort) throws Exception;
    boolean isInExplicitCohort(String subscriberConfigName, String nhsNumber) throws Exception;

}
