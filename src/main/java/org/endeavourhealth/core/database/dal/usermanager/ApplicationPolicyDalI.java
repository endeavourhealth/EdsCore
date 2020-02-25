package org.endeavourhealth.core.database.dal.usermanager;

import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationPolicyEntity;

import java.util.List;

public interface ApplicationPolicyDalI {

    public ApplicationPolicyEntity getApplicationPolicy(String roleId) throws Exception;
    public List<ApplicationPolicyEntity> getAllApplicationPolicies() throws Exception;

}
