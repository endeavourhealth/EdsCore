package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.PurposeEntity;

import java.util.List;

public interface PurposeDalI {

    public List<PurposeEntity> getPurposesFromList(List<String> purposes) throws Exception;

}
