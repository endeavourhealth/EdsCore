package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.TppConfigListOption;

import java.util.List;
import java.util.UUID;

public interface TppConfigListOptionDalI {

    TppConfigListOption getListOptionFromRowId(Long rowId, UUID serviceId) throws Exception;
    TppConfigListOption getListOptionFromRowAndListId(Long rowId, Long configListId, UUID serviceId) throws Exception;

    void save(UUID serviceId, TppConfigListOption mapping) throws Exception;
    void save(UUID serviceId, List<TppConfigListOption> mapping) throws Exception;
}
