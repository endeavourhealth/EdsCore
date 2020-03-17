package org.endeavourhealth.core.database.rdbms.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.common.fhir.schema.OrganisationType;
import org.endeavourhealth.core.database.dal.admin.LibraryRepositoryHelper;
import org.endeavourhealth.core.database.dal.admin.ServiceDalI;
import org.endeavourhealth.core.database.dal.admin.models.Service;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.fhirStorage.ServiceInterfaceEndpoint;
import org.endeavourhealth.core.xml.QueryDocument.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.*;

public class RdbmsServiceDal implements ServiceDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsServiceDal.class);

    private static Boolean cachedHasNewCols = null;
    private static final String NOTES_KEY = "Notes";

    private static boolean hasNewCols() throws Exception {
        if (cachedHasNewCols == null) {

            Connection conn = ConnectionManager.getAdminConnection();
            PreparedStatement ps = null;
            try {
                String sql = "SELECT 1 FROM service WHERE alias IS NULL AND tags IS NULL";
                ps = conn.prepareStatement(sql);
                ps.executeQuery();

                //if the above works, then the column is present
                cachedHasNewCols = Boolean.TRUE;
                //LOG.debug("Alias column found");

            } catch (Exception ex) {
                //any exception with the above and the column isn't present
                cachedHasNewCols = Boolean.FALSE;
                LOG.debug("Alias column not found");

            } finally {
                ps.close();
                conn.close();
            }
        }
        return cachedHasNewCols.booleanValue();
    }

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

        Connection connection = ConnectionManager.getAdminConnection();
        PreparedStatement ps = null;

        try {
            ps = createSaveServicePreparedStatement(connection);
            addToSaveServicePreparedStatement(ps, service);

            ps.executeUpdate();
            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }

        return serviceUuid;
    }

    public static PreparedStatement createSaveServicePreparedStatement(Connection connection) throws Exception {

        String sql = null;
        if (hasNewCols()) {
            sql = "INSERT INTO service"
                    + " (id, name, local_id, endpoints, publisher_config_name, notes, postcode, ccg_code, organisation_type, alias, tags)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " name = VALUES(name),"
                    + " local_id = VALUES(local_id),"
                    + " endpoints = VALUES(endpoints),"
                    + " publisher_config_name = VALUES(publisher_config_name),"
                    + " notes = VALUES(notes),"
                    + " postcode = VALUES(postcode),"
                    + " ccg_code = VALUES(ccg_code),"
                    + " organisation_type = VALUES(organisation_type),"
                    + " alias = VALUES(alias),"
                    + " tags = VALUES(tags)";

        } else {
            sql = "INSERT INTO service"
                    + " (id, name, local_id, endpoints, publisher_config_name, notes, postcode, ccg_code, organisation_type)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " name = VALUES(name),"
                    + " local_id = VALUES(local_id),"
                    + " endpoints = VALUES(endpoints),"
                    + " publisher_config_name = VALUES(publisher_config_name),"
                    + " notes = VALUES(notes),"
                    + " postcode = VALUES(postcode),"
                    + " ccg_code = VALUES(ccg_code),"
                    + " organisation_type = VALUES(organisation_type)";
        }

        return connection.prepareStatement(sql);
    }

    public static void addToSaveServicePreparedStatement(PreparedStatement ps, Service service) throws Exception {

        int col = 1;
        ps.setString(col++, service.getId().toString());
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
        if (!Strings.isNullOrEmpty(service.getPublisherConfigName())) {
            ps.setString(col++, service.getPublisherConfigName());
        } else {
            ps.setNull(col++, Types.VARCHAR);
        }

        //keep the notes column synced with the notes tag
        String notes = null;
        if (service.getTags() != null) {
            notes = service.getTags().get(NOTES_KEY);
        }

        if (!Strings.isNullOrEmpty(notes)) {
            ps.setString(col++, notes);
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
        if (service.getOrganisationType() != null) {
            ps.setString(col++, service.getOrganisationType().getCode());
        } else {
            ps.setNull(col++, Types.VARCHAR);
        }
        if (hasNewCols()) {
            if (service.getAlias() != null) {
                ps.setString(col++, service.getAlias());
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (service.getTags() != null) {
                String tagsJson = ObjectMapperPool.getInstance().writeValueAsString(service.getTags());
                ps.setString(col++, tagsJson);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
        }
    }


    public Service getById(UUID id) throws Exception {
        Connection connection = ConnectionManager.getAdminConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT " + getSelectColumns()
                    + " FROM service"
                    + " WHERE id = ?";

            ps = connection.prepareStatement(sql);
            ps.setString(1, id.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Service ret = factoryFromResultSet(rs);
                convertServiceIfRequired(ret);
                return ret;

            } else {
                return null;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    private static Service factoryFromResultSet(ResultSet rs) throws Exception {

        int col = 1;
        Service ret = new Service();
        ret.setId(UUID.fromString(rs.getString(col++)));
        ret.setName(rs.getString(col++));
        ret.setLocalId(rs.getString(col++));
        ret.setEndpoints(rs.getString(col++));
        ret.setPublisherConfigName(rs.getString(col++));
        String notes = rs.getString(col++);
        ret.setPostcode(rs.getString(col++));
        ret.setCcgCode(rs.getString(col++));

        String orgTypeCode = rs.getString(col++);
        if (orgTypeCode != null) {
            ret.setOrganisationType(OrganisationType.fromCode(orgTypeCode));
        }

        if (hasNewCols()) {
            ret.setAlias(rs.getString(col++));

            String tagsJson = rs.getString(col++);
            if (tagsJson != null) {
                Map<String, String> tags = ObjectMapperPool.getInstance().readValue(tagsJson, new TypeReference<Map<String, String>>() {});
                ret.setTags(tags);
            }
        }

        //if we have notes, just carry them over into the tags
        if (!Strings.isNullOrEmpty(notes)) {
            Map<String, String> tags = ret.getTags();
            if (tags == null) {
                tags = new HashMap<>();
                ret.setTags(tags);
            }
            tags.put(NOTES_KEY, notes);
        }

        return ret;
    }

    public void delete(Service service) throws Exception {

        Connection connection = ConnectionManager.getAdminConnection();
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM service"
                    + " WHERE id = ?";;

            ps = connection.prepareStatement(sql);

            ps.setString(1, service.getId().toString());

            ps.executeUpdate();
            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    private static String getSelectColumns() throws Exception {
        if (hasNewCols()) {
            return "id, name, local_id, endpoints, publisher_config_name, notes, postcode, ccg_code, organisation_type, alias, tags";
        } else {
            return "id, name, local_id, endpoints, publisher_config_name, notes, postcode, ccg_code, organisation_type";
        }
    }


    public List<Service> getAll() throws Exception {
        Connection connection = ConnectionManager.getAdminConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT " + getSelectColumns()
                    + " FROM service";

            ps = connection.prepareStatement(sql);

            List<Service> ret = new ArrayList<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ret.add(factoryFromResultSet(rs));
            }

            convertServicesIfRequired(ret);

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    public List<Service> search(String searchData) throws Exception {
        Connection connection = ConnectionManager.getAdminConnection();
        PreparedStatement ps = null;
        try {
            if (hasNewCols()) {
                String sql = "SELECT " + getSelectColumns()
                        + " FROM service"
                        + " WHERE name LIKE ?"
                        + " OR alias LIKE ?"
                        + " OR local_id = ?";

                ps = connection.prepareStatement(sql);

                int col = 1;
                ps.setString(col++, searchData + "%");
                ps.setString(col++, searchData + "%");
                ps.setString(col++, searchData);

            } else {
                String sql = "SELECT " + getSelectColumns()
                        + " FROM service"
                        + " WHERE name LIKE ?"
                        + " OR local_id = ?";

                ps = connection.prepareStatement(sql);

                int col = 1;
                ps.setString(col++, searchData + "%");
                ps.setString(col++, searchData);
            }

            List<Service> ret = new ArrayList<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ret.add(factoryFromResultSet(rs));
            }

            convertServicesIfRequired(ret);

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    public Service getByLocalIdentifier(String localIdentifier) throws Exception {
        Connection connection = ConnectionManager.getAdminConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT " + getSelectColumns()
                    + " FROM service"
                    + " WHERE local_id = ?";

            ps = connection.prepareStatement(sql);
            ps.setString(1, localIdentifier);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Service ret = factoryFromResultSet(rs);
                convertServiceIfRequired(ret);
                return ret;

            } else {
                return null;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
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
