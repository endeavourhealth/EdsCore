package org.endeavourhealth.core.database.dal.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.admin.models.ActiveItem;
import org.endeavourhealth.core.database.dal.admin.models.Item;
import org.endeavourhealth.core.database.dal.admin.models.Service;
import org.endeavourhealth.core.fhirStorage.ServiceInterfaceEndpoint;
import org.endeavourhealth.core.xml.QueryDocument.LibraryItem;
import org.endeavourhealth.core.xml.QueryDocument.System;
import org.endeavourhealth.core.xml.QueryDocument.TechnicalInterface;
import org.endeavourhealth.core.xml.QueryDocumentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class SystemHelper {

    private static final int SYSTEM_ITEM_TYPE = 9; //relates to item_type enum

    public static List<UUID> getSystemIdsForService(Service service) throws Exception {

        if (service == null) {
            throw new RuntimeException("Service cannot be null");
        }

        List<UUID> ret = new ArrayList<>();

        List<ServiceInterfaceEndpoint> endpoints = null;
        try {
            endpoints = ObjectMapperPool.getInstance().readValue(service.getEndpoints(), new TypeReference<List<ServiceInterfaceEndpoint>>() {});
            for (ServiceInterfaceEndpoint endpoint: endpoints) {
                UUID endpointSystemId = endpoint.getSystemUuid();
                ret.add(endpointSystemId);
            }
        } catch (Exception e) {
            throw new Exception("Failed to process endpoints from service " + service.getId());
        }

        return ret;
    }

    public static ServiceInterfaceEndpoint findEndpointForSoftware(Service service, String software) throws Exception {
        return findEndpointForSoftwareAndVersion(service, software, null);
    }

    public static ServiceInterfaceEndpoint findEndpointForSoftwareAndVersion(Service service, String software, String version) throws Exception {

        if (service == null
                || Strings.isNullOrEmpty(software)) {
            //note version CAN be null
            throw new RuntimeException("Service and software cannot be null");
        }

        List<ServiceInterfaceEndpoint> endpoints = service.getEndpointsList();
        for (ServiceInterfaceEndpoint endpoint: endpoints) {

            UUID endpointSystemId = endpoint.getSystemUuid();
            String endpointInterfaceId = endpoint.getTechnicalInterfaceUuid().toString();

            LibraryDalI libraryRepository = DalProvider.factoryLibraryDal();
            ActiveItem activeItem = libraryRepository.getActiveItemByItemId(endpointSystemId);
            Item item = libraryRepository.getItemByKey(endpointSystemId, activeItem.getAuditId());
            LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(item.getXmlContent());
            System system = libraryItem.getSystem();
            TechnicalInterface technicalInterface = getTechnicalInterface(system);
            String technicalInterfaceId = technicalInterface.getUuid();
            String technicalInterfaceFormat = technicalInterface.getMessageFormat();
            String technicalInterfaceVersion = technicalInterface.getMessageFormatVersion();

            if (endpointInterfaceId.equals(technicalInterfaceId) //not strictly necessary since systems only have one interface
                    && technicalInterfaceFormat.equalsIgnoreCase(software) //match message form
                    && (Strings.isNullOrEmpty(version)
                        || Pattern.matches(technicalInterfaceVersion, version))) {

                return endpoint;
            }
        }

        return null;
    }

    /**
     * finds a system from the "library" for a given name and version
     */
    public static System findSystemForSoftwareAndVersion(String software, String messageVersion) throws Exception {

        if (Strings.isNullOrEmpty(software)
                || Strings.isNullOrEmpty(messageVersion)) {
            throw new RuntimeException("Software and message version cannot be null");
        }

        LibraryDalI libraryDal = DalProvider.factoryLibraryDal();
        List<ActiveItem> activeItems = libraryDal.getActiveItemByTypeId(SYSTEM_ITEM_TYPE, false);
        for (ActiveItem activeItem: activeItems) {
            UUID itemId = activeItem.getItemId();
            UUID auditId = activeItem.getAuditId();
            Item item = libraryDal.getItemByKey(itemId, auditId);
            LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(item.getXmlContent());
            System system = libraryItem.getSystem();
            TechnicalInterface technicalInterface = getTechnicalInterface(system);
            String technicalInterfaceFormat = technicalInterface.getMessageFormat();
            String technicalInterfaceVersion = technicalInterface.getMessageFormatVersion();

            if (technicalInterfaceFormat.equalsIgnoreCase(software)
                && Pattern.matches(technicalInterfaceVersion, messageVersion)) {
                return system;
            }
        }

        return null;
    }

    /**
     * the TechnicalInterface class still supports multiple technical interfaces, but all the UI only supports one,
     * so just ensure there's one and return it
     */
    public static TechnicalInterface getTechnicalInterface(System system) throws Exception {

        if (system == null) {
            throw new RuntimeException("System cannot be null");
        }

        List<TechnicalInterface> interfaces = system.getTechnicalInterface();
        if (interfaces.isEmpty()) {
            throw new Exception("No technical interfaces found for system " + system.getName() + " " + system.getUuid());

        } else if (interfaces.size() > 1) {

            throw new Exception("More than one technical interface found for system " + system.getName() + " " + system.getUuid());
        }

        return interfaces.get(0);
    }
}
