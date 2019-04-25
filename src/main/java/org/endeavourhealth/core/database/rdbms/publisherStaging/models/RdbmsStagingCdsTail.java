package org.endeavourhealth.core.database.rdbms.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCdsTail;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "procedure_cds_tail")
public class RdbmsStagingCdsTail {

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
    private String auditJson;

    public RdbmsStagingCdsTail() {}

    public RdbmsStagingCdsTail(StagingCdsTail proxy) throws Exception {

        this.exchangeId = proxy.getExchangeId();
        this.dtReceived = proxy.getDtReceived();
        this.recordChecksum = proxy.getRecordChecksum();
        this.susRecordType = proxy.getSusRecordType();
        this.cdsUniqueIdentifier = proxy.getCdsUniqueIdentifier();
        this.cdsUpdateType = proxy.getCdsUpdateType();
        this.mrn = proxy.getMrn();
        this.nhsNumber = proxy.getNhsNumber();
        this.personId = proxy.getPersonId();
        this.encounterId = proxy.getEncounterId();
        this.responsibleHcpPersonnelId = proxy.getResponsibleHcpPersonnelId();

        if (proxy.getAudit()!= null) {
            this.auditJson = proxy.getAudit().writeToJson();
        }
    }

    @Id
    @Column(name = "exchange_id")
    public String getExchangeId() {
        return exchangeId;
    }
    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Basic
    @Column(name = "dt_received")
    public Date getDtReceived() {
        return dtReceived;
    }
    public void setDtReceived(Date dtReceived) {
        this.dtReceived = dtReceived;
    }

    @Basic
    @Column(name = "record_checksum")
    public int getRecordChecksum() {
        return recordChecksum;
    }
    public void setRecordChecksum(int recordChecksum) {
        this.recordChecksum = recordChecksum;
    }


    @Basic
    @Column(name = "sus_record_type")
    public String getSusRecordType() {
        return susRecordType;
    }
    public void setSusRecordType(String susRecordType) {
        this.susRecordType = susRecordType;
    }

    @Basic
    @Column(name = "cds_unique_identifier")
    public String getCdsUniqueIdentifier() {
        return cdsUniqueIdentifier;
    }
    public void setCdsUniqueIdentifier(String cdsUniqueIdentifier) {
        this.cdsUniqueIdentifier = cdsUniqueIdentifier;
    }

    @Basic
    @Column(name = "cds_update_type")
    public int getCdsUpdateType() {
        return cdsUpdateType;
    }
    public void setCdsUpdateType(int cdsUpdateType) {
        this.cdsUpdateType = cdsUpdateType;
    }

    @Basic
    @Column(name = "mrn")
    public String getMrn() {
        return mrn;
    }
    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    @Basic
    @Column(name = "nhs_number ")
    public String getNhsNumber () {
        return nhsNumber ;
    }
    public void setNhsNumber (String nhsNumber ) {
        this.nhsNumber  = nhsNumber;
    }

    @Basic
    @Column(name = "person_id")
    public int getPersonId  () {
        return personId ;
    }
    public void setPersonId (int personId ) {
        this.personId = personId;
    }

    @Basic
    @Column(name = "encounter_id")
    public int getEncounterId  () {
        return encounterId ;
    }
    public void setEncounterId (int encounterId ) {
        this.encounterId = encounterId;
    }

    @Basic
    @Column(name = "responsible_hcp_personnel_id")
    public int getResponsibleHcpPersonnelId  () {
        return responsibleHcpPersonnelId ;
    }
    public void setResponsibleHcpPersonnelId (int responsibleHcpPersonnelId)
    { this.responsibleHcpPersonnelId = responsibleHcpPersonnelId; }

    @Basic
    @Column(name = "audit_json", nullable = true)
    public String getAuditJson() { return auditJson; }
    public void setAuditJson(String auditJson) {this.auditJson = auditJson; }

    @Override
    public int hashCode() {

        return Objects.hash(susRecordType,
                            cdsUniqueIdentifier,
                            cdsUpdateType,
                            mrn,
                            nhsNumber,
                            personId,
                            encounterId,
                            responsibleHcpPersonnelId);
    }
}
