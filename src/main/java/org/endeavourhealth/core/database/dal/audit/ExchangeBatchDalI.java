package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.ExchangeBatch;

import java.util.List;
import java.util.UUID;

public interface ExchangeBatchDalI {

    public void save(ExchangeBatch exchangeBatch) throws Exception;
    public List<ExchangeBatch> retrieveForExchangeId(UUID exchangeId) throws Exception;
    public ExchangeBatch retrieveFirstForExchangeId(UUID exchangeId) throws Exception;
    public ExchangeBatch getForExchangeAndBatchId(UUID exchangeId, UUID batchId) throws Exception;
}
