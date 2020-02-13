package org.endeavourhealth.core.database.rdbms.publisherCommon;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherCommon.TppMultiLexToCtv3MapDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppMultiLexToCtv3Map;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.DeadlockHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RdbmsTppMultiLexToCtv3MapDal implements TppMultiLexToCtv3MapDalI {

    @Override
    public TppMultiLexToCtv3Map getMultiLexToCTV3Map(long multiLexProductId) throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT row_id, multilex_product_id, ctv3_read_code, ctv3_read_term, audit_json"
                    + " FROM tpp_multilex_to_ctv3_map"
                    + " WHERE multilex_product_id = ?"
                    + " LIMIT 1";
            ps = connection.prepareStatement(sql);

            ps.setLong(1, multiLexProductId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int col = 1;

                TppMultiLexToCtv3Map ret = new TppMultiLexToCtv3Map();
                ret.setRowId(rs.getLong(col++));
                ret.setMultiLexProductId(rs.getLong(col++));
                ret.setCtv3ReadCode(rs.getString(col++));
                ret.setCtv3ReadTerm(rs.getString(col++));
                String auditJson = rs.getString(col++);
                if (!Strings.isNullOrEmpty(auditJson)) {
                    ret.setAudit(ResourceFieldMappingAudit.readFromJson(auditJson));
                }

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
    public void save(TppMultiLexToCtv3Map mapping) throws Exception {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        List<TppMultiLexToCtv3Map> l = new ArrayList<>();
        l.add(mapping);
        save(l);
    }

    @Override
    public void save(List<TppMultiLexToCtv3Map> mappings) throws Exception {
        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalArgumentException("mappings is null or empty");
        }

        DeadlockHandler h = new DeadlockHandler();
        while (true) {
            try {
                trySaveCodeMappings(mappings);
                break;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    private void trySaveCodeMappings(List<TppMultiLexToCtv3Map> mappings) throws Exception {

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO tpp_multilex_to_ctv3_map "
                    + " (row_id, multilex_product_id, ctv3_read_code, ctv3_read_term, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " multilex_product_id = VALUES(multilex_product_id),"
                    + " ctv3_read_code = VALUES(ctv3_read_code),"
                    + " ctv3_read_term = VALUES(ctv3_read_term),"
                    + " audit_json = VALUES(audit_json)";
            ps = connection.prepareStatement(sql);

            for (TppMultiLexToCtv3Map mapping: mappings) {

                int col = 1;

                ps.setLong(col++, mapping.getRowId());
                ps.setLong(col++, mapping.getMultiLexProductId());
                ps.setString(col++, mapping.getCtv3ReadCode());
                ps.setString(col++, mapping.getCtv3ReadTerm());
                ResourceFieldMappingAudit audit = mapping.getAudit();
                if (audit != null) {
                    ps.setString(col++, audit.writeToJson());
                } else {
                    ps.setNull(col++, Types.VARCHAR);
                }

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
}
