package org.endeavourhealth.core.database.cassandra.audit.accessors;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import org.endeavourhealth.core.database.cassandra.audit.models.CassandraExchangeByService;
import org.endeavourhealth.core.database.cassandra.audit.models.CassandraExchangeEvent;
import org.endeavourhealth.core.database.cassandra.audit.models.CassandraExchangeTransformAudit;
import org.endeavourhealth.core.database.cassandra.audit.models.CassandraExchangeTransformErrorState;

import java.util.Date;
import java.util.UUID;

@Accessor
public interface AuditAccessor {

    @Query("SELECT * FROM audit.exchange_transform_audit WHERE service_id = :service_id AND system_id = :system_id AND exchange_id = :exchange_id LIMIT 1")
    Result<CassandraExchangeTransformAudit> getMostRecentExchangeTransform(@Param("service_id") UUID serviceId,
                                                                           @Param("system_id") UUID systemId,
                                                                           @Param("exchange_id") UUID exchangeId);

    @Query("SELECT * FROM audit.exchange_transform_audit WHERE service_id = :service_id AND system_id = :system_id AND exchange_id = :exchange_id")
    Result<CassandraExchangeTransformAudit> getAllExchangeTransform(@Param("service_id") UUID serviceId,
                                                                    @Param("system_id") UUID systemId,
                                                                    @Param("exchange_id") UUID exchangeId);

    @Query("SELECT * FROM audit.exchange_transform_error_state WHERE service_id = :service_id AND system_id = :system_id LIMIT 1")
    Result<CassandraExchangeTransformErrorState> getErrorState(@Param("service_id") UUID serviceId,
                                                               @Param("system_id") UUID systemId);

    @Query("SELECT * FROM audit.exchange_transform_error_state")
    Result<CassandraExchangeTransformErrorState> getAllErrorStates();

    @Query("SELECT * FROM audit.exchange_transform_audit WHERE service_id = :service_id AND system_id = :system_id LIMIT 1")
    Result<CassandraExchangeTransformAudit> getFirstExchangeTransformAudit(@Param("service_id") UUID serviceId,
                                                                           @Param("system_id") UUID systemId);

    @Query("SELECT * FROM audit.exchange_transform_audit WHERE service_id = :service_id AND system_id = :system_id")
    Result<CassandraExchangeTransformAudit> getAllExchangeTransformAudits(@Param("service_id") UUID serviceId,
                                                                          @Param("system_id") UUID systemId);

    @Query("SELECT * FROM audit.exchange_transform_audit WHERE service_id = :service_id AND system_id = :system_id AND exchange_id = :exchange_id")
    Result<CassandraExchangeTransformAudit> getAllExchangeTransformAudits(@Param("service_id") UUID serviceId,
                                                                          @Param("system_id") UUID systemId,
                                                                          @Param("exchange_id") UUID exchangeId);

    /*@Query("SELECT * FROM audit.exchange ALLOW FILTERING;")
    Result<Exchange> getAllExchanges();*/

    @Query("SELECT * FROM audit.exchange_by_service WHERE service_id = :service_id LIMIT :num_rows")
    Result<CassandraExchangeByService> getExchangesByService(@Param("service_id") UUID serviceId,
                                                             @Param("num_rows") int numRows);

    @Query("SELECT * FROM audit.exchange_by_service WHERE service_id = :service_id AND timestamp >= :date_from AND timestamp <= :date_to LIMIT :num_rows")
    Result<CassandraExchangeByService> getExchangesByService(@Param("service_id") UUID serviceId,
                                                             @Param("num_rows") int numRows,
                                                             @Param("date_from") Date dateFrom,
                                                             @Param("date_to") Date dateTo);

    @Query("SELECT exchange_id FROM audit.exchange_by_service WHERE service_id = :service_id")
    ResultSet getExchangeIdsForService(@Param("service_id") UUID serviceId);

    @Query("SELECT * FROM audit.exchange_event WHERE exchange_id = :exchange_id")
    Result<CassandraExchangeEvent> getExchangeEvents(@Param("exchange_id") UUID exchangeId);

}
