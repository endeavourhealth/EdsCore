package org.endeavourhealth.core.database.dal.reference;

public interface SnomedToBnfChapterDalI {
    String lookupSnomedCode(String snomedCode) throws Exception;
    void updateSnomedToBnfChapterLookup(String snomedCode, String bnfChapterCode) throws Exception;
    void updateSnomedToBnfChapterLookup(String filePath) throws Exception;
}
