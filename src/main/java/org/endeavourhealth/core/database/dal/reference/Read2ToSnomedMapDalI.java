package org.endeavourhealth.core.database.dal.reference;

import org.endeavourhealth.core.database.dal.reference.models.Read2ToSnomedMap;

public interface Read2ToSnomedMapDalI {

    Read2ToSnomedMap getRead2ToSnomedMap(String readCode) throws Exception;
}
