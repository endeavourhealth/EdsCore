package org.endeavourhealth.core.database.cassandra.admin;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.mapping.Mapper;
import com.google.common.collect.Lists;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.admin.accessors.OrganisationAccessor;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraOrganisation;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraService;
import org.endeavourhealth.core.database.dal.admin.OrganisationDalI;
import org.endeavourhealth.core.database.dal.admin.models.Organisation;

import java.util.*;
import java.util.stream.Collectors;

public class CassandraOrganisationRepository extends Repository implements OrganisationDalI {

	public UUID save(Organisation organisation) {

		Mapper<CassandraOrganisation> mapper = getMappingManager().mapper(CassandraOrganisation.class);
		Mapper<CassandraService> serviceMapper = getMappingManager().mapper(CassandraService.class);

		//first need to work out what changes we need to make to any services linked to this organisation
		Set<UUID> additions;
		Set<UUID> deletions;

		if (organisation.getId() == null) {
			// New organisation, just save with all services as additions
			organisation.setId(UUID.randomUUID());
			additions = new TreeSet<>(organisation.getServices().keySet());
			deletions = new TreeSet<>();
		} else {
			// Existing organisation, update service links
			CassandraOrganisation oldOrganisation = mapper.get(organisation.getId());

			additions = new TreeSet<>(organisation.getServices().keySet());
			additions.removeAll(oldOrganisation.getServices().keySet());

			deletions = new TreeSet<>(oldOrganisation.getServices().keySet());
			deletions.removeAll(organisation.getServices().keySet());
		}

		CassandraOrganisation dbOrganisation = new CassandraOrganisation(organisation);

		// Update the org
		BatchStatement batchStatement = new BatchStatement();
		batchStatement.add(mapper.saveQuery(dbOrganisation));

		// Process removed services
		for (UUID serviceUuid : deletions) {
			CassandraService service = serviceMapper.get(serviceUuid);
			service.getOrganisations().remove(organisation.getId());
			batchStatement.add(serviceMapper.saveQuery(service));
		}

		// Process added services
		for (UUID serviceUuid : additions) {
			CassandraService service = serviceMapper.get(serviceUuid);
			service.getOrganisations().put(organisation.getId(), organisation.getName());
			batchStatement.add(serviceMapper.saveQuery(service));
		}

		getSession().execute(batchStatement);

		return organisation.getId();
	}

	/*public Set<Organisation> getByIds(Set<UUID> ids) {
		Set<Organisation> orgs = new HashSet<>();
		for (UUID id: ids) {
			Organisation org = getById(id);
			orgs.add(org);
		}
		return orgs;
	}*/

	public Organisation getById(UUID id) {
		Mapper<CassandraOrganisation> mapper = getMappingManager().mapper(CassandraOrganisation.class);
		CassandraOrganisation result = mapper.get(id);
		if (result != null) {
			return new Organisation(result);
		} else {
			return null;
		}
	}

	public Organisation getByNationalId(String nationalId) {
		OrganisationAccessor accessor = getMappingManager().createAccessor(OrganisationAccessor.class);
		Iterator<CassandraOrganisation> iterator = accessor.getByNationalId(nationalId).iterator();
		if (iterator.hasNext()) {
			CassandraOrganisation result = iterator.next();
			return new Organisation(result);
		} else {
			return null;
		}
	}

	public void delete(Organisation organisation) {

		CassandraOrganisation dbOrganisation = new CassandraOrganisation(organisation);

		Mapper<CassandraOrganisation> organisationMapper = getMappingManager().mapper(CassandraOrganisation.class);
		Mapper<CassandraService> serviceMapper = getMappingManager().mapper(CassandraService.class);

		BatchStatement batchStatement = new BatchStatement();
		batchStatement.add(organisationMapper.deleteQuery(dbOrganisation));

		//also need to update any services that link to our organisation
		for (UUID serviceUuid : organisation.getServices().keySet()) {
			CassandraService dbService = serviceMapper.get(serviceUuid);
			dbService.getOrganisations().remove(organisation.getId());
			batchStatement.add(serviceMapper.saveQuery(dbService));
		}

		getSession().execute(batchStatement);
	}

	public List<Organisation> getAll() {
		OrganisationAccessor accessor = getMappingManager().createAccessor(OrganisationAccessor.class);
		List<CassandraOrganisation> results = Lists.newArrayList(accessor.getAll());
		return results
				.stream()
				.map(T -> new Organisation(T))
				.collect(Collectors.toList());
	}

	/*public Iterable<OrganisationEndUserLink> getByUserId(UUID userId) {
		OrganisationAccessor accessor = getMappingManager().createAccessor(OrganisationAccessor.class);
		return accessor.getOrganisationEndUserLinkByEndUserId(userId);
	}*/

	public List<Organisation> search(String searchData) {
		String rangeEnd = searchData + 'z';
		OrganisationAccessor accessor = getMappingManager().createAccessor(OrganisationAccessor.class);
		List<CassandraOrganisation> results = Lists.newArrayList(accessor.search(searchData, rangeEnd));
		return results
				.stream()
				.map(T -> new Organisation(T))
				.collect(Collectors.toList());
	}

}

