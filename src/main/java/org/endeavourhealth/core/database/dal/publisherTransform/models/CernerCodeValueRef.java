package org.endeavourhealth.core.database.dal.publisherTransform.models;

import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsCernerCodeValueRef;

import java.sql.Date;

public class CernerCodeValueRef {
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

    public CernerCodeValueRef(RdbmsCernerCodeValueRef r) {
        this.codeValueCd = r.getCodeValueCd();
        this.date = r.getDate();
        this.activeInd = r.getActiveInd();
        this.codeDescTxt = r.getCodeDescTxt();
        this.codeDispTxt = r.getCodeDispTxt();
        this.codeMeaningTxt = r.getCodeMeaningTxt();
        this.codeSetNbr = r.getCodeSetNbr();
        this.codeSetDescTxt = r.getCodeSetDescTxt();
        this.aliasNhsCdAlias = r.getAliasNhsCdAlias();
        this.serviceId = r.getServiceId();
        this.auditJson = r.getAuditJson();
    }

    public long getCodeValueCd() {
        return codeValueCd;
    }

    public void setCodeValueCd(long codeValueCd) {
        this.codeValueCd = codeValueCd;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public byte getActiveInd() {
        return activeInd;
    }

    public void setActiveInd(byte activeInd) {
        this.activeInd = activeInd;
    }

    public String getCodeDescTxt() {
        return codeDescTxt;
    }

    public void setCodeDescTxt(String codeDescTxt) {
        this.codeDescTxt = codeDescTxt;
    }

    public String getCodeDispTxt() {
        return codeDispTxt;
    }

    public void setCodeDispTxt(String codeDispTxt) {
        this.codeDispTxt = codeDispTxt;
    }

    public String getCodeMeaningTxt() {
        return codeMeaningTxt;
    }

    public void setCodeMeaningTxt(String codeMeaningTxt) {
        this.codeMeaningTxt = codeMeaningTxt;
    }

    public Long getCodeSetNbr() {
        return codeSetNbr;
    }

    public void setCodeSetNbr(Long codeSetNbr) {
        this.codeSetNbr = codeSetNbr;
    }

    public String getCodeSetDescTxt() {
        return codeSetDescTxt;
    }

    public void setCodeSetDescTxt(String codeSetDescTxt) {
        this.codeSetDescTxt = codeSetDescTxt;
    }

    public String getAliasNhsCdAlias() {
        return aliasNhsCdAlias;
    }

    public void setAliasNhsCdAlias(String aliasNhsCdAlias) {
        this.aliasNhsCdAlias = aliasNhsCdAlias;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }
}
