package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingClinicalEvent {
    private String exchangeId;
    private Date dtReceived;
    private long recordChecksum;
    private long eventId;
    private boolean activeInd;
    private int personId;
    private Integer encounterId;
    private Long orderId;
    private Long parentEventId;
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

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Date getDtReceived() {
        return dtReceived;
    }

    public void setDtReceived(Date dtReceived) {
        this.dtReceived = dtReceived;
    }

    public long getRecordChecksum() {
        return recordChecksum;
    }

    public void setRecordChecksum(long recordChecksum) {
        this.recordChecksum = recordChecksum;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public boolean isActiveInd() {
        return activeInd;
    }

    public void setActiveInd(boolean activeInd) {
        this.activeInd = activeInd;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StagingClinicalEvent that = (StagingClinicalEvent) o;
        return recordChecksum == that.recordChecksum &&
                eventId == that.eventId &&
                activeInd == that.activeInd &&
                personId == that.personId &&
                Objects.equals(exchangeId, that.exchangeId) &&
                Objects.equals(dtReceived, that.dtReceived) &&
                Objects.equals(encounterId, that.encounterId) &&
                Objects.equals(orderId, that.orderId) &&
                Objects.equals(parentEventId, that.parentEventId) &&
                Objects.equals(eventCd, that.eventCd) &&
                Objects.equals(codeDispTxt, that.codeDispTxt) &&
                Objects.equals(eventStartDtTm, that.eventStartDtTm) &&
                Objects.equals(eventEndDtTm, that.eventEndDtTm) &&
                Objects.equals(clinicallySignificantDtTm, that.clinicallySignificantDtTm) &&
                Objects.equals(eventClassCd, that.eventClassCd) &&
                Objects.equals(eventResultStatusCd, that.eventResultStatusCd) &&
                Objects.equals(eventResultTxt, that.eventResultTxt) &&
                Objects.equals(eventResultNbr, that.eventResultNbr) &&
                Objects.equals(eventResultDt, that.eventResultDt) &&
                Objects.equals(normalcyCd, that.normalcyCd) &&
                Objects.equals(normalRangeLowTxt, that.normalRangeLowTxt) &&
                Objects.equals(normalRangeHighTxt, that.normalRangeHighTxt) &&
                Objects.equals(eventPerformedDtTm, that.eventPerformedDtTm) &&
                Objects.equals(eventPerformedPrsnlId, that.eventPerformedPrsnlId) &&
                Objects.equals(eventTag, that.eventTag) &&
                Objects.equals(eventTitleTxt, that.eventTitleTxt) &&
                Objects.equals(eventResultUnitsCd, that.eventResultUnitsCd) &&
                Objects.equals(recordStatusCd, that.recordStatusCd) &&
                Objects.equals(auditJson, that.auditJson);
    }

    @Override
    public int hashCode() {

        return Objects.hash(exchangeId, dtReceived, recordChecksum, eventId, activeInd, personId, encounterId, orderId, parentEventId, eventCd, codeDispTxt, eventStartDtTm, eventEndDtTm, clinicallySignificantDtTm, eventClassCd, eventResultStatusCd, eventResultTxt, eventResultNbr, eventResultDt, normalcyCd, normalRangeLowTxt, normalRangeHighTxt, eventPerformedDtTm, eventPerformedPrsnlId, eventTag, eventTitleTxt, eventResultUnitsCd, recordStatusCd, auditJson);
    }
}
