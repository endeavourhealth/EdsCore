package org.endeavourhealth.core.database.cassandra.admin.accessors;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraService;

@Accessor
public interface ServiceAccessor {
	@Query("SELECT * FROM admin.service")
	Result<CassandraService> getAll();

	@Query("SELECT * FROM admin.service WHERE name >= :searchData AND name < :rangeEnd allow filtering")
	Result<CassandraService> search(@Param("searchData") String searchData, @Param("rangeEnd") String rangeEnd);

	@Query("SELECT * FROM admin.service_by_local_identifier WHERE local_identifier = :localIdentifier")
	Result<CassandraService> getByLocalIdentifier(@Param("localIdentifier") String localIdentifier);
}
