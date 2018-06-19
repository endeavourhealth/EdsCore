package org.endeavourhealth.core.database.dal.reference;

import org.endeavourhealth.core.database.dal.reference.models.SnomedLookup;

public interface SnomedDalI {

    SnomedLookup getSnomedLookup(String conceptId) throws Exception;
    SnomedLookup getSnomedLookupForDescId(String descriptionId) throws Exception;
}
