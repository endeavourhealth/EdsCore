package org.endeavourhealth.core.database.dal.subscriberTransform;

public interface PseudoIdDalI {

    void storePseudoId(String patientId, String pseudoId) throws Exception;
}
