package org.endeavourhealth.core.database.dal.admin;

import org.endeavourhealth.core.database.dal.admin.models.Organisation;

import java.util.List;
import java.util.UUID;

public interface OrganisationDalI {

    UUID save(Organisation organisation) throws Exception;
    Organisation getById(UUID id) throws Exception;
    Organisation getByNationalId(String nationalId) throws Exception;
    void delete(Organisation organisation) throws Exception;
    List<Organisation> getAll() throws Exception;
    List<Organisation> search(String searchData) throws Exception;

}
