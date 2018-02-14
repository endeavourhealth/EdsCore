package org.endeavourhealth.core.database.dal.publisherTransform;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMapping;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.List;
import java.util.UUID;

public interface SourceFileMappingDalI {

    int findOrCreateFileTypeId(UUID serviceId, String typeDescription, List<String> columns) throws Exception;
    Integer findFileAudit(UUID serviceId, UUID systemId, UUID exchangeId, int fileTypeId, String filePath) throws Exception;
    int auditFile(UUID serviceId, UUID systemId, UUID exchangeId, int fileTypeId, String filePath) throws Exception;

    Long findRecordAuditIdForRow(UUID serviceId, int fileAuditId, int rowIndex) throws Exception;
    long auditFileRow(UUID serviceId, CSVParser parser, CSVRecord record, int recordLineNumber, int sourceFileId) throws Exception;

    void saveResourceMappings(UUID serviceId, ResourceWrapper resourceWrapper, ResourceFieldMappingAudit audit) throws Exception;
    List<ResourceFieldMapping> findFieldMappings(UUID serviceId, ResourceType resourceType, UUID resourceId) throws Exception;
    List<ResourceFieldMapping> findFieldMappingsForField(UUID serviceId, ResourceType resourceType, UUID resourceId, String field) throws Exception;
}
