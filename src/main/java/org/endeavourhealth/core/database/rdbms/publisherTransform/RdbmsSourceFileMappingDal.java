package org.endeavourhealth.core.database.rdbms.publisherTransform;

import com.google.common.base.Strings;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.endeavourhealth.core.database.dal.publisherTransform.SourceFileMappingDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMapping;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsSourceFile;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsSourceFileField;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsSourceFileType;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsSourceFileTypeColumn;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.ResourceType;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RdbmsSourceFileMappingDal implements SourceFileMappingDalI {


    /**
     * audits a received CSV/tab-delimited file and returns the ID of that audit record
     */
    public int auditFile(UUID serviceId, UUID systemId, UUID exchangeId, String filePath, String typeDescription, List<String> columns) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

        try {
            //calculate the file type ID
            int fileTypeId = findOrCreateFileTypeId(entityManager, typeDescription, columns);

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

        } finally {
            entityManager.close();
        }
    }

    private static int findOrCreateFileTypeId(EntityManager entityManager, String typeDescription, List<String> columns) throws Exception {

        Map<Integer, List<String>> hmMatches = new HashMap<>();

        PreparedStatement ps = null;
        try {

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

        } finally {
            if (ps != null) {
                ps.close();
            }
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
    }

    public Map<String, Long> auditCsvRecord(UUID serviceId, CSVParser parser, CSVRecord record, int sourceFileId) throws Exception {

        Map<String, Long> ret = new HashMap<>();

        Map<String, Integer> headers = parser.getHeaderMap();

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            int row = (int)record.getRecordNumber();

            entityManager.getTransaction().begin();

            for (String header: headers.keySet()) {
                Integer colIndex = headers.get(header);
                String value = record.get(colIndex);

                if (Strings.isNullOrEmpty(value)) {
                    continue;
                }

                RdbmsSourceFileField fieldObj = new RdbmsSourceFileField();
                fieldObj.setSourceFileId(sourceFileId);
                fieldObj.setRowIndex(row);
                fieldObj.setColumnIndex(colIndex);
                fieldObj.setValue(value);
                entityManager.persist(fieldObj);

                Long fieldId = fieldObj.getId();
                ret.put(header, fieldId);
            }

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
        }

        return ret;
    }


    public List<ResourceFieldMapping> findFieldMappings(UUID serviceId, ResourceType resourceType, UUID resourceId) throws Exception {

        List<ResourceFieldMapping> ret = new ArrayList<>();

        String sql = "SELECT m.resource_id, m.resource_type, m.created_at, m.version, m.resource_field, s.file_path, f.row_index, f.column_index, f.source_location, f.value"
                + " FROM resource_field_mapping m"
                + " INNER JOIN source_file_field f"
                + " ON m.source_file_field_id = f.id"
                + " INNER JOIN source_file s"
                + " ON s.id = f.source_file_id"
                + " WHERE m.resource_id = ?"
                + " AND m.resource_type = ?";

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            ps = connection.prepareStatement(sql);

            ps.setString(1, resourceId.toString());
            ps.setString(2, resourceType.toString());

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("resource_id");
                String type = resultSet.getString("resource_type");
                Date created = resultSet.getDate("created_at");
                String version = resultSet.getString("version");
                String resourceField = resultSet.getString("resource_field_id");
                String filename = resultSet.getString("file_path");
                Integer row = resultSet.getInt("row_index");
                Integer column = resultSet.getInt("column_index");
                String location = resultSet.getString("source_location");
                String value = resultSet.getString("value");

                ResourceFieldMapping mapping = new ResourceFieldMapping();
                mapping.setResourceId(UUID.fromString(id));
                mapping.setResourceType(type);
                mapping.setCreatedAt(created);
                mapping.setVersion(UUID.fromString(version));
                mapping.setResourceField(resourceField);
                mapping.setSourceFileName(filename);
                mapping.setSourceFileRow(row);
                mapping.setSourceFileColumn(column);
                mapping.setSourceLocation(location);
                mapping.setValue(value);

                ret.add(mapping);
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }

        return ret;
    }


}
