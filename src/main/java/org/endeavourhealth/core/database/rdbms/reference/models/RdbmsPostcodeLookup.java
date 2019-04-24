package org.endeavourhealth.core.database.rdbms.reference.models;

import org.endeavourhealth.core.database.dal.reference.models.PostcodeLookup;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "postcode_lookup")
public class RdbmsPostcodeLookup implements Serializable {

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


    public RdbmsPostcodeLookup() {}

    public RdbmsPostcodeLookup(PostcodeLookup proxy) {
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

    @Id
    @Column(name = "postcode_no_space", nullable = false)
    public String getPostcodeNoSpace() {
        return postcodeNoSpace;
    }

    public void setPostcodeNoSpace(String postcodeNoSpace) {
        this.postcodeNoSpace = postcodeNoSpace;
    }

    @Column(name = "postcode")
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Column(name = "lsoa_code")
    public String getLsoaCode() {
        return lsoaCode;
    }

    public void setLsoaCode(String lsoaCode) {
        this.lsoaCode = lsoaCode;
    }

    @Column(name = "msoa_code")
    public String getMsoaCode() {
        return msoaCode;
    }

    public void setMsoaCode(String msoaCode) {
        this.msoaCode = msoaCode;
    }

    @Column(name = "ward_code")
    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    @Column(name = "ccg_code")
    public String getCcgCode() {
        return ccgCode;
    }

    public void setCcgCode(String ccgCode) {
        this.ccgCode = ccgCode;
    }

    @Column(name = "local_authority_code")
    public String getLocalAuthorityCode() {
        return localAuthorityCode;
    }

    public void setLocalAuthorityCode(String localAuthorityCode) {
        this.localAuthorityCode = localAuthorityCode;
    }

    @Column(name = "lsoa_2001_code")
    public String getLsoa2001Code() {
        return lsoa2001Code;
    }

    public void setLsoa2001Code(String lsoa2001Code) {
        this.lsoa2001Code = lsoa2001Code;
    }

    @Column(name = "lsoa_2011_code")
    public String getLsoa2011Code() {
        return lsoa2011Code;
    }

    public void setLsoa2011Code(String lsoa2011Code) {
        this.lsoa2011Code = lsoa2011Code;
    }

    @Column(name = "msoa_2001_code")
    public String getMsoa2001Code() {
        return msoa2001Code;
    }

    public void setMsoa2001Code(String msoa2001Code) {
        this.msoa2001Code = msoa2001Code;
    }

    @Column(name = "msoa_2011_code")
    public String getMsoa2011Code() {
        return msoa2011Code;
    }

    public void setMsoa2011Code(String msoa2011Code) {
        this.msoa2011Code = msoa2011Code;
    }

}
