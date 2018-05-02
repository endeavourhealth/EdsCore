package org.endeavourhealth.core.database.rdbms.reference.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "local_authority_lookup")
public class RdbmsLocalAuthorityLookup implements Serializable {
    private String code;
    private String name;

    public RdbmsLocalAuthorityLookup() {}

    @Id
    @Column(name = "local_authority_code", nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "local_authority_name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
