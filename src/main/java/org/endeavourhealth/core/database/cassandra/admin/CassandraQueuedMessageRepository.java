package org.endeavourhealth.core.database.cassandra.admin;

import com.datastax.driver.mapping.Mapper;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraQueuedMessage;
import org.endeavourhealth.core.database.dal.audit.QueuedMessageDalI;
import org.endeavourhealth.core.database.dal.audit.models.QueuedMessageType;

import java.util.UUID;

public class CassandraQueuedMessageRepository extends Repository implements QueuedMessageDalI {

	public void save(UUID messageId, String messageBody, QueuedMessageType type) {
		Mapper<CassandraQueuedMessage> mapper = getMappingManager().mapper(CassandraQueuedMessage.class);

		CassandraQueuedMessage queuedMessage = new CassandraQueuedMessage();
		queuedMessage.setId(messageId);
		queuedMessage.setMessageBody(messageBody);

		mapper.save(queuedMessage);
	}

	public String getById(UUID id) {
		Mapper<CassandraQueuedMessage> mapper = getMappingManager().mapper(CassandraQueuedMessage.class);
		CassandraQueuedMessage result = mapper.get(id);
		if (result != null) {
			return result.getMessageBody();
		} else {
			return null;
		}
	}

	@Override
	public void delete(UUID id) throws Exception {
		Mapper<CassandraQueuedMessage> mapper = getMappingManager().mapper(CassandraQueuedMessage.class);
		mapper.delete(id);
	}
}

