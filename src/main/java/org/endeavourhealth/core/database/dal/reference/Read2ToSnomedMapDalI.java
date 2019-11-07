package org.endeavourhealth.core.database.dal.reference;

import org.endeavourhealth.core.database.dal.reference.models.Read2ToSnomedMap;
import org.endeavourhealth.core.terminology.Read2Code;

public interface Read2ToSnomedMapDalI {

    Read2Code getRead2Code(String readCode) throws Exception;
    Read2ToSnomedMap getRead2ToSnomedMap(String readCode) throws Exception;
}
