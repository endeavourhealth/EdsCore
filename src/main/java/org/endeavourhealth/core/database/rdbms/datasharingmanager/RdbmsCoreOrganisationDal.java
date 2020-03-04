package org.endeavourhealth.core.database.rdbms.datasharingmanager;


import org.endeavourhealth.core.database.dal.datasharingmanager.OrganisationDalI;
import org.endeavourhealth.core.database.dal.datasharingmanager.enums.MapType;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonOrganisationCCG;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.OrganisationEntity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

public class RdbmsCoreOrganisationDal implements OrganisationDalI {
    private static Map<UUID, List<OrganisationEntity>> cachedOrganisations = new HashMap<>();
    private static Map<UUID, String> cachedSearchTerm = new HashMap<>();

    public List<OrganisationEntity> getOrganisationsFromList(List<String> organisations) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<OrganisationEntity> cq = cb.createQuery(OrganisationEntity.class);
            Root<OrganisationEntity> rootEntry = cq.from(OrganisationEntity.class);

            Predicate predicate = rootEntry.get("uuid").in(organisations);

            cq.where(predicate);
            TypedQuery<OrganisationEntity> query = entityManager.createQuery(cq);

            List<OrganisationEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public OrganisationEntity getOrganisation(String uuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            OrganisationEntity ret = entityManager.find(OrganisationEntity.class, uuid);

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public List<OrganisationEntity> searchOrganisations(String expression, boolean searchServices,
                                                        byte organisationType,
                                                        Integer pageNumber, Integer pageSize,
                                                        String orderColumn, boolean descending, UUID userId) throws Exception {

        //Only query the DB if the search term has changed for that user
        if (cachedSearchTerm.get(userId) == null || !cachedSearchTerm.get(userId).equals(expression) ) {

            cachedOrganisations.remove(userId);
            cachedSearchTerm.put(userId, expression);

            EntityManager entityManager = ConnectionManager.getDsmEntityManager();

            try {
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<OrganisationEntity> cq = cb.createQuery(OrganisationEntity.class);
                Root<OrganisationEntity> rootEntry = cq.from(OrganisationEntity.class);


                Predicate predicate = cb.and(cb.equal(rootEntry.get("isService"), (byte) (searchServices ? 1 : 0)),
                        (cb.or(cb.like(rootEntry.get("name"), "%" + expression + "%"),
                                cb.like(rootEntry.get("odsCode"), "%" + expression + "%"),
                                cb.like(rootEntry.get("alternativeName"), "%" + expression + "%"),
                                cb.like(rootEntry.get("icoCode"), "%" + expression + "%"))));

                cq.where(predicate);

                TypedQuery<OrganisationEntity> query = entityManager.createQuery(cq);

                cachedOrganisations.put(userId, query.getResultList());

            } finally {
                entityManager.close();
            }
        }

        List<OrganisationEntity> cachedOrgs = cachedOrganisations.get(userId);
        //System.out.println("found " + cachedOrgs.size() + " orgs");

        if (cachedOrgs != null) {
            sortOrganisationCache(cachedOrganisations.get(userId), orderColumn, descending);
            return paginateOrganisationCache(cachedOrganisations.get(userId), pageNumber, pageSize);
        }

        return Collections.emptyList();
    }

    private void sortOrganisationCache(List<OrganisationEntity> orgs, String orderColumn, boolean descending) throws Exception {
        switch (orderColumn) {
            case "name":
                orgs.sort(Comparator.comparing(OrganisationEntity::getName, String.CASE_INSENSITIVE_ORDER));
                break;
            case "odsCode":
                orgs.sort(Comparator.comparing(OrganisationEntity::getOdsCode, String.CASE_INSENSITIVE_ORDER));
                break;
            default: throw new Exception("Order column not recognised");
        }

        if (descending) {
            Collections.reverse(orgs);
        }

    }

    private List<OrganisationEntity> paginateOrganisationCache(List<OrganisationEntity> orgs, Integer pageNumber, Integer pageSize) {
        if(pageSize <= 0 || pageNumber <= 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = (pageNumber - 1) * pageSize;
        if(orgs == null || orgs.size() < fromIndex){
            return Collections.emptyList();
        }

        return orgs.subList(fromIndex, Math.min(fromIndex + pageSize, orgs.size()));
    }

    public List<OrganisationEntity> getOrganisationsFromOdsList(List<String> odsCodes) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<OrganisationEntity> cq = cb.createQuery(OrganisationEntity.class);
            Root<OrganisationEntity> rootEntry = cq.from(OrganisationEntity.class);

            Predicate predicate = rootEntry.get("odsCode").in(odsCodes);

            cq.where(predicate);
            TypedQuery<OrganisationEntity> query = entityManager.createQuery(cq);

            List<OrganisationEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public OrganisationEntity getOrganisationsFromOdsCode(String odsCode) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<OrganisationEntity> cq = cb.createQuery(OrganisationEntity.class);
            Root<OrganisationEntity> rootEntry = cq.from(OrganisationEntity.class);

            Predicate predicate = cb.equal(rootEntry.get("odsCode"), odsCode);

            cq.where(predicate);
            TypedQuery<OrganisationEntity> query = entityManager.createQuery(cq);

            List<OrganisationEntity> ret = query.getResultList();
            if (ret.isEmpty()) {
                return null;
            } else {
                return ret.get(0);
            }

        } finally {
            entityManager.close();
        }
    }

    public List<JsonOrganisationCCG> getCCGForOrganisationOdsList(List<String> odsCodes) throws Exception {

        List<JsonOrganisationCCG> orgCCGList = new ArrayList<>();

        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            Query query = entityManager.createQuery(
                    "select child.odsCode, parent.name from OrganisationEntity child " +
                            "inner join MasterMappingEntity mm on mm.childUuid = child.uuid and mm.parentMapTypeId = :orgType and mm.childMapTypeId = :orgType " +
                            "inner join OrganisationEntity parent on parent.uuid = mm.parentUuid " +
                            "where child.odsCode IN (:odsList) " +
                            " and parent.type = 8 " );
            query.setParameter("orgType", MapType.ORGANISATION.getMapType());
            query.setParameter("odsList", odsCodes);

            List<Object[]> results = query.getResultList();

            for (Object[] res : results) {
                JsonOrganisationCCG orgCCG = new JsonOrganisationCCG();
                orgCCG.setOdsCode((String)res[0]);
                orgCCG.setCcgName((String)res[1]);
                orgCCGList.add(orgCCG);
            }

            return orgCCGList;
        } finally {
            entityManager.close();
        }
    }

    public Map<UUID, String> getCachedSearchTerm() throws Exception {
        return cachedSearchTerm;
    }
}
