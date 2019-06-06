package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingConditionCdsCount {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private String susRecordType;
    private String cdsUniqueIdentifier;
    private int conditionCount;
    private ResourceFieldMappingAudit audit = null;

    @Override
    public int hashCode() {

        //only calculate the hash from non-primary key fields
        return Objects.hash(conditionCount);
    }

    @Override
    public String toString() {
        return "CDS Condition Count: ["
                + "exchangeId=" + exchangeId + ", "
                + "dtReceived=" + dtReceived + ", "
                + "recordChecksum=" + recordChecksum + ", "
                + "susRecordType=" + susRecordType + ", "
                + "cdsUniqueIdentifier=" + cdsUniqueIdentifier + ", "
                + "conditionCount=" + conditionCount + ", "
                + "audit=" + audit + "]";
    }

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

    public int getRecordChecksum() {
        return recordChecksum;
    }

    public void setRecordChecksum(int recordChecksum) {
        this.recordChecksum = recordChecksum;
    }

    public String getSusRecordType() {
        return susRecordType;
    }

    public void setSusRecordType(String susRecordType) {
        this.susRecordType = susRecordType;
    }

    public String getCdsUniqueIdentifier() {
        return cdsUniqueIdentifier;
    }

    public void setCdsUniqueIdentifier(String cdsUniqueIdentifier) {
        this.cdsUniqueIdentifier = cdsUniqueIdentifier;
    }

    public int getConditionCount() {
        return conditionCount;
    }

    public void setConditionCount(int conditionCount) {
        this.conditionCount = conditionCount;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}
