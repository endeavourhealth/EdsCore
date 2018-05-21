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
        this.ctv3ConceptId = proxy.getCtv3ConceptId();
        this.ctv3TermId = proxy.getCtv3TermId();
        this.ctv3TermType = proxy.getCtv3TermType();
        this.sctConceptId = proxy.getSctConceptId();
        this.sctDecriptionId = proxy.getSctDescriptionId();
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

    public String getCtv3ConceptId() {
        return ctv3ConceptId;
    }
    public void setCtv3ConceptId(String mapId) {
        this.ctv3ConceptId = ctv3ConceptId;
    }

    public String getCtv3TermId() {
        return ctv3TermId;
    }
    public void setCtv3TermId(String mapId) {
        this.ctv3TermId = ctv3TermId;
    }

    public String getCtv3TermType() {
        return ctv3TermType;
    }
    public void setCtv3TermType(String mapId) {
        this.ctv3TermType = ctv3TermType;
    }

    public String getSctConceptId() {
        return sctConceptId;
    }
    public void setSctConceptId(String mapId) {
        this.sctConceptId = sctConceptId;
    }

    public String getSctDescriptionId() {
        return sctDecriptionId;
    }
    public void setSctDescriptionId(String sctDecriptionId) { this.sctDecriptionId = sctDecriptionId; }

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
