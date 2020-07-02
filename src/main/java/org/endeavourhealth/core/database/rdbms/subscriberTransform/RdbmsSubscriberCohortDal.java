package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.SubscriberCohortDalI;
import org.endeavourhealth.core.database.dal.subscriberTransform.models.SubscriberCohortRecord;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;

public class RdbmsSubscriberCohortDal implements SubscriberCohortDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsSubscriberCohortDal.class);


    @Override
    public void saveCohortRecord(SubscriberCohortRecord record) throws Exception {
        String subscriberConfigName = record.getSubscriberConfigName();
        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO subscriber_cohort"
                    + " (patient_id, subscriber_config_name, service_id, in_cohort, reason, dt_updated, batch_id_updated)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, record.getPatientId().toString());
            ps.setString(col++, record.getSubscriberConfigName());
            ps.setString(col++, record.getServiceId().toString());
            ps.setBoolean(col++, record.isInCohort());
            ps.setString(col++, record.getReason());
            ps.setTimestamp(col++, new java.sql.Timestamp(record.getDtUpdated().getTime()));
            ps.setString(col++, record.getBatchIdUpdated().toString());

            ps.executeUpdate();
            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    /**
     * gets the latest cohort record for the subscriber and patient, where it's NOT the supplied batch ID
     */
    @Override
    public SubscriberCohortRecord getLatestCohortRecord(String subscriberConfigName, UUID patientId, UUID excludeBatchId) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            String sql = "SELECT service_id, in_cohort, reason, dt_updated, batch_id_updated"
                    + " FROM subscriber_cohort"
                    + " WHERE subscriber_config_name = ?"
                    + " AND patient_id = ?"
                    + " AND batch_id_updated != ?"
                    + " ORDER BY dt_updated DESC"
                    + " LIMIT 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, subscriberConfigName);
            ps.setString(col++, patientId.toString());
            ps.setString(col++, excludeBatchId.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                col = 1;
                String serviceIdStr = rs.getString(col++);
                boolean inCohort = rs.getBoolean(col++);
                String reason = rs.getString(col++);
                Date dt = new java.util.Date(rs.getTimestamp(col++).getTime());
                String batchIdStr = rs.getString(col++);
                UUID serviceId = UUID.fromString(serviceIdStr);
                UUID batchId = UUID.fromString(batchIdStr);

                SubscriberCohortRecord ret = new SubscriberCohortRecord(subscriberConfigName, serviceId, batchId, dt, patientId);
                ret.setInCohort(inCohort);
                ret.setReason(reason);
                return ret;

            } else {
                return null;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    @Override
    public boolean wasEverInCohort(String subscriberConfigName, UUID patientId) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            String sql = "SELECT 1"
                    + " FROM subscriber_cohort"
                    + " WHERE subscriber_config_name = ?"
                    + " AND patient_id = ?"
                    + " AND in_cohort = ?"
                    + " LIMIT 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, subscriberConfigName);
            ps.setString(col++, patientId.toString());
            ps.setBoolean(col++, true);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;

            } else {
                return false;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    @Override
    public void saveInExplicitCohort(String subscriberConfigName, String nhsNumber, boolean inCohort) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO explicit_cohort_patient"
                    + " (subscriber_config_name, nhs_number, dt_updated, in_cohort)"
                    + " VALUES (?, ?, ?, ?)";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, subscriberConfigName);
            ps.setString(col++, nhsNumber);
            ps.setTimestamp(col++, new java.sql.Timestamp(new Date().getTime()));
            ps.setBoolean(col++, inCohort);

            ps.executeUpdate();
            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    @Override
    public boolean isInExplicitCohort(String subscriberConfigName, String nhsNumber) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            String sql = "SELECT in_cohort"
                    + " FROM explicit_cohort_patient"
                    + " WHERE subscriber_config_name = ?"
                    + " AND nhs_number = ?"
                    + " ORDER BY dt_updated DESC"
                    + " LIMIT 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, subscriberConfigName);
            ps.setString(col++, nhsNumber.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                col = 1;
                boolean inCohort = rs.getBoolean(col++);
                return inCohort;

            } else {
                return false;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

}
