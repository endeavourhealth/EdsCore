package org.endeavourhealth.core.database.dal.publisherCommon.models;

import com.google.common.base.Strings;
import org.endeavourhealth.common.cache.ParserPool;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsEmisCsvCodeMap;
import org.hl7.fhir.instance.model.CodeableConcept;

public class EmisCsvCodeMap {

    private static final String CODEABLE_CONCEPT = "CodeableConcept";

    private String dataSharingAgreementGuid = null;
    private boolean medication = false;
    private long codeId = -1;
    private String codeType = null;
    private String codeableConcept = null; //JSON of the FHIR codeable concept
    private String readTerm = null;
    private String readCode = null;
    private Long snomedConceptId = null;
    private Long snomedDescriptionId = null;
    private String snomedTerm = null;
    private String nationalCode = null;
    private String nationalCodeCategory = null;
    private String nationalCodeDescription = null;
    private Long parentCodeId = null;
    private ResourceFieldMappingAudit audit = null;

    public EmisCsvCodeMap() {}

    public EmisCsvCodeMap(RdbmsEmisCsvCodeMap proxy) throws Exception {
        //this.dataSharingAgreementGuid = proxy.getDataSharingAgreementGuid(); //not present in the proxy
        this.medication = proxy.isMedication();
        this.codeId = proxy.getCodeId();
        this.codeType = proxy.getCodeType();
        this.codeableConcept = proxy.getCodeableConcept();
        this.readTerm = proxy.getReadTerm();
        this.readCode = proxy.getReadCode();
        this.snomedConceptId = proxy.getSnomedConceptId();
        this.snomedDescriptionId = proxy.getSnomedDescriptionId();
        this.snomedTerm = proxy.getSnomedTerm();
        this.nationalCode = proxy.getNationalCode();
        this.nationalCodeCategory = proxy.getNationalCodeCategory();
        this.nationalCodeDescription = proxy.getNationalCodeDescription();
        this.parentCodeId = proxy.getParentCodeId();
        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }

    /*public EmisCsvCodeMap(CassandraEmisCsvCodeMap proxy) {
        this.dataSharingAgreementGuid = proxy.getDataSharingAgreementGuid();
        this.medication = proxy.isMedication();
        this.codeId = proxy.getCodeId();
        this.codeType = proxy.getCodeType();
        this.codeableConcept = proxy.getCodeableConcept();
        this.readTerm = proxy.getReadTerm();
        this.readCode = proxy.getReadCode();
        this.snomedConceptId = proxy.getSnomedConceptId();
        this.snomedDescriptionId = proxy.getSnomedDescriptionId();
        this.snomedTerm = proxy.getSnomedTerm();
        this.nationalCode = proxy.getNationalCode();
        this.nationalCodeCategory = proxy.getNationalCodeCategory();
        this.nationalCodeDescription = proxy.getNationalCodeDescription();
        this.parentCodeId = proxy.getParentCodeId();
        //no audit JSON
    }*/

    public String getDataSharingAgreementGuid() {
        return dataSharingAgreementGuid;
    }

    public void setDataSharingAgreementGuid(String dataSharingAgreementGuid) {
        this.dataSharingAgreementGuid = dataSharingAgreementGuid;
    }

    public boolean isMedication() {
        return medication;
    }

    public void setMedication(boolean medication) {
        this.medication = medication;
    }

    public long getCodeId() {
        return codeId;
    }

    public void setCodeId(long codeId) {
        this.codeId = codeId;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getCodeableConcept() {
        return codeableConcept;
    }

    public void setCodeableConcept(String codeableConcept) {
        this.codeableConcept = codeableConcept;
    }

    public String getReadTerm() {
        return readTerm;
    }

    public void setReadTerm(String readTerm) {
        this.readTerm = readTerm;
    }

    public String getReadCode() {
        return readCode;
    }

    public void setReadCode(String readCode) {
        this.readCode = readCode;
    }

    public Long getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(Long snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }

    public Long getSnomedDescriptionId() {
        return snomedDescriptionId;
    }

    public void setSnomedDescriptionId(Long snomedDescriptionId) {
        this.snomedDescriptionId = snomedDescriptionId;
    }

    public String getSnomedTerm() {
        return snomedTerm;
    }

    public void setSnomedTerm(String snomedTerm) {
        this.snomedTerm = snomedTerm;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getNationalCodeCategory() {
        return nationalCodeCategory;
    }

    public void setNationalCodeCategory(String nationalCodeCategory) {
        this.nationalCodeCategory = nationalCodeCategory;
    }

    public String getNationalCodeDescription() {
        return nationalCodeDescription;
    }

    public void setNationalCodeDescription(String nationalCodeDescription) {
        this.nationalCodeDescription = nationalCodeDescription;
    }

    public Long getParentCodeId() {
        return parentCodeId;
    }

    public void setParentCodeId(Long parentCodeId) {
        this.parentCodeId = parentCodeId;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    public CodeableConcept getCodeableConceptObject() throws Exception {
        return (CodeableConcept)ParserPool.getInstance().parseType(this.codeableConcept, CODEABLE_CONCEPT);
    }

    public void setCodeableConceptObject(CodeableConcept codeableConcept) throws Exception {
        this.codeableConcept = ParserPool.getInstance().composeString(codeableConcept, CODEABLE_CONCEPT);
    }
}
