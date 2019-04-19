package org.endeavourhealth.core.database.rdbms.reference.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "lsoa_2001_lookup")
public class RdbmsLsoa2001Lookup implements Serializable {

    private String lsoa2001Code = null;
    private String lsoa2001Name = null;

    public RdbmsLsoa2001Lookup() {}

    @Id
    @Column(name = "lsoa_2001_code", nullable = false)
    public String getLsoa2001Code() {
        return lsoa2001Code;
    }

    public void setLsoa2001Code(String lsoa2001Code) {
        this.lsoa2001Code = lsoa2001Code;
    }

    @Column(name = "lsoa_2001_name", nullable = false)
    public String getLsoa2001Name() {
        return lsoa2001Name;
    }

    public void setLsoa2001Name(String lsoa2001Name) {
        this.lsoa2001Name = lsoa2001Name;
    }
}
