package org.endeavourhealth.core.database.dal.usermanager;

import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationAccessProfileEntity;

public interface ApplicationAccessProfileDalI {

    public ApplicationAccessProfileEntity getApplicationProfile(String applicationProfileId) throws Exception;
}
