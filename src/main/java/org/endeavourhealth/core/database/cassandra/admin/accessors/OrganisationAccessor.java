package org.endeavourhealth.core.database.cassandra.admin.accessors;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraOrganisation;

@Accessor
public interface OrganisationAccessor {
	@Query("SELECT * FROM admin.organisation")
	Result<CassandraOrganisation> getAll();

	/*@Query("SELECT * FROM admin.organisation_end_user_link_by_user_id WHERE end_user_id = :end_user_id")
	Result<OrganisationEndUserLink> getOrganisationEndUserLinkByEndUserId(@Param("end_user_id") UUID endUserId);*/

	@Query("SELECT * FROM admin.organisation WHERE name >= :searchData AND name < :rangeEnd allow filtering")
	Result<CassandraOrganisation> search(@Param("searchData") String searchData, @Param("rangeEnd") String rangeEnd);

	@Query("SELECT * FROM admin.organisation_by_national_id WHERE national_id = :nationalId")
	Result<CassandraOrganisation> getByNationalId(@Param("nationalId") String nationalId);

}
