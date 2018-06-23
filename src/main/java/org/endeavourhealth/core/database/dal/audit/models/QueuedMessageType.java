package org.endeavourhealth.core.database.dal.audit.models;

public enum QueuedMessageType {

    InboundData(1),
    OutboundData(2),
    ResourceTempStore(3);

    private int value;

    QueuedMessageType(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
