package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMapping;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SourceFileMappingDalI {

    /*int findOrCreateFileTypeId(UUID serviceId, String typeDescription, List<String> columns) throws Exception;
    Integer findFileAudit(UUID serviceId, UUID systemId, UUID exchangeId, int fileTypeId, String filePath) throws Exception;
    int auditFile(UUID serviceId, UUID systemId, UUID exchangeId, int fileTypeId, String filePath) throws Exception;

    Long findRecordAuditIdForRow(UUID serviceId, int fileAuditId, int rowIndex) throws Exception;
    SourceFileRecord findSourceFileRecordRow(UUID serviceId, long auditId) throws Exception;
    void auditFileRow(UUID serviceId, SourceFileRecord record) throws Exception;
    void auditFileRows(UUID serviceId, List<SourceFileRecord> records) throws Exception;

    void saveResourceMappings(ResourceWrapper resourceWrapper, ResourceFieldMappingAudit audit) throws Exception;*/

    void saveResourceMappings(Map<ResourceWrapper, ResourceFieldMappingAudit> audits) throws Exception;
    List<ResourceFieldMapping> findFieldMappings(UUID serviceId, ResourceType resourceType, UUID resourceId) throws Exception;
    List<ResourceFieldMapping> findFieldMappingsForField(UUID serviceId, ResourceType resourceType, UUID resourceId, String field) throws Exception;
}
