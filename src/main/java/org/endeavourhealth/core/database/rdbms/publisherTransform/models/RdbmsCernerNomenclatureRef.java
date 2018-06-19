package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerNomenclatureRef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "cerner_nomenclature_ref")
public class RdbmsCernerNomenclatureRef implements Serializable {

    private String serviceId;
    private long nomenclatureId;
    private boolean active;
    private String mnemonicText;
    private String valueText;
    private String displayText;
    private String descriptionText;
    private Long nomenclatureTypeCode;
    private Long vocabularyCode;
    private String conceptIdentifier;
    private String auditJson;

    public RdbmsCernerNomenclatureRef() {}

    public RdbmsCernerNomenclatureRef(CernerNomenclatureRef proxy) throws Exception {
        this.serviceId = proxy.getServiceId().toString();
        this.nomenclatureId = proxy.getNomenclatureId();
        this.active = proxy.isActive();
        this.mnemonicText = proxy.getMnemonicText();
        this.valueText = proxy.getValueText();
        this.displayText = proxy.getDisplayText();
        this.descriptionText = proxy.getDescriptionText();
        this.nomenclatureTypeCode = proxy.getNomenclatureTypeCode();
        this.vocabularyCode = proxy.getVocabularyCode();
        this.conceptIdentifier = proxy.getConceptIdentifier();
        if (proxy.getAudit() != null) {
            this.auditJson = proxy.getAudit().writeToJson();
        }
    }


    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "nomenclature_id", nullable = false)
    public long getNomenclatureId() {
        return nomenclatureId;
    }

    public void setNomenclatureId(long nomenclatureId) {
        this.nomenclatureId = nomenclatureId;
    }

    @Column(name = "active")
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(name = "mneomonic_text")
    public String getMnemonicText() {
        return mnemonicText;
    }

    public void setMnemonicText(String mnemonicText) {
        this.mnemonicText = mnemonicText;
    }

    @Column(name = "value_text")
    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    @Column(name = "display_text")
    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    @Column(name = "description_text")
    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    @Column(name = "nomenclature_type_code")
    public Long getNomenclatureTypeCode() {
        return nomenclatureTypeCode;
    }

    public void setNomenclatureTypeCode(Long nomenclatureTypeCode) {
        this.nomenclatureTypeCode = nomenclatureTypeCode;
    }

    @Column(name = "vocabulary_code")
    public Long getVocabularyCode() {
        return vocabularyCode;
    }

    public void setVocabularyCode(Long vocabularyCode) {
        this.vocabularyCode = vocabularyCode;
    }

    @Column(name = "concept_identifier")
    public String getConceptIdentifier() {
        return conceptIdentifier;
    }

    public void setConceptIdentifier(String conceptIdentifier) {
        this.conceptIdentifier = conceptIdentifier;
    }

    @Column(name = "audit_json")
    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }

}
