package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.PseudoIdDalI;
import org.endeavourhealth.core.database.dal.subscriberTransform.models.PseudoIdAudit;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

public class RdbmsPseudoIdDal implements PseudoIdDalI {

    private String subscriberConfigName = null;

    public RdbmsPseudoIdDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    @Override
    public void auditPseudoId(String saltName, TreeMap<String, String> keys, String pseudoId) throws Exception {
        PseudoIdAudit audit = new PseudoIdAudit(saltName, keys, pseudoId);
        List<PseudoIdAudit> l = new ArrayList<>();
        l.add(audit);
        auditPseudoIds(l);
    }

    @Override
    public void auditPseudoIds(List<PseudoIdAudit> audits) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO pseudo_id_audit (salt_key_name, source_values, pseudo_id)"
                    + " VALUES (?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " pseudo_id = VALUES(pseudo_id)";

            ps = connection.prepareStatement(sql);

            for (PseudoIdAudit audit: audits) {

                int col = 1;
                ps.setString(col++, audit.getSaltName());
                ps.setString(col++, audit.getKeysAsJson());
                ps.setString(col++, audit.getPseudoId());
                ps.addBatch();
            }

            ps.executeBatch();
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
    public void storePseudoIdOldWay(String patientId, String pseudoId) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {

            String sql = "INSERT INTO pseudo_id_map (patient_id, pseudo_id)"
                        + " VALUES (?, ?)"
                        + " ON DUPLICATE KEY UPDATE"
                        + " pseudo_id = VALUES(pseudo_id)";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, patientId);
            ps.setString(col++, pseudoId);
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
    public void saveSubscriberPseudoId(UUID patientId, long subscriberPatientId, String saltKeyName, String pseudoId) throws Exception {
        PseudoIdAudit audit = new PseudoIdAudit(saltKeyName, null, pseudoId);
        List<PseudoIdAudit> l = new ArrayList<>();
        l.add(audit);
        saveSubscriberPseudoIds(patientId, subscriberPatientId, l);
    }

    @Override
    public void saveSubscriberPseudoIds(UUID patientId, long subscriberPatientId, List<PseudoIdAudit> audits) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO subscriber_pseudo_id_map (patient_id, subscriber_patient_id, salt_key_name, pseudo_id)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " pseudo_id = VALUES(pseudo_id)";

            ps = connection.prepareStatement(sql);

            for (PseudoIdAudit audit: audits) {

                int col = 1;
                ps.setString(col++, patientId.toString());
                ps.setLong(col++, subscriberPatientId);
                ps.setString(col++, audit.getSaltName());
                ps.setString(col++, audit.getPseudoId());
                ps.addBatch();
            }

            ps.executeBatch();
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
    public String findSubscriberPseudoId(UUID patientId, String saltKeyName) throws Exception {

        Connection connection = ConnectionManager.getSubscriberTransformConnection(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            String sql = "SELECT pseudo_id"
                    + " FROM subscriber_pseudo_id_map"
                    + " WHERE patient_id = ? AND salt_key_name = ?";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, patientId.toString());
            ps.setString(col++, saltKeyName);

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
}
