package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.ReferenceUpdaterDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.*;

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

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

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

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }


    @Override
    public void updateCcgMap(String ccgCode, String ccgName) throws Exception {

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();
        try {
            String sql = "select r"
                    + " from RdbmsCcgLookup r"
                    + " where r.code = :ccgCode";


            Query query = entityManager
                    .createQuery(sql, RdbmsCcgLookup.class)
                    .setParameter("ccgCode", ccgCode);

            RdbmsCcgLookup lookup = null;
            try {
                lookup = (RdbmsCcgLookup) query.getSingleResult();
            } catch (NoResultException e) {
                lookup = new RdbmsCcgLookup();
                lookup.setCode(ccgCode);
            }

            lookup.setName(ccgName);

            entityManager.getTransaction().begin();
            entityManager.persist(lookup);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void updateWardMap(String wardCode, String wardName) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();
        try {
            String sql = "select r"
                    + " from RdbmsWardLookup r"
                    + " where r.code = :wardCode";


            Query query = entityManager
                    .createQuery(sql, RdbmsWardLookup.class)
                    .setParameter("wardCode", wardCode);

            RdbmsWardLookup lookup = null;
            try {
                lookup = (RdbmsWardLookup) query.getSingleResult();
            } catch (NoResultException e) {
                lookup = new RdbmsWardLookup();
                lookup.setCode(wardCode);
            }

            lookup.setName(wardName);

            entityManager.getTransaction().begin();
            entityManager.persist(lookup);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void updateLocalAuthorityMap(String localAuthorityCode, String localAuthorityName) throws Exception {
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();
        try {
            String sql = "select r"
                    + " from RdbmsLocalAuthorityLookup r"
                    + " where r.code = :localAuthorityCode";


            Query query = entityManager
                    .createQuery(sql, RdbmsLocalAuthorityLookup.class)
                    .setParameter("localAuthorityCode", localAuthorityCode);

            RdbmsLocalAuthorityLookup lookup = null;
            try {
                lookup = (RdbmsLocalAuthorityLookup) query.getSingleResult();
            } catch (NoResultException e) {
                lookup = new RdbmsLocalAuthorityLookup();
                lookup.setCode(localAuthorityCode);
            }

            lookup.setName(localAuthorityName);

            entityManager.getTransaction().begin();
            entityManager.persist(lookup);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void updatePostcodeMap(String postcode, String lsoaCode, String msoaCode, String ward, String ccgCode, String localAuthority) throws Exception {

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
            postcodeReference.setCcgCode(ccgCode);
            postcodeReference.setLsoaCode(lsoaCode);
            postcodeReference.setMsoaCode(msoaCode);
            postcodeReference.setWardCode(ward);
            postcodeReference.setLocalAuthorityCode(localAuthority);

            entityManager.getTransaction().begin();
            entityManager.persist(postcodeReference);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

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

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }
}
