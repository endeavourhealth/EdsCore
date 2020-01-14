package org.endeavourhealth.core.database.dal.admin;

import com.fasterxml.jackson.core.type.TypeReference;
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

public class SystemHelper {

    public static List<UUID> findSystemIds(Service service) throws Exception {
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

    public static UUID findSystemUuid(Service service, String software) throws Exception {

        List<ServiceInterfaceEndpoint> endpoints = null;
        String json = service.getEndpoints();
        try {
            endpoints = ObjectMapperPool.getInstance().readValue(json, new TypeReference<List<ServiceInterfaceEndpoint>>() {});
        } catch (Exception ex) {
            throw new Exception("Failed to deserialise JSON " + json, ex);
        }

        for (ServiceInterfaceEndpoint endpoint: endpoints) {

            UUID endpointSystemId = endpoint.getSystemUuid();
            String endpointInterfaceId = endpoint.getTechnicalInterfaceUuid().toString();

            LibraryDalI libraryRepository = DalProvider.factoryLibraryDal();
            ActiveItem activeItem = libraryRepository.getActiveItemByItemId(endpointSystemId);
            Item item = libraryRepository.getItemByKey(endpointSystemId, activeItem.getAuditId());
            LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(item.getXmlContent());
            System system = libraryItem.getSystem();

            for (TechnicalInterface technicalInterface: system.getTechnicalInterface()) {

                if (endpointInterfaceId.equals(technicalInterface.getUuid())
                        && technicalInterface.getMessageFormat().equalsIgnoreCase(software)) {

                    return endpointSystemId;
                }
            }
        }

        return null;
    }
}
