package org.endeavourhealth.core.database.rdbms.audit;

import com.google.common.base.Strings;
import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.core.database.dal.audit.SimplePropertyDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RdbmsSimplePropertyDal implements SimplePropertyDalI {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss SSS";

    @Override
    public void savePropertyString(String propertyName, String propertyValue) throws Exception {

        if (Strings.isNullOrEmpty(propertyName)) {
            throw new Exception("Null or empty property name");
        }

        String appId = ConfigManager.getAppId();
        String appInstance = ConfigManager.getAppSubId();

        //don't allow nulls, so just swap to empty String
        if (appInstance == null) {
            appInstance = "";
        }

        Connection connection = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        try {
            if (propertyValue != null) {
                String sql = "REPLACE INTO simple_property"
                        + " (application_name, application_instance_name, property_name, property_value)"
                        + "VALUES (?, ?, ?, ?)";
                ps = connection.prepareStatement(sql);

                int col = 1;
                ps.setString(col++, appId);
                ps.setString(col++, appInstance);
                ps.setString(col++, propertyName);
                ps.setString(col++, propertyValue);

            } else {
                String sql = "DELETE FROM simple_property"
                        + " WHERE application_name = ?"
                        + " AND application_instance_name = ?";
                ps = connection.prepareStatement(sql);

                int col = 1;
                ps.setString(col++, appId);
                ps.setString(col++, appInstance);
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
    public void savePropertyBoolean(String propertyName, Boolean propertyValue) throws Exception {
        if (propertyValue == null) {
            savePropertyString(propertyName, null);
        } else {
            savePropertyString(propertyName, propertyValue.toString());
        }
    }

    @Override
    public void savePropertyDate(String propertyName, Date propertyValue) throws Exception {
        if (propertyValue == null) {
            savePropertyString(propertyName, null);
        } else {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            savePropertyString(propertyName, df.format(propertyValue));
        }
    }

    @Override
    public void savePropertyDouble(String propertyName, Double propertyValue) throws Exception {
        if (propertyValue == null) {
            savePropertyString(propertyName, null);
        } else {
            savePropertyString(propertyName, propertyValue.toString());
        }
    }

    @Override
    public void savePropertyLong(String propertyName, Long propertyValue) throws Exception {
        if (propertyValue == null) {
            savePropertyString(propertyName, null);
        } else {
            savePropertyString(propertyName, propertyValue.toString());
        }
    }

    @Override
    public String getPropertyString(String propertyName) throws Exception {
        if (Strings.isNullOrEmpty(propertyName)) {
            throw new Exception("Null or empty property name");
        }

        String appId = ConfigManager.getAppId();
        String appInstance = ConfigManager.getAppSubId();

        //don't allow nulls, so just swap to empty String
        if (appInstance == null) {
            appInstance = "";
        }

        Connection connection = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT property_value"
                    + " FROM simple_property"
                    + " WHERE application_name = ?"
                    + " AND application_instance_name = ?";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, appId);
            ps.setString(col++, appInstance);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);

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
    public Boolean getPropertyBoolean(String propertyName) throws Exception {
        String s = getPropertyString(propertyName);
        if (s == null) {
            return null;
        } else {
            return Boolean.valueOf(s);
        }
    }

    @Override
    public Date getPropertyDate(String propertyName) throws Exception {
        String s = getPropertyString(propertyName);
        if (s == null) {
            return null;
        } else {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            return df.parse(s);
        }
    }

    @Override
    public Double getPropertyDouble(String propertyName) throws Exception {
        String s = getPropertyString(propertyName);
        if (s == null) {
            return null;
        } else {
            return Double.valueOf(s);
        }
    }

    @Override
    public Long getPropertyLong(String propertyName) throws Exception {
        String s = getPropertyString(propertyName);
        if (s == null) {
            return null;
        } else {
            return Long.valueOf(s);
        }
    }
}
