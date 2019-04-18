package org.endeavourhealth.core.database.rdbms.reference.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "lsoa_2011_lookup")
public class RdbmsLsoa2011Lookup implements Serializable {

    private String lsoa2011Code = null;
    private String lsoa2011Name = null;

    public RdbmsLsoa2011Lookup() {}

    @Id
    @Column(name = "lsoa_2011_code", nullable = false)
    public String getLsoa2011Code() {
        return lsoa2011Code;
    }

    public void setLsoa2011Code(String lsoa2011Code) {
        this.lsoa2011Code = lsoa2011Code;
    }

    @Column(name = "lsoa_2011_name", nullable = false)
    public String getLsoa2011Name() {
        return lsoa2011Name;
    }

    public void setLsoa2011Name(String lsoa2011Name) {
        this.lsoa2011Name = lsoa2011Name;
    }
}
