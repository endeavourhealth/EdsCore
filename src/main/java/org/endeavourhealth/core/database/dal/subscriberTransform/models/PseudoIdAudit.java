package org.endeavourhealth.core.database.dal.subscriberTransform.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PseudoIdAudit {

    private String saltName;
    private TreeMap<String, String> keys;
    private String pseudoId;

    public PseudoIdAudit(String saltName, TreeMap<String, String> keys, String pseudoId) {
        this.saltName = saltName;
        this.keys = keys;
        this.pseudoId = pseudoId;
    }

    public String getSaltName() {
        return saltName;
    }

    public TreeMap<String, String> getKeys() {
        return keys;
    }

    public String getPseudoId() {
        return pseudoId;
    }

    public String getKeysAsJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = new ObjectNode(mapper.getNodeFactory());

        Set set = keys.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            String key = (String)me.getKey();
            String val = (String)me.getValue();
            root.put(key, val);
        }
        return mapper.writeValueAsString(root);
    }
}
