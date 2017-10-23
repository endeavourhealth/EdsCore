package org.endeavourhealth.core.rdbms.audit.models;

public enum QueuedMessageType {

    InboundData(1),
    OutboundData(2);

    private int value;

    QueuedMessageType(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
