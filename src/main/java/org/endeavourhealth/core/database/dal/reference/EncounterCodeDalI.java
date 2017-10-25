package org.endeavourhealth.core.database.dal.reference;

import org.endeavourhealth.core.database.dal.reference.models.EncounterCode;

public interface EncounterCodeDalI {

    EncounterCode findOrCreateCode(String term) throws Exception;
}
