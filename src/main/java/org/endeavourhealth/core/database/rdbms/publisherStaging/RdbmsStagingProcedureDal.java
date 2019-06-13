package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingProcedureDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProcedure;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RdbmsStagingProcedureDal implements StagingProcedureDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingProcedureDal.class);


    private boolean wasSavedAlready(UUID serviceId, StagingProcedure obj) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum "
                    + "from procedure_procedure "
                    + "where encounter_id = ? "
                    + "and proc_dt_tm = ? "
                    + "and proc_cd = ? "
                    + "and dt_received <= ? "
                    + "order by dt_received desc "
                    + "limit 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setInt(col++, obj.getEncounterId());
            ps.setTimestamp(col++, new java.sql.Timestamp(obj.getProcDtTm().getTime()));
            ps.setString(col++, obj.getProcCd());
            ps.setTimestamp(col++, new java.sql.Timestamp(obj.getDtReceived().getTime()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int dbChecksum = rs.getInt(1);
                return dbChecksum == obj.getRecordChecksum();
            } else {
                return false;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }

    }

    @Override
    public void saveProcedure(StagingProcedure stagingProcedure, UUID serviceId) throws Exception {

        if (stagingProcedure == null) {
            throw new IllegalArgumentException("stagingProcedure is null");
        }

        List<StagingProcedure> l = new ArrayList<>();
        l.add(stagingProcedure);
        saveProcedures(l, serviceId);
    }


    @Override
    public void saveProcedures(List<StagingProcedure> stagingProcedures, UUID serviceId) throws Exception {

        List<StagingProcedure> toSave = new ArrayList<>();

        for (StagingProcedure stagingProcedure: stagingProcedures) {

            stagingProcedure.setRecordChecksum(stagingProcedure.hashCode());

            //check if record already filed to avoid duplicates
            if (!wasSavedAlready(serviceId, stagingProcedure)) {
                //   LOG.warn("stagingProcedure data already filed with record_checksum: "+stagingProcedure.hashCode());
                toSave.add(stagingProcedure);
            }
        }

        if (toSave.isEmpty()) {
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();


            String sql = "INSERT INTO procedure_procedure "
                    + " (exchange_id, dt_received, record_checksum, mrn, "
                    + " nhs_number, date_of_birth, encounter_id, consultant, "
                    + " proc_dt_tm, updated_by, freetext_comment, create_dt_tm, "
                    + " proc_cd_type, proc_cd, proc_term, ward, site, "
                    + " lookup_person_id, lookup_consultant_personnel_id, "
                    + " lookup_recorded_by_personnel_id, audit_json)"
                    + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                    + " ON DUPLICATE KEY UPDATE "
                    + " exchange_id = VALUES(exchange_id), "
                    + " dt_received = VALUES(dt_received), "
                    + " record_checksum = VALUES(record_checksum), "
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
                    + " ward = VALUES(ward), "
                    + " site = VALUES(site), "
                    + " lookup_person_id = VALUES(lookup_person_id), "
                    + " lookup_consultant_personnel_id = VALUES(lookup_consultant_personnel_id), "
                    + " lookup_recorded_by_personnel_id = VALUES(lookup_recorded_by_personnel_id), "
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (StagingProcedure stagingProcedure: toSave) {

                int col = 1;

                //all but the last four columns are non-null
                ps.setString(col++, stagingProcedure.getExchangeId());
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingProcedure.getDtReceived().getTime()));
                ps.setInt(col++, stagingProcedure.getRecordChecksum());
                ps.setString(col++, stagingProcedure.getMrn());
                ps.setString(col++, stagingProcedure.getNhsNumber());
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingProcedure.getDateOfBirth().getTime()));
                ps.setInt(col++, stagingProcedure.getEncounterId());
                ps.setString(col++, stagingProcedure.getConsultant());
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingProcedure.getProcDtTm().getTime()));
                ps.setString(col++, stagingProcedure.getUpdatedBy());
                ps.setString(col++, stagingProcedure.getComments());
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingProcedure.getCreateDtTm().getTime()));
                ps.setString(col++, stagingProcedure.getProcCdType());
                ps.setString(col++, stagingProcedure.getProcCd());
                ps.setString(col++, stagingProcedure.getProcTerm());
                ps.setString(col++, stagingProcedure.getWard());
                ps.setString(col++, stagingProcedure.getSite());


                if (stagingProcedure.getLookupPersonId() == null) {
                    ps.setNull(col++, Types.INTEGER);
                } else {
                    ps.setInt(col++, stagingProcedure.getLookupPersonId());
                }

                if (stagingProcedure.getLookupConsultantPersonnelId() == null) {
                    ps.setNull(col++, Types.INTEGER);
                } else {
                    ps.setInt(col++, stagingProcedure.getLookupConsultantPersonnelId());
                }

                if (stagingProcedure.getLookupRecordedByPersonnelId() == null) {
                    ps.setNull(col++, Types.INTEGER);
                } else {
                    ps.setInt(col++, stagingProcedure.getLookupRecordedByPersonnelId());
                }

                if (stagingProcedure.getAudit() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingProcedure.getAudit().writeToJson());
                }

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
}
