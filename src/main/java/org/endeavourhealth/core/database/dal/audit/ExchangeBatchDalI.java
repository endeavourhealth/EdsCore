package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.ExchangeBatch;

import java.util.List;
import java.util.UUID;

public interface ExchangeBatchDalI {

    List<ExchangeBatch> retrieveForExchangeId(UUID exchangeId) throws Exception;
    ExchangeBatch retrieveFirstForExchangeId(UUID exchangeId) throws Exception;
    ExchangeBatch getForExchangeAndBatchId(UUID exchangeId, UUID batchId) throws Exception;
    ExchangeBatch getForBatchId(UUID batchId) throws Exception;

    void save(List<ExchangeBatch> exchangeBatches) throws Exception;
}
