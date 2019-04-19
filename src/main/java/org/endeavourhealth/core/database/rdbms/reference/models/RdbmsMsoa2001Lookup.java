package org.endeavourhealth.core.database.rdbms.reference.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "msoa_2001_lookup")
public class RdbmsMsoa2001Lookup implements Serializable {

    private String msoa2001Code = null;
    private String msoa2001Name = null;

    public RdbmsMsoa2001Lookup() {}

    @Id
    @Column(name = "msoa_2001_code", nullable = false)
    public String getMsoa2001Code() {
        return msoa2001Code;
    }

    public void setMsoa2001Code(String msoa2001Code) {
        this.msoa2001Code = msoa2001Code;
    }

    @Column(name = "msoa_2001_name", nullable = false)
    public String getMsoa2001Name() {
        return msoa2001Name;
    }

    public void setMsoa2001Name(String msoa2001Name) {
        this.msoa2001Name = msoa2001Name;
    }
}