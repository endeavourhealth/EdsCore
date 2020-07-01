package org.endeavourhealth.core.database.rdbms.ehr.models;

import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;

import java.util.List;

public interface AdminResourceRetrieverI {

    List<ResourceWrapper> getNextBatch() throws Exception;
    void close() throws Exception;
}
