package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.SnomedDalI;
import org.endeavourhealth.core.database.dal.reference.models.SnomedLookup;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RdbmsSnomedDal implements SnomedDalI {

    public SnomedLookup getSnomedLookup(String conceptId) throws Exception {
        Set<String> s = new HashSet<>();
        s.add(conceptId);
        Map<String, SnomedLookup> map = getSnomedLookups(s);
        return map.get(conceptId);
    }

    @Override
    public Map<String, SnomedLookup> getSnomedLookups(Collection<String> conceptIds) throws Exception {

        if (conceptIds == null || conceptIds.isEmpty()) {
            return new HashMap<>();
        }

        Connection connection = ConnectionManager.getReferenceConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT concept_id, type_id, term"
                    + " FROM snomed_lookup"
                    + " WHERE concept_id IN (";
            for (int i=0; i<conceptIds.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;
            for (String conceptId: conceptIds) {
                ps.setString(col++, conceptId);
            }

            Map<String, SnomedLookup> ret = new HashMap<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                col = 1;
                SnomedLookup l = new SnomedLookup();
                l.setConceptId(rs.getString(col++));
                l.setTypeId(rs.getString(col++));
                l.setTerm(rs.getString(col++));
                ret.put(l.getConceptId(), l);
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }


    }

    public SnomedLookup getSnomedLookupForDescId(String descriptionId) throws Exception {

        Connection connection = ConnectionManager.getReferenceConnection();
        PreparedStatement ps = null;
        try {
            String sql = "SELECT l.concept_id, l.type_id, l.term"
                    + " FROM snomed_lookup l"
                    + " INNER JOIN snomed_description_link d"
                    + " ON l.concept_id = d.concept_id"
                    + " WHERE d.description_id = ?";
            ps = connection.prepareStatement(sql);

            ps.setString(1, descriptionId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int col = 1;
                SnomedLookup l = new SnomedLookup();
                l.setConceptId(rs.getString(col++));
                l.setTypeId(rs.getString(col++));
                l.setTerm(rs.getString(col++));
                return l;

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
    public void saveSnomedDescriptionToConceptMappings(Map<String, String> mappings) throws Exception {
        Connection connection = ConnectionManager.getReferenceConnection();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT IGNORE INTO snomed_description_link "
                    + " (description_id, concept_id)"
                    + " VALUES (?, ?)";
            ps = connection.prepareStatement(sql);

            for (String descId : mappings.keySet()) {
                String conceptId = mappings.get(descId);

                int col = 1;
                ps.setString(col++, descId);
                ps.setString(col++, conceptId);

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
    public void saveSnomedConcepts(List<SnomedLookup> lookups) throws Exception {
        Connection connection = ConnectionManager.getReferenceConnection();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO snomed_lookup "
                    + " (concept_id, type_id, term)"
                    + " VALUES (?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " type_id = VALUES(type_id),"
                    + " term = VALUES(term)";
            ps = connection.prepareStatement(sql);

            for (SnomedLookup lookup: lookups) {

                int col = 1;
                ps.setString(col++, lookup.getConceptId());
                ps.setString(col++, lookup.getTypeId());
                ps.setString(col++, lookup.getTerm());

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
