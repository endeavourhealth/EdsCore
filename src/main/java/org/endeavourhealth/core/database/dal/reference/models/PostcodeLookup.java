package org.endeavourhealth.core.database.dal.reference.models;

import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsPostcodeLookup;

public class PostcodeLookup {

    private String postcodeNoSpace = null;
    private String postcode = null;
    private String lsoaCode = null;
    private String msoaCode = null;
    private String ward = null;
    private String ward1998 = null;
    private String ccg = null;
    
    public PostcodeLookup() {}

    public PostcodeLookup(RdbmsPostcodeLookup proxy) {
        this.postcodeNoSpace = proxy.getPostcodeNoSpace();
        this.postcode = proxy.getPostcode();
        this.lsoaCode = proxy.getLsoaCode();
        this.msoaCode = proxy.getMsoaCode();
        this.ward = proxy.getWard();
        this.ward1998 = proxy.getWard1998();
        this.ccg = proxy.getCcg();
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

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getWard1998() {
        return ward1998;
    }

    public void setWard1998(String ward1998) {
        this.ward1998 = ward1998;
    }

    public String getCcg() {
        return ccg;
    }

    public void setCcg(String ccg) {
        this.ccg = ccg;
    }
}
