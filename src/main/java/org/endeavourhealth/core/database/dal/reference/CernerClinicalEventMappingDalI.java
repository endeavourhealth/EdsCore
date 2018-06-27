package org.endeavourhealth.core.database.dal.reference;

import org.endeavourhealth.core.database.dal.reference.models.CernerClinicalEventMap;

public interface CernerClinicalEventMappingDalI {

    public CernerClinicalEventMap findMappingForCvrefCode(Long cvrefCode) throws Exception;
}
