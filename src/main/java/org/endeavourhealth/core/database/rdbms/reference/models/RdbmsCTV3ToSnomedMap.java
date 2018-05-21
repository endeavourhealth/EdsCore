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
        this.ctv3ConceptId = proxy.getCTV3ConceptId();
        this.ctv3TermId = proxy.getCTV3TermId() ;
        this.ctv3TermType = proxy.getCTV3TermType();
        this.sctConceptId = proxy.getSCTConceptId();
        this.sctDecriptionId = proxy.getSCTDescriptionId();
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
    public String getCTV3ConceptId() {
        return ctv3ConceptId;
    }
    public void setCTV3ConceptId(String mapId) {
        this.ctv3ConceptId = ctv3ConceptId;
    }

    @Column(name = "ctv3_term_id", nullable = false)
    public String getCTV3TermId() {
        return ctv3TermId;
    }
    public void setCTV3TermId(String mapId) {
        this.ctv3TermId = ctv3TermId;
    }

    @Column(name = "ctv3_term_type", nullable = true)
    public String getCTV3TermType() {
        return ctv3TermType;
    }
    public void setCTV3TermType(String mapId) {
        this.ctv3TermType = ctv3TermType;
    }

    @Column(name = "sct_concept_id", nullable = false)
    public String getSCTConceptId() {
        return sctConceptId;
    }
    public void setSCTConceptId(String mapId) {
        this.sctConceptId = sctConceptId;
    }

    @Column(name = "sct_description_id", nullable = true)
    public String getSCTDescriptionId() {
        return sctDecriptionId;
    }
    public void setSCTDescriptionId(String sctDecriptionId) { this.sctDecriptionId = sctDecriptionId; }

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

