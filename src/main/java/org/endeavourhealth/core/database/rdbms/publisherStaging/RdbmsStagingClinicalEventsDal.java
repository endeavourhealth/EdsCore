package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingClinicalEventDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingClinicalEvent;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.UUID;

public class RdbmsStagingClinicalEventsDal implements StagingClinicalEventDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingPROCEDal.class);

    private boolean wasAlreadySaved(UUID serviceId, StagingClinicalEvent obj) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select record_checksum "
                    + "from clinical_event "
                    + "where event_id = ? "
                    + "and dt_received <= ? "
                    + "order by dt_received desc "
                    + "limit 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setLong(col++, obj.getEventId());
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
    public void save(StagingClinicalEvent stagingClinicalEvent, UUID serviceId) throws Exception {
        if (stagingClinicalEvent == null) {
            throw new IllegalArgumentException("stagingClinicalEvent is null");
        }

        stagingClinicalEvent.setRecordChecksum(stagingClinicalEvent.hashCode());

        //check if record already filed to avoid duplicates
        if (wasAlreadySaved(serviceId, stagingClinicalEvent)) {
            // LOG.warn("procedure_PROCE data already filed with record_checksum: "+stagingClinicalEvent.hashCode());
            return;
        }

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO clinical_event "
                    + " (exchange_id, dt_received, record_checksum, event_id, "
                    + " active_ind, person_id, encounter_id, order_id, "
                    + " parent_event_id, event_cd, code_disp_txt, lookup_event_code, lookup_event_term, event_start_dt_tm, "
                    + " event_end_dt_tm, clinically_significant_dt_tm, event_class_cd, lookup_event_class, "
                    + "event_result_status_cd, lookup_event_result_status, event_result_txt, event_result_nbr, event_result_dt, normalcy_cd,"
                    + "lookup_normalcy_code, normal_range_low_txt, normal_range_high_txt, event_performed_dt_tm, event_performed_prsnl_id, event_tag,"
                    + "event_title_txt, event_result_units_cd, lookup_result_units_code, record_status_cd, lookup_record_status_code, lookup_mrn, audit_json)  "
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                    + " ON DUPLICATE KEY UPDATE "
                    + " exchange_id = VALUES(exchange_id), "
                    + " dt_received = VALUES(dt_received), "
                    + " record_checksum = VALUES(record_checksum), "
                    + " event_id = VALUES(event_id), "
                    + " active_ind = VALUES(active_ind), "
                    + " person_id = VALUES(person_id), "
                    + " encounter_id = VALUES(encounter_id), "
                    + " order_id = VALUES(order_id), "
                    + " parent_event_id = VALUES(parent_event_id), "
                    + " event_cd = VALUES(event_cd), "
                    + " code_disp_txt = VALUES(code_disp_txt), "
                    + " lookup_event_code = VALUES(lookup_event_code), "
                    + " lookup_event_term = VALUES(lookup_event_term), "
                    + " event_start_dt_tm = VALUES(event_start_dt_tm), "
                    + " event_end_dt_tm = VALUES(event_end_dt_tm), "
                    + " clinically_significant_dt_tm = VALUES(clinically_significant_dt_tm), "
                    + " event_class_cd = VALUES(event_class_cd), "
                    + " lookup_event_class = VALUES(lookup_event_class), "
                    + " event_result_status_cd = VALUES(event_result_status_cd), "
                    + " lookup_event_result_status = VALUES(lookup_event_result_status), "
                    + " event_result_txt = VALUES(event_result_txt), "
                    + " event_result_nbr = VALUES(event_result_nbr), "
                    + " event_result_dt = VALUES(event_result_dt), "
                    + " normalcy_cd = VALUES(normalcy_cd), "
                    + " lookup_normalcy_code = VALUES(lookup_normalcy_code), "
                    + " normal_range_low_txt = VALUES(normal_range_low_txt), "
                    + " normal_range_high_txt = VALUES(normal_range_high_txt), "
                    + " event_performed_dt_tm = VALUES(event_performed_dt_tm), "
                    + " event_performed_prsnl_id = VALUES(event_performed_prsnl_id), "
                    + " event_tag = VALUES(event_tag), "
                    + " event_title_txt = VALUES(event_title_txt), "
                    + " event_result_units_cd = VALUES(event_result_units_cd), "
                    + " lookup_result_units_code = VALUES(lookup_result_units_code), "
                    + " record_status_cd = VALUES(record_status_cd), "
                    + " lookup_record_status_code = VALUES(lookup_record_status_code), "
                    + " lookup_mrn = VALUES(lookup_mrn), "
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            int col = 1;

            //first five columns are non-null
            ps.setString(col++, stagingClinicalEvent.getExchangeId());
            ps.setTimestamp(col++, new java.sql.Timestamp(stagingClinicalEvent.getDtReceived().getTime()));
            ps.setLong(col++, stagingClinicalEvent.getRecordChecksum());
            ps.setLong(col++, stagingClinicalEvent.getEventId());
            ps.setBoolean(col++, stagingClinicalEvent.isActiveInd());
            ps.setInt(col++, stagingClinicalEvent.getPersonId());

            if (stagingClinicalEvent.getEncounterId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingClinicalEvent.getEncounterId());
            }

            if (stagingClinicalEvent.getOrderId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingClinicalEvent.getOrderId());
            }

            if (stagingClinicalEvent.getParentEventId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingClinicalEvent.getParentEventId());
            }

            if (stagingClinicalEvent.getEventCd() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getEventCd());
            }

            if (stagingClinicalEvent.getCodeDispTxt() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getCodeDispTxt());
            }

            if (stagingClinicalEvent.getLookupEventCode() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getLookupEventCode());
            }

            if (stagingClinicalEvent.getLookupEventTerm() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getLookupEventTerm());
            }

            if (stagingClinicalEvent.getEventStartDtTm() == null) {
                ps.setNull(col++, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingClinicalEvent.getEventStartDtTm().getTime()));
            }

            if (stagingClinicalEvent.getEventEndDtTm() == null) {
                ps.setNull(col++, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingClinicalEvent.getEventEndDtTm().getTime()));
            }

            if (stagingClinicalEvent.getClinicallySignificantDtTm() == null) {
                ps.setNull(col++, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingClinicalEvent.getClinicallySignificantDtTm().getTime()));
            }

            if (stagingClinicalEvent.getEventClassCd() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else{
                ps.setInt(col++, stagingClinicalEvent.getEventClassCd());
            }

            if (stagingClinicalEvent.getLookupEventClass() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getLookupEventClass());
            }

            if (stagingClinicalEvent.getEventResultStatusCd() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingClinicalEvent.getEventResultStatusCd());
            }

            if (stagingClinicalEvent.getLookupEventResultStatus() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getLookupEventResultStatus());
            }

            if (stagingClinicalEvent.getEventResultTxt() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getEventResultTxt());
            }

            if (stagingClinicalEvent.getEventResultNbr() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingClinicalEvent.getEventResultNbr());
            }

            if (stagingClinicalEvent.getEventResultDt() == null) {
                ps.setNull(col++, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingClinicalEvent.getEventResultDt().getTime()));
            }

            if (stagingClinicalEvent.getNormalcyCd() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingClinicalEvent.getNormalcyCd());
            }

            if (stagingClinicalEvent.getLookupNormalcy() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getLookupNormalcy());
            }

            if (stagingClinicalEvent.getNormalRangeLowTxt() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getNormalRangeLowTxt());
            }

            if (stagingClinicalEvent.getNormalRangeHighTxt() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getNormalRangeHighTxt());
            }

            if (stagingClinicalEvent.getEventPerformedDtTm() == null) {
                ps.setNull(col++, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(stagingClinicalEvent.getEventPerformedDtTm().getTime()));
            }

            if (stagingClinicalEvent.getEventPerformedPrsnlId() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingClinicalEvent.getEventPerformedPrsnlId());
            }

            if (stagingClinicalEvent.getEventTag() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getEventTag());
            }

            if (stagingClinicalEvent.getEventTitleTxt() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getEventTitleTxt());
            }

            if (stagingClinicalEvent.getEventResultUnitsCd() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingClinicalEvent.getEventResultUnitsCd());
            }

            if (stagingClinicalEvent.getLookupEventResultsUnitsCode() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getLookupEventResultsUnitsCode());
            }

            if (stagingClinicalEvent.getRecordStatusCd() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, stagingClinicalEvent.getRecordStatusCd());
            }

            if (stagingClinicalEvent.getLookupRecordStatusCode() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getLookupRecordStatusCode());
            }

            if (stagingClinicalEvent.getLookupMrn() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getLookupMrn());
            }

            if (stagingClinicalEvent.getAuditJson() == null) {
                ps.setNull(col++, Types.VARCHAR);
            } else {
                ps.setString(col++, stagingClinicalEvent.getAuditJson().writeToJson());
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
