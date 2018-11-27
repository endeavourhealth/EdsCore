package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.PublishedFileRecord;
import org.endeavourhealth.core.database.dal.audit.models.PublishedFileType;

import java.util.List;
import java.util.UUID;

public interface PublishedFileDalI {

    //file types
    int findOrCreateFileTypeId(PublishedFileType possibleNewFileType) throws Exception;

    //file instances
    Integer findFileAudit(UUID serviceId, UUID systemId, UUID exchangeId, int fileTypeId, String filePath) throws Exception;
    int auditFile(UUID serviceId, UUID systemId, UUID exchangeId, int fileTypeId, String filePath) throws Exception;

    //file rows
    PublishedFileRecord findRecordAuditForRow(int fileAuditId, int rowIndex) throws Exception;
    void auditFileRows(List<PublishedFileRecord> records) throws Exception;
}
