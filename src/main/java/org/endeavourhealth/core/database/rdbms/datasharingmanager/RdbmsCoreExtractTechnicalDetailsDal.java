package org.endeavourhealth.core.database.rdbms.datasharingmanager;

import org.endeavourhealth.core.database.dal.datasharingmanager.ExtractTechnicalDetailsDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ExtractTechnicalDetailsEntity;

import javax.persistence.EntityManager;

public class RdbmsCoreExtractTechnicalDetailsDal implements ExtractTechnicalDetailsDalI {

    public ExtractTechnicalDetailsEntity getExtractTechnicalDetails(String uuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            ExtractTechnicalDetailsEntity ret = entityManager.find(ExtractTechnicalDetailsEntity.class, uuid);

            return ret;
        } finally {
            entityManager.close();
        }
    }

}
