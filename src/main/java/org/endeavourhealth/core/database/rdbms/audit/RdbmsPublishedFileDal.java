package org.endeavourhealth.core.database.rdbms.audit;

import org.endeavourhealth.core.database.dal.audit.PublishedFileDalI;
import org.endeavourhealth.core.database.dal.audit.models.PublishedFileColumn;
import org.endeavourhealth.core.database.dal.audit.models.PublishedFileRecord;
import org.endeavourhealth.core.database.dal.audit.models.PublishedFileType;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsPublishedFile;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsPublishedFileType;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsPublishedFileTypeColumn;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RdbmsPublishedFileDal implements PublishedFileDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsPublishedFileDal.class);


    @Override
    public int findOrCreateFileTypeId(PublishedFileType possibleNewFileType) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;
        try {

            //first we need to find the ID of the matching file type (or create if we can't match)
            String sql = "SELECT t.id, c.column_name, c.fixed_column_start, c.fixed_column_length"
                    + " FROM published_file_type t"
                    + " INNER JOIN published_file_type_column c"
                    + " ON t.id = c.published_file_type_id"
                    + " WHERE t.file_type = ?";
            if (possibleNewFileType.getVariableColumnDelimiter() == null) {
                sql += " AND t.variable_column_delimiter IS NULL";
            } else {
                sql += " AND t.variable_column_delimiter = ?";
            }
            if (possibleNewFileType.getVariableColumnQuote() == null) {
                sql += " AND t.variable_column_quote IS NULL";
            } else {
                sql += " AND t.variable_column_quote = ?";
            }
            if (possibleNewFileType.getVariableColumnEscape() == null) {
                sql += " AND t.variable_column_escape IS NULL";
            } else {
                sql += " AND t.variable_column_escape = ?";
            }
            sql += " ORDER BY t.id, c.column_index";

            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, possibleNewFileType.getFileType());
            if (possibleNewFileType.getVariableColumnDelimiter() != null) {
                ps.setString(col++, "" + possibleNewFileType.getVariableColumnDelimiter());
            }
            if (possibleNewFileType.getVariableColumnQuote() != null) {
                ps.setString(col++, "" + possibleNewFileType.getVariableColumnQuote());
            }
            if (possibleNewFileType.getVariableColumnEscape() != null) {
                ps.setString(col++, "" + possibleNewFileType.getVariableColumnEscape());
            }
            ResultSet resultSet = ps.executeQuery();

            Map<Integer, List<PublishedFileColumn>> hmPossibleMatches = new HashMap<>();

            while (resultSet.next()) {

                col = 1;
                Integer fileTypeId = new Integer(resultSet.getInt(col++));
                String columnName = resultSet.getString(col++);

                PublishedFileColumn existingColumn = new PublishedFileColumn();
                existingColumn.setColumnName(columnName);

                int fixedColStart = resultSet.getInt(col++);
                if (!resultSet.wasNull()) {
                    existingColumn.setFixedColumnStart(fixedColStart);
                }

                int fixedColLength = resultSet.getInt(col++);
                if (!resultSet.wasNull()) {
                    existingColumn.setFixedColumnLength(fixedColLength);
                }

                List<PublishedFileColumn> l = hmPossibleMatches.get(fileTypeId);
                if (l == null) {
                    l = new ArrayList<>();
                    hmPossibleMatches.put(fileTypeId, l);
                }
                l.add(existingColumn);
            }
            //LOG.debug("Found " + hmPossibleMatches.size() + " possible matches for file type ID");

            //go through the existing file types to look for a match on the column names
            List<PublishedFileColumn> possibleNewColumns = possibleNewFileType.getColumns();

            for (Integer id: hmPossibleMatches.keySet()) {
                List<PublishedFileColumn> existingColumns = hmPossibleMatches.get(id);

                //see if this existing type has the same columns as this possible new one
                if (existingColumns.equals(possibleNewColumns)) {
                    //LOG.debug("Matched to type " + id);
                    return id.intValue();
                }
                //LOG.debug("Didn't match to type " + id + " on columns - \nwanted: " + possibleNewColumns + "\nfound: " + existingColumns);
            }

            //if we didn't find a match, then create a new one
            entityManager.getTransaction().begin();

            RdbmsPublishedFileType fileTypeObj = new RdbmsPublishedFileType();
            fileTypeObj.setFileType(possibleNewFileType.getFileType());
            fileTypeObj.setVariableColumnDelimiter(possibleNewFileType.getVariableColumnDelimiter());
            fileTypeObj.setVariableColumnQuote(possibleNewFileType.getVariableColumnQuote());
            fileTypeObj.setVariableColumnEscape(possibleNewFileType.getVariableColumnEscape());
            entityManager.persist(fileTypeObj);

            int newId = fileTypeObj.getId().intValue();

            for (int i=0; i<possibleNewColumns.size(); i++) {
                PublishedFileColumn column = possibleNewColumns.get(i);

                RdbmsPublishedFileTypeColumn columnObj = new RdbmsPublishedFileTypeColumn();
                columnObj.setPublishedFileTypeId(newId);
                columnObj.setColumnIndex(i);
                columnObj.setColumnName(column.getColumnName());
                columnObj.setFixedColumnStart(column.getFixedColumnStart());
                columnObj.setFixedColumnLength(column.getFixedColumnLength());

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
    }

    @Override
    public Integer findFileAudit(UUID serviceId, UUID systemId, UUID exchangeId, int fileTypeId, String filePath) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT id"
                    + " FROM published_file"
                    + " WHERE service_id = ?"
                    + " AND system_id = ?"
                    + " AND published_file_type_id = ?"
                    + " AND exchange_id = ?"
                    + " AND file_path = ?";

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
    }

    @Override
    public int auditFile(UUID serviceId, UUID systemId, UUID exchangeId, int fileTypeId, String filePath) throws Exception {

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        try {
            RdbmsPublishedFile fileObj = new RdbmsPublishedFile();
            fileObj.setServiceId(serviceId.toString());
            fileObj.setSystemId(systemId.toString());
            fileObj.setExchangeId(exchangeId.toString());
            fileObj.setFilePath(filePath);
            fileObj.setInsertedAt(new Date());
            fileObj.setPublishedFileTypeId(fileTypeId);

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
    }

    @Override
    public PublishedFileRecord findRecordAuditForRow(int fileAuditId, int rowIndex) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT published_file_id, record_number, byte_start, byte_length"
                    + " FROM published_file_record"
                    + " WHERE published_file_id = ?"
                    + " AND record_number = ?"
                    + " LIMIT 1";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setInt(col++, fileAuditId);
            ps.setString(col++, "" + rowIndex);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                col = 1;
                PublishedFileRecord ret = new PublishedFileRecord();
                ret.setPublishedFileId(resultSet.getInt(col++));
                ret.setRecordNumber(resultSet.getInt(col++));
                ret.setByteStart(resultSet.getLong(col++));
                ret.setByteLength(resultSet.getInt(col++));
                return ret;
            } else {
                return null;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public Map<Integer, PublishedFileRecord> findRecordAuditForRows(int fileAuditId, List<Integer> rowIndexes) throws Exception {

        Map<Integer, PublishedFileRecord> ret = new HashMap<>();
        if (rowIndexes.isEmpty()) {
            return ret;
        }

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            StringBuilder sb = new StringBuilder();
            String s = "SELECT published_file_id, record_number, byte_start, byte_length"
                    + " FROM published_file_record"
                    + " WHERE published_file_id = ?"
                    + " AND record_number IN (";
            sb.append(s);

            for (int i=0; i<rowIndexes.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append("?");
            }
            sb.append(")");
            String sql = sb.toString();

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setInt(col++, fileAuditId);
            for (int i=0; i<rowIndexes.size(); i++) {
                ps.setInt(col++, rowIndexes.get(i));
            }

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                col = 1;
                PublishedFileRecord obj = new PublishedFileRecord();
                obj.setPublishedFileId(resultSet.getInt(col++));
                obj.setRecordNumber(resultSet.getInt(col++));
                obj.setByteStart(resultSet.getLong(col++));
                obj.setByteLength(resultSet.getInt(col++));

                ret.put(obj.getRecordNumber(), obj);
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }

    }

    @Override
    public void auditFileRows(List<PublishedFileRecord> records) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        PreparedStatement psInsert = null;
        try {

            //to increase speed when re-processing data already done, we now skip the checks to see
            //if records have already been audited and simply audit them again. So the IGNORE
            //keyword is used to suppress duplicate key errors
            String sql = "INSERT IGNORE INTO published_file_record"
                    + " (published_file_id, record_number, byte_start, byte_length)"
                    + " VALUES (?, ?, ?, ?)";

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            psInsert = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (PublishedFileRecord record : records) {

                int col = 1;
                psInsert.setInt(col++, record.getPublishedFileId());
                psInsert.setInt(col++, record.getRecordNumber());
                psInsert.setLong(col++, record.getByteStart());
                psInsert.setInt(col++, record.getByteLength());

                psInsert.addBatch();
            }

            psInsert.executeBatch();

            entityManager.getTransaction().commit();


        } catch (Exception ex) {
            entityManager.getTransaction().rollback();

        } finally {
            if (psInsert != null) {
                psInsert.close();
            }
            entityManager.close();
        }
    }

    /*private void saveFileRecordsNotBatched(PreparedStatement psInsert, PreparedStatement psLastId, Connection connection,
                                           List<PublishedFileRecord> records) throws Exception {

        for (PublishedFileRecord record : records) {

            int col = 1;
            psInsert.setInt(col++, record.getPublishedFileId());
            psInsert.setInt(col++, record.getRecordNumber());
            psInsert.setLong(col++, record.getByteStart());
            psInsert.setInt(col++, record.getByteLength());

            psInsert.execute();

            connection.commit();

            ResultSet rs = psLastId.executeQuery();
            rs.next();
            long lastId = rs.getLong(1);
            rs.close();

            //and set the generated ID back on the record object
            record.setId(lastId);
        }
    }

    private void saveFileRecordsBatched(PreparedStatement psInsert, PreparedStatement psLastId, Connection connection,
                                        List<PublishedFileRecord> records) throws Exception {

        //entityManager.getTransaction().begin();

        for (PublishedFileRecord record : records) {

            int col = 1;
            psInsert.setInt(col++, record.getPublishedFileId());
            psInsert.setInt(col++, record.getRecordNumber());
            psInsert.setLong(col++, record.getByteStart());
            psInsert.setInt(col++, record.getByteLength());

            psInsert.addBatch();
        }

        psInsert.executeBatch();

        //calling commit on the entity manager closes the connection, so do it directly
        connection.commit();
        //entityManager.getTransaction().commit();

        //to get the auto-assigned nubmers for the new rows, we use a SQL function which returns
        //the FIRST auto generated ID generated in the last transaction, and because innodb_autoinc_lock_mode is
        //set to 1, the other assigned IDs are guaranteed to be contiguous
        ResultSet rs = psLastId.executeQuery();
        rs.next();
        long lastId = rs.getLong(1);
        rs.close();

        //and set the generated ID back on the record object
        for (PublishedFileRecord record : records) {
            record.setId(lastId);
            lastId++;
        }
    }*/


}
