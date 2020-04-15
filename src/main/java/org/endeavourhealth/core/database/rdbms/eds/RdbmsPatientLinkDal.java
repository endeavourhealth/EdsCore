package org.endeavourhealth.core.database.rdbms.eds;

import com.google.common.base.Strings;
import org.endeavourhealth.common.fhir.IdentifierHelper;
import org.endeavourhealth.core.database.dal.eds.PatientLinkDalI;
import org.endeavourhealth.core.database.dal.eds.models.PatientLinkPair;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.DeadlockHandler;
import org.endeavourhealth.core.database.rdbms.eds.models.RdbmsPatientLink;
import org.endeavourhealth.core.database.rdbms.eds.models.RdbmsPatientLinkHistory;
import org.endeavourhealth.core.database.rdbms.eds.models.RdbmsPatientLinkPerson;
import org.hl7.fhir.instance.model.Patient;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.*;

public class RdbmsPatientLinkDal implements PatientLinkDalI {

    @Override
    public PatientLinkPair updatePersonId(UUID serviceId, Patient fhirPatient) throws Exception {

        DeadlockHandler h = new DeadlockHandler();

        //due to speed of HL7 message processing, we occasionally get errors
        //because we try to insert a duplicate key into patient_link_history, so just
        //give it a few attempts
        h.addOtherErrorMessageToHandler("could not execute statement");

        while (true) {
            try {
                return tryUpdatePersonId(serviceId, fhirPatient);

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }

    }

    private PatientLinkPair tryUpdatePersonId(UUID serviceId, Patient fhirPatient) throws Exception {

        String patientId = fhirPatient.getId();
        String newPersonId = null;
        String previousPersonId = null;

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();
        try {

            //get the current person ID for the patient
            RdbmsPatientLink patientLink = getPatientLink(patientId, entityManager);
            if (patientLink != null) {
                previousPersonId = patientLink.getPersonId();
            }

            //work out what the person ID should be
            String nhsNumber = IdentifierHelper.findNhsNumber(fhirPatient);
            if (!Strings.isNullOrEmpty(nhsNumber)) {
                String sql = "select c"
                        + " from"
                        + " RdbmsPatientLinkPerson c"
                        + " where c.nhsNumber = :nhsNumber";

                Query query = entityManager.createQuery(sql, RdbmsPatientLinkPerson.class)
                        .setParameter("nhsNumber", nhsNumber);

                RdbmsPatientLinkPerson person = null;
                try {
                    person = (RdbmsPatientLinkPerson) query.getSingleResult();

                } catch (NoResultException ex) {
                    //if we haven't got a person ID for this NHS number, then generate one now
                    person = new RdbmsPatientLinkPerson();
                    person.setNhsNumber(nhsNumber);
                    person.setPersonId(UUID.randomUUID().toString());

                    entityManager.getTransaction().begin();
                    entityManager.persist(person);
                    entityManager.getTransaction().commit();
                }

                String matchingPersonId = person.getPersonId();
                if (previousPersonId == null
                        || !previousPersonId.equals(matchingPersonId)) {
                    newPersonId = matchingPersonId;
                }

            } else {
                //if we don't have an NHS number, then just assign a new random person ID
                if (previousPersonId == null) {
                    newPersonId = UUID.randomUUID().toString();

                } else {
                    //if we previously had a person ID, we need to see if that person ID is one matched to
                    //an NHS number or not. If a record has an NHS number and then has that removed, then we need
                    //to make sure a new person ID is generated
                    String sql = "select c"
                            + " from"
                            + " RdbmsPatientLinkPerson c"
                            + " where c.personId = :personId";

                    Query query = entityManager.createQuery(sql, RdbmsPatientLinkPerson.class)
                                    .setParameter("personId", previousPersonId);

                    try {
                        query.getSingleResult();

                        //if the patient_link_person table has a record for this person ID, then
                        //we need to match to a new person ID because we no longer have the NHS number
                        newPersonId = UUID.randomUUID().toString();

                    } catch (NoResultException ex) {
                        //if there's nothing in patient_link_person for this person ID then we didn't have
                        //an NHS number last time through, so leave with the same person ID
                    }
                }
            }

            //if we've assigned a new person ID, then record this in the history table and update the main table
            if (!Strings.isNullOrEmpty(newPersonId)) {

                RdbmsPatientLinkHistory history = new RdbmsPatientLinkHistory();
                history.setPatientId(patientId);
                history.setServiceId(serviceId.toString());
                history.setNewPersonId(newPersonId);
                history.setPreviousPersonId(previousPersonId);
                history.setUpdated(new Date());

                if (patientLink == null) {
                    patientLink = new RdbmsPatientLink();
                    patientLink.setPatientId(patientId);
                    patientLink.setServiceId(serviceId.toString());
                }
                patientLink.setPersonId(newPersonId);

                entityManager.getTransaction().begin();
                entityManager.persist(history);
                entityManager.persist(patientLink);
                entityManager.getTransaction().commit();
                //NOTE: we very occasionally get a duplicate key exception from the above SQL, which
                //is down to two updates for the same patient going through very quickly. It's because patient_link_history
                //uses the timestamp as part of its primary key
            }

            return new PatientLinkPair(patientId, newPersonId, previousPersonId);

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    private static RdbmsPatientLink getPatientLink(String patientId, EntityManager entityManager) {
        String sql = "select c"
                + " from"
                + " RdbmsPatientLink c"
                + " where c.patientId = :patientId";

        Query query = entityManager.createQuery(sql, RdbmsPatientLink.class)
                .setParameter("patientId", patientId);

        try {
            return (RdbmsPatientLink)query.getSingleResult();

        } catch (NoResultException ex) {
            return null;

        }
    }

    @Override
    public String getPersonId(String patientId) throws Exception {

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            RdbmsPatientLink patientLink = getPatientLink(patientId, entityManager);

            if (patientLink != null) {
                return patientLink.getPersonId();
            } else {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    @Override
    public Map<String, String> getPatientAndServiceIdsForPerson(String personId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPatientLink c"
                    + " where c.personId = :personId";

            Query query = entityManager.createQuery(sql, RdbmsPatientLink.class)
                    .setParameter("personId", personId);

            Map<String, String> ret = new HashMap<>();

            List<RdbmsPatientLink> links = query.getResultList();
            for (RdbmsPatientLink link : links) {
                String patientId = link.getPatientId();
                String serviceId = link.getServiceId();
                ret.put(patientId, serviceId);
            }

            return ret;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<PatientLinkPair> getChangesSince(Date timestamp) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPatientLinkHistory c"
                    + " where c.updated >= :timestamp"
                    + " and c.previousPersonId IS NOT NULL";

            Query query = entityManager.createQuery(sql, RdbmsPatientLinkHistory.class)
                    .setParameter("timestamp", timestamp, TemporalType.TIMESTAMP);

            List<RdbmsPatientLinkHistory> links = query.getResultList();

            //sort the links by date, since we need them for the filtering
            links.sort((a, b) -> a.getUpdated().compareTo(b.getUpdated()));

            Map<String, List<RdbmsPatientLinkHistory>> updatesByPatient = new HashMap<>();

            for (RdbmsPatientLinkHistory link : links) {
                String patientId = link.getPatientId();
                List<RdbmsPatientLinkHistory> list = updatesByPatient.get(patientId);
                if (list == null) {
                    list = new ArrayList<>();
                    updatesByPatient.put(patientId, list);
                }
                list.add(link);
            }

            List<PatientLinkPair> ret = new ArrayList<>();

            //if a patient was matched to different persons MULTIPLE times since the timestamp, our
            //results will have two records, for the patient A->B and B->C. To make it easier for consumers,
            //so they don't have to follow that chain, we sanitise the results, so it shows A->C and B->C
            for (String patientId : updatesByPatient.keySet()) {

                List<RdbmsPatientLinkHistory> updates = updatesByPatient.get(patientId);

                RdbmsPatientLinkHistory last = updates.get(updates.size() - 1);
                String latestPersonId = last.getNewPersonId();

                HashSet<String> oldPersonIds = new HashSet<>();
                for (RdbmsPatientLinkHistory update : updates) {
                    String oldPersonId = update.getPreviousPersonId(); //note: this may be null

                    //sometimes the person ID changes back and forth, so if the old person ID is the same as the latest person ID, then skip it
                    if (oldPersonId != null && oldPersonId.equals(latestPersonId)) {
                        continue;
                    }

                    oldPersonIds.add(oldPersonId);
                }

                for (String oldPersonId : oldPersonIds) {
                    ret.add(new PatientLinkPair(patientId, latestPersonId, oldPersonId));
                }
            }

            return ret;

        } finally {
            entityManager.close();
        }
    }

}
