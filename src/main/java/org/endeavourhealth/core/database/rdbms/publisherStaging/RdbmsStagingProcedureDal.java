package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingProcedureDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProcedure;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingProcedure;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.Enumerations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.UUID;

public class RdbmsStagingProcedureDal implements StagingProcedureDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingProcedureDal.class);



    @Override
    public List<UUID> getSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType) throws Exception {
        return null;
    }

    @Override
    public boolean getRecordChecksumFiled(UUID serviceId, StagingProcedure stagingProcedure) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsStagingProcedure c"
                    + " where c.checkSum = :record_checksum";

            Query query = entityManager.createQuery(sql, RdbmsStagingProcedure.class)
                    .setParameter("record_checksum", stagingProcedure.hashCode());

            try {
                RdbmsStagingProcedure result = (RdbmsStagingProcedure)query.getSingleResult();
                return true;
            }
            catch (NoResultException e) {
                return false;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void save(StagingProcedure stagingProcedure,  UUID serviceId) throws Exception {

        if (stagingProcedure == null) {
            throw new IllegalArgumentException("stagingProcedure is null");
        }

        RdbmsStagingProcedure dbObj = new RdbmsStagingProcedure(stagingProcedure);

        //check if record already filed to avoid duplicates
        if (getRecordChecksumFiled(serviceId, stagingProcedure)) {
            LOG.error("stagingProcedure data already filed with record_checksum: "+stagingProcedure.hashCode());
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();


            String sql = "INSERT INTO procedure_procedure "
                    + " (exchange_id, dt_received, record_checksum, mrn, "
                    + " nhs_number, date_of_birth, encounter_id, consultant, "
                    + " proc_dt_tm, updated_by, freetext_comment, create_dt_tm, "
                    + " proc_cd_type, proc_cd, proc_term, person_id, ward, site, "
                    + " lookup_person_id, lookup_consultant_personnel_id, "
                    + " lookup_recorded_by_personnel_id, audit_json)"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                    + " ON DUPLICATE KEY UPDATE "
                    + " exchange_id = VALUES(exchange_id), "
                    + " dt_received = VALUES(dt_received), "
                    + " record_checksum = VALUES(dt_received), "
                    + " mrn = VALUES(mrn), "
                    + " nhs_number = VALUES(nhs_number), "
                    + " date_of_birth = VALUES(date_of_birth), "
                    + " encounter_id = VALUES(encounter_id), "
                    + " consultant = VALUES(consultant), "
                    + " proc_dt_tm = VALUES(proc_dt_tm), "
                    + " updated_by = VALUES(updated_by), "
                    + " freetext_comment = VALUES(freetext_comment), "
                    + " create_dt_tm = VALUES(create_dt_tm), "
                    + " proc_cd_type = VALUES(proc_cd_type), "
                    + " proc_cd = VALUES(proc_cd), "
                    + " proc_term = VALUES(proc_term), "
                    + " person_id = VALUES(person_id), "
                    + " ward = VALUES(ward), "
                    + " site = VALUES(site), "
                    + " lookup_person_id = VALUES(lookup_person), "
                    + " lookup_consultant_personnel_id = VALUES(lookup_consultant_personnel_id), "
                    + " lookup_recorded_by_personnel_id = VALUES(lookup_recorded_by_personnel_id), "
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            ps.setString(1, dbObj.getExchangeId());
            java.sql.Date sqlDate = new java.sql.Date(dbObj.getDateReceived().getTime());
            ps.setDate(2,sqlDate);
            ps.setInt(3,dbObj.getCheckSum());
            ps.setString(4,dbObj.getMrn());
            ps.setString(5,dbObj.getNhsNumber());
            sqlDate = new java.sql.Date(dbObj.getDob().getTime());
            ps.setDate(6,sqlDate);
            ps.setInt(7,dbObj.getEncounterId());
            ps.setString(8,dbObj.getConsultant());
            sqlDate = new java.sql.Date(dbObj.getProc_dt_tm().getTime());
            ps.setDate(9,sqlDate);
            ps.setInt(10,dbObj.getUpdatedBy());
            ps.setString(11,dbObj.getComments());
            sqlDate = new java.sql.Date(dbObj.getCreate_dt_tm().getTime());
            ps.setDate(12,sqlDate);
            ps.setString(13,dbObj.getProcedureCodeType());
            ps.setString(14,dbObj.getProcedureCode());
            ps.setString(15,dbObj.getProcedureTerm());
            ps.setString(16,dbObj.getPersonId());
            ps.setString(17,dbObj.getWard());
            ps.setString(18,dbObj.getSite());
            ps.setString(19,dbObj.getLookupPersonId());
            ps.setInt(20,dbObj.getLookupConsultantPersonnelId());
            ps.setInt(21,dbObj.getLookuprecordedByPersonnelId());
            ps.setString(22,dbObj.getAuditJson());

            ps.executeUpdate();

            //transaction.commit();
            entityManager.getTransaction().commit();
        } catch (SQLIntegrityConstraintViolationException sqlE) {
            LOG.warn("SQLIntegrityConstraintViolationException hadled for " + stagingProcedure.toString());
            entityManager.getTransaction().rollback();
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
