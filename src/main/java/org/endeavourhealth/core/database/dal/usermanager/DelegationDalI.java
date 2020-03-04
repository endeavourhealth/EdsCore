package org.endeavourhealth.core.database.dal.usermanager;

import org.endeavourhealth.core.database.rdbms.usermanager.models.DelegationEntity;

public interface DelegationDalI {

    public DelegationEntity getDelegation(String delegationId) throws Exception;

}
