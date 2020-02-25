package org.endeavourhealth.core.database.dal.usermanager.caching;


import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.DocumentationDalI;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreDocumentationDal;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.DocumentationEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentationCache {

    private static Map<String, DocumentationEntity> documentationMap = new ConcurrentHashMap<>();

    private static DocumentationDalI repository = DalProvider.factoryDSMDocumentationDal();

    public static List<DocumentationEntity> getDocumentDetails(List<String> documents) throws Exception {
        List<DocumentationEntity> documentationEntities = new ArrayList<>();
        List<String> missingDocuments = new ArrayList<>();

        for (String doc : documents) {
            DocumentationEntity docInMap = documentationMap.get(doc);
            if (docInMap != null) {
                documentationEntities.add(docInMap);
            } else {
                missingDocuments.add(doc);
            }
        }

        if (missingDocuments.size() > 0) {
            List<DocumentationEntity> entities = repository.getDocumentsFromList(missingDocuments);

            for (DocumentationEntity doc : entities) {
                documentationMap.put(doc.getUuid(), doc);
                documentationEntities.add(doc);
            }
        }

        CacheManager.startScheduler();

        return documentationEntities;

    }

    public static DocumentationEntity getDocumentDetails(String documentId) throws Exception {

        DocumentationEntity documentationEntity = documentationMap.get(documentId);
        if (documentationEntity == null) {
            documentationEntity = repository.getDocument(documentId);
            documentationMap.put(documentationEntity.getUuid(), documentationEntity);
        }

        CacheManager.startScheduler();

        return documentationEntity;

    }

    public static void clearDocumentCache(String documentId) throws Exception {
        documentationMap.remove(documentId);
    }

    public static void flushCache() throws Exception {
        documentationMap.clear();
    }
}
