package org.endeavourhealth.core.database.dal.publisherTransform.models;

import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsCernerCodeValueRef;

import java.sql.Date;

public class CernerCodeValueRef {

    // Static values to hold the code set values
    public static final Long LOCATION_NAME = 220L;
    public static final Long NHS_NUMBER_STATUS = 29882L;
    public static final Long GENDER = 57L;
    public static final Long ETHNIC_GROUP = 27L;
    public static final Long LANGUAGE = 36L;
    public static final Long RELIGION = 49L;
    public static final Long MARITAL_STATUS = 38L;
    public static final Long NAME_USE = 213L;
    public static final Long PERSONNEL_POSITION = 88L;
    public static final Long PERSONNEL_SPECIALITY = 3394L;
    public static final Long DIAGNOSIS_TYPE = 17L;
    public static final Long PROCEDURE_TYPE = 401L;
    public static final Long RELATIONSHIP_TO_PATIENT = 40L;
    public static final Long PERSON_RELATIONSHIP_TYPE = 351L;
    public static final Long PHONE_TYPE = 43L;
    public static final Long TREATMENT_FUNCTION = 34L;
    public static final Long ALIAS_TYPE = 4L;
    public static final Long CLINICAL_CODE_TYPE = 72L;
    public static final Long CLINICAL_EVENT_NORMALCY = 52L;


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
