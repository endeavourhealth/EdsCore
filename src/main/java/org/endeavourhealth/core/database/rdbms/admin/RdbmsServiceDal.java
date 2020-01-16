package org.endeavourhealth.core.database.rdbms.admin;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.admin.LibraryRepositoryHelper;
import org.endeavourhealth.core.database.dal.admin.ServiceDalI;
import org.endeavourhealth.core.database.dal.admin.models.Service;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsService;
import org.endeavourhealth.core.fhirStorage.ServiceInterfaceEndpoint;
import org.endeavourhealth.core.xml.QueryDocument.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RdbmsServiceDal implements ServiceDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsServiceDal.class);

    public UUID save(Service service) throws Exception{

        UUID serviceUuid = null;

        if (service.getId() == null) {

            //if no UUID has been assigned validate the ODS code isn't already in use before assigning one
            String localId = service.getLocalId();
            if (!Strings.isNullOrEmpty(localId)) {
                Service existingService = getByLocalIdentifier(localId);
                if (existingService != null) {
                    throw new Exception("Service already exists with ID " + localId);
                }
            }

            serviceUuid = UUID.randomUUID();
            service.setId(serviceUuid);

        } else {
            serviceUuid = service.getId();
        }

        RdbmsService dbService = new RdbmsService(service);

        Connection connection = ConnectionManager.getAdminConnection();
        PreparedStatement psService = null;

        try {
            psService = createSaveServicePreparedStatement(connection);
            addToSaveServicePreparedStatement(psService, dbService);

            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (psService != null) {
                psService.close();
            }
            connection.close();
        }

        return serviceUuid;
    }

    public static PreparedStatement createSaveServicePreparedStatement(Connection connection) throws Exception {


        String sql = "INSERT INTO service"
                + " (id, name, local_id, endpoints, organisations, publisher_config_name, notes, postcode, ccg_code, organisation_type)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " name = VALUES(name),"
                + " local_id = VALUES(local_id),"
                + " endpoints = VALUES(endpoints),"
                + " organisations = VALUES(organisations),"
                + " publisher_config_name = VALUES(publisher_config_name),"
                + " notes = VALUES(notes),"
                + " postcode = VALUES(postcode),"
                + " ccg_code = VALUES(ccg_code),"
                + " organisation_type = VALUES(organisation_type)";

        return connection.prepareStatement(sql);
    }

    public static void addToSaveServicePreparedStatement(PreparedStatement ps, RdbmsService service) throws Exception {

        int col = 1;
        ps.setString(col++, service.getId());
        ps.setString(col++, service.getName());
        if (!Strings.isNullOrEmpty(service.getLocalId())) {
            ps.setString(col++, service.getLocalId());
        } else {
            ps.setNull(col++, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(service.getEndpoints())) {
            ps.setString(col++, service.getEndpoints());
        } else {
            ps.setNull(col++, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(service.getOrganisations())) {
            ps.setString(col++, service.getOrganisations());
        } else {
            ps.setNull(col++, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(service.getPublisherConfigName())) {
            ps.setString(col++, service.getPublisherConfigName());
        } else {
            ps.setNull(col++, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(service.getNotes())) {
            ps.setString(col++, service.getNotes());
        } else {
            ps.setNull(col++, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(service.getPostcode())) {
            ps.setString(col++, service.getPostcode());
        } else {
            ps.setNull(col++, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(service.getCcgCode())) {
            ps.setString(col++, service.getCcgCode());
        } else {
            ps.setNull(col++, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(service.getOrganisationType())) {
            ps.setString(col++, service.getOrganisationType());
        } else {
            ps.setNull(col++, Types.VARCHAR);
        }

        ps.executeUpdate();
    }

    private static PreparedStatement createDeleteServicePreparedStatement(Connection connection) throws Exception {

        String sql = "DELETE FROM service"
                + " WHERE id = ?";;

        return connection.prepareStatement(sql);
    }

    private static void addToDeleteServicePreparedStatement(PreparedStatement ps, RdbmsService service) throws Exception {
        ps.setString(1, service.getId());

        ps.executeUpdate();
    }

    public Service getById(UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsService c"
                    + " where c.id = :id";

            Query query = entityManager.createQuery(sql, RdbmsService.class)
                    .setParameter("id", id.toString());

            RdbmsService result = (RdbmsService)query.getSingleResult();
            Service ret = new Service(result);

            convertServiceIfRequired(ret);
            return ret;

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }


    public void delete(Service service) throws Exception {

        RdbmsService dbService = new RdbmsService(service);

        Connection connection = ConnectionManager.getAdminConnection();
        PreparedStatement psService = null;

        try {
            psService = createDeleteServicePreparedStatement(connection);
            addToDeleteServicePreparedStatement(psService, dbService);

            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (psService != null) {
                psService.close();
            }
            connection.close();
        }
    }

    public List<Service> getAll() throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsService c";

            Query query = entityManager.createQuery(sql, RdbmsService.class);

            List<RdbmsService> results = query.getResultList();

            //can't use stream as the constructor throws an exception
            List<Service> ret = new ArrayList<>();
            for (RdbmsService result : results) {
                ret.add(new Service(result));
            }

            convertServicesIfRequired(ret);

            return ret;

        } finally {
            entityManager.close();
        }
    }

    public List<Service> search(String searchData) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsService c"
                    + " where c.name like :search_param_1"
                    + " or c.localId = :search_param_2";

            Query query = entityManager.createQuery(sql, RdbmsService.class)
                    .setParameter("search_param_1", searchData + "%")
                    .setParameter("search_param_2", searchData);

            List<RdbmsService> results = query.getResultList();
            //LOG.debug("Searching for [" + searchData + "] and got " + results.size() + " results");
            //LOG.debug("Query = " + query);

            //can't use stream as the constructor throws an exception
            List<Service> ret = new ArrayList<>();
            for (RdbmsService result : results) {
                ret.add(new Service(result));
            }

            convertServicesIfRequired(ret);
            return ret;

        } finally {
            entityManager.close();
        }
    }

    public Service getByLocalIdentifier(String localIdentifier) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsService c"
                    + " where c.localId = :id";

            Query query = entityManager.createQuery(sql, RdbmsService.class)
                    .setParameter("id", localIdentifier);

            RdbmsService result = (RdbmsService)query.getSingleResult();
            Service ret = new Service(result);

            convertServiceIfRequired(ret);
            return ret;

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    /**
     * the format of the endpoints JSON has changed slightly, so easier to just use this fn
     * to convert on demand so as soon as new core code is run, any services will be converted
     */
    private void convertServiceIfRequired(Service service) throws Exception {
        List<Service> l = new ArrayList<>();
        l.add(service);
        convertServicesIfRequired(l);
    }
    private void convertServicesIfRequired(List<Service> services) throws Exception {

        for (Service service: services) {
            String endpointsJson = service.getEndpoints();
            if (Strings.isNullOrEmpty(endpointsJson)) {
                continue;
            }

            List<ServiceInterfaceEndpoint> endpoints = service.getEndpointsList();

            boolean potentiallyNeedsFixing = false;
            for (ServiceInterfaceEndpoint endpoint: endpoints) {
                String interfaceType = endpoint.getEndpoint();
                if (Strings.isNullOrEmpty(interfaceType)
                        || interfaceType.equals("http://")) {
                    potentiallyNeedsFixing = true;
                    break;
                }
            }

            if (!potentiallyNeedsFixing) {
                continue;
            }

            LOG.debug("Converting endpoints JSON for " + service);

            boolean needsSaving = false;

            String serviceIdStr = service.getId().toString();
            List<LibraryItem> protocols = LibraryRepositoryHelper.getProtocolsByServiceId(serviceIdStr, null);

            for (ServiceInterfaceEndpoint endpoint: endpoints) {
                String interfaceType = endpoint.getEndpoint();

                if (Strings.isNullOrEmpty(interfaceType)
                        || interfaceType.equals("http://")) {

                    //work out type
                    interfaceType = null;

                    String systemIdStr = endpoint.getSystemUuid().toString();

                    for (LibraryItem libraryItem: protocols) {
                        Protocol protocol = libraryItem.getProtocol();

                        for (ServiceContract serviceContract : protocol.getServiceContract()) {
                            if (serviceContract.getService().getUuid().equals(serviceIdStr)
                                    && serviceContract.getSystem().getUuid().equals(systemIdStr)
                                    && serviceContract.getActive() == ServiceContractActive.TRUE) {

                                String thisInterfaceType = null;
                                if (serviceContract.getType().equals(ServiceContractType.PUBLISHER)) {
                                    thisInterfaceType = ServiceInterfaceEndpoint.STATUS_NORMAL;

                                } else {
                                    thisInterfaceType = "";
                                }

                                if (interfaceType == null
                                    || thisInterfaceType.equals(interfaceType)) {
                                    interfaceType = thisInterfaceType;
                                } else {
                                    throw new Exception("Service " + serviceIdStr + " has interface for system " + systemIdStr + " that is publisher AND subscriber");
                                }
                            }
                        }
                    }

                    if (interfaceType == null) {
                        interfaceType = "";
                    }

                    String oldInterfaceType = endpoint.getEndpoint();
                    if (oldInterfaceType == null) {
                        oldInterfaceType = "";
                    }

                    //only set if it's different
                    if (!oldInterfaceType.equals(interfaceType)) {
                        endpoint.setEndpoint(interfaceType);
                        needsSaving = true;
                    }
                }
            }

            //save
            if (needsSaving) {
                service.setEndpointsList(endpoints);

                Connection connection = ConnectionManager.getAdminConnection();
                PreparedStatement ps = null;
                try {
                    ps = connection.prepareStatement("UPDATE service SET endpoints = ? WHERE id = ?");

                    ps.setString(1, service.getEndpoints());
                    ps.setString(2, service.getId().toString());

                    ps.executeUpdate();

                    connection.commit();

                } finally {
                    if (ps != null) {
                        ps.close();
                    }
                    connection.close();
                }

                LOG.debug("Converted endpoints JSON for " + service);
            } else {
                LOG.debug("No conversion required for endpoints JSON for " + service);
            }


        }
    }
}
