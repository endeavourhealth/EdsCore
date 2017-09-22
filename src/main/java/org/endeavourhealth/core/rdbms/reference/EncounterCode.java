package org.endeavourhealth.core.rdbms.reference;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "encounter_code", schema = "public")
public class EncounterCode {

    private long code;
    private String term;
    private String mapping;

    @Id
    @Column(name = "code", nullable = false)
    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    @Column(name = "term", nullable = false)
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @Column(name = "mapping", nullable = false)
    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }
}
