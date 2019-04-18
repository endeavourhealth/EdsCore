package org.endeavourhealth.core.database.dal.publisherStaging.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingCds;

import java.util.Date;
import java.util.Objects;

public class StagingCds {
    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private String susRecordType;
    private String cdsUniqueIdentifier;
    private int cdsUpdateType;
    private String mrn;
    private String nhsNumber;
    private Date dateOfBirth;
    private String consultantCode;
    private Date procedureDate;
    private String procedureOpcsCode;
    private int procedureSeqNbr;
    private String primaryProcedureOpcsCode;
    private String lookupProcedureOpcsTerm;
    private int lookupPersonId ;
    private int lookupConsultantPersonnelId;

    private ResourceFieldMappingAudit audit = null;

    public StagingCds() {}

    public StagingCds(RdbmsStagingCds proxy) throws Exception {
        this.exchangeId = proxy.getExchangeId();
        this.dtReceived = proxy.getDTReceived();
        this.recordChecksum = proxy.getRecordChecksum();
        this.susRecordType = proxy.getSusRecordType();
        this.cdsUniqueIdentifier = proxy.getCdsUniqueIdentifier();
        this.cdsUpdateType = proxy.getCdsUpdateType();
        this.mrn = proxy.getMrn();
        this.nhsNumber = proxy.getNhsNumber();
        this.dateOfBirth = proxy.getDateOfBirth();
        this.consultantCode = proxy.getConsultantCode();
        this.procedureDate = proxy.getProcedureDate();
        this.procedureOpcsCode = proxy.getProcedureOpcsCode();
        this.procedureSeqNbr = proxy.getProcedureSeqNbr();
        this.primaryProcedureOpcsCode = proxy.getPrimaryProcedureOpcsCode();
        this.lookupProcedureOpcsTerm = proxy.getLookupProcedureOpcsTerm();
        this.lookupPersonId = proxy.getLookupPersonId();
        this.lookupConsultantPersonnelId = proxy.getLookupConsultantPersonnelId();

        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }

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

    public Date getDateOfBirth  () {
        return dateOfBirth ;
    }
    public void setDateOfBirth (Date dateOfBirth ) {
        this.dateOfBirth  = dateOfBirth;
    }

    public String getConsultantCode  () {
        return consultantCode ;
    }
    public void setConsultantCode (String consultantCode ) {
        this.consultantCode = consultantCode;
    }

    public Date getProcedureDate () {
        return procedureDate;
    }
    public void setProcedureDate (Date procedureDate ) {
        this.procedureDate  = procedureDate;
    }

    public String getProcedureOpcsCode  () {
        return procedureOpcsCode ;
    }
    public void setProcedureOpcsCode (String procedureOpcsCode ) {
        this.procedureOpcsCode = procedureOpcsCode;
    }

    public int getProcedureSeqNbr  () {
        return procedureSeqNbr ;
    }
    public void setProcedureSeqNbr (int procedureSeqNbr ) {
        this.procedureSeqNbr = procedureSeqNbr;
    }

    public String getPrimaryProcedureOpcsCode () {
        return primaryProcedureOpcsCode ;
    }
    public void setPrimaryProcedureOpcsCode (String primaryProcedureOpcsCode ) {this.primaryProcedureOpcsCode = primaryProcedureOpcsCode; }

    public String getLookupProcedureOpcsTerm () {
        return lookupProcedureOpcsTerm ;
    }
    public void setLookupProcedureOpcsTerm (String lookupProcedureOpcsTerm ) {this.lookupProcedureOpcsTerm = lookupProcedureOpcsTerm; }

    public int getLookupPersonId () {
        return lookupPersonId ;
    }
    public void setLookupPersonId (int lookupPersonId ) {this.lookupPersonId = lookupPersonId; }

    public int getLookupConsultantPersonnelId () {
        return lookupConsultantPersonnelId ;
    }
    public void setLookupConsultantPersonnelId (int lookupConsultantPersonnelId ) {
        this.lookupConsultantPersonnelId = lookupConsultantPersonnelId;}

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }
    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    @Override
    public int hashCode() {

        return Objects.hash(susRecordType,
                cdsUniqueIdentifier,
                cdsUpdateType,
                mrn,
                nhsNumber,
                dateOfBirth,
                consultantCode,
                procedureDate,
                procedureOpcsCode,
                procedureSeqNbr,
                primaryProcedureOpcsCode,
                lookupProcedureOpcsTerm,
                lookupPersonId,
                lookupConsultantPersonnelId);
    }
}