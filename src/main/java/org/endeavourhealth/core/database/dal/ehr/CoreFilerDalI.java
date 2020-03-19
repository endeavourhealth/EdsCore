package org.endeavourhealth.core.database.dal.ehr;

import org.endeavourhealth.core.database.dal.ehr.models.CoreFilerWrapper;

import java.util.List;

public interface CoreFilerDalI {

    void save(List<CoreFilerWrapper> wrappers) throws Exception;

    void delete(List<CoreFilerWrapper> wrappers) throws Exception;

    void save(CoreFilerWrapper wrapper) throws Exception;

}
