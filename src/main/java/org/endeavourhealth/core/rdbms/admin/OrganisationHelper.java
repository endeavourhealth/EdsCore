package org.endeavourhealth.core.rdbms.admin;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.mapping.Mapper;
import org.endeavourhealth.core.rdbms.ConnectionManager;
import org.endeavourhealth.core.rdbms.admin.models.Organisation;
import org.endeavourhealth.core.rdbms.admin.models.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;

public class OrganisationHelper {

    public static UUID save(Organisation organisation) throws Exception {

        Set<UUID> additions = null;
        Set<UUID> deletions = null;

        UUID orgUuid = null;

        if (organisation.getId() == null) {
            // New organisation, just save with all services as additions
            orgUuid = UUID.randomUUID();
            organisation.setId(orgUuid.toString());

            additions = new HashSet<>(organisation.getServicesMap().keySet());
            deletions = new HashSet<>();
        } else {

            // Existing organisation, update service links
            orgUuid = UUID.fromString(organisation.getId());

            Map<UUID, String> newMap = organisation.getServicesMap();

            Organisation oldOrganisation = getById(orgUuid);
            Map<UUID, String> oldMap = oldOrganisation.getServicesMap();

            additions = new TreeSet<>(newMap.keySet());
            additions.removeAll(oldMap.keySet());

            deletions = new TreeSet<>(oldMap.keySet());
            deletions.removeAll(newMap.keySet());
        }

        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(organisation);

        // Process removed services
        for (UUID serviceUuid : deletions) {
            Service service = ServiceHelper.getById(serviceUuid);
            Map<UUID, String> map = service.getOrganisationsMap();
            map.remove(orgUuid);
            service.setOrganisationsMap(map);

            entityManager.persist(service);
        }

        // Process added services
        for (UUID serviceUuid : additions) {
            Service service = ServiceHelper.getById(serviceUuid);
            Map<UUID, String> map = service.getOrganisationsMap();
            map.put(orgUuid, organisation.getName());
            service.setOrganisationsMap(map);

            entityManager.persist(service);
        }

        entityManager.getTransaction().commit();
        entityManager.close();

        return orgUuid;
    }



    public static Organisation getById(UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " Organisation c"
                + " where c.id = :id";

        Query query = entityManager.createQuery(sql, Organisation.class)
                .setParameter("id", id.toString());

        Organisation ret = null;
        try {
            ret = (Organisation)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    public static Organisation getByNationalId(String nationalId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " Organisation c"
                + " where c.nationalId = :id";

        Query query = entityManager.createQuery(sql, Organisation.class)
                .setParameter("id", nationalId);

        Organisation ret = null;
        try {
            ret = (Organisation)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    public static void delete(Organisation organisation) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        entityManager.remove(organisation);
        entityManager.close();
    }

    public static List<Organisation> getAll() throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " Organisation c";

        Query query = entityManager.createQuery(sql, Organisation.class);

        List<Organisation> ret = query.getResultList();

        entityManager.close();

        return ret;
    }


    public static List<Organisation> search(String searchData) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " Organisation c"
                + " where c.name like :id_like"
                + " or c.nationalId = :id_like";

        Query query = entityManager.createQuery(sql, Organisation.class)
                .setParameter("id_like", searchData + "%");

        List<Organisation> ret = query.getResultList();

        entityManager.close();

        return ret;
    }
}
