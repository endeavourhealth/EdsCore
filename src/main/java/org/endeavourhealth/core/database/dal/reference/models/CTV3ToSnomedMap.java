package org.endeavourhealth.core.database.dal.reference.models;

import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsCTV3ToSnomedMap;

import java.util.Date;

public class CTV3ToSnomedMap {

    private String mapId;
    private String ctv3ConceptId;
    private String ctv3TermId;
    private String ctv3TermType;
    private String sctConceptId;
    private String sctDecriptionId;
    private int mapStatus;
    private Date effectiveDate;
    private int isAssured;

    public CTV3ToSnomedMap() {}

    public CTV3ToSnomedMap(RdbmsCTV3ToSnomedMap proxy) {
        this.mapId = proxy.getMapId();
        this.ctv3ConceptId = proxy.getCTV3ConceptId();
        this.ctv3TermId = proxy.getCTV3TermId();
        this.ctv3TermType = proxy.getCTV3TermType();
        this.sctConceptId = proxy.getSCTConceptId();
        this.sctDecriptionId = proxy.getSCTDescriptionId();
        this.mapStatus = proxy.getMapStatus();
        this.effectiveDate = proxy.getEffectiveDate();
        this.isAssured = proxy.getIsAssured();
    }

    public String getMapId() {
        return mapId;
    }
    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public String getCTV3ConceptId() {
        return ctv3ConceptId;
    }
    public void setCTV3ConceptId(String mapId) {
        this.ctv3ConceptId = ctv3ConceptId;
    }

    public String getCTV3TermId() {
        return ctv3TermId;
    }
    public void setCTV3TermId(String mapId) {
        this.ctv3TermId = ctv3TermId;
    }

    public String getCTV3TermType() {
        return ctv3TermType;
    }
    public void setCTV3TermType(String mapId) {
        this.ctv3TermType = ctv3TermType;
    }

    public String getSCTConceptId() {
        return sctConceptId;
    }
    public void setSCTConceptId(String mapId) {
        this.sctConceptId = sctConceptId;
    }

    public String getSCTDescriptionId() {
        return sctDecriptionId;
    }
    public void setSCTDescriptionId(String sctDecriptionId) { this.sctDecriptionId = sctDecriptionId; }

    public Date getEffectiveDate() {
        return effectiveDate;
    }
    public void setEffectiveDate(Date effectiveDate) { this.effectiveDate = effectiveDate; }

    public int getMapStatus() {
        return mapStatus;
    }
    public void setMapStatus(int mapStatus) {
        this.mapStatus = mapStatus;
    }

    public int getIsAssured() {
        return isAssured;
    }
    public void setIsAssured(int isAssured) {
        this.isAssured = isAssured;
    }
}
