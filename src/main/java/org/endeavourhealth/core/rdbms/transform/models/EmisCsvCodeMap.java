package org.endeavourhealth.core.rdbms.transform.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "emis_csv_code_map", schema = "public", catalog = "transform")
public class EmisCsvCodeMap  implements Serializable {

    private String dataSharingAgreementGuid = null;
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

    @Id
    @Column(name = "data_sharing_agreement_guid", nullable = false)
    public String getDataSharingAgreementGuid() {
        return dataSharingAgreementGuid;
    }

    public void setDataSharingAgreementGuid(String dataSharingAgreementGuid) {
        this.dataSharingAgreementGuid = dataSharingAgreementGuid;
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

    @Column(name = "code_type", nullable = false)
    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    @Column(name = "codeable_concept", nullable = false)
    public String getCodeableConcept() {
        return codeableConcept;
    }

    public void setCodeableConcept(String codeableConcept) {
        this.codeableConcept = codeableConcept;
    }

    @Column(name = "read_term", nullable = false)
    public String getReadTerm() {
        return readTerm;
    }

    public void setReadTerm(String readTerm) {
        this.readTerm = readTerm;
    }

    @Column(name = "read_code", nullable = false)
    public String getReadCode() {
        return readCode;
    }

    public void setReadCode(String readCode) {
        this.readCode = readCode;
    }

    @Column(name = "snomed_concept_id", nullable = false)
    public Long getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(Long snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }

    @Column(name = "snomed_description_id", nullable = false)
    public Long getSnomedDescriptionId() {
        return snomedDescriptionId;
    }

    public void setSnomedDescriptionId(Long snomedDescriptionId) {
        this.snomedDescriptionId = snomedDescriptionId;
    }

    @Column(name = "snomed_term", nullable = false)
    public String getSnomedTerm() {
        return snomedTerm;
    }

    public void setSnomedTerm(String snomedTerm) {
        this.snomedTerm = snomedTerm;
    }

    @Column(name = "national_code", nullable = false)
    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    @Column(name = "national_code_category", nullable = false)
    public String getNationalCodeCategory() {
        return nationalCodeCategory;
    }

    public void setNationalCodeCategory(String nationalCodeCategory) {
        this.nationalCodeCategory = nationalCodeCategory;
    }

    @Column(name = "national_code_description", nullable = false)
    public String getNationalCodeDescription() {
        return nationalCodeDescription;
    }

    public void setNationalCodeDescription(String nationalCodeDescription) {
        this.nationalCodeDescription = nationalCodeDescription;
    }

    @Column(name = "parent_code_id", nullable = false)
    public Long getParentCodeId() {
        return parentCodeId;
    }

    public void setParentCodeId(Long parentCodeId) {
        this.parentCodeId = parentCodeId;
    }
}
