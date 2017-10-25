package org.endeavourhealth.core.database.dal.reference;

import org.endeavourhealth.core.database.dal.reference.models.PostcodeLookup;

public interface PostcodeDalI {

    PostcodeLookup getPostcodeReference(String postcode) throws Exception;
}
