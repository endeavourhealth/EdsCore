package org.endeavourhealth.core.database.rdbms.audit;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.audit.ExchangeBatchDalI;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeBatch;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RdbmsExchangeBatchDal implements ExchangeBatchDalI {

    @Override
    public List<ExchangeBatch> retrieveForExchangeId(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;
        try {

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT batch_id, inserted_at, eds_patient_id"
                    + " FROM exchange_batch"
                    + " WHERE exchange_id = ?"
                    + " ORDER BY inserted_at ASC, eds_patient_id"; //include patient ID in the sorting so if the admin batch has the same time, it is still first

            ps = connection.prepareStatement(sql);

            ps.setString(1, exchangeId.toString());

            ResultSet rs = ps.executeQuery();
            return readFromResultSet(rs, exchangeId);

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public ExchangeBatch retrieveFirstForExchangeId(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;
        try {

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT batch_id, inserted_at, eds_patient_id"
                    + " FROM exchange_batch"
                    + " WHERE exchange_id = ?"
                    + " ORDER BY inserted_at ASC, eds_patient_id"
                    + " LIMIT 1";

            ps = connection.prepareStatement(sql);

            ps.setString(1, exchangeId.toString());

            ResultSet rs = ps.executeQuery();
            List<ExchangeBatch> l = readFromResultSet(rs, exchangeId);
            if (l.isEmpty()) {
                return null;
            } else {
                return l.get(0);
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public ExchangeBatch getForBatchId(UUID batchId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;
        try {

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT exchange_id, inserted_at, eds_patient_id"
                    + " FROM exchange_batch"
                    + " WHERE batch_id = ?"
                    + " LIMIT 1";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, batchId.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                col = 1;

                String exhangeIdStr = rs.getString(col++);
                java.sql.Timestamp ts = rs.getTimestamp(col++);
                String patientIdStr = rs.getString(col++);

                ExchangeBatch b = new ExchangeBatch();
                b.setExchangeId(UUID.fromString(exhangeIdStr));
                b.setBatchId(batchId);
                b.setInsertedAt(new Date(ts.getTime()));
                if (!Strings.isNullOrEmpty(patientIdStr)) {
                    b.setEdsPatientId(UUID.fromString(patientIdStr));
                }
                return b;

            } else {
                return null;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public ExchangeBatch getForExchangeAndBatchId(UUID exchangeId, UUID batchId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;
        try {

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT batch_id, inserted_at, eds_patient_id"
                    + " FROM exchange_batch"
                    + " WHERE exchange_id = ?"
                    + " AND batch_id = ?"
                    + " LIMIT 1";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, exchangeId.toString());
            ps.setString(col++, batchId.toString());

            ResultSet rs = ps.executeQuery();
            List<ExchangeBatch> l = readFromResultSet(rs, exchangeId);
            if (l.isEmpty()) {
                return null;
            } else {
                return l.get(0);
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }


    private static List<ExchangeBatch> readFromResultSet(ResultSet rs, UUID exchangeId) throws Exception{
        List<ExchangeBatch> ret = new ArrayList<>();

        while (rs.next()) {
            int col = 1;

            String batchIdStr = rs.getString(col++);
            java.sql.Timestamp ts = rs.getTimestamp(col++);
            String patientIdStr = rs.getString(col++);

            ExchangeBatch b = new ExchangeBatch();
            b.setExchangeId(exchangeId);
            b.setBatchId(UUID.fromString(batchIdStr));
            b.setInsertedAt(new Date(ts.getTime()));
            if (!Strings.isNullOrEmpty(patientIdStr)) {
                b.setEdsPatientId(UUID.fromString(patientIdStr));
            }

            ret.add(b);
        }

        return ret;
    }

    @Override
    public void save(List<ExchangeBatch> exchangeBatches) throws Exception {

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;
        try {
            entityManager.getTransaction().begin();

            //persist only works for initial inserts not updates, so changing to use upsert
            //entityManager.persist(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO exchange_batch"
                    + " (exchange_id, batch_id, inserted_at, eds_patient_id)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " eds_patient_id = VALUES(eds_patient_id)";

            ps = connection.prepareStatement(sql);

            for (ExchangeBatch exchangeBatch: exchangeBatches) {

                int col = 1;
                ps.setString(col++, exchangeBatch.getExchangeId().toString());
                ps.setString(col++, exchangeBatch.getBatchId().toString());
                ps.setTimestamp(col++, new java.sql.Timestamp(exchangeBatch.getInsertedAt().getTime()));
                if (exchangeBatch.getEdsPatientId() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, exchangeBatch.getEdsPatientId().toString());
                }

                ps.addBatch();
            }

            ps.executeBatch();

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
