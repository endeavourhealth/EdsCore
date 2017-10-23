package org.endeavourhealth.core.rdbms.admin;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.mapping.Mapper;
import org.endeavourhealth.core.rdbms.ConnectionManager;
import org.endeavourhealth.core.rdbms.admin.models.Organisation;
import org.endeavourhealth.core.rdbms.admin.models.Service;
import org.endeavourhealth.core.rdbms.eds.models.PatientLink;
import org.endeavourhealth.core.rdbms.subscriber.models.EnterpriseIdMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;

public class ServiceHelper {

    public static UUID save(Service service) throws Exception{

        Set<UUID> additions = null;
        Set<UUID> deletions = null;

        UUID serviceUuid = null;

        if (service.getId() == null) {
            serviceUuid = UUID.randomUUID();
            // New service, just save with all orgs as additions
            service.setId(serviceUuid.toString());

            additions = new HashSet<>(service.getOrganisationsMap().keySet());
            deletions = new HashSet<>();
        } else {
            serviceUuid = UUID.fromString(service.getId());

            // Existing service, update org links
            Map<UUID, String> newMap = service.getOrganisationsMap();

            Service oldService = getById(serviceUuid);
            Map<UUID, String> oldMap = oldService.getOrganisationsMap();

            additions = new HashSet<>(newMap.keySet());
            additions.removeAll(oldMap.keySet());

            deletions = new HashSet<>(oldMap.keySet());
            deletions.removeAll(newMap.keySet());
        }

        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(service);

        // Process removed orgs
        for (UUID orgUuid : deletions) {
            Organisation organisation = OrganisationHelper.getById(orgUuid);
            Map<UUID, String> map = organisation.getServicesMap();
            map.remove(serviceUuid);
            organisation.setServicesMap(map);

            entityManager.persist(organisation);
        }

        // Process added orgs
        for (UUID orgUuid : additions) {
            Organisation organisation = OrganisationHelper.getById(orgUuid);
            Map<UUID, String> map = organisation.getServicesMap();
            map.put(serviceUuid, service.getName());
            organisation.setServicesMap(map);

            entityManager.persist(organisation);
        }

        entityManager.getTransaction().commit();
        entityManager.close();

        return serviceUuid;
    }

    public static Service getById(UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " Service c"
                + " where c.id = :id";

        Query query = entityManager.createQuery(sql, Service.class)
                .setParameter("id", id.toString());

        Service ret = null;
        try {
            ret = (Service)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }


    public static void delete(Service service) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        entityManager.remove(service);
        entityManager.close();
    }

    public static List<Service> getAll() throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " Service c";

        Query query = entityManager.createQuery(sql, Service.class);

        List<Service> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<Service> search(String searchData) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " Service c"
                + " where c.name like :id_like"
                + " or c.localId = :id_like";

        Query query = entityManager.createQuery(sql, Service.class)
                .setParameter("id_like", searchData + "%");

        List<Service> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static Service getByLocalIdentifier(String localIdentifier) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " Service c"
                + " where c.localId = :id";

        Query query = entityManager.createQuery(sql, Service.class)
                .setParameter("id", localIdentifier);

        Service ret = null;
        try {
            ret = (Service)query.getSingleResult();

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
