package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.ReferenceUpdaterDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsDeprivationLookup;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsLsoaLookup;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsMsoaLookup;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsPostcodeLookup;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RdbmsReferenceUpdaterDal implements ReferenceUpdaterDalI {


    @Override
    public void updateLosaMap(String lsoaCode, String lsoaName) throws Exception {

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();
        try {
            String sql = "select r"
                    + " from RdbmsLsoaLookup r"
                    + " where r.lsoaCode = :lsoaCode";

            Query query = entityManager
                    .createQuery(sql, RdbmsLsoaLookup.class)
                    .setParameter("lsoaCode", lsoaCode);

            RdbmsLsoaLookup lookup = null;
            try {
                lookup = (RdbmsLsoaLookup) query.getSingleResult();
            } catch (NoResultException e) {
                lookup = new RdbmsLsoaLookup();
                lookup.setLsoaCode(lsoaCode);
            }

            lookup.setLsoaName(lsoaName);

            entityManager.getTransaction().begin();
            entityManager.persist(lookup);
            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void updateMosaMap(String msoaCode, String msoaName) throws Exception {

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();
        try {
            String sql = "select r"
                    + " from RdbmsMsoaLookup r"
                    + " where r.msoaCode = :msoaCode";


            Query query = entityManager
                    .createQuery(sql, RdbmsMsoaLookup.class)
                    .setParameter("msoaCode", msoaCode);

            RdbmsMsoaLookup lookup = null;
            try {
                lookup = (RdbmsMsoaLookup) query.getSingleResult();
            } catch (NoResultException e) {
                lookup = new RdbmsMsoaLookup();
                lookup.setMsoaCode(msoaCode);
            }

            lookup.setMsoaName(msoaName);

            entityManager.getTransaction().begin();
            entityManager.persist(lookup);
            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void updatePostcodeMap(String postcode, String lsoaCode, String msoaCode, String ward, String ward1998, String ccgCode) throws Exception {

        //always make sure this is uppercase
        postcode = postcode.toUpperCase();
        String postcodeNoSpace = postcode.replace(" ", "");

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select r"
                    + " from RdbmsPostcodeLookup r"
                    + " where r.postcodeNoSpace = :postcodeNoSpace";

            Query query = entityManager
                    .createQuery(sql, RdbmsPostcodeLookup.class)
                    .setParameter("postcodeNoSpace", postcodeNoSpace);

            RdbmsPostcodeLookup postcodeReference = null;

            try {
                postcodeReference = (RdbmsPostcodeLookup) query.getSingleResult();

            } catch (NoResultException e) {
                postcodeReference = new RdbmsPostcodeLookup();
                postcodeReference.setPostcodeNoSpace(postcodeNoSpace);
            }

            postcodeReference.setPostcode(postcode);
            postcodeReference.setCcg(ccgCode);
            postcodeReference.setLsoaCode(lsoaCode);
            postcodeReference.setMsoaCode(msoaCode);
            postcodeReference.setWard(ward);
            postcodeReference.setWard1998(ward1998);
            //postcodeReference.setTownsendScore(townsendScore);

            entityManager.getTransaction().begin();
            entityManager.persist(postcodeReference);
            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void updateDeprivationMap(String lsoaCode, Integer rank, Integer decile, Integer incomeRank, Integer incomeDecile, Integer employmentRank, Integer employmentDecile, Integer educationRank, Integer educationDecile, Integer healthRank, Integer healthDecile, Integer crimeRank, Integer crimeDecile, Integer housingAndServicesBarriersRank, Integer housingAndServicesBarriersDecile, Integer livingEnvironmentRank, Integer livingEnvironmentDecile) throws Exception {

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            String sql = "select r"
                    + " from RdbmsDeprivationLookup r"
                    + " where r.lsoaCode = :lsoaCode";

            Query query = entityManager
                    .createQuery(sql, RdbmsDeprivationLookup.class)
                    .setParameter("lsoaCode", lsoaCode);

            RdbmsDeprivationLookup lookup = null;
            try {
                lookup = (RdbmsDeprivationLookup) query.getSingleResult();
            } catch (NoResultException e) {
                lookup = new RdbmsDeprivationLookup();
                lookup.setLsoaCode(lsoaCode);
            }

            lookup.setImdRank(rank);
            lookup.setImdDecile(decile);
            lookup.setIncomeRank(incomeRank);
            lookup.setIncomeDecile(incomeDecile);
            lookup.setEmploymentRank(employmentRank);
            lookup.setEmploymentDecile(employmentDecile);
            lookup.setEducationRank(educationRank);
            lookup.setEducationDecile(educationDecile);
            lookup.setHealthRank(healthRank);
            lookup.setHealthDecile(healthDecile);
            lookup.setCrimeRank(crimeRank);
            lookup.setCrimeDecile(crimeDecile);
            lookup.setHousingAndServicesBarriersRank(housingAndServicesBarriersRank);
            lookup.setHousingAndServicesBarriersDecile(housingAndServicesBarriersDecile);
            lookup.setLivingEnvironmentRank(livingEnvironmentRank);
            lookup.setLivingEnvironmentDecile(livingEnvironmentDecile);

            entityManager.getTransaction().begin();
            entityManager.persist(lookup);
            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
        }
    }
}
