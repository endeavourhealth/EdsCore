package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.PcrAgeUpdaterlDalI;
import org.endeavourhealth.core.database.dal.subscriberTransform.models.PcrAge;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsPcrAge;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RdbmsPcrAgeUpdaterDal implements PcrAgeUpdaterlDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsPcrAgeUpdaterDal.class);

    private String subscriberConfigName = null;

    public RdbmsPcrAgeUpdaterDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    public List<PcrAge> findAgesToUpdate() throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPcrAge c"
                    + " where c.dateNextChange <= :dateNextChange";


            Query query = entityManager.createQuery(sql, RdbmsPcrAge.class)
                    .setParameter("dateNextChange", new Date());

            List<RdbmsPcrAge> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new PcrAge(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public Integer[] calculateAgeValues(long patientId, Date dateOfBirth) throws Exception {

        //if the date of birth is null, we can't calculate anything and shouldn't save anything
        if (dateOfBirth == null) {
            return new Integer[3];
        }

        RdbmsPcrAge map = findAgeObject(patientId);
        if (map == null) {
            map = new RdbmsPcrAge();
            map.setPcrPatientId(patientId);
        }

        //always re-set these, in case they've changed
        map.setDateOfBirth(dateOfBirth);
        //map.setPcrConfigName(pcrConfigName);

        PcrAge proxyObj = new PcrAge(map);
        Integer[] ret = calculateAgeValues(proxyObj);

        save(proxyObj);

        return ret;
    }

    public Integer[] calculateAgeValues(PcrAge map) throws Exception {

        Integer[] ret = new Integer[3];

        if (map.getDateOfBirth() == null) {
            return ret;
        }

        LocalDate dobLocalDate = map.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate nowLocalDate = LocalDate.now();

        Period period = Period.between(dobLocalDate, nowLocalDate);
        ret[PcrAge.UNIT_YEARS] = new Integer(period.getYears());

        if (ret[PcrAge.UNIT_YEARS].intValue() < 5) {
            ret[PcrAge.UNIT_YEARS] = null;
            ret[PcrAge.UNIT_MONTHS] = new Integer((period.getYears() * 12) + period.getMonths());

            if (ret[PcrAge.UNIT_MONTHS].intValue() <= 12) {
                ret[PcrAge.UNIT_MONTHS] = null;

                //period doesn't help with calculating the number of days in each month, so use an alternative
                //method to calculate the number of days
                long millis = new Date().getTime() - map.getDateOfBirth().getTime();
                int days = (int)TimeUnit.DAYS.convert(millis, TimeUnit.MILLISECONDS);
                ret[PcrAge.UNIT_WEEKS] = new Integer(days / 7);
                //ret[UNIT_WEEKS] = new Integer(period.getDays() / 7);
            }
        }

        //store the dob for the updater job that updates Pcr
        calculateNextUpdateDate(dobLocalDate, nowLocalDate, ret, map);

        return ret;
    }

    public void save(PcrAge obj) throws Exception {

        //just adding to make this clearer if it happens again
        if (obj.getDateOfBirth() == null) {
            throw new IllegalArgumentException("DateOfBirth cannot be null");
        }

        RdbmsPcrAge dbObj = new RdbmsPcrAge(obj);

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        PreparedStatement ps = null;
        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            //syntax for postreSQL is slightly different
            String sql = null;
            if (ConnectionManager.isPostgreSQL(connection)) {
                sql = "INSERT INTO pcr_age"
                        + " (pcr_patient_id, date_of_birth, date_next_change)"
                        + " VALUES (?, ?, ?)"
                        + " ON CONFLICT (pcr_patient_id) DO UPDATE SET"
                        + " date_of_birth = EXCLUDED.date_of_birth,"
                        + " date_next_change = EXCLUDED.date_next_change;";

            } else {
                sql = "INSERT INTO pcr_age"
                        + " (pcr_patient_id, date_of_birth, date_next_change)"
                        + " VALUES (?, ?, ?)"
                        + " ON DUPLICATE KEY UPDATE"
                        + " date_of_birth = VALUES(date_of_birth),"
                        + " date_next_change = VALUES(date_next_change);";
            }

            ps = connection.prepareStatement(sql);

            ps.setLong(1, new Long(dbObj.getPcrPatientId()));
            ps.setDate(2, new java.sql.Date(dbObj.getDateOfBirth().getTime()));
            ps.setDate(3, new java.sql.Date(dbObj.getDateNextChange().getTime()));

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }


    private static void calculateNextUpdateDate(LocalDate dobLocalDate,
                                                LocalDate nowLocalDate,
                                                Integer[] values,
                                                PcrAge map) throws Exception {


        LocalDate nextUpdate = null;

        if (values[PcrAge.UNIT_YEARS] != null) {
            //if counting in years, we want to update next birthday
            int updateDay = dobLocalDate.getDayOfMonth();
            int updateMonth = dobLocalDate.getMonthValue();
            int updateYear = nowLocalDate.getYear();

            nextUpdate = createSafeLocalDate(updateYear, updateMonth, updateDay);

            //if we've already passed their birthday this year, then add a year so we update next year
            if (!nextUpdate.isAfter(nowLocalDate)) {
                updateYear ++;
                nextUpdate = createSafeLocalDate(updateYear, updateMonth, updateDay);
            }

        } else if (values[PcrAge.UNIT_MONTHS] != null) {
            //if counting in months, we want to update the day after the day of birth in the next month
            int updateDay = dobLocalDate.getDayOfMonth();
            int updateMonth = nowLocalDate.getMonthValue();
            int updateYear = nowLocalDate.getYear();

            nextUpdate = createSafeLocalDate(updateYear, updateMonth, updateDay);

            //if we've already passed the day in this month when we'd update, then roll forward to next month
            if (!nextUpdate.isAfter(nowLocalDate)) {
                if (updateMonth == Month.DECEMBER.getValue()) {
                    updateYear ++;
                } else {
                    updateMonth ++;
                }

                nextUpdate = createSafeLocalDate(updateYear, updateMonth, updateDay);
            }

        } else {
            int updateDay = dobLocalDate.getDayOfMonth();
            int updateMonth = dobLocalDate.getMonthValue();
            int updateYear = dobLocalDate.getYear();

            nextUpdate = createSafeLocalDate(updateYear, updateMonth, updateDay);

            //keep looping until we find a date after today, adding a week each time
            while (!nextUpdate.isAfter(nowLocalDate)) {
                updateDay += 7;

                //see if we've gone into the next month
                LocalDate dummy = LocalDate.of(updateYear, updateMonth, 1);
                int monthLen = dummy.lengthOfMonth();
                if (updateDay > monthLen) {
                    updateDay -= monthLen;
                    updateMonth ++;

                    if (updateMonth > 12) {
                        updateMonth = 1;
                        updateYear ++;
                    }
                }

                nextUpdate = createSafeLocalDate(updateYear, updateMonth, updateDay);
            }


        }

        Date nextUpdateDate = Date.from(nextUpdate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        map.setDateNextChange(nextUpdateDate);


    }

    private static LocalDate createSafeLocalDate(int year, int month, int day) throws Exception {
        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException ex) {
            //if we try to create a 29th Feb on a non-leap year, then we'll get this exception
            //so handle this and create as 1st March instead
            if (day >= 29
                    && month == Month.FEBRUARY.getValue()) {
                return createSafeLocalDate(year, Month.MARCH.getValue(), 1);

            } else if (day == 31
                        && month == Month.APRIL.getValue()) {
                return createSafeLocalDate(year, Month.MAY.getValue(), 1);

            } else if (day == 31
                    && month == Month.JUNE.getValue()) {
                return createSafeLocalDate(year, Month.JULY.getValue(), 1);

            } else if (day == 31
                    && month == Month.SEPTEMBER.getValue()) {
                return createSafeLocalDate(year, Month.OCTOBER.getValue(), 1);

            } else if (day == 31
                    && month == Month.NOVEMBER.getValue()) {
                return createSafeLocalDate(year, Month.DECEMBER.getValue(), 1);

            } else {
                throw ex;
            }
        }
    }

    private RdbmsPcrAge findAgeObject(long pcrPatientId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPcrAge c"
                    + " where c.pcrPatientId = :pcrPatientId";

            Query query = entityManager.createQuery(sql, RdbmsPcrAge.class)
                    .setParameter("pcrPatientId", pcrPatientId);

            return (RdbmsPcrAge)query.getSingleResult();

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }



}
