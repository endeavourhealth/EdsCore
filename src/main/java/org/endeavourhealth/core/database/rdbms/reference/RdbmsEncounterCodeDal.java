package org.endeavourhealth.core.database.rdbms.reference;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.reference.EncounterCodeDalI;
import org.endeavourhealth.core.database.dal.reference.VerhoeffCheckDigit;
import org.endeavourhealth.core.database.dal.reference.models.EncounterCode;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsEncounterCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class RdbmsEncounterCodeDal implements EncounterCodeDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsEncounterCodeDal.class);

    private static final int NUMBER_ATTEMPTS = 50; //tried with only five attempts but it kept failing due to other queue readers

    private static final int MAX_LEN_TERM = 255;

    private static final String CODE_NAMESPACE = "9999999";
    private static final String CODE_PARTITION = "10";

    private static volatile Integer lastPrefixAssigned = null;

    public EncounterCode findOrCreateCode(String term) throws Exception {

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            RdbmsEncounterCode result = findOrCreateCode(term, entityManager, NUMBER_ATTEMPTS);
            return new EncounterCode(result);

        } finally {
            entityManager.close();
        }
    }

    private static RdbmsEncounterCode findOrCreateCode(String term, EntityManager entityManager, int attemptsRemaining) throws Exception {

        if (Strings.isNullOrEmpty(term)) {
            return null;
        }

        if (term.length() > MAX_LEN_TERM) {
            throw new Exception("Term longer (" + term.length() + ") than max allowd " + MAX_LEN_TERM);
        }

        //always look up using a upper case version of the term
        String mapping = term.trim().toUpperCase();

        //first look for an existing one
        RdbmsEncounterCode ret = getEncounterCode(entityManager, mapping);
        if (ret != null) {
            return ret;
        }

        //if we didn't find one, create one
        try {
            return createEncounterCode(entityManager, term, mapping);

        } catch (Exception ex) {

            //if another thread/process has beat us to it, either storing our mapping or assigning the code
            //we were going to use, we'll get an exception, so try the find again
            attemptsRemaining --;
            if (attemptsRemaining > 0) {
                return findOrCreateCode(term, entityManager, attemptsRemaining);
            }

            //if we've tried the above a few times and still failed, we've probably got a DB problem
            throw ex;
        }
    }

    private static long generateCode(EntityManager entityManager) throws Exception {

        //if we've not generated a code yet, hit the DB to find the current max and work it out from there
        if (lastPrefixAssigned == null) {
            String sql = "select max(code)"
                    + " from RdbmsEncounterCode r";

            Query query = entityManager.createQuery(sql);
            List<Long> l = query.getResultList();
            if (l.size() > 0) {
                Long max = l.get(0);
                if (max != null) {
                    String maxStr = max.toString();

                    //we need to get the prefix section out of the string, removing the
                    //namspace, partition and check digit
                    int suffixLen = CODE_NAMESPACE.length()
                            + CODE_PARTITION.length()
                            + 1; //check digit
                    int trimLen = maxStr.length() - suffixLen;
                    String trimmed = maxStr.substring(0, trimLen);
                    lastPrefixAssigned = Integer.parseInt(trimmed);
                }
            }

            //if we've not got a max, it's because the table is empty
            if (lastPrefixAssigned == null) {
                lastPrefixAssigned = new Integer(0);
            }
        }

        int prefix = lastPrefixAssigned.intValue();
        prefix ++;
        lastPrefixAssigned = new Integer(prefix);

        String namespace = CODE_NAMESPACE;
        String partition = CODE_PARTITION;
        String s = "" + prefix + namespace + partition;

        String checkDigit = VerhoeffCheckDigit.generateVerhoeff(s);
        String total = s + checkDigit;

        return Long.parseLong(total);
    }

    private static RdbmsEncounterCode createEncounterCode(EntityManager entityManager, String term, String mapping) throws Exception {

        long code = generateCode(entityManager);

        RdbmsEncounterCode encounterCode = new RdbmsEncounterCode();
        encounterCode.setCode(code);
        encounterCode.setTerm(term);
        encounterCode.setMapping(mapping);
        LOG.debug("Saving new encounter code " + code + " with term '" + term + "'");

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(encounterCode);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;
        }

        return encounterCode;
    }


    private static RdbmsEncounterCode getEncounterCode(EntityManager entityManager, String mapping) throws Exception {

        String sql = "select r"
                + " from RdbmsEncounterCode r"
                + " where r.mapping = :mapping";

        Query query = entityManager
                .createQuery(sql, RdbmsEncounterCode.class)
                .setParameter("mapping", mapping);

        try {
            return (RdbmsEncounterCode)query.getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }
}
