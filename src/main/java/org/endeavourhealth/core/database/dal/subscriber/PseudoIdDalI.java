package org.endeavourhealth.core.database.dal.subscriber;

public interface PseudoIdDalI {

    void storePseudoId(String patientId, String pseudoId) throws Exception;
}
