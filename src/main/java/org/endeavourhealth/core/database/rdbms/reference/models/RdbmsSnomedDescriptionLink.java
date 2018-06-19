package org.endeavourhealth.core.database.rdbms.reference.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "snomed_description_link")
public class RdbmsSnomedDescriptionLink implements Serializable {

    private String descriptionId = null;
    private String conceptId = null;

    @Id
    @Column(name = "description_id", nullable = false)
    public String getDescriptionId() {
        return descriptionId;
    }

    public void setDescriptionId(String descriptionId) {
        this.descriptionId = descriptionId;
    }

    @Id
    @Column(name = "concept_id", nullable = false)
    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }
}
