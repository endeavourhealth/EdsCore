package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;

public class StagingClinicalEventTarget {

    private String exchangeId;
    private String uniqueId;
    private long eventId;
    private boolean isDeleted;
    private int personId;
    private Integer encounterId;
    private Integer orderId;
    private Integer parentEventId;
    private String eventCd;
    private String codeDispTxt;
    private String lookupEventCode;
    private String lookupEventTerm;
    private Date eventStartDtTm;
    private Date eventEndDtTm;
    private Date clinicallySignificantDtTm;
    private Integer eventClassCd;
    private String lookupEventClass;
    private Integer eventResultStatusCd;
    private String lookupEventResultStatus;
    private String eventResultTxt;
    private Integer eventResultNbr;
    private String comparator;
    private Double processedNumericResult;
    private Date eventResultDt;
    private Integer normalcyCd;
    private String lookupNormalcy;
    private String normalRangeLowTxt;
    private Double normalRangeLowValue;
    private String normalRangeHighTxt;
    private Double normalRangeHighValue;
    private Date eventPerformedDtTm;
    private Integer eventPerformedPrsnlId;
    private String eventTag;
    private String eventTitleTxt;
    private Integer eventResultUnitsCd;
    private String lookupEventResultsUnitsCode;
    private Integer recordStatusCd;
    private String lookupRecordStatusCode;
    private String lookupMrn;
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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getParentEventId() {
        return parentEventId;
    }

    public void setParentEventId(Integer parentEventId) {
        this.parentEventId = parentEventId;
    }

    public String getEventCd() {
        return eventCd;
    }

    public void setEventCd(String eventCd) {
        this.eventCd = eventCd;
    }

    public String getCodeDispTxt() {
        return codeDispTxt;
    }

    public void setCodeDispTxt(String codeDispTxt) {
        this.codeDispTxt = codeDispTxt;
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

    public Date getEventStartDtTm() {
        return eventStartDtTm;
    }

    public void setEventStartDtTm(Date eventStartDtTm) {
        this.eventStartDtTm = eventStartDtTm;
    }

    public Date getEventEndDtTm() {
        return eventEndDtTm;
    }

    public void setEventEndDtTm(Date eventEndDtTm) {
        this.eventEndDtTm = eventEndDtTm;
    }

    public Date getClinicallySignificantDtTm() {
        return clinicallySignificantDtTm;
    }

    public void setClinicallySignificantDtTm(Date clinicallySignificantDtTm) {
        this.clinicallySignificantDtTm = clinicallySignificantDtTm;
    }

    public Integer getEventClassCd() {
        return eventClassCd;
    }

    public void setEventClassCd(Integer eventClassCd) {
        this.eventClassCd = eventClassCd;
    }

    public String getLookupEventClass() {
        return lookupEventClass;
    }

    public void setLookupEventClass(String lookupEventClass) {
        this.lookupEventClass = lookupEventClass;
    }

    public Integer getEventResultStatusCd() {
        return eventResultStatusCd;
    }

    public void setEventResultStatusCd(Integer eventResultStatusCd) {
        this.eventResultStatusCd = eventResultStatusCd;
    }

    public String getLookupEventResultStatus() {
        return lookupEventResultStatus;
    }

    public void setLookupEventResultStatus(String lookupEventResultStatus) {
        this.lookupEventResultStatus = lookupEventResultStatus;
    }

    public String getEventResultTxt() {
        return eventResultTxt;
    }

    public void setEventResultTxt(String eventResultTxt) {
        this.eventResultTxt = eventResultTxt;
    }

    public Integer getEventResultNbr() {
        return eventResultNbr;
    }

    public void setEventResultNbr(Integer eventResultNbr) {
        this.eventResultNbr = eventResultNbr;
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

    public Date getEventResultDt() {
        return eventResultDt;
    }

    public void setEventResultDt(Date eventResultDt) {
        this.eventResultDt = eventResultDt;
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

    public String getNormalRangeLowTxt() {
        return normalRangeLowTxt;
    }

    public void setNormalRangeLowTxt(String normalRangeLowTxt) {
        this.normalRangeLowTxt = normalRangeLowTxt;
    }

    public Double getNormalRangeLowValue() {
        return normalRangeLowValue;
    }

    public void setNormalRangeLowValue(Double normalRangeLowValue) {
        this.normalRangeLowValue = normalRangeLowValue;
    }

    public String getNormalRangeHighTxt() {
        return normalRangeHighTxt;
    }

    public void setNormalRangeHighTxt(String normalRangeHighTxt) {
        this.normalRangeHighTxt = normalRangeHighTxt;
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

    public String getEventTag() {
        return eventTag;
    }

    public void setEventTag(String eventTag) {
        this.eventTag = eventTag;
    }

    public String getEventTitleTxt() {
        return eventTitleTxt;
    }

    public void setEventTitleTxt(String eventTitleTxt) {
        this.eventTitleTxt = eventTitleTxt;
    }

    public Integer getEventResultUnitsCd() {
        return eventResultUnitsCd;
    }

    public void setEventResultUnitsCd(Integer eventResultUnitsCd) {
        this.eventResultUnitsCd = eventResultUnitsCd;
    }

    public String getLookupEventResultsUnitsCode() {
        return lookupEventResultsUnitsCode;
    }

    public void setLookupEventResultsUnitsCode(String lookupEventResultsUnitsCode) {
        this.lookupEventResultsUnitsCode = lookupEventResultsUnitsCode;
    }

    public Integer getRecordStatusCd() {
        return recordStatusCd;
    }

    public void setRecordStatusCd(Integer recordStatusCd) {
        this.recordStatusCd = recordStatusCd;
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
