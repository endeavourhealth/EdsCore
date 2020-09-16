package org.endeavourhealth.core.database.dal.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.models.PseudoIdAudit;

import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

public interface PseudoIdDalI {

    /**
     * functions for persisting and finding the latest pseudo ID for a patient and salt name
     */
    void saveSubscriberPseudoId(UUID patientId, long subscriberPatientId, String saltKeyName, String pseudoId) throws Exception;
    void saveSubscriberPseudoIds(UUID patientId, long subscriberPatientId, List<PseudoIdAudit> audits) throws Exception;
    String findSubscriberPseudoId(UUID patientId, String saltKeyName) throws Exception;

    /**
     * old function for storing a single pseudo ID against a patient ID from Compass v1 transforms. Table
     * still used by some older extracts, but otherwise unused
     */
    void storePseudoIdOldWay(String patientId, String pseudoId) throws Exception;

    /**
     * write to global audit of all pseudo IDs generated and what from
     */
    void auditPseudoId(String saltName, TreeMap<String, String> keys, String pseudoId) throws Exception;
    void auditPseudoIds(List<PseudoIdAudit> audits) throws Exception;
}
