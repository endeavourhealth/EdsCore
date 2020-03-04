package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.OrganisationEntity;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.RegionEntity;

import java.util.List;

public interface RegionDalI {

    public RegionEntity getSingleRegion(String uuid) throws Exception;
    public List<RegionEntity> getRegionsFromList(List<String> regions) throws Exception;
    public List<RegionEntity> getAllRegions() throws Exception;
    public List<OrganisationEntity> getAllOrganisationsForAllChildRegions(String regionUUID) throws Exception;
    public List<String> getOrganisations(String regionUUID, List<String> organisationUuids) throws Exception;
    public List<RegionEntity> getAllChildRegionsForRegion(String regionId) throws Exception;
    public List<String> getRegions(String regionUUID, List<String> regionUUIDs) throws Exception;

}
