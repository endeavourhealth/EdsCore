package org.endeavourhealth.core.database.dal.reference.models;

import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsSnomedLookup;

public class SnomedLookup {

    private String conceptId = null;
    private String typeId = null;
    private String term = null;

    public SnomedLookup() {}

    /*public SnomedLookup(CassandraSnomedLookup proxy) {
        this.conceptId = proxy.getConceptId();
        this.typeId = proxy.getTypeId();
        this.term = proxy.getTerm();
    }*/

    public SnomedLookup(RdbmsSnomedLookup proxy) {
        this.conceptId = proxy.getConceptId();
        this.typeId = proxy.getTypeId();
        this.term = proxy.getTerm();
    }

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
