package org.endeavourhealth.core.database.rdbms.reference.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "msoa_2011_lookup")
public class RdbmsMsoa2011Lookup implements Serializable {

    private String msoa2011Code = null;
    private String msoa2011Name = null;

    public RdbmsMsoa2011Lookup() {}

    @Id
    @Column(name = "msoa_2011_code", nullable = false)
    public String getMsoa2011Code() {
        return msoa2011Code;
    }

    public void setMsoa2011Code(String msoa2011Code) {
        this.msoa2011Code = msoa2011Code;
    }

    @Column(name = "msoa_2011_name", nullable = false)
    public String getMsoa2011Name() {
        return msoa2011Name;
    }

    public void setMsoa2011Name(String msoa2011Name) {
        this.msoa2011Name = msoa2011Name;
    }
}