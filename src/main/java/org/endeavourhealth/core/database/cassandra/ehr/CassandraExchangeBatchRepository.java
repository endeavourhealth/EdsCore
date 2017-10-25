package org.endeavourhealth.core.database.cassandra.ehr;

import com.datastax.driver.mapping.Mapper;
import com.google.common.collect.Lists;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.ehr.accessors.ExchangeBatchAccessor;
import org.endeavourhealth.core.database.cassandra.ehr.models.CassandraExchangeBatch;
import org.endeavourhealth.core.database.dal.audit.ExchangeBatchDalI;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeBatch;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CassandraExchangeBatchRepository extends Repository implements ExchangeBatchDalI {

    public void save(ExchangeBatch exchangeBatch) {
        if (exchangeBatch == null) {
            throw new IllegalArgumentException("exchangeBatch is null");
        }

        CassandraExchangeBatch dbObj = new CassandraExchangeBatch(exchangeBatch);

        Mapper<CassandraExchangeBatch> mapper = getMappingManager().mapper(CassandraExchangeBatch.class);
        mapper.save(dbObj);
    }

    public List<ExchangeBatch> retrieveForExchangeId(UUID exchangeId) {
        ExchangeBatchAccessor accessor = getMappingManager().createAccessor(ExchangeBatchAccessor.class);
        return Lists.newArrayList(accessor.getForExchangeId(exchangeId))
                .stream()
                .map(T -> new ExchangeBatch(T))
                .collect(Collectors.toList());
    }

    public ExchangeBatch retrieveFirstForExchangeId(UUID exchangeId) {
        ExchangeBatchAccessor accessor = getMappingManager().createAccessor(ExchangeBatchAccessor.class);
        CassandraExchangeBatch result = accessor.getFirstForExchangeId(exchangeId);
        if (result != null) {
            return new ExchangeBatch(result);
        } else {
            return null;
        }
    }

    public ExchangeBatch getForExchangeAndBatchId(UUID exchangeId, UUID batchId) {
        Mapper<CassandraExchangeBatch> mapper = getMappingManager().mapper(CassandraExchangeBatch.class);
        CassandraExchangeBatch result = mapper.get(exchangeId, batchId);
        if (result != null) {
            return new ExchangeBatch(result);
        } else {
            return null;
        }
    }

    /*public ExchangeBatch retrieveFirstForBatchId(UUID batchId) {
        ExchangeBatchAccessor accessor = getMappingManager().createAccessor(ExchangeBatchAccessor.class);
        return accessor.getFirstForBatchId(batchId);
    }*/
}
