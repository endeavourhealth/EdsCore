package org.endeavourhealth.core.database.rdbms.reference;

import com.microsoft.sqlserver.jdbc.StringUtils;
import org.endeavourhealth.core.database.dal.reference.CernerProcedureMapDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CernerProcedureMapDal implements CernerProcedureMapDalI {
//TODO Quick hack to enable us to close DAB-117 to map Barts SURCP proc codes as best we can. Needs an IM solution
    @Override
    public Long getSnomedFromCernerProc(Integer cernerProc) throws Exception {


        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();
        PreparedStatement ps = null;

        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();


            String sql = "SELECT target_concept FROM cerner_procedures_map WHERE original_code = ?";

            ps = connection.prepareStatement(sql);
            String originalCode="BC_" + cernerProc.toString(); //Prefix for Barts Cerner codes
            ps.setString(1, originalCode);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String target = rs.getString(1);
                if (target.startsWith("SN_")) {  //Check it's snomed
                    String snomedStr = target.substring(3);
                    if (StringUtils.isNumeric(snomedStr)) { // We need this for enterprise which expects a Long.
                        return Long.parseLong((snomedStr));
                    }
                }
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
