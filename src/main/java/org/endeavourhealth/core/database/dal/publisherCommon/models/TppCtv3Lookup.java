package org.endeavourhealth.core.database.dal.publisherCommon.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

public class TppCtv3Lookup {

    private String ctv3Code;
    private String ctv3Text;
    private ResourceFieldMappingAudit audit = null;

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

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    @Override
    public String toString() {
        return "Code = [" + ctv3Code + "] Term = [" + ctv3Text + "]";
    }
}
