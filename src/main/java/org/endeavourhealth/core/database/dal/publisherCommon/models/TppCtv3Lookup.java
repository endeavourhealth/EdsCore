package org.endeavourhealth.core.database.dal.publisherCommon.models;

public class TppCtv3Lookup {

    private String ctv3Code;
    private String ctv3Text;

    public TppCtv3Lookup() {};

    public String getCtv3Code() {
        return ctv3Code;
    }

    public void setCtv3Code(String ctv3Code) {
        this.ctv3Code = ctv3Code;
    }

    public String getCtv3Text() {
        return ctv3Text;
    }

    public void setCtv3Text(String ctv3Text) {
        this.ctv3Text = ctv3Text;
    }

    @Override
    public String toString() {
        return "Code = [" + ctv3Code + "] Term = [" + ctv3Text + "]";
    }
}
