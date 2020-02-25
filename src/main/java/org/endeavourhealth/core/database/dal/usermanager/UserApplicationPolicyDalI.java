package org.endeavourhealth.core.database.dal.usermanager;

import org.endeavourhealth.core.database.rdbms.usermanager.models.UserApplicationPolicyEntity;

public interface UserApplicationPolicyDalI {

    public UserApplicationPolicyEntity getUserApplicationPolicy(String userId) throws Exception;

}
