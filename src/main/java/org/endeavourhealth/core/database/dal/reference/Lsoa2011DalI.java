package org.endeavourhealth.core.database.dal.reference;

public interface Lsoa2011DalI {
    String lookupLsoa2011Code(String lsoa2011Code) throws Exception;
    void updateLsoa2011Lookup(String lsoa2011Code, String lsoa2011Name) throws Exception;
}
