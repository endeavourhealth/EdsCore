package org.endeavourhealth.core.database.dal.reference;

public interface Msoa2011DalI {
    String lookupMsoa2011Code(String msoa2011Code) throws Exception;
    void updateMsoa2011Lookup(String msoa2011Code, String msoa2011Name) throws Exception;
}
