package org.endeavourhealth.core.database.dal.publisherCommon.models;

public class TppMultilexProductToCtv3Map {

    private int multiLexProductId;
    private String ctv3ReadCode;
    private String ctv3ReadTerm;

    public TppMultilexProductToCtv3Map() {
    }

    public int getMultiLexProductId() {
        return multiLexProductId;
    }

    public void setMultiLexProductId(int multiLexProductId) {
        this.multiLexProductId = multiLexProductId;
    }

    public String getCtv3ReadCode() {
        return ctv3ReadCode;
    }

    public void setCtv3ReadCode(String ctv3ReadCode) {
        this.ctv3ReadCode = ctv3ReadCode;
    }

    public String getCtv3ReadTerm() {
        return ctv3ReadTerm;
    }

    public void setCtv3ReadTerm(String ctv3ReadTerm) {
        this.ctv3ReadTerm = ctv3ReadTerm;
    }

    @Override
    public String toString() {
        return "ProductId = " + multiLexProductId + " CTV3Code " + ctv3ReadCode + " CTV3Term " + ctv3ReadTerm;
    }
}