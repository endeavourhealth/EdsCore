package org.endeavourhealth.core.database.dal.reference;

import java.io.File;
import java.util.Date;

public interface SnomedToBnfChapterDalI {
    String lookupSnomedCode(String snomedCode) throws Exception;
    void updateSnomedToBnfChapterLookup(String snomedCode, String bnfChapterCode) throws Exception;
    void updateSnomedToBnfChapterLookup(String filePath, Date dataDate) throws Exception;
}
