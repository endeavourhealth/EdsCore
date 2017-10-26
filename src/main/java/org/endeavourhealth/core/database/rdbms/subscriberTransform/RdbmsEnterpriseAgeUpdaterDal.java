package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.EnterpriseAgeUpdaterlDalI;
import org.endeavourhealth.core.database.dal.subscriberTransform.models.EnterpriseAge;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsEnterpriseAge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RdbmsEnterpriseAgeUpdaterDal implements EnterpriseAgeUpdaterlDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsEnterpriseAgeUpdaterDal.class);

    private String subscriberConfigName = null;

    public RdbmsEnterpriseAgeUpdaterDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    public List<EnterpriseAge> findAgesToUpdate() throws Exception {

        String sql = "select c"
                + " from"
                + " RdbmsEnterpriseAge c"
                + " where c.dateNextChange <= :dateNextChange";

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        Query query = entityManager.createQuery(sql, RdbmsEnterpriseAge.class)
                .setParameter("dateNextChange", new Date());

        List<RdbmsEnterpriseAge> ret = query.getResultList();

        entityManager.close();

        return ret
                .stream()
                .map(T -> new EnterpriseAge(T))
                .collect(Collectors.toList());
    }

    public Integer[] calculateAgeValues(long patientId, Date dateOfBirth) throws Exception {

        RdbmsEnterpriseAge map = findAgeObject(patientId);
        if (map == null) {
            map = new RdbmsEnterpriseAge();
            map.setEnterprisePatientId(patientId);
        }

        //always re-set these, in case they've changed
        map.setDateOfBirth(dateOfBirth);
        //map.setEnterpriseConfigName(enterpriseConfigName);

        EnterpriseAge proxyObj = new EnterpriseAge(map);
        Integer[] ret = calculateAgeValues(proxyObj);

        save(proxyObj);

        return ret;
    }

    public Integer[] calculateAgeValues(EnterpriseAge map) throws Exception {

        Integer[] ret = new Integer[3];

        if (map.getDateOfBirth() == null) {
            return ret;
        }

        LocalDate dobLocalDate = map.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate nowLocalDate = LocalDate.now();

        Period period = Period.between(dobLocalDate, nowLocalDate);
        ret[EnterpriseAge.UNIT_YEARS] = new Integer(period.getYears());

        if (ret[EnterpriseAge.UNIT_YEARS].intValue() < 5) {
            ret[EnterpriseAge.UNIT_YEARS] = null;
            ret[EnterpriseAge.UNIT_MONTHS] = new Integer((period.getYears() * 12) + period.getMonths());

            if (ret[EnterpriseAge.UNIT_MONTHS].intValue() <= 12) {
                ret[EnterpriseAge.UNIT_MONTHS] = null;

                //period doesn't help with calculating the number of days in each month, so use an alternative
                //method to calculate the number of days
                long millis = new Date().getTime() - map.getDateOfBirth().getTime();
                int days = (int)TimeUnit.DAYS.convert(millis, TimeUnit.MILLISECONDS);
                ret[EnterpriseAge.UNIT_WEEKS] = new Integer(days / 7);
                //ret[UNIT_WEEKS] = new Integer(period.getDays() / 7);
            }
        }

        //store the dob for the updater job that updates Enterprise
        calculateNextUpdateDate(dobLocalDate, nowLocalDate, ret, map);

        return ret;
    }

    public void save(EnterpriseAge obj) throws Exception {

        RdbmsEnterpriseAge dbObj = new RdbmsEnterpriseAge(obj);

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        entityManager.persist(dbObj);
        entityManager.close();
    }


    private static void calculateNextUpdateDate(LocalDate dobLocalDate,
                                                LocalDate nowLocalDate,
                                                Integer[] values,
                                                EnterpriseAge map) throws Exception {


        LocalDate nextUpdate = null;

        if (values[EnterpriseAge.UNIT_YEARS] != null) {
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

        } else if (values[EnterpriseAge.UNIT_MONTHS] != null) {
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
            if (day == 29
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

    private RdbmsEnterpriseAge findAgeObject(long enterprisePatientId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        String sql = "select c"
                + " from"
                + " RdbmsEnterpriseAge c"
                + " where c.enterprisePatientId = :enterprisePatientId";


        Query query = entityManager.createQuery(sql, RdbmsEnterpriseAge.class)
                .setParameter("enterprisePatientId", enterprisePatientId);

        try {
            return (RdbmsEnterpriseAge)query.getSingleResult();

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }



}
