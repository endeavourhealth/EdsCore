package org.endeavourhealth.core.database.rdbms.publisherCommon.models;

import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisCsvCodeMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "emis_csv_code_map")
public class RdbmsEmisCsvCodeMap implements Serializable {

    private boolean medication = false;
    private long codeId = -1;
    private String codeType = null;
    private String codeableConcept = null; //JSON of the FHIR codeable concept
    private String readTerm = null;
    private String readCode = null;
    private Long snomedConceptId = null;
    private Long snomedDescriptionId = null;
    private String snomedTerm = null;
    private String nationalCode = null;
    private String nationalCodeCategory = null;
    private String nationalCodeDescription = null;
    private Long parentCodeId = null;
    private String auditJson = null; //JSON giving the audit details of the resource, so they can be applied when saving to core DB

    public RdbmsEmisCsvCodeMap() { }

    public RdbmsEmisCsvCodeMap(EmisCsvCodeMap proxy) throws Exception {
        this.medication = proxy.isMedication();
        this.codeId = proxy.getCodeId();
        this.codeType = proxy.getCodeType();
        this.codeableConcept = proxy.getCodeableConcept();
        this.readTerm = proxy.getReadTerm();
        this.readCode = proxy.getReadCode();
        this.snomedConceptId = proxy.getSnomedConceptId();
        this.snomedDescriptionId = proxy.getSnomedDescriptionId();
        this.snomedTerm = proxy.getSnomedTerm();
        this.nationalCode = proxy.getNationalCode();
        this.nationalCodeCategory = proxy.getNationalCodeCategory();
        this.nationalCodeDescription = proxy.getNationalCodeDescription();
        this.parentCodeId = proxy.getParentCodeId();

        if (proxy.getAudit() != null) {
            this.auditJson = proxy.getAudit().writeToJson();
        }
    }

    @Id
    @Column(name = "medication", nullable = false)
    public boolean isMedication() {
        return medication;
    }

    public void setMedication(boolean medication) {
        this.medication = medication;
    }

    @Id
    @Column(name = "code_id", nullable = false)
    public long getCodeId() {
        return codeId;
    }

    public void setCodeId(long codeId) {
        this.codeId = codeId;
    }

    @Column(name = "code_type", nullable = true)
    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    @Column(name = "codeable_concept", nullable = true)
    public String getCodeableConcept() {
        return codeableConcept;
    }

    public void setCodeableConcept(String codeableConcept) {
        this.codeableConcept = codeableConcept;
    }

    @Column(name = "read_term", nullable = true)
    public String getReadTerm() {
        return readTerm;
    }

    public void setReadTerm(String readTerm) {
        this.readTerm = readTerm;
    }

    @Column(name = "read_code", nullable = true)
    public String getReadCode() {
        return readCode;
    }

    public void setReadCode(String readCode) {
        this.readCode = readCode;
    }

    @Column(name = "snomed_concept_id", nullable = true)
    public Long getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(Long snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }

    @Column(name = "snomed_description_id", nullable = true)
    public Long getSnomedDescriptionId() {
        return snomedDescriptionId;
    }

    public void setSnomedDescriptionId(Long snomedDescriptionId) {
        this.snomedDescriptionId = snomedDescriptionId;
    }

    @Column(name = "snomed_term", nullable = true)
    public String getSnomedTerm() {
        return snomedTerm;
    }

    public void setSnomedTerm(String snomedTerm) {
        this.snomedTerm = snomedTerm;
    }

    @Column(name = "national_code", nullable = true)
    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    @Column(name = "national_code_category", nullable = true)
    public String getNationalCodeCategory() {
        return nationalCodeCategory;
    }

    public void setNationalCodeCategory(String nationalCodeCategory) {
        this.nationalCodeCategory = nationalCodeCategory;
    }

    @Column(name = "national_code_description", nullable = true)
    public String getNationalCodeDescription() {
        return nationalCodeDescription;
    }

    public void setNationalCodeDescription(String nationalCodeDescription) {
        this.nationalCodeDescription = nationalCodeDescription;
    }

    @Column(name = "parent_code_id", nullable = true)
    public Long getParentCodeId() {
        return parentCodeId;
    }

    public void setParentCodeId(Long parentCodeId) {
        this.parentCodeId = parentCodeId;
    }

    @Column(name = "audit_json", nullable = true)
    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }
}
