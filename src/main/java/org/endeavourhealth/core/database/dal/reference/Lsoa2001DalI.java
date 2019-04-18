package org.endeavourhealth.core.database.dal.reference;

public interface Lsoa2001DalI {
    String lookupLsoa2001Code(String lsoa2001Code) throws Exception;
    void updateLsoa2001Lookup(String lsoa2001Code, String lsoa2001Name) throws Exception;
}
