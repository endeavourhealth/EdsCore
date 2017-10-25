package org.endeavourhealth.core.database.rdbms.audit.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "queued_message", schema = "public")
public class RdbmsQueuedMessage implements Serializable {

    private String id = null;
    private String messageBody = null;
    private Date timestamp = null;
    private int queuedMessageTypeId = -1;

    @Id
    @Column(name = "id", nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "message_body", nullable = false)
    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    @Column(name = "timestamp", nullable = false)
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "queued_message_type_id", nullable = false)
    public int getQueuedMessageTypeId() {
        return queuedMessageTypeId;
    }

    public void setQueuedMessageTypeId(int queuedMessageTypeId) {
        this.queuedMessageTypeId = queuedMessageTypeId;
    }
}
