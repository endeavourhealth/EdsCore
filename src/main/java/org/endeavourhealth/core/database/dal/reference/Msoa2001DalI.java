package org.endeavourhealth.core.database.dal.reference;

public interface Msoa2001DalI {
    String lookupMsoa2001Code(String msoa2001Code) throws Exception;
    void updateMsoa2001Lookup(String msoa2001Code, String msoa2001Name) throws Exception;
}