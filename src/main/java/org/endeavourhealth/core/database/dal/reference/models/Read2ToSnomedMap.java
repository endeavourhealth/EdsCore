package org.endeavourhealth.core.database.dal.reference.models;

import java.util.Date;

public class Read2ToSnomedMap {

    private String mapId;
    private String readCode;
    private String termCode;
    private String conceptId;
    private Date effectiveDate;
    private int mapStatus;

    public Read2ToSnomedMap() {
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public String getReadCode() {
        return readCode;
    }

    public void setReadCode(String readCode) {
        this.readCode = readCode;
    }

    public String getTermCode() {
        return termCode;
    }

    public void setTermCode(String termCode) {
        this.termCode = termCode;
    }

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public int getMapStatus() {
        return mapStatus;
    }

    public void setMapStatus(int mapStatus) {
        this.mapStatus = mapStatus;
    }

    @Override
    public String toString() {
        return "mapId = " + mapId + ", "
                + "readCode = " + readCode + ", "
                + "termCode = " + termCode + ", "
                + "conceptId = " + conceptId + ", "
                + "effectiveDate = " + effectiveDate + ", "
                + "mapStatus = " + mapStatus;
    }

}
