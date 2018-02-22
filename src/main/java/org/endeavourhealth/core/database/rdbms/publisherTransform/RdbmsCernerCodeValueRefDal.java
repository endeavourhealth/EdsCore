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
    public static final Long PERSONNEL_POSITION = 88L;
    public static final Long PERSONNEL_SPECIALITY = 3394L;
    public static final Long DIAGNOSIS_TYPE = 17L;
    public static final Long PROCEDURE_TYPE = 401L;
    public static final Long RELATIONSHIP_TO_PATIENT = 40L;
    public static final Long PERSON_RELATIONSHIP_TYPE = 351L;
    public static final Long PHONE_TYPE = 43L;
    public static final Long TREATMENT_FUNCTION = 34L;
    public static final Long ALIAS_TYPE = 4L;
    public static final Long CLINICAL_CODE_TYPE = 72L;


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
                RdbmsCernerCodeValueRef result = (RdbmsCernerCodeValueRef)query.getSingleResult();
                return new CernerCodeValueRef(result);
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