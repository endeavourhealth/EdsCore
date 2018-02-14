package org.endeavourhealth.core.database.dal.publisherTransform.models;

import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.common.cache.ObjectMapperPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceFieldMappingAudit {

    //although it seems simpler to use a list, the map is faster for lookups
    private Map<Long, ResourceFieldMappingAuditRow> audits = new HashMap<>();

    public ResourceFieldMappingAudit() {}


    public String writeToJson() throws Exception {
        //write it out as a LIST to keep the JSON as small as possible
        List<ResourceFieldMappingAuditRow> list = new ArrayList<>(audits.values());
        return ObjectMapperPool.getInstance().writeValueAsString(list);
    }

    public static ResourceFieldMappingAudit readFromJson(String json) throws Exception {

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
    }

    public Map<Long, ResourceFieldMappingAuditRow> getAudits() {
        return audits;
    }

    public void auditValue(long rowAuditId, int colIndex, String jsonField) {

        ResourceFieldMappingAuditRow audit = audits.get(new Long(rowAuditId));
        if (audit == null) {
            audit = new ResourceFieldMappingAuditRow(rowAuditId);
            audits.put(new Long(rowAuditId), audit);
        }
        audit.addColumnMapping(colIndex, jsonField);
    }

    public class ResourceFieldMappingAuditRow {
        //variable names kept short as this object is persisted to JSON and I want to avoid using excessive storage space
        private long auditId;
        private List<ResourceFieldMappingAuditCol> cols = new ArrayList<>();


        public ResourceFieldMappingAuditRow(long auditId) {
            this.auditId = auditId;
        }

        public long getAuditId() {
            return auditId;
        }

        public List<ResourceFieldMappingAuditCol> getCols() {
            return cols;
        }

        public void addColumnMapping(int col, String field) {
            this.cols.add(new ResourceFieldMappingAuditCol(col, field));
        }
    }


    public class ResourceFieldMappingAuditCol {
        //variable names kept short as this object is persisted to JSON and I want to avoid using excessive storage space
        private int col;
        private String field;


        public ResourceFieldMappingAuditCol(int col, String field) {
            this.col = col;
            this.field = field;
        }

        public int getCol() {
            return col;
        }

        public String getField() {
            return field;
        }
    }
}

