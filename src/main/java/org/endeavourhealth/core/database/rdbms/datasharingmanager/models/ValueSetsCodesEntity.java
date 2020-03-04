package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "value_sets_codes", schema = "data_sharing_manager")
public class ValueSetsCodesEntity {

    private static final Logger LOG = LoggerFactory.getLogger(ValueSetsCodesEntity.class);

    private String uuid;
    private String valueSetsUuid;
    private String read2ConceptId;
    private String ctv3ConceptId;
    private String sctConceptId;

    @Id
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "value_sets_uuid")
    public String getValueSetsUuid() {
        return valueSetsUuid;
    }

    public void setValueSetsUuid(String valueSetsUuid) {
        this.valueSetsUuid = valueSetsUuid;
    }

    @Basic
    @Column(name = "read2_concept_id")
    public String getRead2ConceptId() {
        return read2ConceptId;
    }

    public void setRead2ConceptId(String read2ConceptId) {
        this.read2ConceptId = read2ConceptId;
    }

    @Basic
    @Column(name = "ctv3_concept_id")
    public String getCtv3ConceptId() {
        return ctv3ConceptId;
    }

    public void setCtv3ConceptId(String ctv3ConceptId) {
        this.ctv3ConceptId = ctv3ConceptId;
    }

    @Basic
    @Column(name = "sct_concept_id")
    public String getSctConceptId() {
        return sctConceptId;
    }

    public void setSctConceptId(String sctConceptId) {
        this.sctConceptId = sctConceptId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueSetsCodesEntity that = (ValueSetsCodesEntity) o;
        return valueSetsUuid == that.valueSetsUuid &&
                Objects.equals(read2ConceptId, that.read2ConceptId) &&
                Objects.equals(ctv3ConceptId, that.ctv3ConceptId) &&
                Objects.equals(sctConceptId, that.sctConceptId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueSetsUuid, read2ConceptId, ctv3ConceptId, sctConceptId);
    }

}