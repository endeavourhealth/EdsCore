package org.endeavourhealth.core.database.dal.publisherStaging.models;

import java.util.Date;
import java.util.Objects;

public class StagingCdsCount {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private String susRecordType;
    private String cdsUniqueIdentifier;
    private int procedureCount;

    @Override
    public int hashCode() {

        return Objects.hash(susRecordType,
                cdsUniqueIdentifier,
                procedureCount);
    }

    @Override
    public String toString() {
        return "CDS Count: ["
                + "exchangeId=" + exchangeId + ", "
                + "dtReceived=" + dtReceived + ", "
                + "recordChecksum=" + recordChecksum + ", "
                + "susRecordType=" + susRecordType + ", "
                + "cdsUniqueIdentifier=" + cdsUniqueIdentifier + ", "
                + "procedureCount=" + procedureCount + "]";
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

    public int getProcedureCount() {
        return procedureCount;
    }

    public void setProcedureCount(int procedureCount) {
        this.procedureCount = procedureCount;
    }


}
