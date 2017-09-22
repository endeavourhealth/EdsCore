package org.endeavourhealth.core.rdbms.reference;

import com.google.common.base.Strings;
import org.endeavourhealth.core.rdbms.transform.EnterpriseIdMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class EncounterCodeHelper {
    private static final Logger LOG = LoggerFactory.getLogger(EncounterCodeHelper.class);

    private static final String DELIMITER = "|";
    private static final int MAX_LEN_TERM = 255;
    private static final int MAX_LEN_MAPPING = 1024;

    private static volatile int lastDigitAssigned = 0;

    public static EncounterCode findOrCreateCode(String term, String... mappingElements) throws Exception {
        return findOrCreateCode(term, 5, mappingElements);
    }

    private static EncounterCode findOrCreateCode(String term, int attemptsRamaining, String... mappingElements) throws Exception {

        EntityManager entityManager = ReferenceConnection.getEntityManager();
        String mappingStr = combineMappings(mappingElements);

        if (term.length() > MAX_LEN_TERM) {
            throw new Exception("Term longer (" + term.length() + ") than max allowd " + MAX_LEN_TERM);
        }
        if (mappingStr.length() > MAX_LEN_MAPPING) {
            throw new Exception("Mapping longer (" + mappingStr.length() + ") than max allowd " + MAX_LEN_MAPPING);
        }

        //first look for an existing one
        EncounterCode ret = getEncounterCode(entityManager, mappingStr);
        if (ret != null) {
            entityManager.close();
            return ret;
        }

        //if we didn't find one, create one
        try {
            return createEncounterCode(entityManager, term, mappingStr);

        } catch (Exception ex) {

            //if another thread/process has beat us to it, either storing our mapping or assigning the code
            //we were going to use, we'll get an exception, so try the find again
            attemptsRamaining --;
            if (attemptsRamaining > 0) {
                return findOrCreateCode(term, attemptsRamaining, mappingElements);
            }

            //if we've tried the above a few times and still failed, we've probably got a DB problem
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    private static long generateCode() {

        lastDigitAssigned ++;
        int digit = lastDigitAssigned;

        String namespace = "9999999";
        String partition = "10";
        String s = "" + digit + namespace + partition;

        String checkDigit = VerhoeffCheckDigit.generateVerhoeff(s);
        String total = s + checkDigit;

        return Long.parseLong(total);
    }

    private static EncounterCode createEncounterCode(EntityManager entityManager, String term, String mappingStr) throws Exception {

        long code = generateCode();

        EncounterCode encounterCode = new EncounterCode();
        encounterCode.setCode(code);
        encounterCode.setTerm(term);
        encounterCode.setMapping(mappingStr);

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


    private static String combineMappings(String... mapping) {
        String s =  String.join(DELIMITER, mapping);
        s = s.toUpperCase(); //always force to upper case so we don't have to worry about case
        return s;
    }

    private static EncounterCode getEncounterCode(EntityManager entityManager, String mappingStr) throws Exception {

        //if called with an empty postcode, just return null
        if (Strings.isNullOrEmpty(mappingStr)) {
            return null;
        }

        String sql = "select r"
                + " from EncounterCode r"
                + " where r.mapping = :mapping";

        Query query = entityManager
                .createQuery(sql, EncounterCode.class)
                .setParameter("mapping", mappingStr);

        try {
            return (EncounterCode)query.getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }
}
