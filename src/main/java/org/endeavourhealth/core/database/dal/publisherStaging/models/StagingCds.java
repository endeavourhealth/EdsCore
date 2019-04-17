package org.endeavourhealth.core.database.dal.publisherStaging.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingCds;

import java.util.Date;

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
    private Date procedureDate;
    private String procedureOpcsCode;
    private String procedureOpcsTerm;
    private int procedureSeqNbr;
    private String consultantCode;
    private String location;
    private int personId;
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
        this.procedureDate = proxy.getProcedureDate();
        this.procedureOpcsCode = proxy.getProcedureOpcsCode();
        this.procedureOpcsTerm = proxy.getProcedureOpcsTerm();
        this.procedureSeqNbr = proxy.getProcedureSeqNbr();
        this.consultantCode = proxy.getConsultantCode();
        this.location = proxy.getLocation();
        this.personId = proxy.getPersonId();
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

    public String getProcedureOpcsTerm  () {
        return procedureOpcsTerm ;
    }
    public void setProcedureOpcsTerm (String procedureOpcsTerm ) {
        this.procedureOpcsTerm = procedureOpcsTerm;
    }

    public int getProcedureSeqNbr  () {
        return procedureSeqNbr ;
    }
    public void setProcedureSeqNbr (int procedureSeqNbr ) {
        this.procedureSeqNbr = procedureSeqNbr;
    }

    public String getConsultantCode  () {
        return consultantCode ;
    }
    public void setConsultantCode (String consultantCode ) {
        this.consultantCode = consultantCode;
    }

    public String getLocation  () {
        return location ;
    }
    public void setLocation (String location ) {
        this.location = location;
    }

    public int getPersonId  () {
        return personId ;
    }
    public void setPersonId (int personId ) {
        this.personId = personId;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }
    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}