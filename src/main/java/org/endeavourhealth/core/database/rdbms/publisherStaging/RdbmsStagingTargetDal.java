package org.endeavourhealth.core.database.rdbms.publisherStaging;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherStaging.StagingTargetDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.*;
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
    public void processStagingForTargetCriticalCareCds(UUID exchangeId, UUID serviceId) throws Exception {

        Connection connection = ConnectionManager.getPublisherStagingNonPooledConnection(serviceId); //don't use a pooled connection as this is slow
        CallableStatement stmt = null;
        try {
            String sql = "{call process_critical_care_cds_staging_exchange(?)}";
            stmt = connection.prepareCall(sql);

            stmt.setString(1, exchangeId.toString());

            stmt.execute();

            connection.commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            connection.close();
        }
    }

    @Override
    public void processStagingForTargetOutpatientCds(UUID exchangeId, UUID serviceId) throws Exception {

        Connection connection = ConnectionManager.getPublisherStagingNonPooledConnection(serviceId); //don't use a pooled connection as this is slow
        CallableStatement stmt = null;
        try {
            String sql = "{call process_outpatient_cds_staging_exchange(?)}";
            stmt = connection.prepareCall(sql);

            stmt.setString(1, exchangeId.toString());

            stmt.execute();

            connection.commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            connection.close();
        }
    }

    @Override
    public void processStagingForTargetInpatientCds(UUID exchangeId, UUID serviceId) throws Exception {

        Connection connection = ConnectionManager.getPublisherStagingNonPooledConnection(serviceId); //don't use a pooled connection as this is slow
        CallableStatement stmt = null;
        try {
            String sql = "{call process_inpatient_cds_staging_exchange(?)}";
            stmt = connection.prepareCall(sql);

            stmt.setString(1, exchangeId.toString());

            stmt.execute();

            connection.commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            connection.close();
        }
    }

    @Override
    public void processStagingForTargetEmergencyCds(UUID exchangeId, UUID serviceId) throws Exception {

        Connection connection = ConnectionManager.getPublisherStagingNonPooledConnection(serviceId); //don't use a pooled connection as this is slow
        CallableStatement stmt = null;
        try {
            String sql = "{call process_emergency_cds_staging_exchange(?)}";
            stmt = connection.prepareCall(sql);

            stmt.setString(1, exchangeId.toString());

            stmt.execute();

            connection.commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            connection.close();
        }
    }

    @Override
    public void processStagingForTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception {

        Connection connection = ConnectionManager.getPublisherStagingNonPooledConnection(serviceId); //don't use a pooled connection as this is slow
        CallableStatement stmt = null;
        try {
            String sql = "{call process_procedure_staging_exchange(?)}";
            stmt = connection.prepareCall(sql);

            stmt.setString(1, exchangeId.toString());

            stmt.execute();

            connection.commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            connection.close();
        }
    }

    public void processStagingForTargetConditions(UUID exchangeId, UUID serviceId) throws Exception {

        Connection connection = ConnectionManager.getPublisherStagingNonPooledConnection(serviceId); //don't use a pooled connection as this is slow
        CallableStatement stmt = null;
        try {
            String sql = "{call process_condition_staging_exchange(?)}";
            stmt = connection.prepareCall(sql);

            stmt.setString(1, exchangeId.toString());

            stmt.execute();

            connection.commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            connection.close();
        }
    }

    @Override
    public List<StagingProcedureTarget> getTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityManager(serviceId);
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

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityManager(serviceId);
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

        Connection connection = ConnectionManager.getPublisherStagingNonPooledConnection(serviceId); //don't use a pooled connection as this is slow
        CallableStatement stmt = null;
        try {
            String sql = "{call process_clinical_events_staging_exchange(?)}";
            stmt = connection.prepareCall(sql);

            stmt.setString(1, exchangeId.toString());

            stmt.execute();

            connection.commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            connection.close();
        }
    }

    @Override
    public List<StagingClinicalEventTarget> getTargetClinicalEvents(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityManager(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "select unique_id, is_delete, event_id, person_id, encounter_id, order_id, parent_event_id, " +
                    " lookup_event_code, lookup_event_term, clinically_significant_dt_tm,  "+
                    " processed_numeric_result, comparator, normalcy_cd, lookup_normalcy_code,  "+
                    " normal_range_low_value, normal_range_high_value, event_performed_dt_tm, event_performed_prsnl_id,  "+
                    " event_title_txt, lookup_result_units_code, lookup_record_status_code, lookup_mrn, audit_json, is_confidential "+
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

                long eventId = rs.getLong(col++);
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

                Long orderId = rs.getLong(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setOrderId(orderId);
                }

                long parentEventId = rs.getLong(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setParentEventId(parentEventId);
                }

                stagingClinicalEventTarget.setLookupEventCode(rs.getString(col++));
                stagingClinicalEventTarget.setLookupEventTerm(rs.getString(col++));

                java.sql.Timestamp tsCSD = rs.getTimestamp(col++);
                if (tsCSD != null) {
                    stagingClinicalEventTarget.setClinicallySignificantDtTm(new Date(tsCSD.getTime()));
                }

                double processedNumericResult = rs.getDouble(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setProcessedNumericResult(processedNumericResult);
                }

                stagingClinicalEventTarget.setComparator(rs.getString(col++));

                int normalcyCode = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setNormalcyCd(normalcyCode);
                }

                stagingClinicalEventTarget.setLookupNormalcy(rs.getString(col++));

                double normalRangeLowValue = rs.getDouble(col++);
                if (!rs.wasNull()) {
                    stagingClinicalEventTarget.setNormalRangeLowValue(normalRangeLowValue);
                }

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

                stagingClinicalEventTarget.setEventTitleTxt(rs.getString(col++));

                stagingClinicalEventTarget.setLookupEventResultsUnitsCode(rs.getString(col++));

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

    @Override
    public List<StagingEmergencyCdsTarget> getTargetEmergencyCds(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityManager(serviceId);

        CallableStatement cs = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "{call get_target_emergency_cds_exchange(?)}";

            cs = connection.prepareCall(sql);
            cs.setString(1, exchangeId.toString());

            ResultSet rs = cs.executeQuery();
            List<StagingEmergencyCdsTarget> resultList = new ArrayList<>();
            while (rs.next()) {
                int col = 1;

                StagingEmergencyCdsTarget stagingEmergencyCdsTarget = new StagingEmergencyCdsTarget();

                stagingEmergencyCdsTarget.setUniqueId(rs.getString(col++));
                stagingEmergencyCdsTarget.setDeleted(rs.getBoolean(col++));

                int personId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingEmergencyCdsTarget.setPersonId(personId);
                }

                int encounterId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingEmergencyCdsTarget.setEncounterId(encounterId);
                }

                int episodeId = rs.getInt(col++);
                if (!rs.wasNull() && episodeId > 0) {
                    stagingEmergencyCdsTarget.setEpisodeId(episodeId);
                }

                int performedPrsnlId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingEmergencyCdsTarget.setPerformerPersonnelId(performedPrsnlId);
                }

                stagingEmergencyCdsTarget.setDepartmentType(rs.getString(col++));
                stagingEmergencyCdsTarget.setAmbulanceNo(rs.getString(col++));
                stagingEmergencyCdsTarget.setOrganisationCode(rs.getString(col++));
                stagingEmergencyCdsTarget.setAttendanceId(rs.getString(col++));
                stagingEmergencyCdsTarget.setArrivalMode(rs.getString(col++));
                stagingEmergencyCdsTarget.setAttendanceCategory(rs.getString(col++));

                java.sql.Timestamp tsArrival = rs.getTimestamp(col++);
                if (tsArrival != null) {
                    stagingEmergencyCdsTarget.setDtArrival(new Date(tsArrival.getTime()));
                }
                java.sql.Timestamp tsInitialAssessment = rs.getTimestamp(col++);
                if (tsInitialAssessment != null) {
                    stagingEmergencyCdsTarget.setDtInitialAssessment(new Date(tsInitialAssessment.getTime()));
                }
                stagingEmergencyCdsTarget.setChiefComplaint(rs.getString(col++));

                java.sql.Timestamp tsSeenForTreatment = rs.getTimestamp(col++);
                if (tsSeenForTreatment != null) {
                    stagingEmergencyCdsTarget.setDtSeenForTreatment(new Date(tsSeenForTreatment.getTime()));
                }
                java.sql.Timestamp tsDecidedToAdmit = rs.getTimestamp(col++);
                if (tsDecidedToAdmit != null) {
                    stagingEmergencyCdsTarget.setDtDecidedToAdmit(new Date(tsDecidedToAdmit.getTime()));
                }
                stagingEmergencyCdsTarget.setTreatmentFunctionCode(rs.getString(col++));
                stagingEmergencyCdsTarget.setDischargeStatus(rs.getString(col++));
                stagingEmergencyCdsTarget.setDischargeDestination(rs.getString(col++));

                java.sql.Timestamp tsConclusion = rs.getTimestamp(col++);
                if (tsConclusion != null) {
                    stagingEmergencyCdsTarget.setDtConclusion(new Date(tsConclusion.getTime()));
                }
                java.sql.Timestamp tsDeparture = rs.getTimestamp(col++);
                if (tsDeparture != null) {
                    stagingEmergencyCdsTarget.setDtDeparture(new Date(tsDeparture.getTime()));
                }
                stagingEmergencyCdsTarget.setDiagnosis(rs.getString(col++));
                stagingEmergencyCdsTarget.setInvestigations(rs.getString(col++));
                stagingEmergencyCdsTarget.setTreatments(rs.getString(col++));
                stagingEmergencyCdsTarget.setReferredToServices(rs.getString(col++));
                stagingEmergencyCdsTarget.setSafeguardingConcerns(rs.getString(col++));

                String auditJson = rs.getString(col++);
                if (!Strings.isNullOrEmpty(auditJson)) {
                    ResourceFieldMappingAudit audit = combineJson(auditJson);
                    stagingEmergencyCdsTarget.setAudit(audit);
                }
                boolean confidential = rs.getBoolean(col++);
                if (!rs.wasNull()) {
                    stagingEmergencyCdsTarget.setConfidential(confidential);
                }

                resultList.add(stagingEmergencyCdsTarget);
            }

            return resultList;

        } finally {
            if (cs != null) {
                cs.close();
            }
            entityManager.close();
        }
    }

    @Override
    public List<StagingInpatientCdsTarget> getTargetInpatientCds(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityManager(serviceId);

        CallableStatement cs = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "{call get_target_inpatient_cds_exchange(?)}";

            cs = connection.prepareCall(sql);
            cs.setString(1, exchangeId.toString());

            ResultSet rs = cs.executeQuery();
            List<StagingInpatientCdsTarget> resultList = new ArrayList<>();
            while (rs.next()) {
                int col = 1;

                StagingInpatientCdsTarget stagingInpatientCdsTarget = new StagingInpatientCdsTarget();

                stagingInpatientCdsTarget.setUniqueId(rs.getString(col++));
                stagingInpatientCdsTarget.setDeleted(rs.getBoolean(col++));

                int personId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingInpatientCdsTarget.setPersonId(personId);
                }

                int encounterId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingInpatientCdsTarget.setEncounterId(encounterId);
                }

                int episodeId = rs.getInt(col++);
                if (!rs.wasNull() && episodeId > 0) {
                    stagingInpatientCdsTarget.setEpisodeId(episodeId);
                }

                int performedPrsnlId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingInpatientCdsTarget.setPerformerPersonnelId(performedPrsnlId);
                }
                stagingInpatientCdsTarget.setPatientPathwayIdentifier(rs.getString(col++));
                stagingInpatientCdsTarget.setSpellNumber(rs.getString(col++));
                stagingInpatientCdsTarget.setAdmissionMethodCode(rs.getString(col++));
                stagingInpatientCdsTarget.setAdmissionSourceCode(rs.getString(col++));
                stagingInpatientCdsTarget.setPatientClassification(rs.getString(col++));
                java.sql.Timestamp tsSpellStart = rs.getTimestamp(col++);
                if (tsSpellStart != null) {
                    stagingInpatientCdsTarget.setDtSpellStart(new Date(tsSpellStart.getTime()));
                }
                stagingInpatientCdsTarget.setEpisodeNumber(rs.getString(col++));
                stagingInpatientCdsTarget.setEpisodeStartSiteCode(rs.getString(col++));
                stagingInpatientCdsTarget.setEpisodeStartWardCode(rs.getString(col++));

                java.sql.Timestamp tsEpisodeStart = rs.getTimestamp(col++);
                if (tsEpisodeStart != null) {
                    stagingInpatientCdsTarget.setDtEpisodeStart(new Date(tsEpisodeStart.getTime()));
                }
                stagingInpatientCdsTarget.setEpisodeEndSiteCode(rs.getString(col++));
                stagingInpatientCdsTarget.setEpisodeEndWardCode(rs.getString(col++));

                java.sql.Timestamp tsEpisodeEnd = rs.getTimestamp(col++);
                if (tsEpisodeEnd != null) {
                    stagingInpatientCdsTarget.setDtEpisodeEnd(new Date(tsEpisodeEnd.getTime()));
                }
                java.sql.Timestamp tsDischarge = rs.getTimestamp(col++);
                if (tsDischarge != null) {
                    stagingInpatientCdsTarget.setDtDischarge(new Date(tsDischarge.getTime()));
                }
                stagingInpatientCdsTarget.setDischargeDestinationCode(rs.getString(col++));
                stagingInpatientCdsTarget.setDischargeMethod(rs.getString(col++));
                stagingInpatientCdsTarget.setPrimaryDiagnosisICD(rs.getString(col++));
                stagingInpatientCdsTarget.setSecondaryDiagnosisICD(rs.getString(col++));
                stagingInpatientCdsTarget.setOtherDiagnosisICD(rs.getString(col++));
                stagingInpatientCdsTarget.setPrimaryProcedureOPCS(rs.getString(col++));

                java.sql.Timestamp tsPrimaryProc = rs.getTimestamp(col++);
                if (tsPrimaryProc != null) {
                    stagingInpatientCdsTarget.setPrimaryProcedureDate(new Date(tsPrimaryProc.getTime()));
                }
                stagingInpatientCdsTarget.setSecondaryProcedureOPCS(rs.getString(col++));

                java.sql.Timestamp tsSecondaryProc = rs.getTimestamp(col++);
                if (tsSecondaryProc != null) {
                    stagingInpatientCdsTarget.setSecondaryProcedureDate(new Date(tsSecondaryProc.getTime()));
                }
                stagingInpatientCdsTarget.setOtherProceduresOPCS(rs.getString(col++));

                String auditJson = rs.getString(col++);
                if (!Strings.isNullOrEmpty(auditJson)) {
                    ResourceFieldMappingAudit audit = combineJson(auditJson);
                    stagingInpatientCdsTarget.setAudit(audit);
                }
                boolean confidential = rs.getBoolean(col++);
                if (!rs.wasNull()) {
                    stagingInpatientCdsTarget.setConfidential(confidential);
                }

                resultList.add(stagingInpatientCdsTarget);
            }

            return resultList;

        } finally {
            if (cs != null) {
                cs.close();
            }
            entityManager.close();
        }
    }

    @Override
    public List<StagingOutpatientCdsTarget> getTargetOutpatientCds(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityManager(serviceId);

        CallableStatement cs = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "{call get_target_outpatient_cds_exchange(?)}";

            cs = connection.prepareCall(sql);
            cs.setString(1, exchangeId.toString());

            ResultSet rs = cs.executeQuery();
            List<StagingOutpatientCdsTarget> resultList = new ArrayList<>();
            while (rs.next()) {
                int col = 1;

                StagingOutpatientCdsTarget stagingOutpatientCdsTarget = new StagingOutpatientCdsTarget();

                stagingOutpatientCdsTarget.setUniqueId(rs.getString(col++));
                stagingOutpatientCdsTarget.setDeleted(rs.getBoolean(col++));

                int personId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingOutpatientCdsTarget.setPersonId(personId);
                }

                int encounterId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingOutpatientCdsTarget.setEncounterId(encounterId);
                }

                int episodeId = rs.getInt(col++);
                if (!rs.wasNull() && episodeId > 0) {
                    stagingOutpatientCdsTarget.setEpisodeId(episodeId);
                }

                int performedPrsnlId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingOutpatientCdsTarget.setPerformerPersonnelId(performedPrsnlId);
                }
                stagingOutpatientCdsTarget.setPatientPathwayIdentifier(rs.getString(col++));
                stagingOutpatientCdsTarget.setApptAttendanceIdentifier(rs.getString(col++));
                stagingOutpatientCdsTarget.setApptAttendedCode(rs.getString(col++));
                stagingOutpatientCdsTarget.setApptOutcomeCode(rs.getString(col++));

                java.sql.Timestamp tsApptDate = rs.getTimestamp(col++);
                if (tsApptDate != null) {
                    stagingOutpatientCdsTarget.setApptDate(new Date(tsApptDate.getTime()));
                }
                stagingOutpatientCdsTarget.setApptSiteCode(rs.getString(col++));

                stagingOutpatientCdsTarget.setPrimaryDiagnosisICD(rs.getString(col++));
                stagingOutpatientCdsTarget.setSecondaryDiagnosisICD(rs.getString(col++));
                stagingOutpatientCdsTarget.setOtherDiagnosisICD(rs.getString(col++));
                stagingOutpatientCdsTarget.setPrimaryProcedureOPCS(rs.getString(col++));

                java.sql.Timestamp tsPrimaryProc = rs.getTimestamp(col++);
                if (tsPrimaryProc != null) {
                    stagingOutpatientCdsTarget.setPrimaryProcedureDate(new Date(tsPrimaryProc.getTime()));
                }
                stagingOutpatientCdsTarget.setSecondaryProcedureOPCS(rs.getString(col++));

                java.sql.Timestamp tsSecondaryProc = rs.getTimestamp(col++);
                if (tsSecondaryProc != null) {
                    stagingOutpatientCdsTarget.setSecondaryProcedureDate(new Date(tsSecondaryProc.getTime()));
                }
                stagingOutpatientCdsTarget.setOtherProceduresOPCS(rs.getString(col++));

                String auditJson = rs.getString(col++);
                if (!Strings.isNullOrEmpty(auditJson)) {
                    ResourceFieldMappingAudit audit = combineJson(auditJson);
                    stagingOutpatientCdsTarget.setAudit(audit);
                }
                boolean confidential = rs.getBoolean(col++);
                if (!rs.wasNull()) {
                    stagingOutpatientCdsTarget.setConfidential(confidential);
                }

                resultList.add(stagingOutpatientCdsTarget);
            }

            return resultList;

        } finally {
            if (cs != null) {
                cs.close();
            }
            entityManager.close();
        }
    }

    @Override
    public List<StagingCriticalCareCdsTarget> getTargetCriticalCareCds(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityManager(serviceId);

        CallableStatement cs = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "{call get_target_critical_care_cds_exchange(?)}";

            cs = connection.prepareCall(sql);
            cs.setString(1, exchangeId.toString());

            ResultSet rs = cs.executeQuery();
            List<StagingCriticalCareCdsTarget> resultList = new ArrayList<>();
            while (rs.next()) {
                int col = 1;

                StagingCriticalCareCdsTarget stagingCriticalCareCdsTarget = new StagingCriticalCareCdsTarget();

                stagingCriticalCareCdsTarget.setUniqueId(rs.getString(col++));
                stagingCriticalCareCdsTarget.setDeleted(rs.getBoolean(col++));

                int personId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setPersonId(personId);
                }
                int performedPrsnlId = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setPerformerPersonnelId(performedPrsnlId);
                }
                stagingCriticalCareCdsTarget.setCriticalCareTypeId(rs.getString(col++));
                stagingCriticalCareCdsTarget.setSpellNumber(rs.getString(col++));
                stagingCriticalCareCdsTarget.setEpisodeNumber(rs.getString(col++));
                stagingCriticalCareCdsTarget.setCriticalCareIdentifier(rs.getString(col++));

                java.sql.Timestamp tsCareStart = rs.getTimestamp(col++);
                if (tsCareStart != null) {
                    stagingCriticalCareCdsTarget.setCareStartDate(new Date(tsCareStart.getTime()));
                }
                stagingCriticalCareCdsTarget.setCareUnitFunction(rs.getString(col++));
                stagingCriticalCareCdsTarget.setAdmissionSourceCode(rs.getString(col++));
                stagingCriticalCareCdsTarget.setAdmissionTypeCode(rs.getString(col++));
                stagingCriticalCareCdsTarget.setAdmissionLocation(rs.getString(col++));
                stagingCriticalCareCdsTarget.setGestationLengthAtDelivery(rs.getString(col++));

                int advancedRespiratorySupportDays = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setAdvancedCardiovascularSupportDays(advancedRespiratorySupportDays);
                }
                int basicRespiratorySupportsDays = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setBasicRespiratorySupportsDays(basicRespiratorySupportsDays);
                }
                int advancedCardiovascularSupportDays = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setAdvancedCardiovascularSupportDays(advancedCardiovascularSupportDays);
                }
                int basicCardiovascularSupportDays = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setBasicCardiovascularSupportDays(basicCardiovascularSupportDays);
                }
                int renalSupportDays = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setRenalSupportDays(renalSupportDays);
                }
                int neurologicalSupportDays = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setNeurologicalSupportDays(neurologicalSupportDays);
                }
                int gastroIntestinalSupportDays = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setGastroIntestinalSupportDays(gastroIntestinalSupportDays);
                }
                int dermatologicalSupportDays = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setDermatologicalSupportDays(dermatologicalSupportDays);
                }
                int liverSupportDays = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setLiverSupportDays(liverSupportDays);
                }
                int organSupportMaximum = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setOrganSupportMaximum(organSupportMaximum);
                }
                int criticalCareLevel2Days = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setCriticalCareLevel2Days(criticalCareLevel2Days);
                }
                int criticalCareLevel3Days = rs.getInt(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setCriticalCareLevel3Days(criticalCareLevel3Days);
                }
                java.sql.Timestamp tsDischarge = rs.getTimestamp(col++);
                if (tsDischarge != null) {
                    stagingCriticalCareCdsTarget.setDischargeDate(new Date(tsDischarge.getTime()));
                }
                java.sql.Timestamp tsDischargeReady = rs.getTimestamp(col++);
                if (tsDischargeReady != null) {
                    stagingCriticalCareCdsTarget.setDischargeReadyDate(new Date(tsDischargeReady.getTime()));
                }
                stagingCriticalCareCdsTarget.setDischargeStatusCode(rs.getString(col++));
                stagingCriticalCareCdsTarget.setDischargeDestination(rs.getString(col++));
                stagingCriticalCareCdsTarget.setDischargeLocation(rs.getString(col++));
                stagingCriticalCareCdsTarget.setCareActivity1(rs.getString(col++));
                stagingCriticalCareCdsTarget.setCareActivity2100(rs.getString(col++));

                String auditJson = rs.getString(col++);
                if (!Strings.isNullOrEmpty(auditJson)) {
                    ResourceFieldMappingAudit audit = combineJson(auditJson);
                    stagingCriticalCareCdsTarget.setAudit(audit);
                }
                boolean confidential = rs.getBoolean(col++);
                if (!rs.wasNull()) {
                    stagingCriticalCareCdsTarget.setConfidential(confidential);
                }

                resultList.add(stagingCriticalCareCdsTarget);
            }

            return resultList;

        } finally {
            if (cs != null) {
                cs.close();
            }
            entityManager.close();
        }
    }
}