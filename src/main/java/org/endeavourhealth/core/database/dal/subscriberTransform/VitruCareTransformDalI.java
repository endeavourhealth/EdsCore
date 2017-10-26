package org.endeavourhealth.core.database.dal.subscriberTransform;

import java.util.UUID;

public interface VitruCareTransformDalI {

    void saveVitruCareIdMapping(UUID edsPatientId, UUID serviceId, UUID systemId, String virtruCareId) throws Exception;
    String getVitruCareId(UUID edsPatientId) throws Exception;
}
