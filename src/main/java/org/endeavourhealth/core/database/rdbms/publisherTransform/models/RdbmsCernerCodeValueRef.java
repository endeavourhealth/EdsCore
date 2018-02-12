package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "cerner_code_value_ref", schema = "publisher_transform")
public class RdbmsCernerCodeValueRef implements Serializable {
    private long codeValueCd;
    private Date date;
    private byte activeInd;
    private String codeDescTxt;
    private String codeDispTxt;
    private String codeMeaningTxt;
    private Long codeSetNbr;
    private String codeSetDescTxt;
    private String aliasNhsCdAlias;
    private String serviceId;
    private String auditJson;

    @Id
    @Column(name = "code_value_cd")
    public long getCodeValueCd() {
        return codeValueCd;
    }

    public void setCodeValueCd(long codeValueCd) {
        this.codeValueCd = codeValueCd;
    }

    @Basic
    @Column(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Basic
    @Column(name = "active_ind")
    public byte getActiveInd() {
        return activeInd;
    }

    public void setActiveInd(byte activeInd) {
        this.activeInd = activeInd;
    }

    @Basic
    @Column(name = "code_desc_txt")
    public String getCodeDescTxt() {
        return codeDescTxt;
    }

    public void setCodeDescTxt(String codeDescTxt) {
        this.codeDescTxt = codeDescTxt;
    }

    @Basic
    @Column(name = "code_disp_txt")
    public String getCodeDispTxt() {
        return codeDispTxt;
    }

    public void setCodeDispTxt(String codeDispTxt) {
        this.codeDispTxt = codeDispTxt;
    }

    @Basic
    @Column(name = "code_meaning_txt")
    public String getCodeMeaningTxt() {
        return codeMeaningTxt;
    }

    public void setCodeMeaningTxt(String codeMeaningTxt) {
        this.codeMeaningTxt = codeMeaningTxt;
    }

    @Basic
    @Column(name = "code_set_nbr")
    public Long getCodeSetNbr() {
        return codeSetNbr;
    }

    public void setCodeSetNbr(Long codeSetNbr) {
        this.codeSetNbr = codeSetNbr;
    }

    @Basic
    @Column(name = "code_set_desc_txt")
    public String getCodeSetDescTxt() {
        return codeSetDescTxt;
    }

    public void setCodeSetDescTxt(String codeSetDescTxt) {
        this.codeSetDescTxt = codeSetDescTxt;
    }

    @Basic
    @Column(name = "alias_nhs_cd_alias")
    public String getAliasNhsCdAlias() {
        return aliasNhsCdAlias;
    }

    public void setAliasNhsCdAlias(String aliasNhsCdAlias) {
        this.aliasNhsCdAlias = aliasNhsCdAlias;
    }

    @Basic
    @Column(name = "service_id")
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Basic
    @Column(name = "audit_json")
    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsCernerCodeValueRef that = (RdbmsCernerCodeValueRef) o;
        return codeValueCd == that.codeValueCd &&
                activeInd == that.activeInd &&
                Objects.equals(date, that.date) &&
                Objects.equals(codeDescTxt, that.codeDescTxt) &&
                Objects.equals(codeDispTxt, that.codeDispTxt) &&
                Objects.equals(codeMeaningTxt, that.codeMeaningTxt) &&
                Objects.equals(codeSetNbr, that.codeSetNbr) &&
                Objects.equals(codeSetDescTxt, that.codeSetDescTxt) &&
                Objects.equals(aliasNhsCdAlias, that.aliasNhsCdAlias) &&
                Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(auditJson, that.auditJson);
    }

    @Override
    public int hashCode() {

        return Objects.hash(codeValueCd, date, activeInd, codeDescTxt, codeDispTxt, codeMeaningTxt, codeSetNbr, codeSetDescTxt, aliasNhsCdAlias, serviceId, auditJson);
    }
}
