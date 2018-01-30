package org.endeavourhealth.core.database.dal.reference;

public interface Opcs4DalI {

    String lookupCode(String opcs4Code) throws Exception;
    void updateOpcs4Lookup(String procedureCode, String procedureName) throws Exception;
}
