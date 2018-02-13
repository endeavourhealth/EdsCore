package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.CernerCodeValueRefDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerCodeValueRef;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsCernerCodeValueRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.UUID;

public class RdbmsCernerCodeValueRefDal implements CernerCodeValueRefDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsCernerCodeValueRefDal.class);

    // Static values to hold the code set values
    public static final Long LOCATION_NAME = 220L;
    public static final Long NHS_NUMBER_STATUS = 29882L;
    public static final Long GENDER = 57L;
    public static final Long ETHNIC_GROUP = 27L;
    public static final Long LANGUAGE = 36L;
    public static final Long RELIGION = 49L;
    public static final Long MARITAL_STATUS = 38L;
    public static final Long NAME_USE = 213L;


    @Override
    public CernerCodeValueRef getCodeFromCodeSet(Long codeSet, Long code, UUID serviceId) throws Exception {
        LOG.trace("readMergeRecordDB:" + codeSet + " " + code);
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsCernerCodeValueRef c"
                    + " where c.serviceId = :service_id"
                    + " and c.codeSetNbr = :codeSet"
                    + " and c.codeValueCd = :code"
                    + " and c.activeInd = 1";

            Query query = entityManager.createQuery(sql, RdbmsCernerCodeValueRef.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("codeSet", codeSet)
                    .setParameter("code", code)
                    .setMaxResults(1);

            try {
                return ((CernerCodeValueRef) query.getSingleResult());
            }
            catch (NoResultException e) {
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
