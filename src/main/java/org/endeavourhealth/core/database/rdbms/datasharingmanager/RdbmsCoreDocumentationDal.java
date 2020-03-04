package org.endeavourhealth.core.database.rdbms.datasharingmanager;


import org.endeavourhealth.core.database.dal.datasharingmanager.DocumentationDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.DocumentationEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class RdbmsCoreDocumentationDal implements DocumentationDalI {

    public DocumentationEntity getDocument(String uuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            DocumentationEntity ret = entityManager.find(DocumentationEntity.class, uuid);

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public List<DocumentationEntity> getDocumentsFromList(List<String> documents) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<DocumentationEntity> cq = cb.createQuery(DocumentationEntity.class);
            Root<DocumentationEntity> rootEntry = cq.from(DocumentationEntity.class);

            Predicate predicate = rootEntry.get("uuid").in(documents);

            cq.where(predicate);
            TypedQuery<DocumentationEntity> query = entityManager.createQuery(cq);

            List<DocumentationEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }
}
