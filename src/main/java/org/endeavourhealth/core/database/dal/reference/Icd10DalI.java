package org.endeavourhealth.core.database.dal.reference;

public interface Icd10DalI {

    String lookupCode(String icd10Code) throws Exception;
    void updateIcd10Lookup(String code, String description) throws Exception;
}
