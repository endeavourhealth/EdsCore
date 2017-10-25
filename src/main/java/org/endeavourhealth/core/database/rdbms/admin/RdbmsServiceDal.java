package org.endeavourhealth.core.database.rdbms.admin;

import org.endeavourhealth.core.database.dal.admin.ServiceDalI;
import org.endeavourhealth.core.database.dal.admin.models.Organisation;
import org.endeavourhealth.core.database.dal.admin.models.Service;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsOrganisation;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsService;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;

public class RdbmsServiceDal implements ServiceDalI {

    public UUID save(Service service) throws Exception{

        Set<UUID> additions = null;
        Set<UUID> deletions = null;

        UUID serviceUuid = null;

        if (service.getId() == null) {
            serviceUuid = UUID.randomUUID();
            // New service, just save with all orgs as additions
            service.setId(serviceUuid);

            additions = new HashSet<>(service.getOrganisations().keySet());
            deletions = new HashSet<>();
        } else {
            serviceUuid = service.getId();

            // Existing service, update org links
            Map<UUID, String> newMap = service.getOrganisations();

            Service oldService = getById(serviceUuid);
            Map<UUID, String> oldMap = oldService.getOrganisations();

            additions = new HashSet<>(newMap.keySet());
            additions.removeAll(oldMap.keySet());

            deletions = new HashSet<>(oldMap.keySet());
            deletions.removeAll(newMap.keySet());
        }

        RdbmsService dbService = new RdbmsService(service);

        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(dbService);

        RdbmsOrganisationDal organisationDal = new RdbmsOrganisationDal();

        // Process removed orgs
        for (UUID orgUuid : deletions) {
            Organisation organisation = organisationDal.getById(orgUuid);
            Map<UUID, String> map = organisation.getServices();
            map.remove(serviceUuid);

            RdbmsOrganisation dbOrganisation = new RdbmsOrganisation(organisation);
            entityManager.persist(dbOrganisation);
        }

        // Process added orgs
        for (UUID orgUuid : additions) {
            Organisation organisation = organisationDal.getById(orgUuid);
            Map<UUID, String> map = organisation.getServices();
            map.put(serviceUuid, service.getName());

            RdbmsOrganisation dbOrganisation = new RdbmsOrganisation(organisation);
            entityManager.persist(dbOrganisation);
        }

        entityManager.getTransaction().commit();
        entityManager.close();

        return serviceUuid;
    }

    public Service getById(UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsService c"
                + " where c.id = :id";

        Query query = entityManager.createQuery(sql, RdbmsService.class)
                .setParameter("id", id.toString());

        Service ret = null;
        try {
            RdbmsService result = (RdbmsService)query.getSingleResult();
            ret = new Service(result);

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }


    public void delete(Service service) throws Exception {

        RdbmsService dbService = new RdbmsService(service);

        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        entityManager.getTransaction().begin();
        entityManager.remove(dbService);

        //also need to update any linked organisations
        RdbmsOrganisationDal organisationDal = new RdbmsOrganisationDal();

        for (UUID orgUuid : service.getOrganisations().keySet()) {
            Organisation organisation = organisationDal.getById(orgUuid);
            if (organisation != null) {
                Map<UUID, String> map = organisation.getServices();
                map.remove(service.getId());

                RdbmsOrganisation dbOrganisation = new RdbmsOrganisation(organisation);
                entityManager.persist(dbOrganisation);
            }
        }

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public List<Service> getAll() throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsService c";

        Query query = entityManager.createQuery(sql, RdbmsService.class);

        List<RdbmsService> results = query.getResultList();
        entityManager.close();

        //can't use stream as the constructor throws an exception
        List<Service> ret = new ArrayList<>();
        for (RdbmsService result: results) {
            ret.add(new Service(result));
        }
        return ret;
    }

    public List<Service> search(String searchData) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsService c"
                + " where c.name like :id_like"
                + " or c.localId = :id_like";

        Query query = entityManager.createQuery(sql, RdbmsService.class)
                .setParameter("id_like", searchData + "%");

        List<RdbmsService> results = query.getResultList();
        entityManager.close();

        //can't use stream as the constructor throws an exception
        List<Service> ret = new ArrayList<>();
        for (RdbmsService result: results) {
            ret.add(new Service(result));
        }
        return ret;
    }

    public Service getByLocalIdentifier(String localIdentifier) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsService c"
                + " where c.localId = :id";

        Query query = entityManager.createQuery(sql, RdbmsService.class)
                .setParameter("id", localIdentifier);

        Service ret = null;
        try {
            RdbmsService result = (RdbmsService)query.getSingleResult();
            ret = new Service(result);

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    /*public static Service getByOrganisationNationalId(String nationalId) throws Exception {

        Organisation organisation = OrganisationHelper.getByNationalId(nationalId);
        if (organisation == null) {
            return null;
        }

        Iterator<UUID> iterator = organisation.getServicesMap().keySet().iterator();
        if (iterator.hasNext()) {
            UUID serviceId = iterator.next();
            return getById(serviceId);
        } else {
            return null;
        }
    }*/
}
