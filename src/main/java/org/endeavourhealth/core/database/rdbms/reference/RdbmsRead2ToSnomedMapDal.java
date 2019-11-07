package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.Read2ToSnomedMapDalI;
import org.endeavourhealth.core.database.dal.reference.models.Read2ToSnomedMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsRead2ToSnomedMap;
import org.endeavourhealth.core.terminology.Read2Code;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class RdbmsRead2ToSnomedMapDal implements Read2ToSnomedMapDalI {

    @Override
    public Read2Code getRead2Code(String readCode) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "select read_code, preferred_term"
                    + " from"
                    + " read2_lookup "
                    + " where read_code = ?";
            ps = connection.prepareStatement(sql);

            ps.setString(1, readCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String code = rs.getString(1);
                String term = rs.getString(2);
                return new Read2Code(code, term);

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
    public Read2ToSnomedMap getRead2ToSnomedMap(String readCode) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsRead2ToSnomedMap c"
                    + " where c.readCode = :read_code"
                    + " order by c.termCode asc, c.effectiveDate desc";

            Query query = entityManager.createQuery(sql, RdbmsRead2ToSnomedMap.class)
                    .setParameter("read_code", readCode);

            try {
                List<RdbmsRead2ToSnomedMap> result = (List<RdbmsRead2ToSnomedMap>) query.getResultList();

                if (result.size() > 0) {
                    return new Read2ToSnomedMap(result.get(0));
                } else {
                    return null;
                }
            } catch (NoResultException ex) {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }
}