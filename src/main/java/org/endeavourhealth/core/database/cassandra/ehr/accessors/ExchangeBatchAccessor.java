package org.endeavourhealth.core.database.cassandra.ehr.accessors;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import org.endeavourhealth.core.database.cassandra.ehr.models.CassandraExchangeBatch;

import java.util.UUID;

@Accessor
public interface ExchangeBatchAccessor {

    @Query("SELECT * FROM ehr.exchange_batch WHERE exchange_id = :exchange_id")
    Result<CassandraExchangeBatch> getForExchangeId(@Param("exchange_id") UUID exchangeId);

    @Query("SELECT * FROM ehr.exchange_batch WHERE exchange_id = :exchange_id LIMIT 1")
    CassandraExchangeBatch getFirstForExchangeId(@Param("exchange_id") UUID exchangeId);

    @Query("SELECT * FROM ehr.exchange_batch WHERE batch_id = :batch_id LIMIT 1 ALLOW FILTERING")
    CassandraExchangeBatch getFirstForBatchId(@Param("batch_id") UUID batchId);

}