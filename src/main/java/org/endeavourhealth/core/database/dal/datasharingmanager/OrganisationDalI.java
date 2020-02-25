package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonOrganisationCCG;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.OrganisationEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrganisationDalI {

    public List<OrganisationEntity> getOrganisationsFromList(List<String> organisations) throws Exception;
    public OrganisationEntity getOrganisation(String uuid) throws Exception;
    public List<OrganisationEntity> searchOrganisations(String expression, boolean searchServices,
                                                        byte organisationType,
                                                        Integer pageNumber, Integer pageSize,
                                                        String orderColumn, boolean descending, UUID userId) throws Exception;

    public List<OrganisationEntity> getOrganisationsFromOdsList(List<String> odsCodes) throws Exception;
    public OrganisationEntity getOrganisationsFromOdsCode(String odsCode) throws Exception;
    public List<JsonOrganisationCCG> getCCGForOrganisationOdsList(List<String> odsCodes) throws Exception;
    public Map<UUID, String> getCachedSearchTerm() throws Exception;

}
