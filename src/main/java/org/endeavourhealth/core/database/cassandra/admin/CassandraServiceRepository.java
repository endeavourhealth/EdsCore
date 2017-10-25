package org.endeavourhealth.core.database.cassandra.admin;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.mapping.Mapper;
import com.google.common.collect.Lists;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.admin.accessors.ServiceAccessor;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraOrganisation;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraService;
import org.endeavourhealth.core.database.dal.admin.ServiceDalI;
import org.endeavourhealth.core.database.dal.admin.models.Service;

import java.util.*;
import java.util.stream.Collectors;

public class CassandraServiceRepository extends Repository implements ServiceDalI {

	public UUID save(Service service) {

		Mapper<CassandraService> mapper = getMappingManager().mapper(CassandraService.class);
		Mapper<CassandraOrganisation> orgMapper = getMappingManager().mapper(CassandraOrganisation.class);

		//first need to work out any changes that need to be made to linked organisations
		Set<UUID> additions;
		Set<UUID> deletions;

		if (service.getId() == null) {
			// New service, just save with all orgs as additions
			service.setId(UUID.randomUUID());
			additions = new TreeSet<>(service.getOrganisations().keySet());
			deletions = new TreeSet<>();
		} else {
			// Existing service, update org links
			CassandraService oldService = mapper.get(service.getId());

			additions = new TreeSet<>(service.getOrganisations().keySet());
			additions.removeAll(oldService.getOrganisations().keySet());

			deletions = new TreeSet<>(oldService.getOrganisations().keySet());
			deletions.removeAll(service.getOrganisations().keySet());
		}

		CassandraService dbService = new CassandraService(service);

		// Update the service
		BatchStatement batchStatement = new BatchStatement();
		batchStatement.add(mapper.saveQuery(dbService));



		// Process removed orgs
		for (UUID orgUuid : deletions) {
			CassandraOrganisation organisation = orgMapper.get(orgUuid);
			organisation.getServices().remove(service.getId());
			batchStatement.add(orgMapper.saveQuery(organisation));
		}

		// Process added orgs
		for (UUID orgUuid : additions) {
			CassandraOrganisation organisation = orgMapper.get(orgUuid);
			organisation.getServices().put(service.getId(), service.getName());
			batchStatement.add(orgMapper.saveQuery(organisation));
		}

		getSession().execute(batchStatement);

		return service.getId();
	}

	public Service getById(UUID id) {
		Mapper<CassandraService> mapper = getMappingManager().mapper(CassandraService.class);
		CassandraService result = mapper.get(id);
		if (result != null) {
			return new Service(result);

		} else{
			return null;
		}
	}


	public void delete(Service service) {

		CassandraService dbObj = new CassandraService(service);

		Mapper<CassandraService> serviceMapper = getMappingManager().mapper(CassandraService.class);
		Mapper<CassandraOrganisation> orgMapper = getMappingManager().mapper(CassandraOrganisation.class);

		BatchStatement batchStatement = new BatchStatement();
		batchStatement.add(serviceMapper.deleteQuery(dbObj));

		for (UUID orgUuid : service.getOrganisations().keySet()) {
			CassandraOrganisation organisation = orgMapper.get(orgUuid);
			if (organisation != null) {
				organisation.getServices().remove(service.getId());
				batchStatement.add(orgMapper.saveQuery(organisation));
			}
		}

		getSession().execute(batchStatement);
	}

	public List<Service> getAll() {
		ServiceAccessor accessor = getMappingManager().createAccessor(ServiceAccessor.class);
		List<CassandraService> result = Lists.newArrayList(accessor.getAll());
		return result
				.stream()
				.map(T -> new Service(T))
				.collect(Collectors.toList());
	}

	public List<Service> search(String searchData) {
		String rangeEnd = searchData + 'z';
		ServiceAccessor accessor = getMappingManager().createAccessor(ServiceAccessor.class);
		List<CassandraService> result = Lists.newArrayList(accessor.search(searchData, rangeEnd));
		return result
				.stream()
				.map(T -> new Service(T))
				.collect(Collectors.toList());
	}

	public Service getByLocalIdentifier(String localIdentifier) {
		ServiceAccessor accessor = getMappingManager().createAccessor(ServiceAccessor.class);
		Iterator<CassandraService> iterator = accessor.getByLocalIdentifier(localIdentifier).iterator();
		if (iterator.hasNext()) {
			CassandraService result = iterator.next();
			return new Service(result);
		} else {
			return null;
		}
	}

	/*public Service getByOrganisationNationalId(String nationalId) {
		OrganisationRepository organisationRepository = new OrganisationRepository();
		Organisation organisation = organisationRepository.getByNationalId(nationalId);
		if (organisation == null)
			return null;

		Iterator<UUID> iterator = organisation.getServices().keySet().iterator();
		if (iterator.hasNext()) {
			UUID serviceId = iterator.next();
			return getById(serviceId);
		} else {
			return null;
		}
	}*/
}

