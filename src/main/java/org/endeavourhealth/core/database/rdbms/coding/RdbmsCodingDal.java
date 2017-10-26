package org.endeavourhealth.core.database.rdbms.coding;

import org.endeavourhealth.core.database.dal.coding.CodingDalI;
import org.endeavourhealth.core.database.dal.coding.models.Concept;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.coding.models.RdbmsConcept;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

public class RdbmsCodingDal implements CodingDalI {

    public List<Concept> search(String term, int maxResultsSize, int start) throws Exception {
        EntityManager entityManager = ConnectionManager.getCodingEntityManager();

        String sql = "select c" +
                " from " +
                "    RdbmsConcept c" +
                " where" +
                "    c.display like :term " +
                " order by "+
                "    length(c.display) ";

        Query query = entityManager.createQuery(sql, RdbmsConcept.class)
                .setParameter("term", "%"+term+"%")
                .setFirstResult(start * maxResultsSize)
                .setMaxResults(maxResultsSize);

        List<RdbmsConcept> ret = query.getResultList();

        entityManager.close();

        return ret
                .stream()
                .map(T -> new Concept(T))
                .collect(Collectors.toList());
    }

    public Concept getConcept(String code) throws Exception {
        EntityManager entityManager = ConnectionManager.getCodingEntityManager();

        String sql = "select c" +
                " from " +
                "    RdbmsConcept c" +
                " where" +
                "    c.code = :code ";

        Query query = entityManager.createQuery(sql, RdbmsConcept.class)
                .setParameter("code", code);

        List<RdbmsConcept> ret = query.getResultList();

        entityManager.close();

        if (ret.size() > 0) {
            RdbmsConcept result = ret.get(0);
            return new Concept(result);
        } else {
            return null;
        }
    }

    public List<Concept> getChildren(String code) throws Exception {
        EntityManager entityManager = ConnectionManager.getCodingEntityManager();

        String sql = "select r" +
                " from RdbmsConcept c" +
                " join RdbmsConceptPcLink l on l.parent_pid = c.pid " +
                " join RdbmsConcept r on r.pid = l.child_pid " +
                " where" +
                "    c.code = :code ";

        Query query = entityManager.createQuery(sql, RdbmsConcept.class)
                .setParameter("code", code);

        List<RdbmsConcept> ret = query.getResultList();

        entityManager.close();

        return ret
                .stream()
                .map(T -> new Concept(T))
                .collect(Collectors.toList());
    }


    public List<Concept> getParents(String code) throws Exception {
        EntityManager entityManager = ConnectionManager.getCodingEntityManager();

        String sql = "select r" +
                " from RdbmsConcept c" +
                " join RdbmsConceptPcLink l on l.child_pid = c.pid " +
                " join RdbmsConcept r on r.pid = l.parent_pid " +
                " where" +
                "    c.code = :code ";

        Query query = entityManager.createQuery(sql, RdbmsConcept.class)
                .setParameter("code", code);

        List<RdbmsConcept> ret = query.getResultList();

        entityManager.close();

        return ret
                .stream()
                .map(T -> new Concept(T))
                .collect(Collectors.toList());
    }
}
