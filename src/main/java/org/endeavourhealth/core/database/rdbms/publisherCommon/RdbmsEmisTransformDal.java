package org.endeavourhealth.core.database.rdbms.publisherCommon;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherCommon.EmisTransformDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisAdminResourceCache;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisCsvCodeMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsEmisAdminResourceCache;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsEmisCsvCodeMap;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RdbmsEmisTransformDal implements EmisTransformDalI {

    public void save(EmisCsvCodeMap mapping) throws Exception
    {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsEmisCsvCodeMap emisMapping = new RdbmsEmisCsvCodeMap(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
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
                    + " (medication, code_id, code_type, codeable_concept, read_term, read_code, snomed_concept_id, snomed_description_id, snomed_term, national_code, national_code_category, national_code_description, parent_code_id, audit_json)"
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
                    + " parent_code_id = VALUES(parent_code_id),"
                    + " audit_json = VALUES(audit_json);";

            ps = connection.prepareStatement(sql);

            ps.setBoolean(1, emisMapping.isMedication());
            ps.setLong(2, emisMapping.getCodeId());
            if (Strings.isNullOrEmpty(emisMapping.getCodeType())) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, emisMapping.getCodeType());
            }
            if (Strings.isNullOrEmpty(emisMapping.getCodeableConcept())) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, emisMapping.getCodeableConcept());
            }
            if (Strings.isNullOrEmpty(emisMapping.getReadTerm())) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, emisMapping.getReadTerm());
            }
            if (Strings.isNullOrEmpty(emisMapping.getReadCode())) {
                ps.setNull(6, Types.VARCHAR);
            } else {
                ps.setString(6, emisMapping.getReadCode());
            }
            if (emisMapping.getSnomedConceptId() == null) {
                ps.setNull(7, Types.BIGINT);
            } else {
                ps.setLong(7, emisMapping.getSnomedConceptId());
            }
            if (emisMapping.getSnomedDescriptionId() == null) {
                ps.setNull(8, Types.BIGINT);
            } else {
                ps.setLong(8, emisMapping.getSnomedDescriptionId());
            }
            if (Strings.isNullOrEmpty(emisMapping.getSnomedTerm())) {
                ps.setNull(9, Types.VARCHAR);
            } else {
                ps.setString(9, emisMapping.getSnomedTerm());
            }
            if (Strings.isNullOrEmpty(emisMapping.getNationalCode())) {
                ps.setNull(10, Types.VARCHAR);
            } else {
                ps.setString(10, emisMapping.getNationalCode());
            }
            if (Strings.isNullOrEmpty(emisMapping.getNationalCodeCategory())) {
                ps.setNull(11, Types.VARCHAR);
            } else {
                ps.setString(11, emisMapping.getNationalCodeCategory());
            }
            if (Strings.isNullOrEmpty(emisMapping.getNationalCodeDescription())) {
                ps.setNull(12, Types.VARCHAR);
            } else {
                ps.setString(12, emisMapping.getNationalCodeDescription());
            }
            if (emisMapping.getParentCodeId() == null) {
                ps.setNull(13, Types.BIGINT);
            } else {
                ps.setLong(13, emisMapping.getParentCodeId());
            }
            if (emisMapping.getAuditJson() == null) {
                ps.setNull(14, Types.VARCHAR);
            } else {
                ps.setString(14, emisMapping.getAuditJson());
            }

            ps.executeUpdate();

            //transaction.commit();
            entityManager.getTransaction().commit();

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

    public EmisCsvCodeMap getMostRecentCode(String dataSharingAgreementGuid, boolean medication, Long codeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsEmisCsvCodeMap c"
                    + " where c.medication = :medication"
                    + " and c.codeId = :code_id";

            Query query = entityManager.createQuery(sql, RdbmsEmisCsvCodeMap.class)
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

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {

            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO emis_admin_resource_cache"
                    + " (data_sharing_agreement_guid, emis_guid, resource_type, resource_data, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " resource_data = VALUES(resource_data),"
                    + " audit_json = VALUES(audit_json);";

            ps = connection.prepareStatement(sql);

            ps.setString(1, emisObj.getDataSharingAgreementGuid());
            ps.setString(2, emisObj.getEmisGuid());
            ps.setString(3, emisObj.getResourceType());
            ps.setString(4, emisObj.getResourceData());
            ps.setString(5, emisObj.getAuditJson());
            //entityManager.persist(emisObj);

            ps.executeUpdate();

            entityManager.getTransaction().commit();

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

    public void delete(EmisAdminResourceCache resourceCache) throws Exception {
        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache is null");
        }

        RdbmsEmisAdminResourceCache emisObj = new RdbmsEmisAdminResourceCache(resourceCache);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
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

    public List<EmisAdminResourceCache> getCachedResources(String dataSharingAgreementGuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsEmisAdminResourceCache c"
                    + " where c.dataSharingAgreementGuid = :data_sharing_agreement_guid";

            Query query = entityManager.createQuery(sql, RdbmsEmisAdminResourceCache.class)
                    .setParameter("data_sharing_agreement_guid", dataSharingAgreementGuid);

            List<RdbmsEmisAdminResourceCache> results = query.getResultList();

            List<EmisAdminResourceCache> ret = new ArrayList<>();
            for (RdbmsEmisAdminResourceCache result: results) {
                ret.add(new EmisAdminResourceCache(result));
            }

            return ret;

        } finally {
            entityManager.close();
        }
    }
}
