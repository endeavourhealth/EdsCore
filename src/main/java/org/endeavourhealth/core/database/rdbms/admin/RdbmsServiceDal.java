package org.endeavourhealth.core.database.rdbms.admin;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.admin.ServiceDalI;
import org.endeavourhealth.core.database.dal.admin.models.Organisation;
import org.endeavourhealth.core.database.dal.admin.models.Service;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsOrganisation;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsService;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
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
        PreparedStatement psService = null;
        PreparedStatement psOrganisation = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statements to avoid having to do a retrieve before every update
            //entityManager.persist(dbService);

            psService = createSaveServicePreparedStatement(entityManager);
            addToSaveServicePreparedStatement(psService, dbService);

            RdbmsOrganisationDal organisationDal = new RdbmsOrganisationDal();
            psOrganisation = RdbmsOrganisationDal.createSaveOrganisationPreparedStatement(entityManager);

            // Process removed orgs
            for (UUID orgUuid : deletions) {
                Organisation organisation = organisationDal.getById(orgUuid);
                Map<UUID, String> map = organisation.getServices();
                map.remove(serviceUuid);

                RdbmsOrganisation dbOrganisation = new RdbmsOrganisation(organisation);
                RdbmsOrganisationDal.addToSaveOrganisationPreparedStatement(psOrganisation, dbOrganisation);
            }

            // Process added orgs
            for (UUID orgUuid : additions) {
                Organisation organisation = organisationDal.getById(orgUuid);
                Map<UUID, String> map = organisation.getServices();
                map.put(serviceUuid, service.getName());

                RdbmsOrganisation dbOrganisation = new RdbmsOrganisation(organisation);
                RdbmsOrganisationDal.addToSaveOrganisationPreparedStatement(psOrganisation, dbOrganisation);
            }

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
            if (psService != null) {
                psService.close();
            }
            if (psOrganisation != null) {
                psOrganisation.close();
            }
        }

        return serviceUuid;
    }

    public static PreparedStatement createSaveServicePreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "INSERT INTO service"
                + " (id, name, local_id, endpoints, organisations)"
                + " VALUES (?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " name = VALUES(name),"
                + " local_id = VALUES(local_id),"
                + " endpoints = VALUES(endpoints),"
                + " organisations = VALUES(organisations);";

        return connection.prepareStatement(sql);
    }

    public static void addToSaveServicePreparedStatement(PreparedStatement ps, RdbmsService service) throws Exception {

        ps.setString(1, service.getId());
        ps.setString(2, service.getName());
        if (!Strings.isNullOrEmpty(service.getLocalId())) {
            ps.setString(3, service.getLocalId());
        } else {
            ps.setNull(3, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(service.getEndpoints())) {
            ps.setString(4, service.getEndpoints());
        } else {
            ps.setNull(4, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(service.getOrganisations())) {
            ps.setString(5, service.getOrganisations());
        } else {
            ps.setNull(5, Types.VARCHAR);
        }

        ps.executeUpdate();
    }

    private static PreparedStatement createDeleteServicePreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "DELETE FROM service"
                + " WHERE id = ?";;

        return connection.prepareStatement(sql);
    }

    private static void addToDeleteServicePreparedStatement(PreparedStatement ps, RdbmsService service) throws Exception {
        ps.setString(1, service.getId());

        ps.executeUpdate();
    }

    public Service getById(UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsService c"
                    + " where c.id = :id";

            Query query = entityManager.createQuery(sql, RdbmsService.class)
                    .setParameter("id", id.toString());

            RdbmsService result = (RdbmsService)query.getSingleResult();
            return new Service(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }


    public void delete(Service service) throws Exception {

        RdbmsService dbService = new RdbmsService(service);

        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        PreparedStatement psService = null;
        PreparedStatement psOrganisation = null;

        try {
            entityManager.getTransaction().begin();

            psService = createDeleteServicePreparedStatement(entityManager);
            addToDeleteServicePreparedStatement(psService, dbService);
            //entityManager.remove(dbService);

            //also need to update any linked organisations
            RdbmsOrganisationDal organisationDal = new RdbmsOrganisationDal();
            psOrganisation = RdbmsOrganisationDal.createSaveOrganisationPreparedStatement(entityManager);

            for (UUID orgUuid : service.getOrganisations().keySet()) {
                Organisation organisation = organisationDal.getById(orgUuid);
                if (organisation != null) {
                    Map<UUID, String> map = organisation.getServices();
                    map.remove(service.getId());

                    RdbmsOrganisation dbOrganisation = new RdbmsOrganisation(organisation);
                    RdbmsOrganisationDal.addToSaveOrganisationPreparedStatement(psOrganisation, dbOrganisation);
                }
            }

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
            if (psService != null) {
                psService.close();
            }
            if (psOrganisation != null) {
                psOrganisation.close();
            }
        }

    }

    public List<Service> getAll() throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsService c";

            Query query = entityManager.createQuery(sql, RdbmsService.class);

            List<RdbmsService> results = query.getResultList();

            //can't use stream as the constructor throws an exception
            List<Service> ret = new ArrayList<>();
            for (RdbmsService result : results) {
                ret.add(new Service(result));
            }
            return ret;

        } finally {
            entityManager.close();
        }
    }

    public List<Service> search(String searchData) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsService c"
                    + " where c.name like :id_like"
                    + " or c.localId = :id_like";

            Query query = entityManager.createQuery(sql, RdbmsService.class)
                    .setParameter("id_like", searchData + "%");

            List<RdbmsService> results = query.getResultList();

            //can't use stream as the constructor throws an exception
            List<Service> ret = new ArrayList<>();
            for (RdbmsService result : results) {
                ret.add(new Service(result));
            }
            return ret;

        } finally {
            entityManager.close();
        }
    }

    public Service getByLocalIdentifier(String localIdentifier) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsService c"
                    + " where c.localId = :id";

            Query query = entityManager.createQuery(sql, RdbmsService.class)
                    .setParameter("id", localIdentifier);

            RdbmsService result = (RdbmsService)query.getSingleResult();
            return new Service(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

}