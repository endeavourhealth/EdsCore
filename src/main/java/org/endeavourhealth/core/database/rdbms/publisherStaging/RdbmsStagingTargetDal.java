package org.endeavourhealth.core.database.rdbms.publisherStaging;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherStaging.StagingTargetDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingClinicalEventTarget;
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

                int personId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingProcedureTarget.setPersonId(personId);
                }

                int encounterId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingProcedureTarget.setEncounterId(encounterId);
                }

                int performerId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingProcedureTarget.setPerformerPersonnelId(performerId);
                }

                java.sql.Timestamp ts = rs.getTimestamp(col++);
                if (!rs.wasNull()) {
                    stagingProcedureTarget.setDtPerformed(new Date(ts.getTime()));
                }

                ts = rs.getTimestamp(col++);
                if (!rs.wasNull()) {
                    stagingProcedureTarget.setDtEnded(new Date(ts.getTime()));
                }

                stagingProcedureTarget.setFreeText(rs.getString(col++));

                int recordedBy = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingProcedureTarget.setRecordedByPersonnelId(recordedBy);
                }

                ts = rs.getTimestamp(col++);
                if (!rs.wasNull()) {
                    stagingProcedureTarget.setDtRecorded(new Date(ts.getTime()));
                }

                stagingProcedureTarget.setProcedureType(rs.getString(col++));
                stagingProcedureTarget.setProcedureTerm(rs.getString(col++));
                stagingProcedureTarget.setProcedureCode(rs.getString(col++));

                int seqNumber = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingProcedureTarget.setProcedureSeqNbr(seqNumber);
                }

                stagingProcedureTarget.setParentProcedureUniqueId(rs.getString(col++));
                stagingProcedureTarget.setQualifier(rs.getString(col++));
                stagingProcedureTarget.setLocation(rs.getString(col++));
                stagingProcedureTarget.setSpecialty(rs.getString(col++));

                String auditJson = rs.getString(col++);

                if (!Strings.isNullOrEmpty(auditJson)) {
                    ResourceFieldMappingAudit audit = combineJson(auditJson);
                    stagingProcedureTarget.setAudit(audit);
                }

                boolean confidential = rs.getBoolean(col++);
                if (!rs.wasNull()) {
                    stagingProcedureTarget.setConfidential(confidential);
                }

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

    /**
     * the stored procedure concatenates JSON blobs together using pipe characters,
     * so we need to parse that and combine it all into one JSON object
     */
    private ResourceFieldMappingAudit combineJson(String auditJson) throws Exception {
        String[] toks = auditJson.split("&");
        if (toks.length == 1) {
            String tok = toks[0];
            return ResourceFieldMappingAudit.readFromJson(tok);
        }

        //in some of the test data, before the audit was fully implemented, we ended up with some slightly
        //wrong audit strings, so we need to ensure that we strip out any empty tokens
        List<String> tidiedToks = new ArrayList<>();
        for (String tok: toks) {
            String tidied = tok.trim(); //trim not necessary, but can't hurt
            if (!Strings.isNullOrEmpty(tidied)) {
                tidied = tidied.substring(1, tok.length()-1); //remove square brackets
                tidiedToks.add(tidied);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(String.join(",", tidiedToks));
        sb.append("]");

        return ResourceFieldMappingAudit.readFromJson(sb.toString());
    }

    @Override
    public List<StagingConditionTarget> getTargetConditions(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "select unique_id, is_delete, person_id, encounter_id, performer_personnel_id, dt_performed, dt_precision, " +
                    " condition_code_type, condition_code, condition_term, condition_type, free_text, sequence_number, "+
                    " parent_condition_unique_id, classification, confirmation, problem_status, problem_status_date, "+
                    " ranking, axis, location, audit_json, is_confidential "+
                    " from "+
                    " condition_target "+
                    " where exchange_id = ?";

            ps = connection.prepareStatement(sql);
            ps.setString(1, exchangeId.toString());

            ResultSet rs = ps.executeQuery();
            List<StagingConditionTarget> resultList = new ArrayList<>();
            while (rs.next()) {
                int col = 1;
                StagingConditionTarget stagingConditionTarget = new StagingConditionTarget();

                stagingConditionTarget.setUniqueId(rs.getString(col++));
                stagingConditionTarget.setDeleted(rs.getBoolean(col++));

                int personId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingConditionTarget.setPersonId(personId);
                }

                int encounterId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingConditionTarget.setEncounterId(encounterId);
                }

                int performerId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingConditionTarget.setPerformerPersonnelId(performerId);
                }

                java.sql.Timestamp tsPD = rs.getTimestamp(col++);
                if (tsPD != null) {
                    stagingConditionTarget.setDtPerformed(new Date(tsPD.getTime()));
                }

                stagingConditionTarget.setDtPrecision(rs.getString(col++));

                stagingConditionTarget.setConditionCodeType(rs.getString(col++));
                stagingConditionTarget.setConditionCode(rs.getString(col++));
                stagingConditionTarget.setConditionTerm(rs.getString(col++));
                stagingConditionTarget.setConditionType(rs.getString(col++));
                stagingConditionTarget.setFreeText(rs.getString(col++));

                int seqNumber = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingConditionTarget.setSequenceNumber(seqNumber);
                }

                stagingConditionTarget.setParentConditionUniqueId(rs.getString(col++));
                stagingConditionTarget.setClassification(rs.getString(col++));
                stagingConditionTarget.setConfirmation(rs.getString(col++));
                stagingConditionTarget.setProblemStatus(rs.getString(col++));

                java.sql.Timestamp tsSD = rs.getTimestamp(col++);
                if (tsSD != null) {
                    stagingConditionTarget.setProblemStatusDate(new Date(tsSD.getTime()));
                }

                stagingConditionTarget.setRanking(rs.getString(col++));
                stagingConditionTarget.setAxis(rs.getString(col++));
                stagingConditionTarget.setLocation(rs.getString(col++));

                String auditJson = rs.getString(col++);
                if (!Strings.isNullOrEmpty(auditJson)) {
                    ResourceFieldMappingAudit audit = combineJson(auditJson);
                    stagingConditionTarget.setAudit(audit);
                }

                boolean confidential = rs.getBoolean(col++);
                if (!rs.wasNull()) {
                    stagingConditionTarget.setConfidential(confidential);
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

    @Override
    public void processStagingForTargetClinicalEvents(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        CallableStatement stmt = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "{call process_clinical_events_staging_exchange(?)}";
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
    public List<StagingClinicalEventTarget> getTargetClinicalEvents(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "select unique_id, is_delete, event_id, person_id, encounter_id, order_id, parent_event_id, event_cd, " +
                    " lookup_event_code, lookup_event_term, event_start_dt_tm, event_end_dt_tm, clinically_significant_dt_tm, event_class_cd, "+
                    " lookup_event_class, event_result_status_cd, lookup_event_result_status, event_result_txt, event_result_nbr, "+
                    " processed_numeric_result, comparator, event_result_dt, normalcy_cd, lookup_normalcy_code, normal_range_low_txt, "+
                    " normal_range_low_value, normal_range_high_txt, normal_range_high_value, event_performed_dt_tm, event_performed_prsnl_id, event_tag, "+
                    " event_title_txt, event_result_units_cd, lookup_result_units_code, record_status_cd, lookup_record_status_code, lookup_mrn, audit_json "+
                    " from "+
                    " clinical_event_target "+
                    " where exchange_id = ?";

            ps = connection.prepareStatement(sql);
            ps.setString(1, exchangeId.toString());

            ResultSet rs = ps.executeQuery();
            List<StagingClinicalEventTarget> resultList = new ArrayList<>();
            while (rs.next()) {
                int col = 1;
                StagingClinicalEventTarget stagingClinicalEventTarget = new StagingClinicalEventTarget();

                stagingClinicalEventTarget.setUniqueId(rs.getString(col++));
                stagingClinicalEventTarget.setDeleted(rs.getBoolean(col++));

                int eventId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setEventId(eventId);
                }

                int personId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setPersonId(personId);
                }

                int encounterId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setEncounterId(encounterId);
                }

                int orderId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setOrderId(orderId);
                }

                int parentEventId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setParentEventId(parentEventId);
                }

                stagingClinicalEventTarget.setEventCd(rs.getString(col++));
                stagingClinicalEventTarget.setLookupEventCode(rs.getString(col++));
                stagingClinicalEventTarget.setLookupEventTerm(rs.getString(col++));

                java.sql.Timestamp tsESD = rs.getTimestamp(col++);
                if (tsESD != null) {
                    stagingClinicalEventTarget.setEventStartDtTm(new Date(tsESD.getTime()));
                }

                java.sql.Timestamp tsEED = rs.getTimestamp(col++);
                if (tsEED != null) {
                    stagingClinicalEventTarget.setEventEndDtTm(new Date(tsEED.getTime()));
                }

                java.sql.Timestamp tsCSD = rs.getTimestamp(col++);
                if (tsCSD != null) {
                    stagingClinicalEventTarget.setClinicallySignificantDtTm(new Date(tsCSD.getTime()));
                }

                int eventClassCode = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setEventClassCd(eventClassCode);
                }

                stagingClinicalEventTarget.setLookupEventClass(rs.getString(col++));

                int eventResultStatusCode = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setEventResultStatusCd(eventResultStatusCode);
                }

                stagingClinicalEventTarget.setLookupEventResultStatus(rs.getString(col++));
                stagingClinicalEventTarget.setEventResultTxt(rs.getString(col++));

                int eventResultNbr = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setEventResultNbr(eventResultNbr);
                }

                double processedNumericResult = rs.getDouble(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setProcessedNumericResult(processedNumericResult);
                }

                stagingClinicalEventTarget.setComparator(rs.getString(col++));

                java.sql.Timestamp tsERD = rs.getTimestamp(col++);
                if (tsERD != null) {
                    stagingClinicalEventTarget.setEventResultDt(new Date(tsERD.getTime()));
                }

                int normalcyCode = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setNormalcyCd(normalcyCode);
                }

                stagingClinicalEventTarget.setLookupNormalcy(rs.getString(col++));
                stagingClinicalEventTarget.setNormalRangeLowTxt(rs.getString(col++));

                double normalRangeLowValue = rs.getDouble(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setNormalRangeLowValue(normalRangeLowValue);
                }

                stagingClinicalEventTarget.setNormalRangeHighTxt(rs.getString(col++));

                double normalRangeHighValue = rs.getDouble(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setNormalRangeHighValue(normalRangeHighValue);
                }

                java.sql.Timestamp tsEPD = rs.getTimestamp(col++);
                if (tsEPD != null) {
                    stagingClinicalEventTarget.setEventPerformedDtTm(new Date(tsEPD.getTime()));
                }

                int performedPrsnl = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setEventPerformedPrsnlId(performedPrsnl);
                }

                stagingClinicalEventTarget.setEventTag(rs.getString(col++));
                stagingClinicalEventTarget.setEventTitleTxt(rs.getString(col++));

                int eventResultsUnitCode = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setEventResultUnitsCd(eventResultsUnitCode);
                }

                stagingClinicalEventTarget.setLookupEventResultsUnitsCode(rs.getString(col++));

                int recordStatusCode = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setRecordStatusCd(recordStatusCode);
                }

                stagingClinicalEventTarget.setLookupRecordStatusCode(rs.getString(col++));
                stagingClinicalEventTarget.setLookupMrn(rs.getString(col++));

                String auditJson = rs.getString(col++);
                if (!Strings.isNullOrEmpty(auditJson)) {
                    ResourceFieldMappingAudit audit = combineJson(auditJson);
                    stagingClinicalEventTarget.setAuditJson(audit);
                }

                boolean confidential = rs.getBoolean(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setConfidential(confidential);
                }

                resultList.add(stagingClinicalEventTarget);
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