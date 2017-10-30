package org.endeavourhealth.core.database.dal.reference;

import org.endeavourhealth.core.database.dal.reference.models.Concept;

import java.util.List;

public interface CodingDalI {

    List<Concept> search(String term, int maxResultsSize, int start) throws Exception;
    Concept getConcept(String code) throws Exception;
    List<Concept> getChildren(String code) throws Exception;
    List<Concept> getParents(String code) throws Exception;

}
