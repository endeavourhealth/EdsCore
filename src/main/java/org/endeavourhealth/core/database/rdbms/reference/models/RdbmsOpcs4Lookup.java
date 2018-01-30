package org.endeavourhealth.core.database.rdbms.reference.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "opcs4_lookup")
public class RdbmsOpcs4Lookup implements Serializable {

    private String procedureCode;
    private String procedureName;

    public RdbmsOpcs4Lookup() {}

    @Id
    @Column(name = "procedure_code", nullable = false)
    public String getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCode = procedureCode;
    }

    @Column(name = "procedure_name", nullable = false)
    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }
}
