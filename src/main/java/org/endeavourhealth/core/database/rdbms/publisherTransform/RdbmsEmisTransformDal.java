package org.endeavourhealth.core.database.rdbms.publisherTransform;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherTransform.EmisTransformDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.EmisAdminResourceCache;
import org.endeavourhealth.core.database.dal.publisherTransform.models.EmisCsvCodeMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsEmisAdminResourceCache;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsEmisCsvCodeMap;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

public class RdbmsEmisTransformDal implements EmisTransformDalI {

    public void save(EmisCsvCodeMap mapping) throws Exception
    {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsEmisCsvCodeMap emisMapping = new RdbmsEmisCsvCodeMap(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        PreparedStatement ps = null;

        try {
            //EntityTransaction transaction = entityManager.getTransaction();
            //transaction.begin();
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO emis_csv_code_map"
                    + " (data_sharing_agreement_guid, medication, code_id, code_type, codeable_concept, read_term, read_code, snomed_concept_id, snomed_description_id, snomed_term, national_code, national_code_category, national_code_description, parent_code_id)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " code_type = VALUES(code_type),"
                    + " codeable_concept = VALUES(codeable_concept),"
                    + " read_term = VALUES(read_term),"
                    + " read_code = VALUES(read_code),"
                    + " snomed_concept_id = VALUES(snomed_concept_id),"
                    + " snomed_description_id = VALUES(snomed_description_id),"
                    + " snomed_term = VALUES(snomed_term),"
                    + " national_code = VALUES(national_code),"
                    + " national_code_category = VALUES(national_code_category),"
                    + " national_code_description = VALUES(national_code_description),"
                    + " parent_code_id = VALUES(parent_code_id);";

            ps = connection.prepareStatement(sql);

            ps.setString(1, emisMapping.getDataSharingAgreementGuid());
            ps.setBoolean(2, emisMapping.isMedication());
            ps.setLong(3, emisMapping.getCodeId());
            if (Strings.isNullOrEmpty(emisMapping.getCodeType())) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, emisMapping.getCodeType());
            }
            if (Strings.isNullOrEmpty(emisMapping.getCodeableConcept())) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, emisMapping.getCodeableConcept());
            }
            if (Strings.isNullOrEmpty(emisMapping.getReadTerm())) {
                ps.setNull(6, Types.VARCHAR);
            } else {
                ps.setString(6, emisMapping.getReadTerm());
            }
            if (Strings.isNullOrEmpty(emisMapping.getReadCode())) {
                ps.setNull(7, Types.VARCHAR);
            } else {
                ps.setString(7, emisMapping.getReadCode());
            }
            if (emisMapping.getSnomedConceptId() == null) {
                ps.setNull(8, Types.BIGINT);
            } else {
                ps.setLong(8, emisMapping.getSnomedConceptId());
            }
            if (emisMapping.getSnomedDescriptionId() == null) {
                ps.setNull(9, Types.BIGINT);
            } else {
                ps.setLong(9, emisMapping.getSnomedDescriptionId());
            }
            if (Strings.isNullOrEmpty(emisMapping.getSnomedTerm())) {
                ps.setNull(10, Types.VARCHAR);
            } else {
                ps.setString(10, emisMapping.getSnomedTerm());
            }
            if (Strings.isNullOrEmpty(emisMapping.getNationalCode())) {
                ps.setNull(11, Types.VARCHAR);
            } else {
                ps.setString(11, emisMapping.getNationalCode());
            }
            if (Strings.isNullOrEmpty(emisMapping.getNationalCodeCategory())) {
                ps.setNull(12, Types.VARCHAR);
            } else {
                ps.setString(12, emisMapping.getNationalCodeCategory());
            }
            if (Strings.isNullOrEmpty(emisMapping.getNationalCodeDescription())) {
                ps.setNull(13, Types.VARCHAR);
            } else {
                ps.setString(13, emisMapping.getNationalCodeDescription());
            }
            if (emisMapping.getParentCodeId() == null) {
                ps.setNull(14, Types.BIGINT);
            } else {
                ps.setLong(14, emisMapping.getParentCodeId());
            }

            ps.executeUpdate();

            //transaction.commit();
            entityManager.getTransaction().commit();

        } finally {

            entityManager.close();
            if (ps != null) {
                ps.close();
            }
        }
    }

    public EmisCsvCodeMap getMostRecentCode(String dataSharingAgreementGuid, boolean medication, Long codeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsEmisCsvCodeMap c"
                    + " where c.dataSharingAgreementGuid = :data_sharing_agreement_guid"
                    + " and c.medication = :medication"
                    + " and c.codeId = :code_id";

            Query query = entityManager.createQuery(sql, RdbmsEmisCsvCodeMap.class)
                    .setParameter("data_sharing_agreement_guid", dataSharingAgreementGuid)
                    .setParameter("medication", new Boolean(medication))
                    .setParameter("code_id", codeId);

            RdbmsEmisCsvCodeMap emisMapping = (RdbmsEmisCsvCodeMap)query.getSingleResult();
            return new EmisCsvCodeMap(emisMapping);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public void save(EmisAdminResourceCache resourceCache) throws Exception {
        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache is null");
        }

        RdbmsEmisAdminResourceCache emisObj = new RdbmsEmisAdminResourceCache(resourceCache);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        PreparedStatement ps = null;

        try {

            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO emis_admin_resource_cache"
                    + " (data_sharing_agreement_guid, emis_guid, resource_type, resource_data)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " resource_data = VALUES(resource_data);";

            ps = connection.prepareStatement(sql);

            ps.setString(1, emisObj.getDataSharingAgreementGuid());
            ps.setString(2, emisObj.getEmisGuid());
            ps.setString(3, emisObj.getResourceType());
            ps.setString(4, emisObj.getResourceData());
            //entityManager.persist(emisObj);

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
            if (ps != null) {
                ps.close();
            }

        }
    }

    public void delete(EmisAdminResourceCache resourceCache) throws Exception {
        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache is null");
        }

        RdbmsEmisAdminResourceCache emisObj = new RdbmsEmisAdminResourceCache(resourceCache);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.remove(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "DELETE FROM emis_admin_resource_cache"
                    + " WHERE data_sharing_agreement_guid = ?"
                    + " AND emis_guid = ?"
                    + " AND resource_type = ?;";

            ps = connection.prepareStatement(sql);

            ps.setString(1, emisObj.getDataSharingAgreementGuid());
            ps.setString(2, emisObj.getEmisGuid());
            ps.setString(3, emisObj.getResourceType());

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
            if (ps != null) {
                ps.close();
            }
        }
    }

    public List<EmisAdminResourceCache> getCachedResources(String dataSharingAgreementGuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsEmisAdminResourceCache c"
                    + " where c.dataSharingAgreementGuid = :data_sharing_agreement_guid";

            Query query = entityManager.createQuery(sql, RdbmsEmisAdminResourceCache.class)
                    .setParameter("data_sharing_agreement_guid", dataSharingAgreementGuid);

            List<RdbmsEmisAdminResourceCache> results = query.getResultList();

            List<EmisAdminResourceCache> ret = results
                    .stream()
                    .map(T -> new EmisAdminResourceCache(T))
                    .collect(Collectors.toList());

            return ret;

        } finally {
            entityManager.close();
        }
    }
}
