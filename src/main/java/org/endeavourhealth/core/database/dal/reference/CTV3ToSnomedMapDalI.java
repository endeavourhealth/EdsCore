package org.endeavourhealth.core.database.dal.reference;

import org.endeavourhealth.core.database.dal.reference.models.CTV3ToSnomedMap;

public interface CTV3ToSnomedMapDalI {

    CTV3ToSnomedMap getCtv3ToSnomedMap(String ctv3ConceptId) throws Exception;
}
