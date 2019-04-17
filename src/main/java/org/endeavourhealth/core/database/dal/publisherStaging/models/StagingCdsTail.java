package org.endeavourhealth.core.database.dal.publisherStaging.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingCdsTail;

import java.util.Date;

public class StagingCdsTail {
    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private String susRecordType;
    private String cdsUniqueIdentifier;
    private int cdsUpdateType;
    private String mrn;
    private String nhsNumber;
    private int personId;
    private int encounterId;
    private int responsibleHcpPersonnelId;
    private ResourceFieldMappingAudit audit = null;

    public StagingCdsTail() {}

    public StagingCdsTail(RdbmsStagingCdsTail proxy) throws Exception {
        this.exchangeId = proxy.getExchangeId();
        this.dtReceived = proxy.getDTReceived();
        this.recordChecksum = proxy.getRecordChecksum();
        this.susRecordType = proxy.getSusRecordType();
        this.cdsUniqueIdentifier = proxy.getCdsUniqueIdentifier();
        this.cdsUpdateType = proxy.getCdsUpdateType();
        this.mrn = proxy.getMrn();
        this.nhsNumber = proxy.getNhsNumber();
        this.personId = proxy.getPersonId();
        this.encounterId = proxy.getEncounterId();
        this.responsibleHcpPersonnelId = proxy.getResponsibleHcpPersonnelId();

        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }

//    public StagingCds(long rowId,
//                      long multiLexProductId,
//                      String ctv3ReadCode,
//                      String ctv3ReadTerm,
//                      ResourceFieldMappingAudit audit) {
//        this.rowId = rowId;
//        this.multiLexProductId = multiLexProductId;
//        this.ctv3ReadCode = ctv3ReadCode;
//        this.ctv3ReadTerm = ctv3ReadTerm;
//        this.audit = audit;
//    }

    public String getExchangeId() {
        return exchangeId;
    }
    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Date getDTReceived() {
        return dtReceived;
    }
    public void setDTReceived(Date dtReceived) {
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
    public void setCdsUniqueIdentifierm(String cdsUniqueIdentifier) {
        this.cdsUniqueIdentifier = cdsUniqueIdentifier;
    }

    public int getCdsUpdateType() {
        return cdsUpdateType;
    }
    public void setCdsUpdateType(int cdsUpdateType) {
        this.cdsUpdateType = cdsUpdateType;
    }

    public String getMrn() {
        return mrn;
    }
    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public String getNhsNumber () {
        return nhsNumber ;
    }
    public void setNhsNumber (String nhsNumber ) {
        this.nhsNumber  = nhsNumber;
    }

    public int getPersonId  () {
        return personId ;
    }
    public void setPersonId (int personId ) {
        this.personId = personId;
    }

    public int getEncounterId  () {
        return encounterId ;
    }
    public void setEncounterId (int encounterId ) {
        this.encounterId = encounterId;
    }

    public int getResponsibleHcpPersonnelId () {
        return responsibleHcpPersonnelId ;
    }
    public void setResponsibleHcpPersonnelId (int responsibleHcpPersonnelId) {
        this.responsibleHcpPersonnelId = responsibleHcpPersonnelId;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }
    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}