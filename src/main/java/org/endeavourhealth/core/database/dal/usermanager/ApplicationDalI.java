package org.endeavourhealth.core.database.dal.usermanager;

import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationEntity;

public interface ApplicationDalI {

    public ApplicationEntity getApplication(String applicationId) throws Exception;
}
