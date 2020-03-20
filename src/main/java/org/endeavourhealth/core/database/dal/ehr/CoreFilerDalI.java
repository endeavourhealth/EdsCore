package org.endeavourhealth.core.database.dal.ehr;

import org.endeavourhealth.core.database.dal.ehr.models.CoreFilerWrapper;
import org.endeavourhealth.core.database.dal.ehr.models.CoreId;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CoreFilerDalI {

    void save(UUID serviceId, List<CoreFilerWrapper> wrappers) throws Exception;

    void delete(UUID serviceId, List<CoreFilerWrapper> wrappers) throws Exception;

    void save(UUID serviceId, CoreFilerWrapper wrapper) throws Exception;

    CoreId findOrCreateCoreId(UUID serviceId,  byte coreTable, String sourceId) throws Exception;

    Map<String, CoreId> findOrCreateCoreIds(UUID serviceId, byte coreTable, List<String> sourceIds) throws Exception;

    Integer findOrganizationIdFromOdsCode(UUID serviceId, String odsCode) throws Exception;
}
