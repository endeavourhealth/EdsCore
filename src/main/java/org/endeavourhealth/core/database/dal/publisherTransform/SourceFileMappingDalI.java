package org.endeavourhealth.core.database.dal.publisherTransform;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMapping;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SourceFileMappingDalI {

    int auditFile(UUID serviceId, UUID systemId, UUID exchangeId, String filePath, String typeDescription, List<String> columns) throws Exception;
    Map<String, Long> auditCsvRecord(UUID serviceId, CSVParser parser, CSVRecord record, int sourceFileId) throws Exception;
    List<ResourceFieldMapping> findFieldMappings(UUID serviceId, ResourceType resourceType, UUID resourceId) throws Exception;

}
