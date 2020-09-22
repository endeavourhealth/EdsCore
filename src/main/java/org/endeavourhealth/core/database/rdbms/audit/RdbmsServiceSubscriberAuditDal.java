package org.endeavourhealth.core.database.rdbms.audit;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.audit.ServiceSubscriberAuditDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.regex.Pattern;

public class RdbmsServiceSubscriberAuditDal implements ServiceSubscriberAuditDalI {

    private static final String DELIM = "|";

    @Override
    public List<String> getLatestSubscribers(UUID serviceId) throws Exception {
        Connection conn = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT subscriber_config_names"
                    + " FROM service_subscriber_audit"
                    + " WHERE service_id = ?"
                    + " ORDER BY dt_changed DESC"
                    + " LIMIT 1";
            ps = conn.prepareStatement(sql);

            ps.setString(1, serviceId.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String subscriberConfigNames = rs.getString(1);
                if (!Strings.isNullOrEmpty(subscriberConfigNames)) {
                    String[] toks = subscriberConfigNames.split(Pattern.quote(DELIM)); //use the Pattern.quote fn to make it NOT use regex
                    return new ArrayList<>(Arrays.asList(toks));
                }
            }

            return null;

        } finally {
            if (ps != null) {
                ps.close();
            }
            conn.close();
        }
    }

    @Override
    public void saveSubscribers(UUID serviceId, List<String> subscriberConfigNames) throws Exception {

        List<String> l = new ArrayList<>(subscriberConfigNames);
        //always re-sort so the table is consistent
        l.sort((a, b) -> a.compareToIgnoreCase(b));
        String subscriberConfigStr = String.join(DELIM, l);

        Connection conn = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO service_subscriber_audit"
                    + " (service_id, dt_changed, subscriber_config_names)"
                    + " VALUES"
                    + " (?, ?, ?)";
            ps = conn.prepareStatement(sql);

            ps.setString(1, serviceId.toString());
            ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
            ps.setString(3, subscriberConfigStr);

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
    public Map<Date, List<String>> getSubscriberHistory(UUID serviceId) throws Exception {

        Connection conn = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT dt_changed, subscriber_config_names"
                    + " FROM service_subscriber_audit"
                    + " WHERE service_id = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, serviceId.toString());

            Map<Date, List<String>> ret = new HashMap<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Date d = new java.util.Date(rs.getTimestamp(1).getTime());
                String subscriberConfigNames = rs.getString(2);

                if (Strings.isNullOrEmpty(subscriberConfigNames)) {
                    ret.put(d, new ArrayList<>());

                } else {
                    String[] toks = subscriberConfigNames.split(Pattern.quote(DELIM)); //use the Pattern.quote fn to make it NOT use regex
                    List<String> l = new ArrayList(Arrays.asList(toks));
                    ret.put(d, l);
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
