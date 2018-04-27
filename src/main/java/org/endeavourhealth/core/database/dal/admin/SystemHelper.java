package org.endeavourhealth.core.database.dal.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.admin.models.ActiveItem;
import org.endeavourhealth.core.database.dal.admin.models.Item;
import org.endeavourhealth.core.database.dal.admin.models.Service;
import org.endeavourhealth.core.fhirStorage.JsonServiceInterfaceEndpoint;
import org.endeavourhealth.core.xml.QueryDocument.LibraryItem;
import org.endeavourhealth.core.xml.QueryDocument.System;
import org.endeavourhealth.core.xml.QueryDocument.TechnicalInterface;
import org.endeavourhealth.core.xml.QueryDocumentSerializer;

import java.util.List;
import java.util.UUID;

public class SystemHelper {

    public static UUID findSystemUuid(Service service, String software) throws Exception {

        List<JsonServiceInterfaceEndpoint> endpoints = null;
        String json = service.getEndpoints();
        try {
            endpoints = ObjectMapperPool.getInstance().readValue(json, new TypeReference<List<JsonServiceInterfaceEndpoint>>() {});
        } catch (Exception ex) {
            throw new Exception("Failed to deserialise JSON " + json, ex);
        }

        for (JsonServiceInterfaceEndpoint endpoint: endpoints) {

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
