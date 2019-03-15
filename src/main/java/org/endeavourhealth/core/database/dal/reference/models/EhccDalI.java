package org.endeavourhealth.core.database.dal.reference.models;

public interface EhccDalI {
    String lookupCode(String ehccCode) throws Exception;
    void updateEhccLookup(String[] ehccData) throws Exception;
}
