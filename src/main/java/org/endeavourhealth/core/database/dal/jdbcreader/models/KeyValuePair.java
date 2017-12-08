package org.endeavourhealth.core.database.dal.jdbcreader.models;

import org.endeavourhealth.core.database.rdbms.jdbcreader.models.RdbmsBatch;
import org.endeavourhealth.core.database.rdbms.jdbcreader.models.RdbmsKeyValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KeyValuePair {

    private String batchName = null;
    private String connectionName = null;
    private String keyValue = null;
    private String dataValue = null;

    public KeyValuePair() {}

    public KeyValuePair(RdbmsKeyValuePair proxy) {
        this.batchName = proxy.getBatchName();
        this.connectionName= proxy.getConnectionName();
        this.keyValue = proxy.getKeyValue();
        this.dataValue = proxy.getDataValue();
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getDataValue() {
        return dataValue;
    }

    public int getDataValueAsInt() {
        return Integer.parseInt(getDataValue());
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
}
