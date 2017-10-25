package org.endeavourhealth.core.database.rdbms.reference.models;

import org.endeavourhealth.core.database.dal.reference.models.SnomedLookup;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "snomed_lookup", schema = "public")
public class RdbmsSnomedLookup implements Serializable {

    private String conceptId = null;
    private String typeId = null;
    private String term = null;

    public RdbmsSnomedLookup() {}

    public RdbmsSnomedLookup(SnomedLookup proxy) {
        this.conceptId = proxy.getConceptId();
        this.typeId = proxy.getTypeId();
        this.term = proxy.getTerm();
    }

    @Id
    @Column(name = "concept_id", nullable = false)
    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    @Column(name = "type_id", nullable = false)
    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    @Column(name = "term", nullable = false)
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
