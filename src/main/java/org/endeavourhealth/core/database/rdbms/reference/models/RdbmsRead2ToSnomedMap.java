package org.endeavourhealth.core.database.rdbms.reference.models;

import org.endeavourhealth.core.database.dal.reference.models.Read2ToSnomedMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "read2_to_snomed_map")
public class RdbmsRead2ToSnomedMap implements Serializable {

    private String mapId;
    private String readCode;
    private String termCode;
    private String conceptId;
    private Date effectiveDate;
    private int mapStatus;

    public RdbmsRead2ToSnomedMap() {}

    public RdbmsRead2ToSnomedMap(Read2ToSnomedMap proxy) {
        this.mapId = proxy.getMapId();
        this.readCode = proxy.getReadCode();
        this.termCode = proxy.getTermCode();
        this.conceptId = proxy.getConceptId();
        this.effectiveDate = proxy.getEffectiveDate();
        this.mapStatus = proxy.getMapStatus();
    }

    @Id
    @Column(name = "map_id", nullable = false)
    public String getMapId() {
        return mapId;
    }
    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    @Column(name = "read_code", nullable = false)
    public String getReadCode() {
        return readCode;
    }
    public void setReadCode(String readCode) {
        this.readCode = readCode;
    }

    @Column(name = "term_code", nullable = false)
    public String getTermCode() {
        return termCode;
    }
    public void setTermCode(String termCode) {
        this.termCode = termCode;
    }

    @Column(name = "concept_id", nullable = false)
    public String getConceptId() {
        return conceptId;
    }
    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

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
}

