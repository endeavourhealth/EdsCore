package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.SubscriberPatientDateDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;
import java.util.UUID;

public class RdbmsSubscriberPatientDateDal implements SubscriberPatientDateDalI {

    @Override
    public Date getDateLastTransformedPatient(String subscriberConfigName, UUID patientId) throws Exception {
        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            String sql = "SELECT dt_version"
                    + " FROM patient_version_transformed"
                    + " WHERE patient_id = ?"
                    + " AND subscriber_config_name = ?";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, patientId.toString());
            ps.setString(col++, subscriberConfigName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                java.sql.Timestamp ts = rs.getTimestamp(1);
                if (rs.wasNull()) {
                    return null;
                } else {
                    return new java.util.Date(ts.getTime());
                }

            } else {
                return null;

                /*//if not found in the proper table, check the subscriber ID map table(s) which is where this
                //concept used to be stored
                Date d = getDateLastTransformedPatientFromOldTable(connection, "subscriber_id_map", patientId);
                if (d == null) {
                    d = getDateLastTransformedPatientFromOldTable(connection, "subscriber_id_map_3", patientId);
                }
                return d;*/
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    /*private static Date getDateLastTransformedPatientFromOldTable(Connection connection, String tableName, UUID patientId) throws Exception {

        PreparedStatement ps = null;
        try {
            String sql = "SELECT dt_previously_sent"
                    + " FROM " + tableName
                    + " WHERE source_id = ?"
                    + " AND subscriber_table = ?";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, ReferenceHelper.createResourceReference(ResourceType.Patient, patientId.toString()));
            ps.setInt(col++, 2); //2 = patient table

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                java.sql.Timestamp ts = rs.getTimestamp(1);
                if (rs.wasNull()) {
                    return null;
                } else {
                    return new java.util.Date(ts.getTime());
                }

            } else {
                return null;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }*/

    @Override
    public void saveDateLastTransformedPatient(String subscriberConfigName, UUID patientId, long subscriberId, Date dtVersion) throws Exception {
        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO patient_version_transformed (patient_id, subscriber_config_name, subscriber_id, dt_version)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " subscriber_id = VALUES(subscriber_id),"
                    + " dt_version = VALUES(dt_version)";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, patientId.toString());
            ps.setString(col++, subscriberConfigName);
            ps.setLong(col++, subscriberId);
            if (dtVersion == null) {
                ps.setNull(col++, Types.TIMESTAMP);

            } else {
                ps.setTimestamp(col++, new java.sql.Timestamp(dtVersion.getTime()));
            }

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
