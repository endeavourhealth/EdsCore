package org.endeavourhealth.core.database.dal.subscriberTransform;

import org.hl7.fhir.instance.model.Address;

public interface HouseholdIdDalI {

    Long findOrCreateHouseholdId(Address address) throws Exception;
}
