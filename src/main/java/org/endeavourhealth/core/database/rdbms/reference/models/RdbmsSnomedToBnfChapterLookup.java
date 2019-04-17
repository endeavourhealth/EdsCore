package org.endeavourhealth.core.database.rdbms.reference.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "snomed_to_bnf_chapter_lookup")
public class RdbmsSnomedToBnfChapterLookup implements Serializable {

    private String snomedCode;
    private String bnfChapterCode;

    public RdbmsSnomedToBnfChapterLookup() {}

    @Id
    @Column(name = "snomed_code", nullable = false)
    public String getSnomedCode() {
        return snomedCode;
    }

    public void setSnomedCode(String procedureCode) {
        this.snomedCode = procedureCode;
    }

    @Column(name = "bnf_chapter_code", nullable = true)
    public String getBnfChapterCode() {
        return bnfChapterCode;
    }

    public void setBnfChapterCode(String bnfChapterCode) {
        this.bnfChapterCode = bnfChapterCode;
    }
}
