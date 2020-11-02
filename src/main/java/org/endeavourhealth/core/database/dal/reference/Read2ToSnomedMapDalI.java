package org.endeavourhealth.core.database.dal.reference;

import org.endeavourhealth.core.database.dal.reference.models.Read2ToSnomedMap;
import org.endeavourhealth.core.terminology.Read2Code;

import java.util.Collection;
import java.util.Map;

public interface Read2ToSnomedMapDalI {

    Read2Code getRead2Code(String readCode) throws Exception;
    Map<String, Read2Code> getRead2Codes(Collection<String> readCodes) throws Exception;

    Read2ToSnomedMap getRead2ToSnomedMap(String readCode) throws Exception;
}
