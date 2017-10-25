package org.endeavourhealth.core.database.dal.reference.models;

import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsEncounterCode;

public class EncounterCode {

    private long code;
    private String term;
    private String mapping; //note mapping is the same as term except with forced upper case

    public EncounterCode() {}

    public EncounterCode(RdbmsEncounterCode proxy) {
        this.code = proxy.getCode();
        this.term = proxy.getTerm();
        this.mapping = proxy.getMapping();
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }
}
