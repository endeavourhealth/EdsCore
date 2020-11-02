package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.Read2ToSnomedMapDalI;
import org.endeavourhealth.core.database.dal.reference.models.Read2ToSnomedMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.terminology.Read2Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RdbmsRead2ToSnomedMapDal implements Read2ToSnomedMapDalI {

    @Override
    public Read2Code getRead2Code(String readCode) throws Exception {
        Set<String> s = new HashSet<>();
        s.add(readCode);
        Map<String, Read2Code> map = getRead2Codes(s);
        return map.get(readCode);

    }

    @Override
    public Map<String, Read2Code> getRead2Codes(Collection<String> readCodes) throws Exception {
        Connection connection = ConnectionManager.getReferenceConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT read_code, preferred_term"
                    + " FROM read2_lookup "
                    + " WHERE read_code IN (";
            for (int i=0; i<readCodes.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;
            for (String readCode: readCodes) {
                ps.setString(col++, readCode);
            }

            Map<String, Read2Code> ret = new HashMap<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                col = 1;
                String code = rs.getString(col++);
                String term = rs.getString(col++);
                Read2Code c = new Read2Code(code, term);
                ret.put(code, c);
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
    public Read2ToSnomedMap getRead2ToSnomedMap(String readCode) throws Exception {
        Connection connection = ConnectionManager.getReferenceConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT map_id, read_code, term_code, concept_id, effective_date, map_status"
                    + " FROM read2_to_snomed_map"
                    + " WHERE read_code = ?"
                    + " ORDER BY term_code, effective_date DESC"
                    + " LIMIT 1";
            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, readCode);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                col = 1;

                Read2ToSnomedMap ret = new Read2ToSnomedMap();
                ret.setMapId(rs.getString(col++));
                ret.setReadCode(rs.getString(col++));
                ret.setTermCode(rs.getString(col++));
                ret.setConceptId(rs.getString(col++));
                ret.setEffectiveDate(new java.util.Date(rs.getTimestamp(col++).getTime()));
                ret.setMapStatus(rs.getInt(col++));

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
}