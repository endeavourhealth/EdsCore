package org.endeavourhealth.core.database.dal.usermanager;

import org.endeavourhealth.core.database.rdbms.usermanager.models.DelegationRelationshipEntity;

import java.util.List;

public interface DelegationRelationshipDalI {

    public List<DelegationRelationshipEntity> getDelegatedOrganisations(String organisationId) throws Exception;
    public DelegationRelationshipEntity getDelegationRelationship(String relationshipId) throws Exception;

}
