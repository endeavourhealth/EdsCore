package org.endeavourhealth.core.database.dal.datasharingmanager.models;

public class JsonValueSetCodes {

    private String valueSetsUuid;
    private String read2ConceptId;
    private String ctv3ConceptId;
    private String sctConceptId;

    public String getValueSetsUuid() {
        return valueSetsUuid;
    }

    public void setValueSetsUuid(String valueSetsUuid) {
        this.valueSetsUuid = valueSetsUuid;
    }

    public String getRead2ConceptId() {
        return read2ConceptId;
    }

    public void setRead2ConceptId(String read2ConceptId) {
        this.read2ConceptId = read2ConceptId;
    }

    public String getCtv3ConceptId() {
        return ctv3ConceptId;
    }

    public void setCtv3ConceptId(String ctv3ConceptId) {
        this.ctv3ConceptId = ctv3ConceptId;
    }

    public String getSctConceptId() {
        return sctConceptId;
    }

    public void setSctConceptId(String sctConceptId) {
        this.sctConceptId = sctConceptId;
    }
}
