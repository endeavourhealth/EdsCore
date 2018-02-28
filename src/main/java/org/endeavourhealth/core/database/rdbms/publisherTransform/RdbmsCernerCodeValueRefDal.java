package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.CernerCodeValueRefDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerCodeValueRef;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsCernerCodeValueRef;
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
    public CernerCodeValueRef getCodeFromCodeSet(Long codeSet, Long code, UUID serviceId) throws Exception {
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
                LOG.error("No code found for codeSet " + codeSet + ", code " + code + ", service " + serviceId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public CernerCodeValueRef getCodeWithoutCodeSet(Long code, UUID serviceId) throws Exception {
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
    public CernerCodeValueRef getCodeFromMultipleCodeSets(Long code, UUID serviceId, Long... codeSets) throws Exception {
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

    public void save(CernerCodeValueRef mapping, UUID serviceId) throws Exception
    {
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
                    + " audit_json = VALUES(audit_json);";

            ps = connection.prepareStatement(sql);
            // Only JSON audit field is nullable
            ps.setLong(1, cernerMapping.getCodeValueCd());
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
}
