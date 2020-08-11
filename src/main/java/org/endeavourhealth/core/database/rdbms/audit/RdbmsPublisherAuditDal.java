package org.endeavourhealth.core.database.rdbms.audit;

import org.endeavourhealth.core.database.dal.audit.ServicePublisherAuditDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RdbmsPublisherAuditDal implements ServicePublisherAuditDalI {

    @Override
    public Boolean getLatestDpaState(UUID serviceId) throws Exception {
        Connection conn = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT has_dpa"
                    + " FROM service_publisher_audit"
                    + " WHERE service_id = ?"
                    + " ORDER BY dt_changed DESC"
                    + " LIMIT 1";
            ps = conn.prepareStatement(sql);

            ps.setString(1, serviceId.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean hasDpa = rs.getBoolean(1);
                if (rs.wasNull()) {
                    return null;
                } else {
                    return new Boolean(hasDpa);
                }

            } else {
                return null;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            conn.close();
        }
    }

    @Override
    public void saveDpaState(UUID serviceId, boolean hasDpa) throws Exception {

        Connection conn = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO service_publisher_audit"
                    + " (service_id, dt_changed, has_dpa)"
                    + " VALUES"
                    + " (?, ?, ?)";
            ps = conn.prepareStatement(sql);

            ps.setString(1, serviceId.toString());
            ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
            ps.setBoolean(3, hasDpa);

            ps.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();

        } finally {
            if (ps != null) {
                ps.close();
            }
            conn.close();
        }
    }

    @Override
    public Map<Date, Boolean> getDpaHistory(UUID serviceId) throws Exception {
        Connection conn = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT dt_changed, has_dpa"
                    + " FROM service_publisher_audit"
                    + " WHERE service_id = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, serviceId.toString());

            Map<Date, Boolean> ret = new HashMap<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Date d = new java.util.Date(rs.getTimestamp(1).getTime());
                boolean hasDpa = rs.getBoolean(2);
                if (rs.wasNull()) {
                    ret.put(d, null);
                } else {
                    ret.put(d, Boolean.valueOf(hasDpa));
                }
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            conn.close();
        }

    }
}
