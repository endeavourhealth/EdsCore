package org.endeavourhealth.core.database.cassandra.transform.accessors;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import org.endeavourhealth.core.database.cassandra.transform.models.CassandraResourceIdMap;
import org.endeavourhealth.core.database.cassandra.transform.models.CassandraResourceIdMapByEdsId;

import java.util.UUID;

@Accessor
public interface ResourceIdMapAccessor {

    @Query("SELECT * FROM transform.resource_id_map WHERE service_id = :service_id AND system_id = :system_id AND resource_type = :resource_type AND source_id = :source_id LIMIT 1")
    Result<CassandraResourceIdMap> getResourceIdMap(@Param("service_id") UUID serviceId,
                                                    @Param("system_id") UUID systemId,
                                                    @Param("resource_type") String resourceType,
                                                    @Param("source_id") String sourceId);

    @Query("SELECT * FROM transform.resource_id_map_by_eds_id WHERE resource_type = :resource_type AND eds_id = :eds_id LIMIT 1")
    Result<CassandraResourceIdMapByEdsId> getResourceIdMapByEdsId(@Param("resource_type") String resourceType,
                                                                  @Param("eds_id") UUID edsId);

}
