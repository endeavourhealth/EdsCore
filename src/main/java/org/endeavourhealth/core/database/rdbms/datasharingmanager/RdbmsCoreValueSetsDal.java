package org.endeavourhealth.core.database.rdbms.datasharingmanager;

import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.core.database.dal.datasharingmanager.ValueSetsDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ValueSetsCodesEntity;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ValueSetsEntity;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonValueSetCodes;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonValueSets;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RdbmsCoreValueSetsDal implements ValueSetsDalI {

    public List<ValueSetsCodesEntity> getValueSetsCode(String valueSetsUUid) throws Exception {

        EntityManager entityManager = ConnectionManager.getDsmEntityManager();
        List<ValueSetsCodesEntity> ret = null;
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ValueSetsCodesEntity> cq = cb.createQuery(ValueSetsCodesEntity.class);
            Root<ValueSetsCodesEntity> rootEntry = cq.from(ValueSetsCodesEntity.class);
            cq.select(rootEntry);
            cq.where(cb.equal(rootEntry.get("valueSetsUuid"), valueSetsUUid));
            TypedQuery<ValueSetsCodesEntity> query = entityManager.createQuery(cq);
            ret = query.getResultList();
        } finally {
            entityManager.close();
        }
        return ret;
    }

    public void createValueSetsCodes(ArrayList<ValueSetsCodesEntity> codeSetCodes) throws Exception {

        EntityManager entityManager = ConnectionManager.getDsmEntityManager();
        try {
            entityManager.getTransaction().begin();
            for (ValueSetsCodesEntity entity : codeSetCodes) {
                entityManager.getTransaction().begin();
                entityManager.persist(entity);
                entityManager.getTransaction().commit();
            }
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    public void deleteValueSetsCodes(String valueSetsUuid) throws Exception {

        EntityManager entityManager = ConnectionManager.getDsmEntityManager();
        try {
            for (ValueSetsCodesEntity entity : getValueSetsCode(valueSetsUuid)) {
                entityManager.getTransaction().begin();
                entityManager.detach(entity);
                entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
                entityManager.getTransaction().commit();
            }
        } finally {
            entityManager.close();
        }
    }

    public List<JsonValueSets> getAllValueSets(String expression, Integer pageNumber, Integer pageSize,
                                               String orderColumn, boolean descending) throws Exception {

        ArrayList<JsonValueSets> jsonValueSets = new ArrayList();
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();
        List<ValueSetsEntity> ret = null;
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ValueSetsEntity> cq = cb.createQuery(ValueSetsEntity.class);
            Root<ValueSetsEntity> rootEntry = cq.from(ValueSetsEntity.class);

            Predicate predicate = null;
            if (StringUtils.isNotEmpty(expression)) {
                predicate = cb.like(rootEntry.get("name"), "%" + expression + "%");
            }

            if (descending) {
                if (predicate != null) {
                    cq.where(predicate).orderBy(cb.desc(rootEntry.get(orderColumn)));
                } else {
                    cq.orderBy(cb.desc(rootEntry.get(orderColumn)));
                }
            }  else {
                if (predicate != null) {
                    cq.where(predicate).orderBy(cb.asc(rootEntry.get(orderColumn)));
                } else {
                    cq.orderBy(cb.asc(rootEntry.get(orderColumn)));
                }
            }

            TypedQuery<ValueSetsEntity> query = entityManager.createQuery(cq);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
            ret = query.getResultList();
            for (ValueSetsEntity entity : ret) {
                if (entity.getUuid() == null) {
                    entityManager.getTransaction().begin();
                    entity.setUuid(UUID.randomUUID().toString());
                    entityManager.merge(entity);
                    entityManager.getTransaction().commit();
                }
                jsonValueSets.add(parseEntityToJson(entity));
            }
        } finally {
            entityManager.close();
        }

        return jsonValueSets;
    }

    public Long getTotalNumber(String expression) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<ValueSetsEntity> rootEntry = cq.from(ValueSetsEntity.class);

            Predicate predicate = null;
            if (StringUtils.isNotEmpty(expression)) {
                predicate = cb.like(cb.upper(rootEntry.get("name")), "%" + expression.toUpperCase() + "%");
            }

            cq.select((cb.countDistinct(rootEntry)));
            if (predicate != null) {
                cq.where(predicate);
            }

            Long ret = entityManager.createQuery(cq).getSingleResult();

            return ret;

        } finally {
            entityManager.close();
        }
    }

    public ValueSetsEntity getValuesSets(int id) throws Exception {

        EntityManager entityManager = ConnectionManager.getDsmEntityManager();
        ValueSetsEntity ret = null;
        try {
            ret = entityManager.find(ValueSetsEntity.class, id);
        } finally {
            entityManager.close();
        }
        return ret;
    }

    public ValueSetsEntity deleteValuesSets(String uuid) throws Exception {

        EntityManager entityManager = ConnectionManager.getDsmEntityManager();
        ValueSetsEntity entry = null;
        try {
            entityManager.getTransaction().begin();
            entry = entityManager.find(ValueSetsEntity.class, uuid);
            entry = entityManager.merge(entry);
            entityManager.remove(entry);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
        return entry;
    }

    public ValueSetsEntity createValuesSets(ValueSetsEntity valuesSets) throws Exception {

        EntityManager entityManager = ConnectionManager.getDsmEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(valuesSets);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
        return valuesSets;
    }

    public ValueSetsEntity updateValuesSets(ValueSetsEntity valuesSets) throws Exception {

        EntityManager entityManager = ConnectionManager.getDsmEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(valuesSets);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
        return valuesSets;
    }

    public JsonValueSets parseEntityToJson(ValueSetsEntity codeSet) throws Exception {

        List<ValueSetsCodesEntity> codeSetCodes = getValueSetsCode(codeSet.getUuid());
        JsonValueSetCodes[] jsonCodeSetValues = new JsonValueSetCodes[codeSetCodes.size()];

        String read2ConceptIds = "";
        String ctv3ConceptIds = "";
        String sctConceptIds = "";

        for (int i = 0; i < codeSetCodes.size(); i++) {
            jsonCodeSetValues[i] = new JsonValueSetCodes();
            jsonCodeSetValues[i].setValueSetsUuid(codeSetCodes.get(i).getValueSetsUuid());
            jsonCodeSetValues[i].setRead2ConceptId(codeSetCodes.get(i).getRead2ConceptId());
            jsonCodeSetValues[i].setCtv3ConceptId(codeSetCodes.get(i).getCtv3ConceptId());
            jsonCodeSetValues[i].setSctConceptId(codeSetCodes.get(i).getSctConceptId());

            if (codeSetCodes.get(i).getRead2ConceptId() != null &&
                    codeSetCodes.get(i).getRead2ConceptId().length() > 0 &&
                    read2ConceptIds.indexOf(codeSetCodes.get(i).getRead2ConceptId()) == -1 ) {
                read2ConceptIds += codeSetCodes.get(i).getRead2ConceptId() + "; ";
            }

            if (codeSetCodes.get(i).getCtv3ConceptId() != null &&
                    codeSetCodes.get(i).getCtv3ConceptId().length() > 0 &&
                    ctv3ConceptIds.indexOf(codeSetCodes.get(i).getCtv3ConceptId()) == -1 ) {
                ctv3ConceptIds += codeSetCodes.get(i).getCtv3ConceptId() + "; ";
            }

            if (codeSetCodes.get(i).getSctConceptId() != null &&
                    codeSetCodes.get(i).getSctConceptId().length() > 0 &&
                    sctConceptIds.indexOf(codeSetCodes.get(i).getSctConceptId()) == -1 ) {
                sctConceptIds += codeSetCodes.get(i).getSctConceptId() + "; ";
            }
        }

        JsonValueSets jsonValueSets = new JsonValueSets();
        jsonValueSets.setUuid(codeSet.getUuid());
        jsonValueSets.setName(codeSet.getName());
        jsonValueSets.setValuesSetCodes(jsonCodeSetValues);

        jsonValueSets.setRead2ConceptIds(trimLastChar(read2ConceptIds));
        jsonValueSets.setCtv3ConceptIds(trimLastChar(ctv3ConceptIds));
        jsonValueSets.setSctConceptIds(trimLastChar(sctConceptIds));

        return jsonValueSets;
    }

    private static String trimLastChar(String value) {
        if (value.endsWith("; ")) {
            value = value.substring(0, (value.length() - 2));
        }
        return value;
    }
}
