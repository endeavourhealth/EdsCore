package org.endeavourhealth.core.database.cassandra.ehr.accessors;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import org.endeavourhealth.core.database.cassandra.ehr.models.CassandraResourceByExchangeBatch;
import org.endeavourhealth.core.database.cassandra.ehr.models.CassandraResourceByPatient;
import org.endeavourhealth.core.database.cassandra.ehr.models.CassandraResourceByService;

import java.util.List;
import java.util.UUID;

@Accessor
public interface ResourceAccessor {
    @Query("SELECT * FROM ehr.resource_by_patient WHERE service_id = :service_id AND system_id = :system_id AND patient_id = :patient_id")
    Result<CassandraResourceByPatient> getResourcesByPatient(@Param("service_id") UUID serviceId,
                                                             @Param("system_id") UUID systemId,
                                                             @Param("patient_id") UUID patientId);

    @Query("SELECT * FROM ehr.resource_by_patient WHERE service_id = :service_id AND system_id = :system_id AND patient_id = :patient_id AND resource_type = :resource_type")
    Result<CassandraResourceByPatient> getResourcesByPatient(@Param("service_id") UUID serviceId,
                                                             @Param("system_id") UUID systemId,
                                                             @Param("patient_id") UUID patientId,
                                                             @Param("resource_type") String resourceType);

    @Query("SELECT * FROM ehr.resource_by_patient_all_systems WHERE service_id = :service_id AND patient_id = :patient_id AND resource_type = :resource_type")
    Result<CassandraResourceByPatient> getResourcesByPatientAllSystems(@Param("service_id") UUID serviceId,
                                                                       @Param("patient_id") UUID patientId,
                                                                       @Param("resource_type") String resourceType);

    @Query("SELECT * FROM ehr.resource_by_exchange_batch WHERE batch_id = :batch_id")
    Result<CassandraResourceByExchangeBatch> getResourcesForBatch(@Param("batch_id") UUID batchId);

    @Query("SELECT * FROM ehr.resource_by_exchange_batch WHERE batch_id = :batch_id AND resource_type = :resource_type")
    Result<CassandraResourceByExchangeBatch> getResourcesForBatch(@Param("batch_id") UUID batchId,
                                                                  @Param("resource_type") String resourceType);

    @Query("SELECT * FROM ehr.resource_by_exchange_batch WHERE batch_id = :batch_id AND resource_type = :resource_type AND resource_id = :resource_id")
    Result<CassandraResourceByExchangeBatch> getResourcesForBatch(@Param("batch_id") UUID batchId,
                                                                  @Param("resource_type") String resourceType,
                                                                  @Param("resource_id") UUID resourceId);


    @Query("SELECT count(*) FROM ehr.resource_by_service WHERE service_id = :service_id AND system_id = :system_id AND resource_type = :resource_type")
    ResultSet getResourceCountByService(@Param("service_id") UUID serviceId,
																				@Param("system_id") UUID systemId,
																				@Param("resource_type") String resourceType);

    @Query("SELECT resource_metadata FROM ehr.resource_by_service WHERE service_id = :service_id AND system_id = :system_id AND resource_type = :resource_type")
    ResultSet getMetadataByService(@Param("service_id") UUID serviceId,
																	 @Param("system_id") UUID systemId,
																	 @Param("resource_type") String resourceType);

    @Query("SELECT * FROM ehr.resource_by_service WHERE service_id = :service_id AND system_id = :system_id AND resource_type = :resource_type and resource_id IN :resource_id")
    Result<CassandraResourceByService> getResourcesByService(@Param("service_id") UUID serviceId,
                                                             @Param("system_id") UUID systemId,
                                                             @Param("resource_type") String resourceType,
                                                             @Param("resource_id") List<UUID> resourceIds);

    @Query("SELECT * FROM ehr.resource_by_service_all_systems WHERE service_id = :service_id AND resource_type = :resource_type and resource_id IN :resource_id")
    Result<CassandraResourceByService> getResourcesByServiceAllSystems(@Param("service_id") UUID serviceId,
                                                                       @Param("resource_type") String resourceType,
                                                                       @Param("resource_id") List<UUID> resourceIds);

    @Query("SELECT * FROM ehr.resource_by_service WHERE service_id = :service_id AND system_id = :system_id AND resource_type = :resource_type LIMIT 1")
    Result<CassandraResourceByService> getFirstResourceByService(@Param("service_id") UUID serviceId,
                                                                 @Param("system_id") UUID systemId,
                                                                 @Param("resource_type") String resourceType);

    @Query("SELECT * FROM ehr.resource_by_service WHERE service_id = :service_id AND system_id = :system_id AND resource_type = :resource_type")
    Result<CassandraResourceByService> getResourcesByService(@Param("service_id") UUID serviceId,
                                                             @Param("system_id") UUID systemId,
                                                             @Param("resource_type") String resourceType);

    @Query("SELECT * FROM ehr.resource_by_exchange_batch WHERE resource_type = :resource_type AND resource_id = :resource_id LIMIT 1 ALLOW FILTERING")
    public CassandraResourceByExchangeBatch getFirstResourceByExchangeBatch(@Param("resource_type") String resourceType,
                                                                            @Param("resource_id") UUID resourceId);
}
