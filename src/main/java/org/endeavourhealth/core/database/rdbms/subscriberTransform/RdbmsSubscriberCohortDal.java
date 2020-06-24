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
    public SubscriberCohortRecord getCohortRecord(String subscriberConfigName, UUID patientId) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            String sql = "SELECT service_id, in_cohort, reason, dt_updated"
                    + " FROM subscriber_cohort"
                    + " WHERE subscriber_config_name = ?"
                    + " AND patient_id = ?"
                    + " ORDER BY dt_updated DESC"
                    + " LIMIT 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, subscriberConfigName);
            ps.setString(col++, patientId.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                col = 1;
                String serviceIdStr = rs.getString(col++);
                boolean inCohort = rs.getBoolean(col++);
                String reason = rs.getString(col++);
                Date dt = new java.util.Date(rs.getTimestamp(col++).getTime());

                SubscriberCohortRecord ret = new SubscriberCohortRecord(subscriberConfigName, patientId);
                ret.setServiceId(UUID.fromString(serviceIdStr));
                ret.setInCohort(inCohort);
                ret.setReason(reason);
                ret.setDtUpdated(dt);
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
    public void saveCohortRecord(SubscriberCohortRecord record) throws Exception {
        String subscriberConfigName = record.getSubscriberConfigName();
        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO subscriber_cohort"
                    + " (patient_id, subscriber_config_name, service_id, in_cohort, reason, dt_updated)"
                    + " VALUES (?, ?, ?, ?, ?, ?)";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, record.getPatientId().toString());
            ps.setString(col++, record.getSubscriberConfigName());
            ps.setString(col++, record.getServiceId().toString());
            ps.setBoolean(col++, record.isInCohort());
            ps.setString(col++, record.getReason());
            ps.setTimestamp(col++, new java.sql.Timestamp(record.getDtUpdated().getTime()));
LOG.debug("Saving with timestamp " + record.getDtUpdated().getTime());
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
}
