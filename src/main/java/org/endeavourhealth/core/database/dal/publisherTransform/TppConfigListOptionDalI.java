package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.TppConfigListOption;

import java.util.UUID;

public interface TppConfigListOptionDalI {

    TppConfigListOption getListOptionFromRowId(Long rowId, UUID serviceId) throws Exception;
    TppConfigListOption getListOptionFromRowAndListId(Long rowId, Long configListId, UUID serviceId) throws Exception;

    void save(TppConfigListOption mapping, UUID serviceId) throws Exception;
}
