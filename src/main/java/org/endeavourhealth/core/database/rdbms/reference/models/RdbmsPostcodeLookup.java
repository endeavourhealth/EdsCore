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
    private String ward = null;
    private String ward1998 = null;
    private String ccg = null;
    //private BigDecimal townsendScore = null;

    public RdbmsPostcodeLookup() {}

    public RdbmsPostcodeLookup(PostcodeLookup proxy) {
        this.postcodeNoSpace = proxy.getPostcodeNoSpace();
        this.postcode = proxy.getPostcode();
        this.lsoaCode = proxy.getLsoaCode();
        this.msoaCode = proxy.getMsoaCode();
        this.ward = proxy.getWard();
        this.ward1998 = proxy.getWard1998();
        this.ccg = proxy.getCcg();
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

    @Column(name = "ward")
    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    @Column(name = "ward_1998")
    public String getWard1998() {
        return ward1998;
    }

    public void setWard1998(String ward1998) {
        this.ward1998 = ward1998;
    }

    @Column(name = "ccg")
    public String getCcg() {
        return ccg;
    }

    public void setCcg(String ccg) {
        this.ccg = ccg;
    }

    /*@Column(name = "townsend_score")
    public BigDecimal getTownsendScore() {
        return townsendScore;
    }

    public void setTownsendScore(BigDecimal townsendScore) {
        this.townsendScore = townsendScore;
    }*/
}
