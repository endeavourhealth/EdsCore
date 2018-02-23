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

    @Override
    public CernerCodeValueRef getCodeFromCodeSet(Long codeSet, Long code, UUID serviceId) throws Exception {
        //LOG.trace("readCernerCodeRefDB:" + codeSet + " " + code);
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsCernerCodeValueRef c"
                    + " where c.serviceId = :service_id"
                    + " and c.codeSetNbr = :codeSet"
                    + " and c.codeValueCd = :code";
                    //+ " and c.activeInd = 1"; //don't restrict on this when retrieving

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
                LOG.error("No code found for codeSet " + codeSet + ", code " + code + ", service " + serviceId);
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
