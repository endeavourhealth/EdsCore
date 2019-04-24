package org.endeavourhealth.core.database.dal.reference.models;

import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsPostcodeLookup;

public class PostcodeLookup {

    private String postcodeNoSpace = null;
    private String postcode = null;
    private String lsoaCode = null;
    private String msoaCode = null;
    private String wardCode = null;
    private String ccgCode = null;
    private String localAuthorityCode = null;
    private String lsoa2001Code = null;
    private String lsoa2011Code = null;
    private String msoa2001Code = null;
    private String msoa2011Code = null;
    
    public PostcodeLookup() {}

    public PostcodeLookup(RdbmsPostcodeLookup proxy) {
        this.postcodeNoSpace = proxy.getPostcodeNoSpace();
        this.postcode = proxy.getPostcode();
        this.lsoaCode = proxy.getLsoaCode();
        this.msoaCode = proxy.getMsoaCode();
        this.wardCode = proxy.getWardCode();
        this.ccgCode = proxy.getCcgCode();
        this.localAuthorityCode = proxy.getLocalAuthorityCode();
        this.lsoa2001Code = proxy.getLsoa2001Code();
        this.lsoa2011Code = proxy.getLsoa2011Code();
        this.msoa2001Code = proxy.getMsoa2001Code();
        this.msoa2011Code = proxy.getMsoa2011Code();
    }

    public String getPostcodeNoSpace() {
        return postcodeNoSpace;
    }

    public void setPostcodeNoSpace(String postcodeNoSpace) {
        this.postcodeNoSpace = postcodeNoSpace;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getLsoaCode() {
        return lsoaCode;
    }

    public void setLsoaCode(String lsoaCode) {
        this.lsoaCode = lsoaCode;
    }

    public String getMsoaCode() {
        return msoaCode;
    }

    public void setMsoaCode(String msoaCode) {
        this.msoaCode = msoaCode;
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    public String getCcgCode() {
        return ccgCode;
    }

    public void setCcgCode(String ccgCode) {
        this.ccgCode = ccgCode;
    }

    public String getLocalAuthorityCode() {
        return localAuthorityCode;
    }

    public void setLocalAuthorityCode(String localAuthorityCode) {
        this.localAuthorityCode = localAuthorityCode;
    }

    public String getLsoa2001Code() {
        return lsoa2001Code;
    }

    public void setLsoa2001Code(String lsoa2001Code) {
        this.lsoa2001Code = lsoa2001Code;
    }

    public String getLsoa2011Code() {
        return lsoa2011Code;
    }

    public void setLsoa2011Code(String lsoa2011Code) {
        this.lsoa2011Code = lsoa2011Code;
    }

    public String getMsoa2001Code() {
        return msoa2001Code;
    }

    public void setMsoa2001Code(String msoa2001Code) {
        this.msoa2001Code = msoa2001Code;
    }

    public String getMsoa2011Code() {
        return msoa2011Code;
    }

    public void setMsoa2011Code(String msoa2011Code) {
        this.msoa2011Code = msoa2011Code;
    }

}
