package org.endeavourhealth.core.database.dal.publisherTransform.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ResourceFieldMappingAudit {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceFieldMappingAudit.class);
    //although it seems simpler to use a list, the map is faster for lookups
    private List<ResourceFieldMappingAuditRow> audits = new ArrayList<>();
    private List<ResourceFieldMappingAuditRow> oldStyleAudits = null;

    public ResourceFieldMappingAudit() {}


    public String writeToJson() throws Exception {
        //write it out as a LIST to keep the JSON as small as possible
        List<ResourceFieldMappingAuditRow> list = getAudits();
        return ObjectMapperPool.getInstance().writeValueAsString(list);
    }

    public static ResourceFieldMappingAudit readFromJson(String json) throws Exception {
        ResourceFieldMappingAudit ret = new ResourceFieldMappingAudit();

        /*List<ResourceFieldMappingAuditRow> readAudits = new ArrayList<>();
        readAudits = ObjectMapperPool.getInstance().readValue(json, readAudits.getClass());*/

        List<ResourceFieldMappingAuditRow> readAudits = ObjectMapperPool.getInstance().readValue(json, new TypeReference<List<ResourceFieldMappingAuditRow>>() {});
        for (ResourceFieldMappingAuditRow audit: readAudits) {

            int fileId = audit.getFileId();
            if (fileId > 0) {
                ret.audits.add(audit);

            } else if (audit.getOldStyleAuditId() != null) {

                if (ret.oldStyleAudits == null) {
                    ret.oldStyleAudits = new ArrayList<>();
                }

                ret.oldStyleAudits.add(audit);
            } else if (fileId == -1) {
                LOG.warn("Audit skipped for dummy CsvCell with -1 fileid hence no audit info:" + json);
            } else {
                throw new Exception("No PRID in audit from " + json);
            }
        }

        return ret;
    }

    public List<ResourceFieldMappingAuditRow> getAudits() {
        List<ResourceFieldMappingAuditRow> list = new ArrayList<>(audits);

        if (oldStyleAudits != null) {
            List<ResourceFieldMappingAuditRow> listOldStyle = new ArrayList<>(oldStyleAudits);
            list.addAll(listOldStyle);
        }

        return list;
    }


    /**
     * audits that a file/record/column was transformed to a specific FHIR field
     */
    public void auditValue(int publishedFileId, int recordNumber, short colIndex, String jsonField) {
        ResourceFieldMappingAuditRow audit = auditRecordImpl(publishedFileId, recordNumber);
        audit.addColumnMapping(colIndex, jsonField);
    }

    /**
     * sometimes we can't link a specific file/record/column to a specific FHIR field,
     * but we can link to the file/record (e.g. staging table transform used for Barts)
     */
    public void auditRecord(int publishedFileId, int recordNumber) {
        auditRecordImpl(publishedFileId, recordNumber);
    }

    private ResourceFieldMappingAuditRow auditRecordImpl(int publishedFileId, int recordNumber) {

        ResourceFieldMappingAuditRow audit = null;
        for (ResourceFieldMappingAuditRow r: audits) {
            if (r.getFileId() == publishedFileId
                    && r.getRecord() == recordNumber) {
                audit = r;
                break;
            }
        }

        if (audit == null) {
            audit = new ResourceFieldMappingAuditRow();
            audit.setFileId(publishedFileId);
            audit.setRecord(recordNumber);
            audits.add(audit);
        }

        return audit;
    }

    public void auditValueOldStyle(Long oldStyleAuditId, short colIndex, String jsonField) {

        if (oldStyleAudits == null) {
            oldStyleAudits = new ArrayList<>();
        }

        ResourceFieldMappingAuditRow audit = null;
        for (ResourceFieldMappingAuditRow r: oldStyleAudits) {
            if (r.getOldStyleAuditId().equals(oldStyleAuditId)) {
                audit = r;
                break;
            }
        }

        if (audit == null) {
            audit = new ResourceFieldMappingAuditRow();
            audit.setOldStyleAuditId(oldStyleAuditId);
            oldStyleAudits.add(audit);;
        }

        audit.addColumnMapping(colIndex, jsonField);
    }

    /**
     * sometimes a resource is part-built with data that is then removed before saving, so this method is used to
     * undo the auditing of the now-removed data
     */
    public void removeAudit(String auditJsonPrefix) {

        //use an iterator and while loop so we can remove as we go
        for (ResourceFieldMappingAuditRow rowAudit: getAudits()) {

            List<ResourceFieldMappingAuditCol> colAudits = rowAudit.getCols();
            for (int i=colAudits.size()-1; i>=0; i--) {
                ResourceFieldMappingAuditCol colAudit = colAudits.get(i);
                if (colAudit.getField().startsWith(auditJsonPrefix)) {
                    colAudits.remove(i);
                }
            }

            if (colAudits.isEmpty()) {
                audits.remove(rowAudit);
                if (oldStyleAudits != null) {
                    oldStyleAudits.remove(rowAudit);
                }
            }
        }
    }

    public boolean isEmpty() {
        return audits.isEmpty()
                && (oldStyleAudits == null || oldStyleAudits.isEmpty());
    }

    //use non-empty annotation so we don't write out the cols list if we've not audited any
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ResourceFieldMappingAuditRow {
        //variable names kept short as this object is persisted to JSON and I want to avoid using excessive storage space
        private int fileId; //published file ID
        private int record; //record number (starts at 1)
        private List<ResourceFieldMappingAuditCol> cols = new ArrayList<>();
        private Long oldStyleAuditId; //used to allow backwards compatability with old auditing mechanism

        public ResourceFieldMappingAuditRow() {}

        public void addColumnMapping(short col, String field) {
            ResourceFieldMappingAuditCol obj = new ResourceFieldMappingAuditCol();
            obj.setCol(col);
            obj.setField(field);
            getCols().add(obj);
        }

        public int getFileId() {
            return fileId;
        }

        public void setFileId(int fileId) {
            this.fileId = fileId;
        }

        public int getRecord() {
            return record;
        }

        public void setRecord(int record) {
            this.record = record;
        }

        public List<ResourceFieldMappingAuditCol> getCols() {
            return cols;
        }

        public void setCols(List<ResourceFieldMappingAuditCol> cols) {
            this.cols = cols;
        }

        public Long getOldStyleAuditId() {
            return oldStyleAuditId;
        }

        public void setOldStyleAuditId(Long oldStyleAuditId) {
            this.oldStyleAuditId = oldStyleAuditId;
        }

        /**
         * required for the de-serialisation of the old-style JSON
         */
        public void setAuditId(Long oldStyleAuditId) {
            this.oldStyleAuditId = oldStyleAuditId;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResourceFieldMappingAuditCol {
        //variable names kept short as this object is persisted to JSON and I want to avoid using excessive storage space
        private short col; //changed from int to short as max column count is only about 600
        private String field;

        public ResourceFieldMappingAuditCol() { }

        public short getCol() {
            return col;
        }

        public void setCol(short col) {
            this.col = col;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}

