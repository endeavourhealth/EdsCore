package org.endeavourhealth.core.database.dal.admin;

import org.endeavourhealth.core.database.dal.admin.models.Service;

import java.util.List;
import java.util.UUID;

public interface ServiceDalI {

    UUID save(Service service) throws Exception;
    Service getById(UUID id) throws Exception;
    void delete(Service service) throws Exception;
    List<Service> getAll() throws Exception;
    List<Service> search(String searchData) throws Exception;
    Service getByLocalIdentifier(String localIdentifier) throws Exception;
}
