package org.endeavourhealth.core.database.rdbms.datasharingmanager;

import org.endeavourhealth.core.database.dal.datasharingmanager.DataSetDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.DataSetEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class RdbmsCoreDataSetDal implements DataSetDalI {

    public List<DataSetEntity> getDataSetsFromList(List<String> datasets) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<DataSetEntity> cq = cb.createQuery(DataSetEntity.class);
            Root<DataSetEntity> rootEntry = cq.from(DataSetEntity.class);

            Predicate predicate = rootEntry.get("uuid").in(datasets);

            cq.where(predicate);
            TypedQuery<DataSetEntity> query = entityManager.createQuery(cq);

            List<DataSetEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public DataSetEntity getDataSet(String uuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            DataSetEntity ret = entityManager.find(DataSetEntity.class, uuid);

            return ret;
        } finally {
            entityManager.close();
        }

    }
}
