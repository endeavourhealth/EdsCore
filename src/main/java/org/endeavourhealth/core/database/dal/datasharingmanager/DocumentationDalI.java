package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.DocumentationEntity;

import java.util.List;

public interface DocumentationDalI {

    public DocumentationEntity getDocument(String uuid) throws Exception;
    public List<DocumentationEntity> getDocumentsFromList(List<String> documents) throws Exception;

}
