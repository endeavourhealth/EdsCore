package org.endeavourhealth.core.database.dal.publisherTransform.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import org.endeavourhealth.common.cache.ObjectMapperPool;

import java.util.ArrayList;
import java.util.List;

public class ResourceFieldMappingAudit {

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

    /*public static ResourceFieldMappingAudit readFromJson(String json) throws Exception {
        ResourceFieldMappingAudit ret = new ResourceFieldMappingAudit();

        JsonNode tree = ObjectMapperPool.getInstance().readTree(json);
        for (int i=0; i<tree.size(); i++) {
            JsonNode auditNode = tree.get(i);

            long auditId = auditNode.get("auditId").asLong();
            JsonNode colsNode = auditNode.get("cols");
            for (int j=0; j<colsNode.size(); j++) {
                JsonNode colNode = colsNode.get(j);
                int colIndex = colNode.get("col").asInt();
                String field = colNode.get("field").asText();

                ret.auditValue(auditId, colIndex, field);
            }
        }

        return ret;
    }*/

    /*public Map<Long, ResourceFieldMappingAuditRow> getAudits() {
        return audits;
    }*/

    public void auditValue(int publishedFileId, int recordNumber, int colIndex, String jsonField) {

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

        audit.addColumnMapping(colIndex, jsonField);
    }

    public void auditValueOldStyle(Long oldStyleAuditId, int colIndex, String jsonField) {

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResourceFieldMappingAuditRow {
        //variable names kept short as this object is persisted to JSON and I want to avoid using excessive storage space
        private int fileId; //published file ID
        private int record; //record number (starts at 1)
        private List<ResourceFieldMappingAuditCol> cols = new ArrayList<>();
        private Long oldStyleAuditId; //used to allow backwards compatability with old auditing mechanism

        public ResourceFieldMappingAuditRow() {}

        public void addColumnMapping(int col, String field) {
            ResourceFieldMappingAuditCol obj = new ResourceFieldMappingAuditCol();
            obj.setCol(col);
            obj.setField(field);
            this.cols.add(obj);
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
        private int col;
        private String field;

        public ResourceFieldMappingAuditCol() { }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
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

