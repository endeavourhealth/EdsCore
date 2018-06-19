package org.endeavourhealth.core.database.dal.publisherTransform.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsCernerNomenclatureRef;

import java.util.UUID;

public class CernerNomenclatureRef {

    private UUID serviceId;
    private long nomenclatureId;
    private boolean active;
    private String mnemonicText;
    private String valueText;
    private String displayText;
    private String descriptionText;
    private Long nomenclatureTypeCode;
    private Long vocabularyCode;
    private String conceptIdentifier;
    private ResourceFieldMappingAudit audit;

    public CernerNomenclatureRef() { }

    public CernerNomenclatureRef(RdbmsCernerNomenclatureRef proxy) throws Exception {
        this.serviceId = UUID.fromString(proxy.getServiceId());
        this.nomenclatureId = proxy.getNomenclatureId();
        this.active = proxy.isActive();
        this.mnemonicText = proxy.getMnemonicText();
        this.valueText = proxy.getValueText();
        this.displayText = proxy.getDisplayText();
        this.descriptionText = proxy.getDescriptionText();
        this.nomenclatureTypeCode = proxy.getNomenclatureTypeCode();
        this.vocabularyCode = proxy.getVocabularyCode();
        this.conceptIdentifier = proxy.getConceptIdentifier();
        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public long getNomenclatureId() {
        return nomenclatureId;
    }

    public void setNomenclatureId(long nomenclatureId) {
        this.nomenclatureId = nomenclatureId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getMnemonicText() {
        return mnemonicText;
    }

    public void setMnemonicText(String mnemonicText) {
        this.mnemonicText = mnemonicText;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public Long getNomenclatureTypeCode() {
        return nomenclatureTypeCode;
    }

    public void setNomenclatureTypeCode(Long nomenclatureTypeCode) {
        this.nomenclatureTypeCode = nomenclatureTypeCode;
    }

    public Long getVocabularyCode() {
        return vocabularyCode;
    }

    public void setVocabularyCode(Long vocabularyCode) {
        this.vocabularyCode = vocabularyCode;
    }

    public String getConceptIdentifier() {
        return conceptIdentifier;
    }

    public void setConceptIdentifier(String conceptIdentifier) {
        this.conceptIdentifier = conceptIdentifier;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}
