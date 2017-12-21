package org.endeavourhealth.core.database.rdbms.eds;

import com.google.common.base.Strings;
import org.endeavourhealth.common.fhir.IdentifierHelper;
import org.endeavourhealth.core.database.dal.eds.PatientLinkDalI;
import org.endeavourhealth.core.database.dal.eds.models.PatientLinkPair;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
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

    public PatientLinkPair updatePersonId(Patient fhirPatient) throws Exception {

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
            String nhsNumber = IdentifierHelper.findNhsNumberTrueNhsNumber(fhirPatient);
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
                }
            }

            //if we've assigned a new person ID, then record this in the history table and update the main table
            if (!Strings.isNullOrEmpty(newPersonId)) {

                RdbmsPatientLinkHistory history = new RdbmsPatientLinkHistory();
                history.setPatientId(patientId);
                history.setNewPersonId(newPersonId);
                history.setPreviousPersonId(previousPersonId);
                history.setUpdated(new Date());

                if (patientLink == null) {
                    patientLink = new RdbmsPatientLink();
                    patientLink.setPatientId(patientId);
                }
                patientLink.setPersonId(newPersonId);

                entityManager.getTransaction().begin();
                entityManager.persist(history);
                entityManager.persist(patientLink);
                entityManager.getTransaction().commit();
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

    public List<String> getPatientIds(String personId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPatientLink c"
                    + " where c.personId = :personId";

            Query query = entityManager.createQuery(sql, RdbmsPatientLink.class)
                    .setParameter("personId", personId);

            List<String> ret = new ArrayList<>();

            List<RdbmsPatientLink> links = query.getResultList();
            for (RdbmsPatientLink link : links) {
                ret.add(link.getPatientId());
            }
            return ret;

        } finally {
            entityManager.close();
        }
    }

    public List<PatientLinkPair> getChangesSince(Date timestamp) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPatientLinkHistory c"
                    + " where c.updated >= :timestamp";

            Query query = entityManager.createQuery(sql, RdbmsPatientLinkHistory.class)
                    .setParameter("timestamp", timestamp, TemporalType.TIMESTAMP);

            List<RdbmsPatientLinkHistory> links = query.getResultList();

            //sort the links by date, since we need them for the filtering
            links.sort((a, b) -> a.getUpdated().compareTo(b.getUpdated()));
            //TODO - ensure this sorting is correct

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
