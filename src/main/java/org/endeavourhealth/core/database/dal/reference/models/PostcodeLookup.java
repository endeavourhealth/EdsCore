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
    
    public PostcodeLookup() {}

    public PostcodeLookup(RdbmsPostcodeLookup proxy) {
        this.postcodeNoSpace = proxy.getPostcodeNoSpace();
        this.postcode = proxy.getPostcode();
        this.lsoaCode = proxy.getLsoaCode();
        this.msoaCode = proxy.getMsoaCode();
        this.wardCode = proxy.getWardCode();
        this.ccgCode = proxy.getCcgCode();
        this.localAuthorityCode = proxy.getLocalAuthorityCode();
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
}
