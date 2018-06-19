package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerCodeValueRef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "cerner_code_value_ref")
public class RdbmsCernerCodeValueRef implements Serializable {
    private String codeValueCd;
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

    public RdbmsCernerCodeValueRef() {}

    public RdbmsCernerCodeValueRef(CernerCodeValueRef proxy) throws Exception {
        this.codeValueCd = proxy.getCodeValueCd();
        this.date = proxy.getDate();
        this.activeInd = proxy.getActiveInd();
        this.codeDescTxt = proxy.getCodeDescTxt();
        this.codeDispTxt = proxy.getCodeDispTxt();
        this.codeMeaningTxt = proxy.getCodeMeaningTxt();
        this.codeSetNbr = proxy.getCodeSetNbr();
        this.codeSetDescTxt = proxy.getCodeSetDescTxt();
        this.aliasNhsCdAlias = proxy.getAliasNhsCdAlias();
        this.serviceId = proxy.getServiceId().toString();
        if (proxy.getAudit() != null) {
            this.auditJson = proxy.getAudit().writeToJson();
        }
    }

    @Id
    @Column(name = "code_value_cd", nullable = false)
    public String getCodeValueCd() {
        return codeValueCd;
    }

    public void setCodeValueCd(String codeValueCd) {
        this.codeValueCd = codeValueCd;
    }

    @Column(name = "date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "active_ind", nullable = false)
    public byte getActiveInd() {
        return activeInd;
    }

    public void setActiveInd(byte activeInd) {
        this.activeInd = activeInd;
    }

    @Column(name = "code_desc_txt", nullable = false)
    public String getCodeDescTxt() {
        return codeDescTxt;
    }

    public void setCodeDescTxt(String codeDescTxt) {
        this.codeDescTxt = codeDescTxt;
    }

    @Column(name = "code_disp_txt", nullable = false)
    public String getCodeDispTxt() {
        return codeDispTxt;
    }

    public void setCodeDispTxt(String codeDispTxt) {
        this.codeDispTxt = codeDispTxt;
    }

    @Column(name = "code_meaning_txt", nullable = false)
    public String getCodeMeaningTxt() {
        return codeMeaningTxt;
    }

    public void setCodeMeaningTxt(String codeMeaningTxt) {
        this.codeMeaningTxt = codeMeaningTxt;
    }

    @Id
    @Column(name = "code_set_nbr", nullable = false)
    public Long getCodeSetNbr() {
        return codeSetNbr;
    }

    public void setCodeSetNbr(Long codeSetNbr) {
        this.codeSetNbr = codeSetNbr;
    }

    @Column(name = "code_set_desc_txt", nullable = false)
    public String getCodeSetDescTxt() {
        return codeSetDescTxt;
    }

    public void setCodeSetDescTxt(String codeSetDescTxt) {
        this.codeSetDescTxt = codeSetDescTxt;
    }

    @Column(name = "alias_nhs_cd_alias", nullable = false)
    public String getAliasNhsCdAlias() {
        return aliasNhsCdAlias;
    }

    public void setAliasNhsCdAlias(String aliasNhsCdAlias) {
        this.aliasNhsCdAlias = aliasNhsCdAlias;
    }

    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "audit_json", nullable = true)
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
