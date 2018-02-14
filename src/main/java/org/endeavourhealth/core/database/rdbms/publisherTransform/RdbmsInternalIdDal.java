package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.InternalIdDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.ResourceMergeDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.InternalIdMap;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceMergeMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsInternalIdMap;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsResourceMergeMap;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RdbmsInternalIdDal implements InternalIdDalI {
    public static final String IDTYPE_ALTKEY_LOCATION = "ALTKEY-LOCATION";
    public static final String IDTYPE_MRN_MILLENNIUM_PERS_ID = "MRNtoMILLPERSID";
    public static final String IDTYPE_ENCOUNTER_ID_VISIT_ID = "ENCOUNTERIDtoVISITID";
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsInternalIdDal.class);

    @Override
    public String getDestinationId(UUID serviceId, String idType, String sourceId) throws Exception {
        //LOG.trace("readMergeRecordDB:" + idType + " " + sourceId);
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsInternalIdMap c"
                    + " where c.serviceId = :service_id"
                    + " and c.idType = :id_type"
                    + " and c.sourceId = :source_id";

            Query query = entityManager.createQuery(sql, RdbmsInternalIdMap.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("id_type", idType)
                    .setParameter("source_id", sourceId)
                    .setMaxResults(1);

            try {
                return ((RdbmsInternalIdMap) query.getSingleResult()).getDestinationId();
            }
            catch (NoResultException e) {
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public List<InternalIdMap> getSourceId(UUID serviceId, String idType, String destinationId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsInternalIdMap c"
                    + " where c.serviceId = :service_id"
                    + " and c.idType = :id_type"
                    + " and c.destinationId = :destination_id";

            Query query = entityManager.createQuery(sql, RdbmsInternalIdMap.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("id_type", idType)
                    .setParameter("destination_id", destinationId);

            List<RdbmsInternalIdMap> results = query.getResultList();
            return results
                    .stream()
                    .map(T -> new InternalIdMap((T)))
                    .collect(Collectors.toList());

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void upsertRecord(UUID serviceId, String idType, String sourceId, String destinationId) throws Exception {
        //LOG.trace("insertMergeRecord:" + idType + " " + sourceId + " " + destinationId);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

        PreparedStatement ps = null;
        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO internal_id_map"
                    + " (service_id, id_type, source_id, destination_id, updated_at)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " destination_id = VALUES(destination_id),"
                    + " updated_at = VALUES(updated_at);";

            ps = connection.prepareStatement(sql);

            ps.setString(1, serviceId.toString());
            ps.setString(2, idType);
            ps.setString(3, sourceId);
            ps.setString(4, destinationId);
            ps.setTimestamp(5, new java.sql.Timestamp(new Date().getTime()));

            ps.executeUpdate();

            entityManager.getTransaction().commit();
            //LOG.trace("Saved mergeRecord for " + resourceType + " " + resourceFrom.toString() + "==>" + resourceTo.toString());

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public void insertRecord(UUID serviceId, String idType, String sourceId, String destinationId) throws Exception {
        //LOG.trace("insertMergeRecord:" + idType + " " + sourceId + " " + destinationId);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

        PreparedStatement ps = null;
        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO internal_id_map"
                    + " (service_id, id_type, source_id, destination_id, updated_at)"
                    + " VALUES (?, ?, ?, ?, ?);";

            ps = connection.prepareStatement(sql);

            ps.setString(1, serviceId.toString());
            ps.setString(2, idType);
            ps.setString(3, sourceId);
            ps.setString(4, destinationId);
            ps.setTimestamp(5, new java.sql.Timestamp(new Date().getTime()));

            ps.executeUpdate();

            entityManager.getTransaction().commit();
            //LOG.trace("Saved mergeRecord for " + resourceType + " " + resourceFrom.toString() + "==>" + resourceTo.toString());

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

}
