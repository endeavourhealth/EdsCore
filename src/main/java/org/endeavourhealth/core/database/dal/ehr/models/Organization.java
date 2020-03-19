package org.endeavourhealth.core.database.dal.ehr.models;

public class Organization {

    private int id;
    private String odsCode;
    private String name;
    private int typeId;
    private String postCode;
    private int parentOrganizationId;

    public Organization() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOdsCode() {
        return odsCode;
    }

    public void setOdsCode(String odsCode) {
        this.odsCode = odsCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public int getParentOrganizationId() {
        return parentOrganizationId;
    }

    public void setParentOrganizationId(int parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
    }
}