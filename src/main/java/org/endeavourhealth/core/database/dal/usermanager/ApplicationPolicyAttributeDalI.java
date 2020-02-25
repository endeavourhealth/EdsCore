package org.endeavourhealth.core.database.dal.usermanager;

import org.endeavourhealth.core.database.dal.usermanager.models.JsonApplicationPolicyAttribute;
import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationPolicyAttributeEntity;

import java.util.List;

public interface ApplicationPolicyAttributeDalI {

    public List<JsonApplicationPolicyAttribute> getApplicationPolicyAttributes(String roleTypeId) throws Exception;
    public ApplicationPolicyAttributeEntity getRoleTypeAccessProfile(String profileId) throws Exception;
}
