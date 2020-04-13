package org.endeavourhealth.core.database.rdbms.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.EmisMissingCodeDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisCodeType;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisMissingCodes;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RdbmsEmisMissingCodeDal implements EmisMissingCodeDalI {


    @Override
    public void saveMissingCodeError(EmisMissingCodes errorCodeVals) throws Exception {

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement psInsert = null;
        try {
            Date now = new Date();

            //use insert ignore so that if we re-process the same file (without any fixed data)
            //we don't have any issues when it tries to log the error again
            String sql = "INSERT IGNORE INTO emis_missing_code_error"
                    + " (service_id, exchange_id, timestmp, file_type, patient_guid, code_id, record_guid, dt_fixed, code_type)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

            psInsert = connection.prepareStatement(sql);
            int col = 1;
            psInsert.setString(col++, errorCodeVals.getServiceId().toString());
            psInsert.setString(col++, errorCodeVals.getExchangeId().toString());
            psInsert.setTimestamp(col++, new java.sql.Timestamp(now.getTime()));
            psInsert.setString(col++, errorCodeVals.getFileType());
            psInsert.setString(col++, errorCodeVals.getPatientGuid());
            psInsert.setLong(col++, errorCodeVals.getCodeId());
            psInsert.setString(col++, errorCodeVals.getRecordGuid());
            psInsert.setTimestamp(col++, null);
            psInsert.setString(col++, errorCodeVals.getCodeType().getCodeValue());

            psInsert.executeUpdate();
            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (psInsert != null) {
                psInsert.close();
            }
            connection.close();
        }
    }

    @Override
    public Set<Long> retrieveMissingCodes(EmisCodeType emisCodeType, UUID serviceId) throws Exception {

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;

        try {
            String sql = "SELECT DISTINCT code_id"
                    + " FROM emis_missing_code_error"
                    + " WHERE dt_fixed IS NULL AND code_type= ? AND service_id= ?";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, emisCodeType.getCodeValue());
            ps.setString(col++, serviceId.toString());

            Set<Long> ret = new HashSet<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long codeId = rs.getLong(1);
                ret.add(new Long(codeId));
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    @Override
    public Set<String> retrievePatientGuidsForMissingCodes(Set<Long> hsEmisMissingCodes, UUID serviceId) throws Exception {

        List<Long> emisMissingCodes = new ArrayList<>(hsEmisMissingCodes);

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT DISTINCT patient_guid"
                    + " FROM emis_missing_code_error"
                    + " WHERE service_id=?"
                    + " AND dt_fixed IS NULL"
                    + " AND code_id IN (";
            for (int i = 0; i < emisMissingCodes.size(); i++) {
                if (i > 0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, serviceId.toString());
            for (int i = 0; i < emisMissingCodes.size(); i++) {
                Long codeId = emisMissingCodes.get(i);
                ps.setLong(col++, codeId.longValue());
            }

            Set<String> ret = new HashSet<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String patientGuid = rs.getString(1);
                ret.add(patientGuid);
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    @Override
    public void setMissingCodesFixed(Set<Long> hsEmisMissingCodes, UUID serviceId) throws Exception {

        Date now = new Date();

        List<Long> emisCodeIds = new ArrayList<>(hsEmisMissingCodes);

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE emis_missing_code_error"
                    + " SET dt_fixed = ?"
                    + " WHERE service_id= ?"
                    + " AND dt_fixed IS NULL"
                    + " AND code_id IN (";
            for (int i = 0; i < emisCodeIds.size(); i++) {
                if (i > 0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setTimestamp(col++, new java.sql.Timestamp(now.getTime()));
            ps.setString(col++, serviceId.toString());

            for (int i = 0; i < emisCodeIds.size(); i++) {
                Long codeId = emisCodeIds.get(i);
                ps.setLong(col++, codeId.longValue());
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
    public UUID retrieveOldestExchangeIdForMissingCodes(Set<Long> hsEmisMissingCodes, UUID serviceId) throws Exception {

        List<Long> emisMissingCodes = new ArrayList<>(hsEmisMissingCodes);

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT exchange_id"
                    + " FROM emis_missing_code_error"
                    + " WHERE service_id = ?"
                    + " AND dt_fixed IS NULL"
                    + " AND code_id IN (";

            for (int i = 0; i < emisMissingCodes.size(); i++) {
                if (i > 0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")"
                    + " ORDER BY timestmp ASC"
                    + " LIMIT 1";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, serviceId.toString());
            for (int i = 0; i < emisMissingCodes.size(); i++) {
                Long codeId = emisMissingCodes.get(i);
                ps.setLong(col++, codeId.longValue());
            }

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                String s = rs.getString(1);
                return UUID.fromString(s);
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

}
