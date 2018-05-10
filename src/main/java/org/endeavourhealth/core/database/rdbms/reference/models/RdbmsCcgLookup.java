package org.endeavourhealth.core.database.rdbms.reference.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ccg_lookup")
public class RdbmsCcgLookup implements Serializable {
    private String code;
    private String name;

    public RdbmsCcgLookup() {}

    @Id
    @Column(name = "ccg_code", nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "ccg_name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
