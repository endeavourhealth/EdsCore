package org.endeavourhealth.core.database.rdbms.reference.models;

import org.endeavourhealth.core.database.dal.reference.models.CTV3ToSnomedMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ctv3_to_snomed_map")
public class RdbmsCTV3ToSnomedMap implements Serializable {

    private String mapId;
    private String ctv3ConceptId;
    private String ctv3TermId;
    private String ctv3TermType;
    private String sctConceptId;
    private String sctDecriptionId;
    private int mapStatus;
    private Date effectiveDate;
    private int isAssured;

    public RdbmsCTV3ToSnomedMap() {}

    public RdbmsCTV3ToSnomedMap(CTV3ToSnomedMap proxy) {
        this.mapId = proxy.getMapId();
        this.ctv3ConceptId = proxy.getCtv3ConceptId();
        this.ctv3TermId = proxy.getCtv3TermId() ;
        this.ctv3TermType = proxy.getCtv3TermType();
        this.sctConceptId = proxy.getSctConceptId();
        this.sctDecriptionId = proxy.getSctDescriptionId();
        this.mapStatus = proxy.getMapStatus();
        this.effectiveDate = proxy.getEffectiveDate();
        this.isAssured = proxy.getIsAssured();
    }

    @Id
    @Column(name = "map_id", nullable = false)
    public String getMapId() {
        return mapId;
    }
    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    @Column(name = "ctv3_concept_id", nullable = false)
    public String getCtv3ConceptId() {
        return ctv3ConceptId;
    }
    public void setCtv3ConceptId(String ctv3ConceptId) {
        this.ctv3ConceptId = ctv3ConceptId;
    }

    @Column(name = "ctv3_term_id", nullable = false)
    public String getCtv3TermId() {
        return ctv3TermId;
    }
    public void setCtv3TermId(String ctv3TermId) {
        this.ctv3TermId = ctv3TermId;
    }

    @Column(name = "ctv3_term_type", nullable = true)
    public String getCtv3TermType() {
        return ctv3TermType;
    }
    public void setCtv3TermType(String ctv3TermType) {
        this.ctv3TermType = ctv3TermType;
    }

    @Column(name = "sct_concept_id", nullable = false)
    public String getSctConceptId() {
        return sctConceptId;
    }
    public void setSctConceptId(String sctConceptId) {
        this.sctConceptId = sctConceptId;
    }

    @Column(name = "sct_description_id", nullable = true)
    public String getSctDescriptionId() {
        return sctDecriptionId;
    }
    public void setSctDescriptionId(String sctDecriptionId) { this.sctDecriptionId = sctDecriptionId; }

    @Column(name = "effective_date", nullable = false)
    public Date getEffectiveDate() {
        return effectiveDate;
    }
    public void setEffectiveDate(Date effectiveDate) { this.effectiveDate = effectiveDate; }

    @Column(name = "map_status", nullable = false)
    public int getMapStatus() {
        return mapStatus;
    }
    public void setMapStatus(int mapStatus) {
        this.mapStatus = mapStatus;
    }

    @Column(name = "is_assured", nullable = false)
    public int getIsAssured() {
        return isAssured;
    }
    public void setIsAssured(int isAssured) {
        this.isAssured = isAssured;
    }
}

