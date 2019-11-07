package org.endeavourhealth.core.terminology;

import com.google.common.base.Strings;
import org.endeavourhealth.common.fhir.FhirCodeUri;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.reference.*;
import org.endeavourhealth.core.database.dal.reference.models.CTV3ToSnomedMap;
import org.endeavourhealth.core.database.dal.reference.models.Read2ToSnomedMap;
import org.endeavourhealth.core.database.dal.reference.models.SnomedLookup;
import org.endeavourhealth.core.exceptions.TransformException;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Coding;

import java.util.List;

public abstract class TerminologyService {

    private static SnomedDalI snomedRepository = DalProvider.factorySnomedDal();
    private static Read2ToSnomedMapDalI read2ToSnomedRepository = DalProvider.factoryRead2ToSnomedMapDal();
    private static CTV3ToSnomedMapDalI ctv3ToSnomedRepository = DalProvider.factoryCTV3ToSnomedMapDal();
    private static Opcs4DalI opcs4Repository = DalProvider.factoryOpcs4Dal();
    private static Icd10DalI icd10Repository = DalProvider.factoryIcd10Dal();

    public static String lookupSnomedTerm(String conceptId) throws Exception {
        SnomedCode snomedCode = lookupSnomedFromConceptId(conceptId);
        if (snomedCode == null) {
            return null;
        } else {
            return snomedCode.getTerm();
        }
    }

    public static SnomedCode lookupSnomedFromConceptId(String conceptId) throws Exception {
        SnomedLookup snomedLookup = snomedRepository.getSnomedLookup(conceptId);
        if (snomedLookup == null) {
            return null;
        }
        return new SnomedCode(conceptId, snomedLookup.getTerm());
    }

    public static String lookupSnomedPreferredTermForDescription(String descriptionId) throws Exception {
        SnomedCode snomedCode = lookupSnomedConceptForDescriptionId(descriptionId);
        if (snomedCode == null) {
            return null;
        } else {
            return snomedCode.getTerm();
        }
    }

    public static SnomedCode lookupSnomedConceptForDescriptionId(String descriptionId) throws Exception {
        SnomedLookup snomedLookup = snomedRepository.getSnomedLookupForDescId(descriptionId);
        if (snomedLookup == null) {
            return null;
        }
        return new SnomedCode(snomedLookup.getConceptId(), snomedLookup.getTerm());
    }

    public static SnomedCode translateRead2ToSnomed(String code) throws Exception {
        //get conceptId from Read2/Snomed map table
        Read2ToSnomedMap read2ToSnomedMap = read2ToSnomedRepository.getRead2ToSnomedMap(code);
        if (read2ToSnomedMap == null) {
            return null;
        }
        String conceptId = read2ToSnomedMap.getConceptId();

        //get Snomed term from lookup table using conceptId
        SnomedLookup snomedLookup = snomedRepository.getSnomedLookup(conceptId);
        if (snomedLookup == null) {
            return null;
        }
        return new SnomedCode(conceptId, snomedLookup.getTerm());
    }

    public static SnomedCode translateCtv3ToSnomed(String code) throws Exception {
        //get conceptId from CTV3/Snomed map table
        CTV3ToSnomedMap ctv3ToSnomedMap = ctv3ToSnomedRepository.getCTV3ToSnomedMap(code);
        if (ctv3ToSnomedMap == null) {
            return null;
        }

        String sctConceptId = ctv3ToSnomedMap.getSctConceptId();

        //get Snomed term from lookup table using conceptId
        SnomedLookup snomedLookup = snomedRepository.getSnomedLookup(sctConceptId);
        if (snomedLookup == null) {
            return null;
        }
        return new SnomedCode(sctConceptId, snomedLookup.getTerm());
    }
    public static SnomedCode translateEmisSnomedToSnomed(String code) {
        //TODO - terminology service needs completing
        return null;
    }
    public static SnomedCode translateEmisPreparationToSnomed(String code) {
        //TODO - terminology service needs completing
        return null;
    }

    /**
     * checks the first Coding element in the CodeableConcept and adds a second Coding if it
     * needs to be mapped to SNOMED CT
     */
    public static void translateToSnomed(CodeableConcept codeableConcept) throws TransformException {
        List<Coding> codingList = codeableConcept.getCoding();
        if (codingList.isEmpty()) {
            return;
        }

        Coding coding = codingList.get(0);
        String system = coding.getSystem();
        try {
            if (system.equals(FhirCodeUri.CODE_SYSTEM_SNOMED_CT)) {
                //mapping required if no display term present, i.e. use Snomed translator to get us the mapped term
                if (Strings.isNullOrEmpty(coding.getDisplay())) {
                    SnomedCode mapping = TerminologyService.lookupSnomedFromConceptId(coding.getCode());
                    codingList.remove(0);  //remove placeholder as we only need the translated code here
                    codeableConcept.addCoding(mapping.toCoding());
                }
            } else if (system.equals(FhirCodeUri.CODE_SYSTEM_CTV3)) {
                SnomedCode mapping = TerminologyService.translateCtv3ToSnomed(coding.getCode());
                codeableConcept.addCoding(mapping.toCoding());
            } else if (system.equals(FhirCodeUri.CODE_SYSTEM_READ2)) {
                SnomedCode mapping = TerminologyService.translateRead2ToSnomed(coding.getCode());
                codeableConcept.addCoding(mapping.toCoding());
            } else if (system.equals(FhirCodeUri.CODE_SYSTEM_EMISPREPARATION)) {
                SnomedCode mapping = TerminologyService.translateEmisPreparationToSnomed(coding.getCode());
                codeableConcept.addCoding(mapping.toCoding());
            } else if (system.equals(FhirCodeUri.CODE_SYSTEM_EMISSNOMED)) {
                SnomedCode mapping = TerminologyService.translateEmisSnomedToSnomed(coding.getCode());
                codeableConcept.addCoding(mapping.toCoding());
            } else {
                throw new TransformException("Unexpected coding system [" + system + "]");
            }
        }
        catch (Exception e) {
            throw new TransformException("Code Translation Exception for code [" + coding.getCode() +"]" , e);
        }
    }

    public static String lookupOpcs4ProcedureName(String opcs4ProcedureCode) throws Exception {
        return opcs4Repository.lookupCode(opcs4ProcedureCode);
    }

    public static String lookupIcd10CodeDescription(String icd10Code) throws Exception {
        return icd10Repository.lookupCode(icd10Code);
    }

    /**
     * fn to ensure OPCS-4 codes are in the standard format as we get them in mixed formats
     */
    public static String standardiseOpcs4Code(String code) {

        //if one of the "chapter" headings or already containing a code, just return it as is
        if (code.length() <= 3
            || code.indexOf(".") > -1) {
            return code;
        }

        String prefix = code.substring(0, 3);
        String suffix = code.substring(3);
        return prefix + "." + suffix;
    }

    /**
     * fn to ensure ICD-10 codes are in the standard format
     */
    public static String standardiseIcd10Code(String code) {
        //ICD-10 is the same structure as OPCS-4, so just call into the same fn
        return standardiseOpcs4Code(code);
    }

    public static Read2Code lookupRead2Code(String code) throws Exception {
        return read2ToSnomedRepository.getRead2Code(code);
    }
}

