package org.endeavourhealth.core.database.dal.reference;

import org.endeavourhealth.core.database.dal.reference.models.SnomedLookup;

import java.util.List;
import java.util.Map;

public interface SnomedDalI {

    SnomedLookup getSnomedLookup(String conceptId) throws Exception;
    SnomedLookup getSnomedLookupForDescId(String descriptionId) throws Exception;

    void saveSnomedDescriptionToConceptMappings(Map<String, String> mappings) throws Exception;
    void saveSnomedConcepts(List<SnomedLookup> lookups) throws Exception;
}
