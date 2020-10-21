package org.endeavourhealth.core.database.rdbms.publisherTransform;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.common.utility.FileHelper;
import org.endeavourhealth.common.utility.FileInfo;
import org.endeavourhealth.core.database.dal.audit.models.PublishedFile;
import org.endeavourhealth.core.database.dal.audit.models.PublishedFileColumn;
import org.endeavourhealth.core.database.dal.audit.models.PublishedFileRecord;
import org.endeavourhealth.core.database.dal.audit.models.PublishedFileType;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.database.dal.publisherTransform.SourceFileMappingDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMapping;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsResourceFieldMappings;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsResourceFieldMappingsS3;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

public class RdbmsSourceFileMappingDal implements SourceFileMappingDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsSourceFileMappingDal.class);

    //private static final String CSV_DELIM = "|";

    private final Random random = new Random(System.currentTimeMillis());
    private static Integer cachedPercentageToSendToFhirAudit = null;
    private static long cachedPercentageExpiry = -1;
    private static String s3BucketPath = "s3://discovery-antar/"; //TODO this should be read from property file
    //private static final FHIRAuditUtil propertyUtil = new FHIRAuditUtil();
    //private static final String propFileName = "fhir_audit.properties";


    public List<ResourceFieldMapping> findFieldMappings(UUID serviceId, ResourceType resourceType, UUID resourceId) throws Exception {
        return findFieldMappingsForField(serviceId, resourceType, resourceId, null);
    }

    public List<ResourceFieldMapping> findFieldMappingsForField(UUID serviceId, ResourceType resourceType, UUID resourceId, String specificField) throws Exception {

        //retrieve the resource mappings audit objects
        List<AuditWrapper> resourceAudits = findResourceMappings(serviceId, resourceType, resourceId);

        //from the audits, find the distinct IDs of the audit records we'll need
        List<PublishedFileRecord> publishedRecords = findPublisherRecords(resourceAudits, specificField, serviceId);

        //retrieve the record and file meta data from the audit DB
        Map<Integer, PublishedFile> hmPublishedFiles = findPublishedFiles(publishedRecords);
        Map<Integer, PublishedFileType> hmPublishedFileTypes = findPublishedFileTypes(hmPublishedFiles);

        //if we've got any content pointing to old-style audits, in the publisher transform DB, retrieve them
        /*Map<Long, RdbmsSourceFileRecord> hmOldStyleRecords = null;
        Map<Integer, String> hmOldStyleSourceFileNames = null;

        if (!oldStyleRecordIds.isEmpty()) {
            hmOldStyleRecords = findOldStyleSourceFileRecords(serviceId, oldStyleRecordIds);
            hmOldStyleSourceFileNames = findOldStyleSourceFileNames(serviceId, hmOldStyleRecords);
        }*/

        List<ResourceFieldMapping> ret = createFileMappingObjects(resourceAudits, publishedRecords, hmPublishedFiles, hmPublishedFileTypes, specificField);

        //the above fn doesn't ahve the resource ID or type in scope so we them on the objects now
        for (ResourceFieldMapping obj: ret) {
            obj.setResourceId(resourceId);
            obj.setResourceType(resourceType.toString());
        }

        return ret;
    }


    private Map<Integer, PublishedFileType> findPublishedFileTypes(Map<Integer, PublishedFile> hmPublishedFiles) throws Exception {

        Map<Integer, PublishedFileType> ret = new HashMap<>();
        if (hmPublishedFiles.isEmpty()) {
            return ret;
        }

        //find distinct file IDs from records
        Set<Integer> distinctTypeIds = new HashSet<>();
        for (Integer fileId: hmPublishedFiles.keySet()) {
            PublishedFile file = hmPublishedFiles.get(fileId);
            distinctTypeIds.add(file.getPublishedFileTypeId());
        }

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT id, file_type, variable_column_delimiter, variable_column_quote, variable_column_escape,"
                    + " column_name, fixed_column_start, fixed_column_length"
                    + " FROM published_file_type t"
                    + " INNER JOIN published_file_type_column c"
                    + " ON c.published_file_type_id = t.id"
                    + " WHERE t.id IN (";
            for (int i=0; i<distinctTypeIds.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";
            sql += " ORDER BY t.id, c.column_index";

            ps = connection.prepareStatement(sql);

            int col = 1;
            for (Integer id: distinctTypeIds) {
                ps.setLong(col++, id);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                col = 1;
                int id = rs.getInt(col++);
                String fileType = rs.getString(col++);
                String variableColDelimiter = rs.getString(col++);
                String variableColQuote = rs.getString(col++);
                String variableColEscape = rs.getString(col++);
                String columnName = rs.getString(col++);

                Integer fixedColStart = null;
                int val = rs.getInt(col++);
                if (!rs.wasNull()) {
                    fixedColStart = new Integer(val);
                }

                Integer fixedColLength = null;
                val = rs.getInt(col++);
                if (!rs.wasNull()) {
                    fixedColLength = new Integer(val);
                }

                PublishedFileType typeObj = ret.get(new Integer(id));
                if (typeObj == null) {
                    typeObj = new PublishedFileType();
                    typeObj.setFileType(fileType);;
                    if (variableColDelimiter != null) {
                        typeObj.setVariableColumnDelimiter(variableColDelimiter.charAt(0));
                    }
                    if (variableColQuote != null) {
                        typeObj.setVariableColumnQuote(variableColQuote.charAt(0));
                    }
                    if (variableColEscape != null) {
                        typeObj.setVariableColumnEscape(variableColEscape.charAt(0));
                    }

                    ret.put(new Integer(id), typeObj);
                }

                PublishedFileColumn colObj = new PublishedFileColumn();
                colObj.setColumnName(columnName);
                colObj.setFixedColumnStart(fixedColStart);
                colObj.setFixedColumnLength(fixedColLength);

                typeObj.getColumns().add(colObj);
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }

        return ret;
    }

    private List<ResourceFieldMapping> createFileMappingObjects(List<AuditWrapper> resourceAudits,
                                                                List<PublishedFileRecord> publishedRecords,
                                                                Map<Integer, PublishedFile> hmPublishedFiles,
                                                                Map<Integer, PublishedFileType> hmPublishedFileTypes,
                                                                //Map<Long, RdbmsSourceFileRecord> hmOldStyleRecords,
                                                                //Map<Integer, String> hmOldStyleSourceFileNames,
                                                                String specificField) throws Exception {

        List<ResourceFieldMapping> ret = new ArrayList<>();

        Set<String> fieldsDoneSet = new HashSet<>();

        //hash the published records so we don't have to iterate through the list
        Map<String, PublishedFileRecord> hmPublishedRecords = new HashMap<>();
        for (PublishedFileRecord record: publishedRecords) {
            hmPublishedRecords.put("" + record.getPublishedFileId() + "_" + record.getRecordNumber(), record);
        }

        for (int i=resourceAudits.size()-1; i>=0; i--) {
            AuditWrapper auditWrapper = resourceAudits.get(i);
            ResourceFieldMappingAudit audit = auditWrapper.getAudit();

            Set<String> fieldsDoneThisAudit = new HashSet<>();

            for (ResourceFieldMappingAudit.ResourceFieldMappingAuditRow row: audit.getAudits()) {

                for (ResourceFieldMappingAudit.ResourceFieldMappingAuditCol col: row.getCols()) {
                    String field = col.getField();
                    int colIndex = col.getCol();

                    //if we're only interested in a specific field, then skip everything else
                    if (specificField != null
                            && !specificField.equalsIgnoreCase(field)) {
                        continue;
                    }

                    //only include mappings for a field for the first audit object we hit that field name in
                    if (fieldsDoneSet.contains(field)) {
                        continue;
                    }
                    fieldsDoneThisAudit.add(field); //add to a set for this specific audit

                    ResourceFieldMapping obj = new ResourceFieldMapping();
                    obj.setCreatedAt(auditWrapper.getCreated());
                    obj.setVersion(auditWrapper.getVersion());
                    obj.setResourceField(field);
                    obj.setSourceFileColumn(new Integer(colIndex));

                    //if we have a publisher record ID, then it's a new-style audit
                    PublishedFileRecord record = hmPublishedRecords.get("" + row.getFileId() + "_" + row.getRecord());
                    int fileId = record.getPublishedFileId();
                    PublishedFile file = hmPublishedFiles.get(new Integer(fileId));
                    int fileTypeId = file.getPublishedFileTypeId();
                    PublishedFileType fileType = hmPublishedFileTypes.get(new Integer(fileTypeId));

                    String sourceFilePath = file.getFilePath();
                    obj.setSourceFileName(sourceFilePath);

                    int recordNumber = record.getRecordNumber();
                    obj.setSourceFileRow(new Integer(recordNumber));

                    String sourceValue = findSourceValue(record, file, fileType, colIndex);
                    obj.setValue(sourceValue);

                    ret.add(obj);
                }
            }

            //once we've finished this audit object, add the set of all fields encountered to the larger set
            fieldsDoneSet.addAll(fieldsDoneThisAudit);
        }

        return ret;
    }

    private String findSourceValue(PublishedFileRecord record, PublishedFile file, PublishedFileType fileType, int colIndex) throws Exception {

        String filePath = file.getFilePath();
        long byteStart = record.getByteStart();
        int byteLen = record.getByteLength();

        String line = FileHelper.readCharactersFromSharedStorage(filePath, byteStart, byteLen);

        String value = null;

        //if we have no delimiter, it's a fixed, width file
        if (fileType.getVariableColumnDelimiter() == null) {
            PublishedFileColumn col = fileType.getColumns().get(colIndex);
            int colStart = col.getFixedColumnStart() - 1; //the column start is one-based, so subtract one
            int colEnd = col.getFixedColumnLength() + colStart;
            value = line.substring(colStart, colEnd);

        } else {
            //if not fixed-wdith, then parse using a CSV parser
            //compose a suitable CSV format from what we know of the type
            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withDelimiter(fileType.getVariableColumnDelimiter())
                    .withQuote(fileType.getVariableColumnQuote())
                    .withEscape(fileType.getVariableColumnEscape())
                    .withQuoteMode(QuoteMode.MINIMAL);

            CSVParser parser = CSVParser.parse(line, csvFormat);
            Iterator<CSVRecord> iterator = parser.iterator();
            CSVRecord csvRecord = iterator.next();
            value = csvRecord.get(colIndex);
            parser.close();
        }

        return value;
    }

    private Map<Integer, PublishedFile> findPublishedFiles(List<PublishedFileRecord> publishedRecords) throws Exception {

        Map<Integer, PublishedFile> ret = new HashMap<>();
        if (publishedRecords.isEmpty()) {
            return ret;
        }

        //find distinct file IDs from records
        Set<Integer> distinctFileIds = new HashSet<>();
        for (PublishedFileRecord publishedRecord: publishedRecords) {
            distinctFileIds.add(publishedRecord.getPublishedFileId());
        }

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT id, file_path, published_file_type_id"
                    + " FROM published_file"
                    + " WHERE id IN (";
            for (int i=0; i<distinctFileIds.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;
            for (Integer id: distinctFileIds) {
                ps.setLong(col++, id);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                col = 1;
                int id = rs.getInt(col++);
                String filePath = rs.getString(col++);
                int typeId = rs.getInt(col++);

                PublishedFile obj = new PublishedFile();
                obj.setId(id);
                obj.setFilePath(filePath);
                obj.setPublishedFileTypeId(typeId);

                ret.put(new Integer(id), obj);
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }

        return ret;
    }

    private void populatePublishedFileRecords(List<PublishedFileRecord> publishedRecords) throws Exception {

        if (publishedRecords.isEmpty()) {
            return;
        }

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT published_file_id, record_number, byte_start, byte_length"
                    + " FROM published_file_record"
                    + " WHERE";

            for (int i=0; i<publishedRecords.size(); i++) {
                if (i>0) {
                    sql += " OR";
                }
                sql += " (published_file_id = ? AND record_number = ?)";
            }

            ps = connection.prepareStatement(sql);

            int col = 1;
            for (int i=0; i<publishedRecords.size(); i++) {
                PublishedFileRecord record = publishedRecords.get(i);
                ps.setInt(col++, record.getPublishedFileId());
                ps.setInt(col++, record.getRecordNumber());
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                col = 1;
                int publishedFileId = rs.getInt(col++);
                int recordNumber = rs.getInt(col++);
                long byteStart = rs.getLong(col++);
                int byteLength = rs.getInt(col++);

                for (PublishedFileRecord record: publishedRecords) {
                    if (record.getPublishedFileId() == publishedFileId
                            && record.getRecordNumber() == recordNumber) {
                        record.setByteStart(byteStart);
                        record.setByteLength(byteLength);
                    }
                }
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }


    /*private Map<Integer, String> findOldStyleSourceFileNames(UUID serviceId, Map<Long, RdbmsSourceFileRecord> oldStyleFileRecordsMap) throws Exception {

        //find distinct file IDs from the records
        Set<Integer> fileIds = new HashSet<>();

        for (Long recordId: oldStyleFileRecordsMap.keySet()) {
            RdbmsSourceFileRecord oldStyleRecord = oldStyleFileRecordsMap.get(recordId);
            int fileId = oldStyleRecord.getSourceFileId();
            fileIds.add(new Integer(fileId));
        }
        List<Integer> distinctFileIds = new ArrayList<>(fileIds);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsSourceFile c"
                    + " where c.id IN :id_list";

            Map<Integer, String> ret = new HashMap<>();

            Query query = entityManager.createQuery(sql, RdbmsSourceFile.class)
                    .setParameter("id_list", distinctFileIds);

            List<RdbmsSourceFile> results = query.getResultList();
            for (RdbmsSourceFile result: results) {
                ret.put(result.getId(), result.getFilePath());
            }

            return ret;
        } finally {
            entityManager.close();
        }
    }*/


    private static String createAuditDir(String topLevelPath, UUID serviceId, String resourceType, UUID resourceId) {
        String ret = FilenameUtils.concat(topLevelPath, serviceId.toString());
        ret = FilenameUtils.concat(ret, resourceType);
        ret = FilenameUtils.concat(ret, resourceId.toString());
        return ret;
    }

    private List<AuditWrapper> findResourceMappings(UUID serviceId, ResourceType resourceType, UUID resourceId) throws Exception {

        List<AuditWrapper> ret = new ArrayList<>();

        //the audit history of a resource may be in the audit storage path or in the DB, depending on the configuration state,
        //so we need to look in both places (for the time being)
        /*String topLevelStoragePath = getAuditStoragePath();
        if (!Strings.isNullOrEmpty(topLevelStoragePath)) {
            findResourceMappingsFromDisk(ret, topLevelStoragePath, serviceId, resourceType, resourceId);
        }*/

        //look in the global FHIR audit DB
        try {
            EntityManager entityManager = ConnectionManager.getFhirAuditEntityManager();
            try {
                findResourceMappingsFromDatabase(ret, entityManager, serviceId, resourceType, resourceId);
            } finally {
                entityManager.close();
            }

        } catch (Exception ex) {
            //if the DB isn't configured, we'll get an exception
        }

        //also look in publisher transform DB for any FHIR mapping audits
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            findResourceMappingsFromDatabase(ret, entityManager, serviceId, resourceType, resourceId);

            //get the mappings from  publisher_transform_a.resource_field_mappings_s3
            findResourceMappingsFromNewDatabase(ret, entityManager, serviceId, resourceType, resourceId);
        } finally {
            entityManager.close();
        }

        //get the mappings from AWS S3
        try {
            try {
                findResourceMappingsFromS3(ret, entityManager, serviceId, resourceType, resourceId);
            } finally {
                entityManager.close();
            }

        } catch (Exception ex) {
            //if the DB isn't configured, we'll get an exception
        }

        //we always need the mappings in date order, since we only need to retrieve the most recent audit for each JSON field in the resource
        ret.sort((a, b) -> a.getCreated().compareTo(b.getCreated()));

        return ret;
    }

    private void findResourceMappingsFromDatabase(List<AuditWrapper> wrappers, EntityManager entityManager, UUID serviceId, ResourceType resourceType, UUID resourceId) throws Exception {
        String sql = "select c"
                + " from"
                + " RdbmsResourceFieldMappings c"
                + " where c.resourceId = :resource_id"
                + " and c.resourceType = :resource_type";

        Query query = entityManager.createQuery(sql, RdbmsResourceFieldMappings.class)
                .setParameter("resource_id", resourceId.toString())
                .setParameter("resource_type", resourceType.toString());

        List<RdbmsResourceFieldMappings> dbObjs = query.getResultList();
        for (RdbmsResourceFieldMappings dbObj: dbObjs) {

            Date d = dbObj.getCreatedAt();
            String json = dbObj.getMappingsJson();
            ResourceFieldMappingAudit audit = ResourceFieldMappingAudit.readFromJson(json);
            String version = dbObj.getVersion();

            AuditWrapper wrapper = new AuditWrapper(audit, UUID.fromString(version), d);
            wrappers.add(wrapper);
        }
    }

    private void findResourceMappingsFromNewDatabase(List<AuditWrapper> wrappers, EntityManager entityManager, UUID serviceId, ResourceType resourceType, UUID resourceId) throws Exception {
        String sql = "select c"
                + " from"
                + " RdbmsResourceFieldMappingsS3 c"
                + " where c.resourceId = :resource_id"
                + " and c.resourceType = :resource_type";

        Query query = entityManager.createQuery(sql, RdbmsResourceFieldMappingsS3.class)
                .setParameter("resource_id", resourceId.toString())
                .setParameter("resource_type", resourceType.toString());

        List<RdbmsResourceFieldMappingsS3> dbObjs = query.getResultList();
        for (RdbmsResourceFieldMappingsS3 dbObj: dbObjs) {

            Date d = dbObj.getCreatedAt();
            String json = dbObj.getMappingsJson();
            ResourceFieldMappingAudit audit = ResourceFieldMappingAudit.readFromJson(json);
            String version = dbObj.getVersion();

            AuditWrapper wrapper = new AuditWrapper(audit, UUID.fromString(version), d);
            wrappers.add(wrapper);
        }
    }
    /*
     * Read all files from S3 bucket and search content
     */
    private void findResourceMappingsFromS3(List<AuditWrapper> wrappers, EntityManager entityManager, UUID serviceId, ResourceType resourceType, UUID resourceId) throws Exception {

        //List<FileInfo> s3Files = FileHelper.listFilesInSharedStorageWithInfo(propertyUtil.getProperty(
                //propFileName, FHIRAuditConstants.S3_PATH);

        List<FileInfo> s3Files = FileHelper.listFilesInSharedStorageWithInfo(s3BucketPath);

        for (FileInfo s3Info : s3Files) {
            String s3Path = s3Info.getFilePath();
            searchMappingRecordsInS3( wrappers, s3Path, resourceId, resourceType);

        }
    }

    public void searchMappingRecordsInS3(List<AuditWrapper> wrappers, String s3Path, UUID resourceIdIn, ResourceType resourceTypeIn) throws IOException {
        try {
            LOG.info("searchMappingRecordsInS3 s3Path " + s3Path);
            InputStreamReader reader = FileHelper.readFileReaderFromSharedStorage(s3Path);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());

            String serviceId = null;
            Date d = null;
            String json = null;
            String version = null;
            StringBuffer sb = new StringBuffer();
            Iterator<CSVRecord> iterator = csvParser.iterator();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String resourceId = resourceIdIn != null ? resourceIdIn.toString() : "";
            String resourceType = resourceTypeIn != null ? resourceTypeIn.toString() : "";
            LOG.info("Search parameters: resourceId " + resourceId + " resourceType " + resourceType);
            // CSV header service_id,resource_id,resource_type,version,created_at,mappings_json
            while (iterator.hasNext()) {
                CSVRecord record = iterator.next();
                if (record.toString().contains(resourceId) || record.toString().contains(resourceType)) {
                    serviceId = record.get(0); //service_id
                    version = record.get(3); //version
                    d = sdf.parse(record.get(4)); //createdAt
                    if (record != null && record.size() >= 5) {
                        for (int i = 5; i < record.size(); i++) {
                            sb.append(record.get(i));
                        }
                    }
                    json = sb.toString();
                    ResourceFieldMappingAudit audit = ResourceFieldMappingAudit.readFromJson(json);
                    AuditWrapper wrapper = new AuditWrapper(audit, UUID.fromString(version), d);
                    wrappers.add(wrapper);
                }
            }
        } catch (Exception e) {
            LOG.error(" Error " + e.getMessage());
        }
    }
    /*private Map<Long, RdbmsSourceFileRecord> findOldStyleSourceFileRecords(UUID serviceId, Set<Long> oldStyleRecordAuditIds) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsSourceFileRecord c"
                    + " where c.id IN :audit_ids";

            Query query = entityManager.createQuery(sql, RdbmsSourceFileRecord.class)
                    .setParameter("audit_ids", new ArrayList<>(oldStyleRecordAuditIds));

            Map<Long, RdbmsSourceFileRecord> ret = new HashMap<>();

            List<RdbmsSourceFileRecord> results = query.getResultList();
            for (RdbmsSourceFileRecord result: results) {
                ret.put(result.getId(), result);
            }

            return ret;

        } finally {
            entityManager.close();
        }
    }*/


    /**
     * finds the distinct publisher records IDs (and old-style record IDs) for the given resource audits,
     * but has extra logic to ignore any record IDs that won't be required because a later audit overwrites that data
     */
    private List<PublishedFileRecord> findPublisherRecords(List<AuditWrapper> resourceAudits, String specificField, UUID serviceId) throws Exception {

        List<PublishedFileRecord> publishedRecords = new ArrayList<>();

        Set<String> fieldsDoneSet = new HashSet<>();

        for (int i=resourceAudits.size()-1; i>=0; i--) {
            AuditWrapper auditWrapper = resourceAudits.get(i);
            ResourceFieldMappingAudit audit = auditWrapper.getAudit();

            Set<String> fieldsDoneThisAudit = new HashSet<>();

            for (ResourceFieldMappingAudit.ResourceFieldMappingAuditRow row: audit.getAudits()) {

                for (ResourceFieldMappingAudit.ResourceFieldMappingAuditCol col: row.getCols()) {
                    String field = col.getField();

                    //if only looking for a specific field, and we don't match, skip it
                    if (specificField != null
                            && !field.equalsIgnoreCase(specificField)) {
                        continue;
                    }

                    //if we've already handled this field on a previous audit, skip it
                    if (fieldsDoneSet.contains(field)) {
                        continue;
                    }

                    fieldsDoneThisAudit.add(field);

                    //until all old-style audits are converted over, we need to handle both the new style
                    //and the old style (that audited the full publisher record in the publisher transform DB)
                    if (row.getOldStyleAuditId() != null) {
                        //if we've got an unconverted old-style audit, then we can look up the new-style details
                        //in the mapping table on each database
                        convertOldStyleAuditToNew(row, serviceId);
                        //oldStyleRecordIds.add(oldStyleAuditId);
                    }

                    int publishedFileId = row.getFileId();
                    int recordNumber = row.getRecord();

                    if (publishedFileId == 0) {
                        throw new Exception("No published file ID or old-style audit ID for audit for resource");
                    }

                    PublishedFileRecord record = new PublishedFileRecord();
                    record.setPublishedFileId(publishedFileId);
                    record.setRecordNumber(recordNumber);
                    publishedRecords.add(record);
                }
            }

            //append all the fields we just covered to the set so far
            fieldsDoneSet.addAll(fieldsDoneThisAudit);
        }

        //we've only populated some values on the records, so we need to hit the DB to get the rest
        populatePublishedFileRecords(publishedRecords);

        return publishedRecords;
    }

    private void convertOldStyleAuditToNew(ResourceFieldMappingAudit.ResourceFieldMappingAuditRow row, UUID serviceId) throws Exception {
        Long oldSourceRecordId = row.getOldStyleAuditId();

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT record_number, published_file_id"
                    + " FROM source_file_record_audit"
                    + " WHERE id = ?";
            ps = connection.prepareStatement(sql);

            ps.setLong(1, oldSourceRecordId.longValue());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int recordNumber = rs.getInt(1);
                int publishedFileId = rs.getInt(2);

                row.setFileId(publishedFileId);
                row.setRecord(recordNumber);
                row.setOldStyleAuditId(null);
                return;
            }

            //if the population of the above table hasn't completed yet, then check the old table
            /*ps.close();

            sql = "SELECT r.source_location, f.new_published_file_id"
                    + " FROM source_file_record r"
                    + " INNER JOIN source_file f"
                    + " ON r.source_file_id = f.id"
                    + " WHERE r.id = ?";
            ps = connection.prepareStatement(sql);

            ps.setLong(1, oldSourceRecordId.longValue());

            rs = ps.executeQuery();
            if (rs.next()) {
                String locationStr = rs.getString(1);
                int publishedFileId = rs.getInt(2);

                row.setFileId(publishedFileId);
                row.setRecord(Integer.parseInt(locationStr));
                row.setOldStyleAuditId(null);
                return;
            }*/

            throw new Exception("Failed to find published file details for old-style audit ID " + oldSourceRecordId);

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }


    @Override
    public void saveResourceMappings(Map<ResourceWrapper, ResourceFieldMappingAudit> audits) throws Exception {

        if (audits == null || audits.isEmpty()) {
            return;
        }

        //if configured, use the common FHIR audit DB
        //for the time-being, attempt to distribute the load between the publisher transform DB and the global
        //FHIR audit DB. The load is too much for the global one, as it causes a massive bottleneck, but it's there
        //so send some traffic to it.
        EntityManager entityManager = null;
        int r = random.nextInt(100);
        int percentage = getPercentageToSendToFhirAudit();
        boolean useFhirAuditDb = r < percentage;

        //call separate functions like this so we can see which route it's taking in stack dumps
        if (useFhirAuditDb) {
            saveResourceMappingsToFhirAudit(audits);
        } else {
            saveResourceMappingsToPublisherTransform(audits);
        }

        /*String auditStoragePath = getAuditStoragePath();
        if (!Strings.isNullOrEmpty(auditStoragePath)) {
            //if we have an audit path, write the audits to that
            saveResourceMappingsToFile(audits, auditStoragePath);

        } else {
            //if we don't have an audit path, write the audits to the DB
            saveResourceMappingsToDatabase(audits);
        }*/
    }

    private void saveResourceMappingsToPublisherTransform(Map<ResourceWrapper, ResourceFieldMappingAudit> audits) throws Exception {
        saveResourceMappingsToDatabase(audits, false);
    }

    private void saveResourceMappingsToFhirAudit(Map<ResourceWrapper, ResourceFieldMappingAudit> audits) throws Exception {
        saveResourceMappingsToDatabase(audits, true);
    }

    private void saveResourceMappingsToDatabaseOld(Map<ResourceWrapper, ResourceFieldMappingAudit> audits, boolean useFhirAuditDb) throws Exception {

        UUID serviceId = null;
        for (ResourceWrapper wrapper : audits.keySet()) {
            if (serviceId == null) {
                serviceId = wrapper.getServiceId();
            } else if (!serviceId.equals(wrapper.getServiceId())) {
                throw new IllegalArgumentException("Can't save audits for multiple services at once");
            }
        }

        EntityManager entityManager = null;
        if (useFhirAuditDb) {
            entityManager = ConnectionManager.getFhirAuditEntityManager();
        } else {
            entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        }

        SessionImpl session = (SessionImpl) entityManager.getDelegate();
        Connection connection = session.connection();

        PreparedStatement ps = null;
        try {

            String sql = "INSERT INTO resource_field_mappings"
                    + " (resource_id, resource_type, created_at, version, mappings_json)"
                    + " VALUES (?, ?, ?, ?, ?)";
            //note this entity is always inserted, never updated, so there's no handler for errors with an insert, like resource_current

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (ResourceWrapper wrapper : audits.keySet()) {
                ResourceFieldMappingAudit audit = audits.get(wrapper);
                String mappingJson = audit.writeToJson();

                int col = 1;
                ps.setString(col++, wrapper.getResourceId().toString());
                ps.setString(col++, wrapper.getResourceType());
                ps.setTimestamp(col++, new java.sql.Timestamp(wrapper.getCreatedAt().getTime()));
                ps.setString(col++, wrapper.getVersion().toString());
                ps.setString(col++, mappingJson);

                ps.addBatch();
            }

            ps.executeBatch();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    private void saveResourceMappingsToDatabase(Map<ResourceWrapper, ResourceFieldMappingAudit> audits, boolean useFhirAuditDb) throws Exception {

        UUID serviceId = null;
        for (ResourceWrapper wrapper : audits.keySet()) {
            if (serviceId == null) {
                serviceId = wrapper.getServiceId();
            } else if (!serviceId.equals(wrapper.getServiceId())) {
                throw new IllegalArgumentException("Can't save audits for multiple services at once");
            }
        }
        // Changes to write audit mappings to new table publisher_transform_a.resource_field_mappings_s3
        EntityManager entityManager = null;
        entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

  /*      if (useFhirAuditDb) {
            entityManager = ConnectionManager.getFhirAuditEntityManager();
        } else {
            entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        }
*/
        SessionImpl session = (SessionImpl) entityManager.getDelegate();
        Connection connection = session.connection();

        PreparedStatement ps = null;
        try {

            //String sql = "INSERT INTO resource_field_mappings"
            String sql = "INSERT INTO resource_field_mappings_s3"
                    + " (resource_id, service_id, resource_type, created_at, version, mappings_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?)";
            //note this entity is always inserted, never updated, so there's no handler for errors with an insert, like resource_current

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (ResourceWrapper wrapper : audits.keySet()) {
                ResourceFieldMappingAudit audit = audits.get(wrapper);
                String mappingJson = audit.writeToJson();

                int col = 1;
                ps.setString(col++, wrapper.getResourceId().toString());
                ps.setString(col++, wrapper.getServiceId().toString());
                ps.setString(col++, wrapper.getResourceType());
                ps.setTimestamp(col++, new java.sql.Timestamp(wrapper.getCreatedAt().getTime()));
                ps.setString(col++, wrapper.getVersion().toString());
                ps.setString(col++, mappingJson);

                ps.addBatch();
            }

            ps.executeBatch();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    public static int getPercentageToSendToFhirAudit() {
        if (cachedPercentageToSendToFhirAudit == null
                || System.currentTimeMillis() > cachedPercentageExpiry) {

            //default to 10
            cachedPercentageToSendToFhirAudit = new Integer(10);

            //then overwrite with the setting
            try {
                JsonNode json = ConfigManager.getConfigurationAsJson("common_config", "queuereader");

                JsonNode node = json.get("fhir_audit_db_percentage");
                if (node != null) {
                    cachedPercentageToSendToFhirAudit = node.asInt();
                }

            } catch (Exception ex) {
                //if the config record is there, just log it out rather than throw an exception
                LOG.warn("No common queuereader config found in config DB with app_id queuereader and config_id common_config");
            }

            cachedPercentageExpiry = System.currentTimeMillis() + (5l * 60l * 1000l);
        }

        return cachedPercentageToSendToFhirAudit.intValue();
    }

}

class AuditWrapper {
    private ResourceFieldMappingAudit audit = null;
    private UUID version = null;
    private Date created = null;

    public AuditWrapper(ResourceFieldMappingAudit audit, UUID version, Date created) {
        this.audit = audit;
        this.version = version;
        this.created = created;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public UUID getVersion() {
        return version;
    }

    public Date getCreated() {
        return created;
    }
}