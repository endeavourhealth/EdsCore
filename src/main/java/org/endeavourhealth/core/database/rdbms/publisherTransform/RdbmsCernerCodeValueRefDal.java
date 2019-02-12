package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.CernerCodeValueRefDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerClinicalEventMappingState;
import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerCodeValueRef;
import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerNomenclatureRef;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsCernerCodeValueRef;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsCernerNomenclatureRef;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RdbmsCernerCodeValueRefDal implements CernerCodeValueRefDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsCernerCodeValueRefDal.class);

    @Override
    public CernerCodeValueRef getCodeFromCodeSet(Long codeSet, String code, UUID serviceId) throws Exception {
        //LOG.trace("readCernerCodeRefDB:" + codeSet + " " + code);
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsCernerCodeValueRef c"
                    + " where c.serviceId = :service_id"
                    + " and c.codeSetNbr = :codeSet"
                    + " and c.codeValueCd = :code";
                    //+ " and c.activeInd = 1"; //don't restrict on this when retrieving

            Query query = entityManager.createQuery(sql, RdbmsCernerCodeValueRef.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("codeSet", codeSet)
                    .setParameter("code", code)
                    .setMaxResults(1);

            try {
                RdbmsCernerCodeValueRef result = (RdbmsCernerCodeValueRef)query.getSingleResult();
                return new CernerCodeValueRef(result);
            }
            catch (NoResultException e) {
                //don't assume that a lack of a result is a bad thing - let the caller decide
                //LOG.error("No code found for codeSet " + codeSet + ", code " + code + ", service " + serviceId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public CernerCodeValueRef getCodeWithoutCodeSet(String code, UUID serviceId) throws Exception {
        //LOG.trace("readCernerCodeRefDB:" + codeSet + " " + code);
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsCernerCodeValueRef c"
                    + " where c.serviceId = :service_id"
                    + " and c.codeValueCd = :code";
            //+ " and c.activeInd = 1"; //don't restrict on this when retrieving

            Query query = entityManager.createQuery(sql, RdbmsCernerCodeValueRef.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("code", code)
                    .setMaxResults(1);

            try {
                RdbmsCernerCodeValueRef result = (RdbmsCernerCodeValueRef)query.getSingleResult();
                return new CernerCodeValueRef(result);
            }
            catch (NoResultException e) {
                LOG.error("No code found for code " + code + ", service " + serviceId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public CernerCodeValueRef getCodeFromMultipleCodeSets(String code, UUID serviceId, Long... codeSets) throws Exception {
        //LOG.trace("readCernerCodeRefDB:" + codeSet + " " + code);
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

        List<Long> codeSetList = new ArrayList<>();
        String codeSetsStrings = codeSetList.stream().map(Object::toString)
            .collect(Collectors.joining(", "));
        codeSetList = Arrays.asList(codeSets);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsCernerCodeValueRef c"
                    + " where c.serviceId = :service_id"
                    + " and c.codeSetNbr in :codeSets"
                    + " and c.codeValueCd = :code";
            //+ " and c.activeInd = 1"; //don't restrict on this when retrieving

            Query query = entityManager.createQuery(sql, RdbmsCernerCodeValueRef.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("codeSets", codeSetList)
                    .setParameter("code", code)
                    .setMaxResults(1);

            try {
                RdbmsCernerCodeValueRef result = (RdbmsCernerCodeValueRef)query.getSingleResult();
                return new CernerCodeValueRef(result);
            }
            catch (NoResultException e) {
                LOG.error("No code found for codeSets " + codeSetsStrings + ", code " + code + ", service " + serviceId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public List<CernerCodeValueRef> getCodesForCodeSet(UUID serviceId, Long codeSet) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsCernerCodeValueRef c"
                    + " where c.serviceId = :service_id"
                    + " and c.codeSetNbr = :code_set";

            Query query = entityManager.createQuery(sql, RdbmsCernerCodeValueRef.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("code_set", codeSet);

            List<RdbmsCernerCodeValueRef> results = query.getResultList();

            List<CernerCodeValueRef> ret = new ArrayList<>();
            for (RdbmsCernerCodeValueRef result: results) {
                ret.add(new CernerCodeValueRef(result));
            }
            return ret;

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void save(CernerCodeValueRef mapping, UUID serviceId) throws Exception {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsCernerCodeValueRef cernerMapping = new RdbmsCernerCodeValueRef(mapping);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            //EntityTransaction transaction = entityManager.getTransaction();
            //transaction.begin();
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            // Key is code_value_cd, code_set_nbr, service_id
            String sql = "INSERT INTO cerner_code_value_ref "
                    + " (code_value_cd, date, active_ind, code_desc_txt,code_disp_txt,code_meaning_txt,code_set_nbr,code_set_desc_txt,alias_nhs_cd_alias,service_id,audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " code_value_cd = VALUES(code_value_cd), "
                    + " date = VALUES(date),"
                    + " active_ind = VALUES(active_ind),"
                    + " code_desc_txt = VALUES(code_desc_txt),"
                    + "code_disp_txt = VALUES(code_disp_txt),"
                    + "code_meaning_txt = VALUES(code_meaning_txt),"
                    + "code_set_nbr = VALUES (code_set_nbr),"
                    + "code_set_desc_txt = VALUES(code_set_desc_txt),"
                    + "alias_nhs_cd_alias = VALUES(alias_nhs_cd_alias),"
                    + "service_id = VALUES(service_id),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);
            // Only JSON audit field is nullable
            ps.setString(1, cernerMapping.getCodeValueCd());
            ps.setDate(2, new java.sql.Date(cernerMapping.getDate().getTime()));
            ps.setByte(3, cernerMapping.getActiveInd());
            ps.setString(4,cernerMapping.getCodeDescTxt());
            ps.setString(5, cernerMapping.getCodeDispTxt());
            ps.setString(6, cernerMapping.getCodeMeaningTxt());
            ps.setLong(7,cernerMapping.getCodeSetNbr());
            ps.setString(8,cernerMapping.getCodeSetDescTxt());
            ps.setString(9,cernerMapping.getAliasNhsCdAlias());
            ps.setString(10,cernerMapping.getServiceId());
            if (cernerMapping.getAuditJson() == null) {
                ps.setNull(11, Types.VARCHAR);
            } else {
                ps.setString(11, cernerMapping.getAuditJson());
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


    @Override
    public CernerNomenclatureRef getNomenclatureRefForId(UUID serviceId, Long nomenclatureId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsCernerNomenclatureRef c"
                    + " where c.serviceId = :service_id"
                    + " and c.nomenclatureId = :nomenclature_id";

            Query query = entityManager.createQuery(sql, RdbmsCernerNomenclatureRef.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("nomenclature_id", nomenclatureId)
                    .setMaxResults(1);

            try {
                RdbmsCernerNomenclatureRef result = (RdbmsCernerNomenclatureRef)query.getSingleResult();
                return new CernerNomenclatureRef(result);
            }
            catch (NoResultException e) {
                LOG.error("No nomenclature ref service " + serviceId + " and ID " + nomenclatureId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public CernerNomenclatureRef getNomenclatureRefForValueText(UUID serviceId, String valueText) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsCernerNomenclatureRef c"
                    + " where c.serviceId = :service_id"
                    + " and c.value_text = :value_text";

            Query query = entityManager.createQuery(sql, RdbmsCernerNomenclatureRef.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("value_text", valueText)
                    .setMaxResults(1);

            try {
                RdbmsCernerNomenclatureRef result = (RdbmsCernerNomenclatureRef)query.getSingleResult();
                return new CernerNomenclatureRef(result);
            }
            catch (NoResultException e) {
                LOG.error("No nomenclature ref service " + serviceId + " and ValueText " + valueText);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }



    @Override
    public void saveNomenclatureRef(CernerNomenclatureRef nomenclatureRef) throws Exception {

        if (nomenclatureRef == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsCernerNomenclatureRef dbObj = new RdbmsCernerNomenclatureRef(nomenclatureRef);
        UUID serviceId = nomenclatureRef.getServiceId();

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            //primary key (service_id, nomenclature_id)
            String sql = "INSERT INTO cerner_nomenclature_ref "
                    + " (service_id, nomenclature_id, active, mneomonic_text, value_text, display_text, description_text, nomenclature_type_code, vocabulary_code, concept_identifier, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " active = VALUES(active), "
                    + " mneomonic_text = VALUES(mneomonic_text),"
                    + " value_text = VALUES(value_text),"
                    + " display_text = VALUES(display_text),"
                    + " description_text = VALUES(description_text),"
                    + " nomenclature_type_code = VALUES(nomenclature_type_code),"
                    + " vocabulary_code = VALUES (vocabulary_code),"
                    + " concept_identifier = VALUES(concept_identifier),"
                    + " audit_json = VALUES(audit_json)";

            int col = 1;

            ps = connection.prepareStatement(sql);

            ps.setString(col++, dbObj.getServiceId());
            ps.setLong(col++, dbObj.getNomenclatureId());
            ps.setBoolean(col++, dbObj.isActive());
            if (dbObj.getMnemonicText() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, dbObj.getMnemonicText());
            }
            if (dbObj.getValueText() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, dbObj.getValueText());
            }
            if (dbObj.getDisplayText() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, dbObj.getDisplayText());
            }
            if (dbObj.getDescriptionText() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, dbObj.getDescriptionText());
            }
            if (dbObj.getNomenclatureTypeCode() == null) {
                ps.setNull(col++, Types.BIGINT);
            } else {
                ps.setLong(col++, dbObj.getNomenclatureTypeCode());
            }
            if (dbObj.getVocabularyCode() == null) {
                ps.setNull(col++, Types.BIGINT);
            } else {
                ps.setLong(col++, dbObj.getVocabularyCode());
            }
            if (dbObj.getConceptIdentifier() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, dbObj.getConceptIdentifier());
            }
            if (dbObj.getAuditJson() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, dbObj.getAuditJson());
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

    public void deleteCleveMappingStateTable(CernerClinicalEventMappingState mapping) throws Exception {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        UUID serviceId = mapping.getServiceId();

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            //primary key (service_id, nomenclature_id)
            String sql = "DELETE FROM cerner_clinical_event_mapping_state "
                    + " WHERE service_id = ? and event_id = ?";

            int col = 1;

            ps = connection.prepareStatement(sql);

            ps.setString(col++, mapping.getServiceId().toString());
            ps.setLong(col++, mapping.getEventId().longValue());

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

    private UUID findServiceId(List<CernerClinicalEventMappingState> mappings) throws Exception {

        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalArgumentException("trying to save null or empty mappings");
        }

        UUID serviceId = null;
        for (CernerClinicalEventMappingState mapping: mappings) {
            if (serviceId == null) {
                serviceId = mapping.getServiceId();
            } else if (!serviceId.equals(mapping.getServiceId())) {
                throw new IllegalArgumentException("Can't save resources for different services");
            }
        }
        return serviceId;
    }

    @Override
    public void updateCleveMappingStateTable(List<CernerClinicalEventMappingState> mappings) throws Exception {

        UUID serviceId = findServiceId(mappings);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            //primary key (service_id, nomenclature_id)
            String sql = "INSERT INTO cerner_clinical_event_mapping_state "
                    + " (service_id, event_id, event_cd, event_cd_term, event_class_cd, event_class_cd_term, event_results_units_cd, event_results_units_cd_term, event_result_text, event_title_text, event_tag_text, mapped_snomed_concept_id, dt_mapping_updated)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " event_cd = VALUES(event_cd), "
                    + " event_cd_term = VALUES(event_cd_term),"
                    + " event_class_cd = VALUES(event_class_cd),"
                    + " event_class_cd_term = VALUES(event_class_cd_term),"
                    + " event_results_units_cd = VALUES(event_results_units_cd),"
                    + " event_results_units_cd_term = VALUES(event_results_units_cd_term),"
                    + " event_result_text = VALUES (event_result_text),"
                    + " event_title_text = VALUES(event_title_text),"
                    + " event_tag_text = VALUES(event_tag_text),"
                    + " mapped_snomed_concept_id = VALUES(mapped_snomed_concept_id),"
                    + " dt_mapping_updated = VALUES(dt_mapping_updated)";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (CernerClinicalEventMappingState mapping: mappings) {
                int col = 1;

                ps.setString(col++, mapping.getServiceId().toString());
                ps.setLong(col++, mapping.getEventId().longValue());
                if (mapping.getEventCd() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, mapping.getEventCd());
                }
                if (mapping.getEventCdTerm() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, mapping.getEventCdTerm());
                }
                if (mapping.getEventClassCd() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, mapping.getEventClassCd());
                }
                if (mapping.getEventClassCdTerm() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, mapping.getEventClassCdTerm());
                }
                if (mapping.getEventResultUnitsCd() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, mapping.getEventResultUnitsCd());
                }
                if (mapping.getEventResultUnitsCdTerm() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, mapping.getEventResultUnitsCdTerm());
                }
                if (mapping.getEventResultText() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, mapping.getEventResultText());
                }
                if (mapping.getEventTitleText() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, mapping.getEventTitleText());
                }
                if (mapping.getEventTagText() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, mapping.getEventTagText());
                }
                if (mapping.getMappedSnomedId() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, mapping.getMappedSnomedId());
                }
                //always set the date time column to NULL since this column is used to tell us when
                //we need to go back and update the FHIR
                ps.setNull(col++, Types.DATE);

                ps.addBatch();
            }

            ps.executeBatch();

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

    @Override
    public void deleteCleveMappingStateTable(List<CernerClinicalEventMappingState> mappings) throws Exception {

        UUID serviceId = findServiceId(mappings);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            //primary key (service_id, nomenclature_id)
            String sql = "DELETE FROM cerner_clinical_event_mapping_state "
                    + " WHERE service_id = ? and event_id = ?";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (CernerClinicalEventMappingState mapping: mappings) {
                int col = 1;

                ps.setString(col++, mapping.getServiceId().toString());
                ps.setLong(col++, mapping.getEventId().longValue());

                ps.addBatch();
            }

            ps.executeBatch();

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

    @Override
    public void updateCleveMappingStateTable(CernerClinicalEventMappingState mapping) throws Exception {

        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        UUID serviceId = mapping.getServiceId();

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            //primary key (service_id, nomenclature_id)
            String sql = "INSERT INTO cerner_clinical_event_mapping_state "
                    + " (service_id, event_id, event_cd, event_cd_term, event_class_cd, event_class_cd_term, event_results_units_cd, event_results_units_cd_term, event_result_text, event_title_text, event_tag_text, mapped_snomed_concept_id, dt_mapping_updated)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " event_cd = VALUES(event_cd), "
                    + " event_cd_term = VALUES(event_cd_term),"
                    + " event_class_cd = VALUES(event_class_cd),"
                    + " event_class_cd_term = VALUES(event_class_cd_term),"
                    + " event_results_units_cd = VALUES(event_results_units_cd),"
                    + " event_results_units_cd_term = VALUES(event_results_units_cd_term),"
                    + " event_result_text = VALUES (event_result_text),"
                    + " event_title_text = VALUES(event_title_text),"
                    + " event_tag_text = VALUES(event_tag_text),"
                    + " mapped_snomed_concept_id = VALUES(mapped_snomed_concept_id),"
                    + " dt_mapping_updated = VALUES(dt_mapping_updated)";

            int col = 1;

            ps = connection.prepareStatement(sql);

            ps.setString(col++, mapping.getServiceId().toString());
            ps.setLong(col++, mapping.getEventId().longValue());
            if (mapping.getEventCd() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, mapping.getEventCd());
            }
            if (mapping.getEventCdTerm() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, mapping.getEventCdTerm());
            }
            if (mapping.getEventClassCd() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, mapping.getEventClassCd());
            }
            if (mapping.getEventClassCdTerm() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, mapping.getEventClassCdTerm());
            }
            if (mapping.getEventResultUnitsCd() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, mapping.getEventResultUnitsCd());
            }
            if (mapping.getEventResultUnitsCdTerm() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, mapping.getEventResultUnitsCdTerm());
            }
            if (mapping.getEventResultText() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, mapping.getEventResultText());
            }
            if (mapping.getEventTitleText() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, mapping.getEventTitleText());
            }
            if (mapping.getEventTagText() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, mapping.getEventTagText());
            }
            if (mapping.getMappedSnomedId() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, mapping.getMappedSnomedId());
            }
            //always set the date time column to NULL since this column is used to tell us when
            //we need to go back and update the FHIR
            ps.setNull(col++, Types.DATE);

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

}
