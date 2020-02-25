package org.endeavourhealth.core.database.rdbms.datasharingmanager;

import org.endeavourhealth.core.database.dal.datasharingmanager.MasterMappingDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.MasterMappingEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class RdbmsCoreMasterMappingDal implements MasterMappingDalI {

    public List<String> getParentMappings(String childUuid, Short childMapTypeId, Short parentMapTypeId) throws Exception {
        List<String> childUuids = new ArrayList<>();
        childUuids.add(childUuid);
        return getParentMappings(childUuids, childMapTypeId, parentMapTypeId);
    }


    public List<String> getParentMappings(List<String> childUuids, Short childMapTypeId, Short parentMapTypeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<MasterMappingEntity> cq = cb.createQuery(MasterMappingEntity.class);
            Root<MasterMappingEntity> rootEntry = cq.from(MasterMappingEntity.class);

            Predicate predicate = cb.and((rootEntry.get("childUuid").in(childUuids)),
                    cb.equal(rootEntry.get("childMapTypeId"), childMapTypeId),
                    cb.equal(rootEntry.get("parentMapTypeId"), parentMapTypeId));

            cq.where(predicate);
            TypedQuery<MasterMappingEntity> query = entityManager.createQuery(cq);
            List<MasterMappingEntity> maps = query.getResultList();

            List<String> parents = new ArrayList<>();
            for (MasterMappingEntity mme : maps) {
                parents.add(mme.getParentUuid());
            }

            return parents;
        } finally {
            entityManager.close();
        }
    }

    public List<String> getChildMappings(String parentUuid, Short parentMapTypeId, Short childMapTypeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<MasterMappingEntity> cq = cb.createQuery(MasterMappingEntity.class);
            Root<MasterMappingEntity> rootEntry = cq.from(MasterMappingEntity.class);

            Predicate predicate = cb.and(cb.equal(rootEntry.get("parentUuid"), parentUuid),
                    cb.equal(rootEntry.get("parentMapTypeId"), parentMapTypeId),
                    cb.equal(rootEntry.get("childMapTypeId"), childMapTypeId));

            cq.where(predicate);
            TypedQuery<MasterMappingEntity> query = entityManager.createQuery(cq);
            List<MasterMappingEntity> maps = query.getResultList();

            List<String> children = new ArrayList<>();
            for (MasterMappingEntity mme : maps) {
                children.add(mme.getChildUuid());
            }

            return children;
        } finally {
            entityManager.close();
        }

    }
}
