package org.endeavourhealth.core.database.rdbms.admin;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.admin.OrganisationDalI;
import org.endeavourhealth.core.database.dal.admin.models.Organisation;
import org.endeavourhealth.core.database.dal.admin.models.Service;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsOrganisation;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsService;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.*;

public class RdbmsOrganisationDal implements OrganisationDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsOrganisationDal.class);

    public UUID save(Organisation organisation) throws Exception {

        //first work out any changes that need to be made to services
        Set<UUID> additions = null;
        Set<UUID> deletions = null;

        UUID orgUuid = null;

        if (organisation.getId() == null) {

            //it can't be done using unique indexes, since the UPSERT SQL used during the save would result in
            //overwrites, but manually validate that the national ID is unique if trying to save a new service
            String odsCode = organisation.getNationalId();
            if (!Strings.isNullOrEmpty(odsCode)) {
                Organisation existingOrg = getByNationalId(odsCode);
                if (existingOrg != null) {
                    throw new Exception("Organisation already exists with ID " + odsCode);
                }
            }

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
        PreparedStatement psOrganisation = null;
        PreparedStatement psService = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statements to avoid having to do a retrieve before every update
            //entityManager.persist(dbService);
            //entityManager.persist(dbOrganisation);

            psOrganisation = createSaveOrganisationPreparedStatement(entityManager);
            addToSaveOrganisationPreparedStatement(psOrganisation, dbOrganisation);

            RdbmsServiceDal serviceDal = new RdbmsServiceDal();
            psService = RdbmsServiceDal.createSaveServicePreparedStatement(entityManager);

            // Process removed services
            for (UUID serviceUuid : deletions) {
                Service service = serviceDal.getById(serviceUuid);
                Map<UUID, String> map = service.getOrganisations();
                map.remove(orgUuid);

                RdbmsService dbService = new RdbmsService(service);
                RdbmsServiceDal.addToSaveServicePreparedStatement(psService, dbService);
            }

            // Process added services
            for (UUID serviceUuid : additions) {
                Service service = serviceDal.getById(serviceUuid);
                Map<UUID, String> map = service.getOrganisations();
                map.put(orgUuid, organisation.getName());

                RdbmsService dbService = new RdbmsService(service);
                RdbmsServiceDal.addToSaveServicePreparedStatement(psService, dbService);
            }

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (psOrganisation != null) {
                psOrganisation.close();
            }
            if (psService != null) {
                psService.close();
            }
            entityManager.close();
        }

        return orgUuid;
    }


    public static PreparedStatement createSaveOrganisationPreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "INSERT INTO organisation"
                + " (id, name, national_id, services)"
                + " VALUES (?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " name = VALUES(name),"
                + " national_id = VALUES(national_id),"
                + " services = VALUES(services)";

        return connection.prepareStatement(sql);
    }

    public static void addToSaveOrganisationPreparedStatement(PreparedStatement ps, RdbmsOrganisation organisation) throws Exception {

        ps.setString(1, organisation.getId());
        ps.setString(2, organisation.getName());
        if (!Strings.isNullOrEmpty(organisation.getNationalId())) {
            ps.setString(3, organisation.getNationalId());
        } else {
            ps.setNull(3, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(organisation.getServices())) {
            ps.setString(4, organisation.getServices());
        } else {
            ps.setNull(4, Types.VARCHAR);
        }

        ps.executeUpdate();
    }


    private static PreparedStatement createDeleteServicePreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "DELETE FROM organisation"
                + " WHERE id = ?";;

        return connection.prepareStatement(sql);
    }

    private static void addToDeleteServicePreparedStatement(PreparedStatement ps, RdbmsOrganisation organisation) throws Exception {
        ps.setString(1, organisation.getId());

        ps.executeUpdate();
    }


    public Organisation getById(UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsOrganisation c"
                    + " where c.id = :id";

            Query query = entityManager.createQuery(sql, RdbmsOrganisation.class)
                    .setParameter("id", id.toString());


            RdbmsOrganisation result = (RdbmsOrganisation)query.getSingleResult();
            return new Organisation(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public Organisation getByNationalId(String nationalId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsOrganisation c"
                    + " where c.nationalId = :id";

            Query query = entityManager.createQuery(sql, RdbmsOrganisation.class)
                    .setParameter("id", nationalId);

            RdbmsOrganisation result = (RdbmsOrganisation)query.getSingleResult();
            return new Organisation(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public void delete(Organisation organisation) throws Exception {

        RdbmsOrganisation dbOrganisation = new RdbmsOrganisation(organisation);

        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        PreparedStatement psOrganisation = null;
        PreparedStatement psService = null;

        try {
            entityManager.getTransaction().begin();

            psOrganisation = createDeleteServicePreparedStatement(entityManager);
            addToDeleteServicePreparedStatement(psOrganisation, dbOrganisation);
            //entityManager.remove(dbOrganisation);

            //also need to update any linked services
            RdbmsServiceDal serviceDalDal = new RdbmsServiceDal();
            psService = RdbmsServiceDal.createSaveServicePreparedStatement(entityManager);

            for (UUID serviceUuid : organisation.getServices().keySet()) {
                Service service = serviceDalDal.getById(serviceUuid);
                Map<UUID, String> map = service.getOrganisations();
                map.remove(organisation.getId());

                RdbmsService dbService = new RdbmsService(service);
                RdbmsServiceDal.addToSaveServicePreparedStatement(psService, dbService);
            }

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (psOrganisation != null) {
                psOrganisation.close();
            }
            if (psService != null) {
                psService.close();
            }
            entityManager.close();
        }

    }

    public List<Organisation> getAll() throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsOrganisation c";

            Query query = entityManager.createQuery(sql, RdbmsOrganisation.class);

            List<RdbmsOrganisation> results = query.getResultList();

            //can't use stream as the constructor throws an exception
            List<Organisation> ret = new ArrayList<>();
            for (RdbmsOrganisation result : results) {
                ret.add(new Organisation(result));
            }

            return ret;

        } finally {
            entityManager.close();
        }
    }


    public List<Organisation> search(String searchData) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsOrganisation c"
                    + " where c.name like :param_1"
                    + " or c.nationalId = :param_2";

            Query query = entityManager.createQuery(sql, RdbmsOrganisation.class)
                    .setParameter("param_1", searchData + "%")
                    .setParameter("param_2", searchData);

            List<RdbmsOrganisation> results = query.getResultList();
            //LOG.debug("Searching for [" + searchData + "] and got " + results.size() + " results");
            //LOG.debug("Query = " + query);

            //can't use stream as the constructor throws an exception
            List<Organisation> ret = new ArrayList<>();
            for (RdbmsOrganisation result : results) {
                ret.add(new Organisation(result));
            }

            return ret;

        } finally {
            entityManager.close();
        }
    }
}
