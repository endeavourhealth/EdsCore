package org.endeavourhealth.core.database.dal.subscriberTransform;

import java.util.TreeMap;
import java.util.UUID;

public interface PseudoIdDalI {

    void storePseudoIdOldWay(String patientId, String pseudoId) throws Exception;
    //String findPseudoIdOldWay(String patientId) throws Exception;

    void saveSubscriberPseudoId(UUID patientId, long subscriberPatientId, String saltKeyName, String pseudoId) throws Exception;
    String findSubscriberPseudoId(UUID patientId, String saltKeyName) throws Exception;

    void auditPseudoId(String saltName, TreeMap<String, String> keys, String pseudoId) throws Exception;
}
