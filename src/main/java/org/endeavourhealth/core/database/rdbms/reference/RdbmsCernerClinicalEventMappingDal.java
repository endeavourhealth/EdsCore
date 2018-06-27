package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.CernerClinicalEventMappingDalI;
import org.endeavourhealth.core.database.dal.reference.models.CernerClinicalEventMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RdbmsCernerClinicalEventMappingDal implements CernerClinicalEventMappingDalI {


    @Override
    public CernerClinicalEventMap findMappingForCvrefCode(Long cvrefCode) throws Exception {

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();
        PreparedStatement ps = null;

        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            //primary key (service_id, nomenclature_id)
            String sql = "SELECT * FROM cerner_clinical_event_map WHERE cerner_cvref_code = ?;";

            ps = connection.prepareStatement(sql);
            ps.setString(1, cvrefCode.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

                int col = 1;
                CernerClinicalEventMap ret = new CernerClinicalEventMap();
                ret.setCernerCvrefCode(rs.getString(col++));
                ret.setCernerCvrefTerm(rs.getString(col++));
                ret.setSnomedConceptId(rs.getString(col++));
                ret.setSnomedPreferredTerm(rs.getString(col++));
                ret.setSnomedDescriptionId(rs.getString(col++));
                ret.setSnomedDescriptionTerm(rs.getString(col++));
                ret.setMatchAlgorithm(rs.getString(col++));
                return ret;
            }

            return null;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }

    }
}
