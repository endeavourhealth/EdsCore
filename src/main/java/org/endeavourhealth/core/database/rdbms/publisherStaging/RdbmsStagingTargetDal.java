package org.endeavourhealth.core.database.rdbms.publisherStaging;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherStaging.StagingTargetDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingConditionTarget;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProcedureTarget;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RdbmsStagingTargetDal implements StagingTargetDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingTargetDal.class);

    @Override
    public void processStagingForTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        CallableStatement stmt = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "{call process_procedure_staging_exchange(?)}";
            stmt = connection.prepareCall(sql);

            entityManager.getTransaction().begin();

            stmt.setString(1, exchangeId.toString());

            stmt.execute();

            entityManager.getTransaction().commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            entityManager.close();
        }
    }

    public void processStagingForTargetConditions(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        CallableStatement stmt = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "{call process_condition_staging_exchange(?)}";
            stmt = connection.prepareCall(sql);

            entityManager.getTransaction().begin();

            stmt.setString(1, exchangeId.toString());

            stmt.execute();

            entityManager.getTransaction().commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            entityManager.close();
        }
    }

    @Override
    public List<StagingProcedureTarget> getTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "select  unique_id, is_delete, person_id, encounter_id, performer_personnel_id, " +
                    " dt_performed, dt_ended, " +
                    " free_text, recorded_by_personnel_id, dt_recorded, procedure_type, procedure_term, procedure_code, "+
                    " sequence_number, parent_procedure_unique_id, qualifier, location, specialty, audit_json, is_confidential "+
                    " from "+
                    " procedure_target "+
                    " where exchange_id = ?";

            ps = connection.prepareStatement(sql);
            ps.setString(1, exchangeId.toString());

            ResultSet rs = ps.executeQuery();
            List<StagingProcedureTarget> resultList = new ArrayList<>();
            while (rs.next()) {
                int col = 1;
                StagingProcedureTarget stagingProcedureTarget = new StagingProcedureTarget();
                stagingProcedureTarget.setUniqueId(rs.getString(col++));
                stagingProcedureTarget.setDeleted(rs.getBoolean(col++));
                stagingProcedureTarget.setPersonId(rs.getInt(col++));
                stagingProcedureTarget.setEncounterId(rs.getInt(col++));
                stagingProcedureTarget.setPerformerPersonnelId(rs.getInt(col++));

                java.sql.Timestamp ts = rs.getTimestamp(col++);
                if (ts != null) {
                    stagingProcedureTarget.setDtPerformed(new Date(ts.getTime()));
                }
                ts = rs.getTimestamp(col++);
                if (ts != null) {
                    stagingProcedureTarget.setDtEnded(new Date(ts.getTime()));
                }
                stagingProcedureTarget.setFreeText(rs.getString(col++));
                stagingProcedureTarget.setRecordedByPersonnelId(rs.getInt(col++));

                ts = rs.getTimestamp(col++);
                if (ts != null) {
                    stagingProcedureTarget.setDtRecorded(new Date(ts.getTime()));
                }
                stagingProcedureTarget.setProcedureType(rs.getString(col++));
                stagingProcedureTarget.setProcedureTerm(rs.getString(col++));
                stagingProcedureTarget.setProcedureCode(rs.getString(col++));
                stagingProcedureTarget.setProcedureSeqNbr(rs.getInt(col++));
                stagingProcedureTarget.setParentProcedureUniqueId(rs.getString(col++));
                stagingProcedureTarget.setQualifier(rs.getString(col++));
                stagingProcedureTarget.setLocation(rs.getString(col++));
                stagingProcedureTarget.setSpecialty(rs.getString(col++));

                String auditJson = rs.getString(col++);
                if (!Strings.isNullOrEmpty(auditJson)) {
                    stagingProcedureTarget.setAudit(ResourceFieldMappingAudit.readFromJson(auditJson));
                }

                stagingProcedureTarget.setConfidential(rs.getBoolean(col++));

                resultList.add(stagingProcedureTarget);
            }

            return resultList;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public List<StagingConditionTarget> getTargetConditions(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            //TODO - set final Diagnosis Target DB table structure here
            String sql = "";
//            String sql = "select  unique_id, is_delete, person_id, encounter_id, performer_personnel_id, " +
//                    " dt_performed, dt_ended, " +
//                    " free_text, recorded_by_personnel_id, dt_recorded, procedure_type, procedure_term, procedure_code, "+
//                    " sequence_number, parent_procedure_unique_id, qualifier, location, specialty, audit_json, is_confidential "+
//                    " from "+
//                    " procedure_target "+
//                    " where exchange_id = ?";

            ps = connection.prepareStatement(sql);
            ps.setString(1, exchangeId.toString());

            ResultSet rs = ps.executeQuery();
            List<StagingConditionTarget> resultList = new ArrayList<>();
            while (rs.next()) {
                int col = 1;
                StagingConditionTarget stagingConditionTarget = new StagingConditionTarget();
                stagingConditionTarget.setUniqueId(rs.getString(col++));
                stagingConditionTarget.setDeleted(rs.getBoolean(col++));
                stagingConditionTarget.setPersonId(rs.getInt(col++));
                stagingConditionTarget.setEncounterId(rs.getInt(col++));
                stagingConditionTarget.setPerformerPersonnelId(rs.getInt(col++));

                java.sql.Timestamp ts = rs.getTimestamp(col++);
                if (ts != null) {
                    stagingConditionTarget.setDtPerformed(new Date(ts.getTime()));
                }

                //TODO - set remaining Diagnosis Target values
//                ts = rs.getTimestamp(col++);
//                if (ts != null) {
//                    stagingProcedureTarget.setDtEnded(new Date(ts.getTime()));
//                }
//                stagingProcedureTarget.setFreeText(rs.getString(col++));
//                stagingProcedureTarget.setRecordedByPersonnelId(rs.getInt(col++));
//
//                ts = rs.getTimestamp(col++);
//                if (ts != null) {
//                    stagingProcedureTarget.setDtRecorded(new Date(ts.getTime()));
//                }
//                stagingProcedureTarget.setProcedureType(rs.getString(col++));
//                stagingProcedureTarget.setProcedureTerm(rs.getString(col++));
//                stagingProcedureTarget.setProcedureCode(rs.getString(col++));
//                stagingProcedureTarget.setProcedureSeqNbr(rs.getInt(col++));
//                stagingProcedureTarget.setParentProcedureUniqueId(rs.getString(col++));
//                stagingProcedureTarget.setQualifier(rs.getString(col++));
//                stagingProcedureTarget.setLocation(rs.getString(col++));
//                stagingProcedureTarget.setSpecialty(rs.getString(col++));


                String auditJson = rs.getString(col++);
                if (!Strings.isNullOrEmpty(auditJson)) {
                    stagingConditionTarget.setAudit(ResourceFieldMappingAudit.readFromJson(auditJson));
                }

                resultList.add(stagingConditionTarget);
            }

            return resultList;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }
}
