package org.endeavourhealth.core.database.dal.publisherTransform.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsCernerCodeValueRef;

import java.util.Date;
import java.util.UUID;

public class CernerCodeValueRef {

    private String codeValueCd;
    private Date date;
    private byte activeInd;
    private String codeDescTxt;
    private String codeDispTxt;
    private String codeMeaningTxt;
    private Long codeSetNbr;
    private String codeSetDescTxt;
    private String aliasNhsCdAlias;
    private UUID serviceId;
    private ResourceFieldMappingAudit audit = null;

    public CernerCodeValueRef() {}

    public CernerCodeValueRef(RdbmsCernerCodeValueRef proxy) throws Exception {
        this.codeValueCd = proxy.getCodeValueCd();
        this.date = proxy.getDate();
        this.activeInd = proxy.getActiveInd();
        this.codeDescTxt = proxy.getCodeDescTxt();
        this.codeDispTxt = proxy.getCodeDispTxt();
        this.codeMeaningTxt = proxy.getCodeMeaningTxt();
        this.codeSetNbr = proxy.getCodeSetNbr();
        this.codeSetDescTxt = proxy.getCodeSetDescTxt();
        this.aliasNhsCdAlias = proxy.getAliasNhsCdAlias();
        this.serviceId = UUID.fromString(proxy.getServiceId());
        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }
    public CernerCodeValueRef(String codeValueCd,
                              Date date,
                              byte activeInd,
                              String codeDescTxt,
                              String codeDispTxt,
                              String codeMeaningTxt,
                              Long codeSetNbr,
                              String codeSetDescTxt,
                              String aliasNhsCdAlias,
                              UUID serviceId,
                              ResourceFieldMappingAudit audit) {
        this.codeValueCd = codeValueCd;
        this.date = date;
        this.activeInd = activeInd;
        this.codeDescTxt = codeDescTxt;
        this.codeDispTxt = codeDispTxt;
        this.codeMeaningTxt = codeMeaningTxt;
        this.codeSetNbr = codeSetNbr;
        this.codeSetDescTxt = codeSetDescTxt;
        this.aliasNhsCdAlias = aliasNhsCdAlias;
        this.serviceId = serviceId;
        this.audit = audit;
    }



    public String getCodeValueCd() {
        return codeValueCd;
    }

    public void setCodeValueCd(String codeValueCd) {
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

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}
