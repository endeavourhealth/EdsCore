package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.SnomedDalI;
import org.endeavourhealth.core.database.dal.reference.models.SnomedLookup;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsSnomedLookup;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public class RdbmsSnomedDal implements SnomedDalI {

    public SnomedLookup getSnomedLookup(String conceptId) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsSnomedLookup c"
                    + " where c.conceptId = :concept_id";

            Query query = entityManager.createQuery(sql, RdbmsSnomedLookup.class)
                    .setParameter("concept_id", conceptId);

            try {
                RdbmsSnomedLookup result = (RdbmsSnomedLookup) query.getSingleResult();
                return new SnomedLookup(result);

            } catch (NoResultException ex) {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    public SnomedLookup getSnomedLookupForDescId(String descriptionId) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsSnomedLookup c"
                    + " left join RdbmsSnomedDescriptionLink l"
                    + " on c.conceptId = l.conceptId"
                    + " where l.descriptionId = :description_id";

            Query query = entityManager.createQuery(sql, RdbmsSnomedLookup.class)
                    .setParameter("description_id", descriptionId);

            try {
                RdbmsSnomedLookup result = (RdbmsSnomedLookup) query.getSingleResult();
                return new SnomedLookup(result);

            } catch (NoResultException ex) {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void saveSnomedDescriptionToConceptMappings(Map<String, String> mappings) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "INSERT IGNORE INTO snomed_description_link "
                    + " (description_id, concept_id)"
                    + " VALUES (?, ?)";
            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (String descId : mappings.keySet()) {
                String conceptId = mappings.get(descId);

                int col = 1;
                ps.setString(col++, descId);
                ps.setString(col++, conceptId);

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

    @Override
    public void saveSnomedConcepts(List<SnomedLookup> lookups) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "INSERT INTO snomed_lookup "
                    + " (concept_id, type_id, term)"
                    + " VALUES (?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " type_id = VALUES(type_id),"
                    + " term = VALUES(term)";
            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (SnomedLookup lookup: lookups) {

                int col = 1;
                ps.setString(col++, lookup.getConceptId());
                ps.setString(col++, lookup.getTypeId());
                ps.setString(col++, lookup.getTerm());

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
