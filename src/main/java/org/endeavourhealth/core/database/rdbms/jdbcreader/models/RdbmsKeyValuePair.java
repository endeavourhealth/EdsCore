package org.endeavourhealth.core.database.rdbms.jdbcreader.models;

import org.endeavourhealth.core.database.dal.jdbcreader.models.KeyValuePair;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "key_value_pairs")
public class RdbmsKeyValuePair implements Serializable {

    private String batchName = null;
    private String connectionName = null;
    private String keyValue = null;
    private String dataValue = null;

    public RdbmsKeyValuePair() {}

    public RdbmsKeyValuePair(KeyValuePair proxy) {
        this.batchName = proxy.getBatchName();
        this.connectionName = proxy.getConnectionName();
        this.keyValue = proxy.getKeyValue();
        this.dataValue = proxy.getDataValue();
    }

    @Id
    @Column(name = "batch_name", nullable = false)
    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    @Id
    @Column(name = "connection_name", nullable = false)
    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    @Id
    @Column(name = "key_value", nullable = false)
    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    @Column(name = "data_value", nullable = false)
    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

}

