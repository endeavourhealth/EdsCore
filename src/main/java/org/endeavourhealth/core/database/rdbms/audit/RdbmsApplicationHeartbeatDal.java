package org.endeavourhealth.core.database.rdbms.audit;

import org.endeavourhealth.core.database.dal.audit.ApplicationHeartbeatDalI;
import org.endeavourhealth.core.database.dal.audit.models.ApplicationHeartbeat;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RdbmsApplicationHeartbeatDal implements ApplicationHeartbeatDalI {

    @Override
    public void saveHeartbeat(ApplicationHeartbeat h) throws Exception {
        Connection connection = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO application_heartbeat"
                    + " (application_name, application_instance_name, timestmp, host_name, is_busy, max_heap_mb, current_heap_mb)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " timestmp = VALUES(timestmp),"
                    + " host_name = VALUES(host_name),"
                    + " is_busy = VALUES(is_busy),"
                    + " max_heap_mb = VALUES(max_heap_mb),"
                    + " current_heap_mb = VALUES(current_heap_mb)";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, h.getApplicationName());
            ps.setString(col++, h.getApplicationInstanceName());
            ps.setTimestamp(col++, new java.sql.Timestamp(h.getTimestmp().getTime()));
            ps.setString(col++, h.getHostName());
            if (h.getBusy() == null) {
                ps.setNull(col++, Types.BOOLEAN);
            } else {
                ps.setBoolean(col++, h.getBusy().booleanValue());
            }
            if (h.getMaxHeapMb() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, h.getMaxHeapMb().intValue());
            }
            if (h.getCurrentHeapMb() == null) {
                ps.setNull(col++, Types.INTEGER);
            } else {
                ps.setInt(col++, h.getCurrentHeapMb().intValue());
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

    @Override
    public List<ApplicationHeartbeat> getLatest() throws Exception {
        Connection connection = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT application_name, application_instance_name, timestmp, host_name, is_busy, max_heap_mb, current_heap_mb"
                    + " FROM application_heartbeat";
            ps = connection.prepareStatement(sql);

            List<ApplicationHeartbeat> ret = new ArrayList<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ApplicationHeartbeat h = new ApplicationHeartbeat();

                int col = 1;
                h.setApplicationName(rs.getString(col++));
                h.setApplicationInstanceName(rs.getString(col++));
                h.setTimestmp(new java.util.Date(rs.getTimestamp(col++).getTime()));
                h.setHostName(rs.getString(col++));

                boolean b = rs.getBoolean(col++);
                if (!rs.wasNull()) {
                    h.setBusy(new Boolean(b));
                }

                int i = rs.getInt(col++);
                if (!rs.wasNull()) {
                    h.setMaxHeapMb(new Integer(i));
                }

                i = rs.getInt(col++);
                if (!rs.wasNull()) {
                    h.setCurrentHeapMb(new Integer(i));
                }

                ret.add(h);
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }
}
