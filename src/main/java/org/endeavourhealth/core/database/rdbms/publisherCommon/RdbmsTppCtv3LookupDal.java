package org.endeavourhealth.core.database.rdbms.publisherCommon;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherCommon.TppCtv3LookupDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppCtv3Lookup;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RdbmsTppCtv3LookupDal implements TppCtv3LookupDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppCtv3LookupDal.class);

    @Override
    public TppCtv3Lookup getContentFromCtv3Code(String ctv3Code) throws Exception {
        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT ctv3_code, ctv3_text, audit_json"
                    + " FROM tpp_ctv3_lookup "
                    + " WHERE ctv3_code = ?";

            ps = connection.prepareStatement(sql);
            ps.setString(1, ctv3Code);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int col = 1;
                String code = rs.getString(col++);
                String term = rs.getString(col++);
                String auditJson = rs.getString(col++);

                TppCtv3Lookup ret = new TppCtv3Lookup();
                ret.setCtv3Code(code);
                ret.setCtv3Text(term);
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
    public void save(TppCtv3Lookup ctv3Lookup) throws Exception {
        if (ctv3Lookup == null) {
            throw new IllegalArgumentException("ctv3 lookup is null");
        }

        List<TppCtv3Lookup> l = new ArrayList<>();
        l.add(ctv3Lookup);
        save(l);
    }

    @Override
    public void save(List<TppCtv3Lookup> ctv3Lookups) throws Exception {
        if (ctv3Lookups == null || ctv3Lookups.isEmpty()) {
            throw new IllegalArgumentException("ctv3 lookup is null or empty");
        }

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement ps = null;
        try {

            String sql = "INSERT INTO tpp_ctv3_lookup "
                    + " (ctv3_code, ctv3_text, audit_json)"
                    + " VALUES (?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " ctv3_text = VALUES(ctv3_text),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            for (TppCtv3Lookup lookup : ctv3Lookups) {

                int col = 1;
                // Only JSON audit field is nullable
                ps.setString(col++, lookup.getCtv3Code());
                ps.setString(col++, lookup.getCtv3Text());
                if (lookup.getAudit() == null) {
                    ps.setNull(col++, Types.VARCHAR);
                } else {
                    ps.setString(col++, lookup.getAudit().writeToJson());
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
