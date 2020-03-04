package org.endeavourhealth.core.database.dal.usermanager;

import org.endeavourhealth.core.database.rdbms.usermanager.models.UserRegionEntity;

public interface UserRegionDalI {
    public UserRegionEntity getUserRegion(String userId) throws Exception;
}
