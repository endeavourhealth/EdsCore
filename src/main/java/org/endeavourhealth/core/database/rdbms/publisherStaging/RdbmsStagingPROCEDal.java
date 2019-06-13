package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingPROCEDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingPROCE;
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

public class RdbmsStagingPROCEDal implements StagingPROCEDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingPROCEDal.class);



    /*@Override
    public List<UUID> getSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType) throws Exception {
        return null;
    }*/

    private boolean wasAlreadySaved(UUID serviceId, StagingPROCE obj) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum "
                    + "from procedure_PROCE "
                    + "where procedure_id = ? "
                    + "and dt_received <= ? "
                    + "order by dt_received desc "
                    + "limit 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setInt(col++, obj.getProcedureId());
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
    public void savePROCE(StagingPROCE stagingPROCE, UUID serviceId) throws Exception {

        if (stagingPROCE == null) {
            throw new IllegalArgumentException("stagingPROCE is null");
        }

        List<StagingPROCE> l = new ArrayList<>();
        l.add(stagingPROCE);
        savePROCEs(l, serviceId);
    }


    @Override
    public void savePROCEs(List<StagingPROCE> stagingPROCEs, UUID serviceId) throws Exception {

        List<StagingPROCE> toSave = new ArrayList<>();
        for (StagingPROCE stagingPROCE: stagingPROCEs) {
            stagingPROCE.setRecordChecksum(stagingPROCE.hashCode());

            //check if record already filed to avoid duplicates
            if (!wasAlreadySaved(serviceId, stagingPROCE)) {
                // LOG.warn("procedure_PROCE data already filed with record_checksum: "+stagingPROCE.hashCode());
                toSave.add(stagingPROCE);
            }
        }

        if (toSave.isEmpty()) {
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO procedure_PROCE "
                    + " (exchange_id, dt_received, record_checksum, procedure_id, "
                    + " active_ind, encounter_id, encounter_slice_id, procedure_dt_tm, procedure_type, "
                    + " procedure_code, procedure_term, procedure_seq_nbr, lookup_person_id, "
                    + " lookup_mrn, lookup_responsible_personnel_id, audit_json)  "
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                    + " ON DUPLICATE KEY UPDATE "
                    + " exchange_id = VALUES(exchange_id), "
                    + " dt_received = VALUES(dt_received), "
                    + " record_checksum = VALUES(record_checksum), "
                    + " procedure_id = VALUES(procedure_id), "
                    + " active_ind = VALUES(active_ind), "
                    + " encounter_id = VALUES(encounter_id), "
                    + " encounter_slice_id = VALUES(encounter_slice_id), "
                    + " procedure_dt_tm = VALUES(procedure_dt_tm), "
                    + " procedure_type = VALUES(procedure_type), "
                    + " procedure_code = VALUES(procedure_code), "
                    + " procedure_term = VALUES(procedure_term), "
                    + " procedure_seq_nbr = VALUES(procedure_seq_nbr), "
                    + " lookup_person_id = VALUES(lookup_person_id), "
                    + " lookup_mrn = VALUES(lookup_mrn), "
                    + " lookup_responsible_personnel_id = VALUES(lookup_responsible_personnel_id), "
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (StagingPROCE stagingPROCE: toSave) {

                int col = 1;

                //first five columns are non-null
                ps.setString(col++, stagingPROCE.getExchangeId());
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingPROCE.getDtReceived().getTime()));
                ps.setInt(col++, stagingPROCE.getRecordChecksum());
                ps.setInt(col++, stagingPROCE.getProcedureId());
                ps.setBoolean(col++, stagingPROCE.isActiveInd());

                if (stagingPROCE.getEncounterId() == null) {
                    ps.setNull(col++, Types.INTEGER);
                } else {
                    ps.setInt(col++, stagingPROCE.getEncounterId());
                }

                if (stagingPROCE.getEncounterSliceId() == null) {
                    ps.setNull(col++, Types.INTEGER);
                } else {
                    ps.setInt(col++, stagingPROCE.getEncounterSliceId());
                }

                if (stagingPROCE.getProcedureDtTm() == null) {
                    ps.setNull(col++, Types.TIMESTAMP);
                } else {
                    ps.setTimestamp(col++, new java.sql.Timestamp(stagingPROCE.getProcedureDtTm().getTime()));
                }

                if (stagingPROCE.getProcedureType() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingPROCE.getProcedureType());
                }

                if (stagingPROCE.getProcedureCode() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingPROCE.getProcedureCode());
                }

                if (stagingPROCE.getProcedureTerm() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingPROCE.getProcedureTerm());
                }

                if (stagingPROCE.getProcedureSeqNo() == null) {
                    ps.setNull(col++, Types.INTEGER);
                } else {
                    ps.setInt(col++, stagingPROCE.getProcedureSeqNo());
                }

                if (stagingPROCE.getLookupPersonId() == null) {
                    ps.setNull(col++, Types.INTEGER);
                } else {
                    ps.setInt(col++, stagingPROCE.getLookupPersonId());
                }

                if (stagingPROCE.getLookupMrn() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingPROCE.getLookupMrn());
                }

                if (stagingPROCE.getLookupResponsiblePersonnelId() == null) {
                    ps.setNull(col++, Types.INTEGER);
                } else {
                    ps.setInt(col++, stagingPROCE.getLookupResponsiblePersonnelId());
                }

                if (stagingPROCE.getAudit() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, stagingPROCE.getAudit().writeToJson());
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
