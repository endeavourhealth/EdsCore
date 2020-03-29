package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;

public class StagingClinicalEventTarget {

    private String exchangeId;
    private String uniqueId;
    private boolean isDeleted;
    private long eventId;
    private int personId;
    private Integer encounterId;
    private Long orderId;
    private Long parentEventId;
    private String lookupEventCode;
    private String lookupEventTerm;
    private Date clinicallySignificantDtTm;
    private Double processedNumericResult;
    private String comparator;
    private Integer normalcyCd;
    private String lookupNormalcy;
    private Double normalRangeLowValue;
    private Double normalRangeHighValue;
    private Date eventPerformedDtTm;
    private Integer eventPerformedPrsnlId;
    private String eventTitleTxt;
    private String lookupEventResultsUnitsCode;
    private String lookupRecordStatusCode;
    private String lookupMrn;
    private String eventResultTxt;
    private String lookupResultTxt;
    private ResourceFieldMappingAudit auditJson;
    private Boolean isConfidential;

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public Integer getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(Integer encounterId) {
        this.encounterId = encounterId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getParentEventId() {
        return parentEventId;
    }

    public void setParentEventId(Long parentEventId) {
        this.parentEventId = parentEventId;
    }

    public String getLookupEventCode() {
        return lookupEventCode;
    }

    public void setLookupEventCode(String lookupEventCode) {
        this.lookupEventCode = lookupEventCode;
    }

    public String getLookupEventTerm() {
        return lookupEventTerm;
    }

    public void setLookupEventTerm(String lookupEventTerm) {
        this.lookupEventTerm = lookupEventTerm;
    }

    public Date getClinicallySignificantDtTm() {
        return clinicallySignificantDtTm;
    }

    public void setClinicallySignificantDtTm(Date clinicallySignificantDtTm) {
        this.clinicallySignificantDtTm = clinicallySignificantDtTm;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public Double getProcessedNumericResult() {
        return processedNumericResult;
    }

    public void setProcessedNumericResult(Double processedNumericResult) {
        this.processedNumericResult = processedNumericResult;
    }

    public Integer getNormalcyCd() {
        return normalcyCd;
    }

    public void setNormalcyCd(Integer normalcyCd) {
        this.normalcyCd = normalcyCd;
    }

    public String getLookupNormalcy() {
        return lookupNormalcy;
    }

    public void setLookupNormalcy(String lookupNormalcy) {
        this.lookupNormalcy = lookupNormalcy;
    }

    public Double getNormalRangeLowValue() {
        return normalRangeLowValue;
    }

    public void setNormalRangeLowValue(Double normalRangeLowValue) {
        this.normalRangeLowValue = normalRangeLowValue;
    }

    public Double getNormalRangeHighValue() {
        return normalRangeHighValue;
    }

    public void setNormalRangeHighValue(Double normalRangeHighValue) {
        this.normalRangeHighValue = normalRangeHighValue;
    }

    public Date getEventPerformedDtTm() {
        return eventPerformedDtTm;
    }

    public void setEventPerformedDtTm(Date eventPerformedDtTm) {
        this.eventPerformedDtTm = eventPerformedDtTm;
    }

    public Integer getEventPerformedPrsnlId() {
        return eventPerformedPrsnlId;
    }

    public void setEventPerformedPrsnlId(Integer eventPerformedPrsnlId) {
        this.eventPerformedPrsnlId = eventPerformedPrsnlId;
    }

    public String getEventTitleTxt() {
        return eventTitleTxt;
    }

    public void setEventTitleTxt(String eventTitleTxt) {
        this.eventTitleTxt = eventTitleTxt;
    }

    public String getLookupEventResultsUnitsCode() {
        return lookupEventResultsUnitsCode;
    }

    public void setLookupEventResultsUnitsCode(String lookupEventResultsUnitsCode) {
        this.lookupEventResultsUnitsCode = lookupEventResultsUnitsCode;
    }

    public String getLookupRecordStatusCode() {
        return lookupRecordStatusCode;
    }

    public void setLookupRecordStatusCode(String lookupRecordStatusCode) {
        this.lookupRecordStatusCode = lookupRecordStatusCode;
    }

    public String getLookupMrn() {
        return lookupMrn;
    }

    public void setLookupMrn(String lookupMrn) {
        this.lookupMrn = lookupMrn;
    }

    public String getEventResultTxt() {return eventResultTxt;}

    public void setEventResultTxt(String eventResultTxt) {this.eventResultTxt = eventResultTxt;}

    public String getLookupResultTxt() {return lookupResultTxt;}

    public void setLookupResultTxt(String lookupResultTxt) {this.lookupResultTxt = lookupResultTxt;}

    public ResourceFieldMappingAudit getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(ResourceFieldMappingAudit auditJson) {
        this.auditJson = auditJson;
    }

    public Boolean getConfidential() {
        return isConfidential;
    }

    public void setConfidential(Boolean confidential) {
        isConfidential = confidential;
    }
}
