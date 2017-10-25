package org.endeavourhealth.core.database.rdbms.admin;

import org.endeavourhealth.core.database.dal.admin.OrganisationDalI;
import org.endeavourhealth.core.database.dal.admin.models.Organisation;
import org.endeavourhealth.core.database.dal.admin.models.Service;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsOrganisation;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsService;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;

public class RdbmsOrganisationDal implements OrganisationDalI {

    public UUID save(Organisation organisation) throws Exception {

        //first work out any changes that need to be made to services
        Set<UUID> additions = null;
        Set<UUID> deletions = null;

        UUID orgUuid = null;

        if (organisation.getId() == null) {
            // New organisation, just save with all services as additions
            orgUuid = UUID.randomUUID();
            organisation.setId(orgUuid);

            additions = new HashSet<>(organisation.getServices().keySet());
            deletions = new HashSet<>();
        } else {

            // Existing organisation, update service links
            orgUuid = organisation.getId();

            Map<UUID, String> newMap = organisation.getServices();

            Organisation oldOrganisation = getById(orgUuid);
            Map<UUID, String> oldMap = oldOrganisation.getServices();

            additions = new TreeSet<>(newMap.keySet());
            additions.removeAll(oldMap.keySet());

            deletions = new TreeSet<>(oldMap.keySet());
            deletions.removeAll(newMap.keySet());
        }

        RdbmsOrganisation dbOrganisation = new RdbmsOrganisation(organisation);

        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(dbOrganisation);

        RdbmsServiceDal serviceDal = new RdbmsServiceDal();

        // Process removed services
        for (UUID serviceUuid : deletions) {
            Service service = serviceDal.getById(serviceUuid);
            Map<UUID, String> map = service.getOrganisations();
            map.remove(orgUuid);

            RdbmsService dbService = new RdbmsService(service);
            entityManager.persist(dbService);
        }

        // Process added services
        for (UUID serviceUuid : additions) {
            Service service = serviceDal.getById(serviceUuid);
            Map<UUID, String> map = service.getOrganisations();
            map.put(orgUuid, organisation.getName());

            RdbmsService dbService = new RdbmsService(service);
            entityManager.persist(dbService);
        }

        entityManager.getTransaction().commit();
        entityManager.close();

        return orgUuid;
    }



    public Organisation getById(UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsOrganisation c"
                + " where c.id = :id";

        Query query = entityManager.createQuery(sql, RdbmsOrganisation.class)
                .setParameter("id", id.toString());

        Organisation ret = null;
        try {
            RdbmsOrganisation result = (RdbmsOrganisation)query.getSingleResult();
            ret = new Organisation(result);

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    public Organisation getByNationalId(String nationalId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsOrganisation c"
                + " where c.nationalId = :id";

        Query query = entityManager.createQuery(sql, RdbmsOrganisation.class)
                .setParameter("id", nationalId);

        Organisation ret = null;
        try {
            RdbmsOrganisation result = (RdbmsOrganisation)query.getSingleResult();
            ret = new Organisation(result);

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    public void delete(Organisation organisation) throws Exception {

        RdbmsOrganisation dbOrganisation = new RdbmsOrganisation(organisation);

        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        entityManager.getTransaction().begin();
        entityManager.remove(dbOrganisation);

        //also need to update any linked services
        RdbmsServiceDal serviceDalDal = new RdbmsServiceDal();

        for (UUID serviceUuid : organisation.getServices().keySet()) {
            Service service = serviceDalDal.getById(serviceUuid);
            Map<UUID, String> map = service.getOrganisations();
            map.remove(organisation.getId());

            RdbmsService dbService = new RdbmsService(service);
            entityManager.persist(dbService);
        }

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public List<Organisation> getAll() throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsOrganisation c";

        Query query = entityManager.createQuery(sql, RdbmsOrganisation.class);

        List<RdbmsOrganisation> results = query.getResultList();
        entityManager.close();

        //can't use stream as the constructor throws an exception
        List<Organisation> ret = new ArrayList<>();
        for (RdbmsOrganisation result: results) {
            ret.add(new Organisation(result));
        }

        return ret;
    }


    public List<Organisation> search(String searchData) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsOrganisation c"
                + " where c.name like :id_like"
                + " or c.nationalId = :id_like";

        Query query = entityManager.createQuery(sql, RdbmsOrganisation.class)
                .setParameter("id_like", searchData + "%");

        List<RdbmsOrganisation> results = query.getResultList();
        entityManager.close();

        //can't use stream as the constructor throws an exception
        List<Organisation> ret = new ArrayList<>();
        for (RdbmsOrganisation result: results) {
            ret.add(new Organisation(result));
        }

        return ret;
    }
}
