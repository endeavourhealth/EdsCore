package org.endeavourhealth.core.database.cassandra.admin.models;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.endeavourhealth.core.database.dal.reference.models.SnomedLookup;

@Table(keyspace = "admin", name = "snomed_lookup")
public class CassandraSnomedLookup {

    @PartitionKey
    @Column(name = "concept_id")
    private String conceptId = null;
    @Column(name = "type_id")
    private String typeId = null;
    @Column(name = "term")
    private String term = null;

    public CassandraSnomedLookup() {}

    public CassandraSnomedLookup(SnomedLookup proxy) {
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
