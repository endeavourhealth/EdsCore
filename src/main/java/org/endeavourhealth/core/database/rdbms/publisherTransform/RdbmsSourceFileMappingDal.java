package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.io.FilenameUtils;
import org.endeavourhealth.common.utility.FileHelper;
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
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsSourceFile;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsSourceFileRecord;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RdbmsSourceFileMappingDal implements SourceFileMappingDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsSourceFileMappingDal.class);

    private static final String CSV_DELIM = "|";

    //private static String cachedAuditStoragePath = null;

    /**
     * checks for a pre-existing audit of a CSV/tab-delimited file
     */
    /*@Override
    public Integer findFileAudit(UUID serviceId, UUID systemId, UUID exchangeId, int fileTypeId, String filePath) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT id"
                    + " FROM source_file"
                    + " WHERE service_id = ?"
                    + " AND system_id = ?"
                    + " AND source_file_type_id = ?"
                    + " AND exchange_id = ?"
                    + " AND file_path = ?;";

            ps = connection.prepareStatement(sql);

            ps.setString(1, serviceId.toString());
            ps.setString(2, systemId.toString());
            ps.setInt(3, fileTypeId);
            ps.setString(4, exchangeId.toString());
            ps.setString(5, filePath.toString());

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return new Integer(resultSet.getInt(1));
            } else {
                return null;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }*/


    /**
     * audits a received CSV/tab-delimited file and returns the ID of that audit record
     */
    /*public int auditFile(UUID serviceId, UUID systemId, UUID exchangeId, int fileTypeId, String filePath) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            RdbmsSourceFile fileObj = new RdbmsSourceFile();
            fileObj.setServiceId(serviceId.toString());
            fileObj.setSystemId(systemId.toString());
            fileObj.setExchangeId(exchangeId.toString());
            fileObj.setFilePath(filePath);
            fileObj.setInsertedAt(new Date());
            fileObj.setSourceFileTypeId(fileTypeId);

            entityManager.getTransaction().begin();
            entityManager.persist(fileObj);
            entityManager.getTransaction().commit();

            return fileObj.getId().intValue();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }*/

    /**
     * retrieves the IDs of all field audits for the given file, in a corresponding grid (2D array)
     */
    /*@Override
    public Long findRecordAuditIdForRow(UUID serviceId, int fileAuditId, int rowIndex) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT id"
                    + " FROM source_file_record"
                    + " WHERE source_file_id = ?"
                    + " AND source_location = ?"
                    + " LIMIT 1";

            ps = connection.prepareStatement(sql);

            ps.setInt(1, fileAuditId);
            ps.setString(2, "" + rowIndex);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return new Long(resultSet.getLong(1));
            } else {
                return null;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }*/

    /*@Override
    public SourceFileRecord findSourceFileRecordRow(UUID serviceId, long auditId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            List<Long> auditIds = new ArrayList<>();
            auditIds.add(new Long(auditId));
            List<RdbmsSourceFileRecord> records = findSourceFileRecords(entityManager, auditIds);
            if (records.isEmpty()) {
                return null;
            }

            RdbmsSourceFileRecord record = records.get(0);

            SourceFileRecord ret = new SourceFileRecord();
            ret.setId(record.getId());
            ret.setSourceFileId(record.getSourceFileId());
            ret.setSourceLocation(record.getSourceLocation());
            ret.setValues(record.getValue().split(CSV_DELIM));

            return ret;

        } finally {
            entityManager.close();
        }
    }*/

    /*@Override
    public int findOrCreateFileTypeId(UUID serviceId, String typeDescription, List<String> columns) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;
        try {

            Map<Integer, List<String>> hmMatches = new HashMap<>();

            //first we need to find the ID of the matching file type (or create if we can't match)
            String sql = "SELECT t.id, c.column_name"
                    + " FROM source_file_type t"
                    + " INNER JOIN source_file_type_column c"
                    + " ON t.id = c.source_file_type_id"
                    + " WHERE t.description = ?"
                    + " ORDER BY t.id, c.column_index;";

            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            ps = connection.prepareStatement(sql);

            ps.setString(1, typeDescription);

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Integer id = new Integer(resultSet.getInt(1));
                String column = resultSet.getString(2);

                List<String> l = hmMatches.get(id);
                if (l == null) {
                    l = new ArrayList<>();
                    hmMatches.put(id, l);
                }
                l.add(column);
            }


            //go through the existing file types to look for a match on the column names
            for (Integer id: hmMatches.keySet()) {
                List<String> foundColumns = hmMatches.get(id);

                if (foundColumns.size() != columns.size()) {
                    continue;
                }

                boolean matches = true;

                for (int i=0; i<foundColumns.size(); i++) {
                    String foundColumn = foundColumns.get(i);
                    String column = columns.get(i);
                    if (!foundColumn.equalsIgnoreCase(column)) {
                        matches = false;
                        break;
                    }
                }

                if (matches) {
                    return id.intValue();
                }
            }

            //if we didn't find a match, then create a new one
            entityManager.getTransaction().begin();

            RdbmsSourceFileType fileTypeObj = new RdbmsSourceFileType();
            fileTypeObj.setDescription(typeDescription);
            entityManager.persist(fileTypeObj);

            int newId = fileTypeObj.getId().intValue();

            for (int i=0; i<columns.size(); i++) {
                String column = columns.get(i);

                RdbmsSourceFileTypeColumn columnObj = new RdbmsSourceFileTypeColumn();
                columnObj.setSourceFileTypeId(newId);
                columnObj.setColumnIndex(i);
                columnObj.setColumnName(column);

                entityManager.persist(columnObj);
            }

            entityManager.getTransaction().commit();

            return newId;

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
            if (ps != null) {
                ps.close();
            }
        }
    }*/

    /*@Override
    public void auditFileRow(UUID serviceId, SourceFileRecord record) throws Exception {

        String rowStr = String.join(CSV_DELIM, record.getValues());

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            entityManager.getTransaction().begin();

            RdbmsSourceFileRecord fieldObj = new RdbmsSourceFileRecord();

            fieldObj.setSourceFileId(record.getSourceFileId());
            fieldObj.setSourceLocation(record.getSourceLocation());
            fieldObj.setValue(rowStr);
            entityManager.persist(fieldObj);

            //and set the generated ID back on the record object
            long recordAuditId = fieldObj.getId().longValue();
            record.setId(recordAuditId);

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void auditFileRows(UUID serviceId, List<SourceFileRecord> records) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        SessionImpl session = (SessionImpl) entityManager.getDelegate();
        Connection connection = session.connection();

        //if the connection property "rewriteBatchedStatements=true" is specified then SELECT LAST_INSERT_ID()
        //will return the FIRST auto assigned ID for the batch. If that property is false or absent, then
        //SELECT LAST_INSERT_ID() will return the ID of the LAST assigned ID (because it sends the transactions one by one)
        String connectionUrl = connection.getMetaData().getURL();
        if (!connectionUrl.contains("rewriteBatchedStatements=true")) {

            entityManager.close();

            for (SourceFileRecord record: records) {
                auditFileRow(serviceId, record);
            }

            return;
        }

        PreparedStatement psInsert = null;
        //PreparedStatement psRowCount = null;
        PreparedStatement psLastId = null;

        try {

            String sql = "INSERT INTO source_file_record"
                    + " (source_file_id, source_location, value)"
                    + " VALUES (?, ?, ?)";

            psInsert = connection.prepareStatement(sql);

            //entityManager.getTransaction().begin();

            for (SourceFileRecord record : records) {

                String rowStr = String.join(CSV_DELIM, record.getValues());

                int col = 1;
                psInsert.setInt(col++, record.getSourceFileId());
                psInsert.setString(col++, record.getSourceLocation());
                psInsert.setString(col++, rowStr);

                psInsert.addBatch();
            }

            psInsert.executeBatch();

            //calling commit on the entity manager closes the connection, so do it directly
            connection.commit();
            //entityManager.getTransaction().commit();

            //to get the auto-assigned nubmers for the new rows, we use a SQL function which returns
            //the FIRST auto generated ID generated in the last transaction, and because innodb_autoinc_lock_mode is
            //set to 1, the other assigned IDs are guaranteed to be contiguous
            psLastId = connection.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet rs = psLastId.executeQuery();
            rs.next();
            long lastId = rs.getLong(1);
            rs.close();

            //and set the generated ID back on the record object
            for (SourceFileRecord record : records) {
                record.setId(lastId);
                lastId++;
            }

        } catch (Exception ex) {
            connection.rollback();

        } finally {
            if (psInsert != null) {
                psInsert.close();
            }
            *//*if (psRowCount != null) {
                psRowCount.close();
            }*//*
            if (psLastId != null) {
                psLastId.close();
            }
            entityManager.close();
        }
    }*/


    public List<ResourceFieldMapping> findFieldMappings(UUID serviceId, ResourceType resourceType, UUID resourceId) throws Exception {
        return findFieldMappingsForField(serviceId, resourceType, resourceId, null);
    }

    public List<ResourceFieldMapping> findFieldMappingsForField(UUID serviceId, ResourceType resourceType, UUID resourceId, String specificField) throws Exception {

        //retrieve the resource mappings audit objects
        List<AuditWrapper> resourceAudits = findResourceMappings(serviceId, resourceType, resourceId);

        //from the audits, find the distinct IDs of the audit records we'll need
        Set<Long> oldStyleRecordIds = new HashSet<>();
        List<PublishedFileRecord> publishedRecords = findPublisherRecords(resourceAudits, specificField, oldStyleRecordIds);

        //retrieve the record and file meta data from the audit DB
        Map<Integer, PublishedFile> hmPublishedFiles = findPublishedFiles(publishedRecords);
        Map<Integer, PublishedFileType> hmPublishedFileTypes = findPublishedFileTypes(hmPublishedFiles);

        //if we've got any content pointing to old-style audits, in the publisher transform DB, retrieve them
        Map<Long, RdbmsSourceFileRecord> hmOldStyleRecords = null;
        Map<Integer, String> hmOldStyleSourceFileNames = null;

        if (!oldStyleRecordIds.isEmpty()) {
            hmOldStyleRecords = findOldStyleSourceFileRecords(serviceId, oldStyleRecordIds);
            hmOldStyleSourceFileNames = findOldStyleSourceFileNames(serviceId, hmOldStyleRecords);
        }


        List<ResourceFieldMapping> ret = createFileMappingObjects(resourceAudits, publishedRecords, hmPublishedFiles, hmPublishedFileTypes, hmOldStyleRecords, hmOldStyleSourceFileNames, specificField);

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
            sql += " ORDER BY t.id, c.column_index;";

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
                                                                Map<Long, RdbmsSourceFileRecord> hmOldStyleRecords,
                                                                Map<Integer, String> hmOldStyleSourceFileNames,
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

                    long publishedFileId = row.getFileId();
                    if (publishedFileId > 0) {
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

                    } else if (row.getOldStyleAuditId() != null) {

                        long auditRowId = row.getOldStyleAuditId().longValue();

                        RdbmsSourceFileRecord fileRecord = hmOldStyleRecords.get(new Long(auditRowId));
                        String sourceFilePath = hmOldStyleSourceFileNames.get(new Integer(fileRecord.getSourceFileId()));

                        String[] valueElements = fileRecord.getValue().split("\\" + CSV_DELIM); //need to escape the delimiter so it's not a regex
                        String value = valueElements[colIndex];

                        obj.setSourceFileName(sourceFilePath);
                        try {
                            obj.setSourceFileRow(Integer.parseInt(fileRecord.getSourceLocation()));
                        } catch (NumberFormatException nfe) {
                            obj.setSourceLocation(fileRecord.getSourceLocation());
                        }

                        obj.setValue(value);

                    } else {
                        throw new Exception("No prid or old-style audit ID for audit for resource");
                    }

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




    /*private List<ResourceFieldMapping> createFileMappingObjects(List<RdbmsResourceFieldMappings> resourceMappings,
                                                                List<RdbmsSourceFileRecord> fileRecords,
                                                                List<RdbmsSourceFile> sourceFiles,
                                                                String specificField) throws Exception {

        List<ResourceFieldMapping> ret = new ArrayList<>();

        //hash two of the lists by their ID for speed of lookup
        Map<Long, RdbmsSourceFileRecord> hmFileRecords = new HashMap<>();
        for (RdbmsSourceFileRecord fileRecord: fileRecords) {
            hmFileRecords.put(fileRecord.getId(), fileRecord);
        }

        Map<Integer, RdbmsSourceFile> hmSourceFiles = new HashMap<>();
        for (RdbmsSourceFile sourceFile: sourceFiles) {
            hmSourceFiles.put(sourceFile.getId(), sourceFile);
        }

        Map<String, String> hmFieldVersions = new HashMap<>();
        //Set<String> fieldsDoneSet = new HashSet<>();

        for (int i=resourceMappings.size()-1; i>=0; i--) {
            RdbmsResourceFieldMappings mapping = resourceMappings.get(i);
            String mappingJson = mapping.getMappingsJson();
            ResourceFieldMappingAudit audit = ResourceFieldMappingAudit.readFromJson(mappingJson);
            String resourceVersion = mapping.getVersion();

            for (Long key: audit.getAudits().keySet()) {
                ResourceFieldMappingAudit.ResourceFieldMappingAuditRow row = audit.getAudits().get(key);
                long auditRowId = row.getAuditId();

                for (ResourceFieldMappingAudit.ResourceFieldMappingAuditCol col: row.getCols()) {
                    String field = col.getField();
                    int colIndex = col.getCol();

                    //if we're only interested in a specific field, then skip everything else
                    if (specificField != null
                            && !specificField.equalsIgnoreCase(field)) {
                        continue;
                    }

                    //if we've already found a mapping for this field, ensure that this mapping is for the same version, otherwise skip
                    String version = hmFieldVersions.get(field);
                    if (version != null
                            && !version.equals(resourceVersion)) {
                        continue;
                    }

                    hmFieldVersions.put(field, resourceVersion);

                    RdbmsSourceFileRecord fileRecord = hmFileRecords.get(new Long(auditRowId));
                    RdbmsSourceFile sourceFile = hmSourceFiles.get(new Integer(fileRecord.getSourceFileId()));

                    String[] valueElements = fileRecord.getValue().split("\\" + CSV_DELIM); //need to escape the delimiter so it's not a regex
                    String value = valueElements[colIndex];

                    ResourceFieldMapping obj = new ResourceFieldMapping();
                    obj.setResourceId(UUID.fromString(mapping.getResourceId()));
                    obj.setResourceType(mapping.getResourceType());
                    obj.setCreatedAt(mapping.getCreatedAt());
                    obj.setVersion(UUID.fromString(mapping.getVersion()));
                    obj.setResourceField(field);
                    obj.setSourceFileName(sourceFile.getFilePath());
                    try {
                        obj.setSourceFileRow(Integer.parseInt(fileRecord.getSourceLocation()));
                    } catch (NumberFormatException nfe) {
                        obj.setSourceLocation(fileRecord.getSourceLocation());
                    }
                    obj.setSourceFileColumn(new Integer(colIndex));
                    obj.setValue(value);

                    ret.add(obj);
                }
            }
        }

        return ret;
    }*/

    private Map<Integer, String> findOldStyleSourceFileNames(UUID serviceId, Map<Long, RdbmsSourceFileRecord> oldStyleFileRecordsMap) throws Exception {

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
    }

    /*private List<RdbmsSourceFile> findSourceFiles(EntityManager entityManager, List<Integer> distinctFileIds) {

        String sql = "select c"
                + " from"
                + " RdbmsSourceFile c"
                + " where c.id IN :id_list";

        Query query = entityManager.createQuery(sql, RdbmsSourceFile.class)
                .setParameter("id_list", distinctFileIds);

        return query.getResultList();
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
        } finally {
            entityManager.close();
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

    /*private void findResourceMappingsFromDisk(List<AuditWrapper> wrappers, String topLevelStoragePath, UUID serviceId, ResourceType resourceType, UUID resourceId) throws Exception {
        String storageDir = createAuditDir(topLevelStoragePath, serviceId, resourceType.toString(), resourceId);
        List<FileInfo> infos = FileHelper.listFilesInSharedStorageWithInfo(storageDir);

        for (FileInfo info: infos) {
            Date d = info.getLastModified();
            String path = info.getFilePath();
            String extension = FilenameUtils.getExtension(path);

            String json = null;

            if (extension.equalsIgnoreCase("json")) {
                InputStreamReader reader = FileHelper.readFileReaderFromSharedStorage(path);
                json = IOUtils.toString(reader);
                reader.close();

            } else if (extension.equalsIgnoreCase("zip")) {
                InputStream inputStream = FileHelper.readFileFromSharedStorage(path);
                ZipInputStream zis = new ZipInputStream(inputStream);

                ZipEntry entry = zis.getNextEntry();
                if (entry == null) {
                    throw new Exception("No entry in zip file " + path);
                }
                byte[] entryBytes = IOUtils.toByteArray(zis);
                json = new String(entryBytes);

                inputStream.close();

            } else {
                throw new Exception("Unexpected audit file extension for " + path);
            }

            ResourceFieldMappingAudit audit = ResourceFieldMappingAudit.readFromJson(json);

            //the version ID is the parent part of the path, so stick that in a map so we can avoid duplicates
            String dirPath = FilenameUtils.getPathNoEndSeparator(info.getFilePath());
            String parentDirName = FilenameUtils.getName(dirPath);

            AuditWrapper wrapper = new AuditWrapper(audit, UUID.fromString(parentDirName), d);
            wrappers.add(wrapper);
        }
    }*/

    /*private List<RdbmsResourceFieldMappings> findResourceMappings(EntityManager entityManager, ResourceType resourceType, UUID resourceId) throws Exception {

        String sql = "select c"
                + " from"
                + " RdbmsResourceFieldMappings c"
                + " where c.resourceId = :resource_id"
                + " and c.resourceType = :resource_type";

        Query query = entityManager.createQuery(sql, RdbmsResourceFieldMappings.class)
                .setParameter("resource_id", resourceId.toString())
                .setParameter("resource_type", resourceType.toString());

        List<RdbmsResourceFieldMappings> ret = query.getResultList();

        //we always need the mappings in date order, since we only need to retrieve the most recent audit for
        //each JSON field in the resource
        ret.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));

        return ret;
    }*/

    private Map<Long, RdbmsSourceFileRecord> findOldStyleSourceFileRecords(UUID serviceId, Set<Long> oldStyleRecordAuditIds) throws Exception {

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
    }

    /*private List<RdbmsSourceFileRecord> findSourceFileRecords(EntityManager entityManager, List<Long> auditRowIds) throws Exception {

        String sql = "select c"
                + " from"
                + " RdbmsSourceFileRecord c"
                + " where c.id IN :audit_ids";

        Query query = entityManager.createQuery(sql, RdbmsSourceFileRecord.class)
                .setParameter("audit_ids", auditRowIds);

        return query.getResultList();
    }*/

    /**
     * finds the distinct publisher records IDs (and old-style record IDs) for the given resource audits,
     * but has extra logic to ignore any record IDs that won't be required because a later audit overwrites that data
     */
    private List<PublishedFileRecord> findPublisherRecords(List<AuditWrapper> resourceAudits, String specificField, Set<Long> oldStyleRecordIds) throws Exception {

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
                    int publishedFileId = row.getFileId();
                    if (publishedFileId > 0) {
                        int recordNumber = row.getRecord();
                        PublishedFileRecord record = new PublishedFileRecord();
                        record.setPublishedFileId(publishedFileId);
                        record.setRecordNumber(recordNumber);
                        publishedRecords.add(record);

                    } else {
                        Long oldTyleAuditId = row.getOldStyleAuditId();
                        if (oldTyleAuditId == null) {
                            throw new Exception("No prid or old-style audit ID for audit for resource");
                        }
                        oldStyleRecordIds.add(oldTyleAuditId);
                    }
                }
            }

            //append all the fields we just covered to the set so far
            fieldsDoneSet.addAll(fieldsDoneThisAudit);
        }

        //we've only populated some values on the records, so we need to hit the DB to get the rest
        populatePublishedFileRecords(publishedRecords);

        return publishedRecords;
    }

    /*private List<Long> findDistinctAuditRowIds(List<RdbmsResourceFieldMappings> resourceMappings, String specificField) throws Exception {

        Set<Long> auditRowIds = new HashSet<>();

        Set<String> fieldsDoneSet = new HashSet<>();

        for (int i=resourceMappings.size()-1; i>=0; i--) {
            RdbmsResourceFieldMappings mapping = resourceMappings.get(i);
            String mappingJson = mapping.getMappingsJson();
            ResourceFieldMappingAudit audit = ResourceFieldMappingAudit.readFromJson(mappingJson);

            Set<String> fieldsDoneThisAudit = new HashSet<>();

            for (Long key: audit.getAudits().keySet()) {
                ResourceFieldMappingAudit.ResourceFieldMappingAuditRow row = audit.getAudits().get(key);
                long auditRowId = row.getAuditId();
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
                    auditRowIds.add(auditRowId);
                }
            }

            //append all the fields we just covered to the set so far
            fieldsDoneSet.addAll(fieldsDoneThisAudit);
        }

        return new ArrayList<>(auditRowIds);
    }*/

    /*public void saveResourceMappings(ResourceWrapper resourceWrapper, ResourceFieldMappingAudit audit) throws Exception {

        UUID serviceId = resourceWrapper.getServiceId();
        String mappingJson = audit.writeToJson();

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {

            RdbmsResourceFieldMappings dbObj = new RdbmsResourceFieldMappings();
            dbObj.setResourceId(resourceWrapper.getResourceId().toString());
            dbObj.setResourceType(resourceWrapper.getResourceType());
            dbObj.setCreatedAt(resourceWrapper.getCreatedAt());
            dbObj.setVersion(resourceWrapper.getVersion().toString());
            dbObj.setMappingsJson(mappingJson);

            entityManager.getTransaction().begin();
            entityManager.persist(dbObj);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }*/

    /*private static String getAuditStoragePath() {
        if (cachedAuditStoragePath == null) {
            try {
                JsonNode json = ConfigManager.getConfigurationAsJson("common_config", "queuereader");

                JsonNode node = json.get("audit_storage_path");
                if (node != null) {
                    cachedAuditStoragePath = node.asText();
                }

            } catch (Exception var4) {
                //if the config record is there, just log it out rather than throw an exception
                LOG.warn("No common queuereader config found in config DB with app_id queuereader and config_id common_config");
            }
        }

        if (Strings.isNullOrEmpty(cachedAuditStoragePath)) {
            return null;
        } else {
            return cachedAuditStoragePath;
        }
    }*/

    @Override
    public void saveResourceMappings(Map<ResourceWrapper, ResourceFieldMappingAudit> audits) throws Exception {

        if (audits == null || audits.isEmpty()) {
            return;
        }

        saveResourceMappingsToDatabase(audits);

        /*String auditStoragePath = getAuditStoragePath();
        if (!Strings.isNullOrEmpty(auditStoragePath)) {
            //if we have an audit path, write the audits to that
            saveResourceMappingsToFile(audits, auditStoragePath);

        } else {
            //if we don't have an audit path, write the audits to the DB
            saveResourceMappingsToDatabase(audits);
        }*/
    }

    /*private void saveResourceMappingsToFile(Map<ResourceWrapper, ResourceFieldMappingAudit> audits, String topLevelAuditStoragePath) throws Exception {

        for (ResourceWrapper wrapper : audits.keySet()) {
            ResourceFieldMappingAudit audit = audits.get(wrapper);
            String mappingJson = audit.writeToJson();

            //store in a zip file. Even though very small, we get about 50% compression, which is OK
            String zipEntryName = wrapper.getVersion().toString() + ".json";
            byte[] zippedBytes = writeToZip(mappingJson, zipEntryName);

            //store in structure: service ID, resource type, resource ID, version)
            String storageDir = createAuditDir(topLevelAuditStoragePath, wrapper.getServiceId(), wrapper.getResourceType(), wrapper.getResourceId());
            String storagePath = FilenameUtils.concat(storageDir, wrapper.getVersion().toString() + ".zip");

            FileHelper.writeBytesToSharedStorage(storagePath, zippedBytes);
        }
    }

    private static byte[] writeToZip(String jsonStr, String entryName) throws Exception {

        //may as well zip the data, since it will compress well
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        //the first entry is a json file giving us the target class names for each column
        ObjectNode columnClassMappingJson = new ObjectNode(JsonNodeFactory.instance);

        zos.putNextEntry(new ZipEntry(entryName));
        zos.write(jsonStr.getBytes());
        zos.flush();
        zos.close();

        return baos.toByteArray();
    }*/

    private void saveResourceMappingsToDatabase(Map<ResourceWrapper, ResourceFieldMappingAudit> audits) throws Exception {

        UUID serviceId = null;
        for (ResourceWrapper wrapper: audits.keySet()) {
            if (serviceId == null) {
                serviceId = wrapper.getServiceId();
            } else if (!serviceId.equals(wrapper.getServiceId())) {
                throw new IllegalArgumentException("Can't save audits for multiple services at once");
            }
        }

        //if configured, use the common FHIR audit DB
        EntityManager entityManager = null;
        try {
            entityManager = ConnectionManager.getFhirAuditEntityManager();
        } catch (Exception ex) {
            //if no global FHIR audit DB, then use the publisher transform DB
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

            for (ResourceWrapper wrapper: audits.keySet()) {
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
            entityManager.close();
        }
    }


    /*@Override
    public void saveResourceMappings(Map<ResourceWrapper, ResourceFieldMappingAudit> audits) throws Exception {

        if (audits == null || audits.isEmpty()) {
            return;
        }

        UUID serviceId = null;
        for (ResourceWrapper wrapper: audits.keySet()) {
            if (serviceId == null) {
                serviceId = wrapper.getServiceId();
            } else if (!serviceId.equals(wrapper.getServiceId())) {
                throw new IllegalArgumentException("Can't save audits for multiple services at once");
            }
        }

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

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

            for (ResourceWrapper wrapper: audits.keySet()) {
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
            entityManager.close();
        }
    }*/
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