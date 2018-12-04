package org.endeavourhealth.core.database.rdbms.reference;

import org.endeavourhealth.core.database.dal.reference.ReferenceCopierDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.enterprise.EnterpriseConnector;
import org.endeavourhealth.core.database.rdbms.reference.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RdbmsReferenceCopierDal implements ReferenceCopierDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsReferenceCopierDal.class);

    private static final int BATCH_SIZE = 5000;

    public void copyReferenceDataToEnterprise(String enterpriseConfigName) throws Exception {

        LOG.info("Reference Copy to " + enterpriseConfigName + " Starting");
        Connection enterpriseConnection = EnterpriseConnector.openConnection(enterpriseConfigName);
        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();

        try {
            copyLsoas(enterpriseConnection, entityManager);
            copyMsoas(enterpriseConnection, entityManager);
            copyDeprivation(enterpriseConnection, entityManager); //this must be done AFTER the LSOAs
            copyWards(enterpriseConnection, entityManager);
            copyLocalAuthorities(enterpriseConnection, entityManager);

        } finally {
            enterpriseConnection.close();
            entityManager.close();
        }
    }

    /**
     * copies the local_authority_lookup table from the main reference database to the subscriber DB specified
     */
    private void copyLocalAuthorities(Connection enterpriseConnection, EntityManager entityManager) throws Exception {
        LOG.info("Starting LocalAuthority copying");

        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO local_authority_lookup (local_authority_code, local_authority_name)"
                    + " VALUES (?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " local_authority_name = VALUES(local_authority_name)";

            ps = enterpriseConnection.prepareStatement(sql);

            int batch = 0;
            while (copyLocalAuthorityBatch(entityManager, batch, enterpriseConnection, ps)) {
                batch ++;
                LOG.info("Done " + (batch * BATCH_SIZE));
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        LOG.info("Finished LocalAuthority copying");
    }

    private static boolean copyLocalAuthorityBatch(EntityManager entityManager, int batch, Connection enterpriseConnection, PreparedStatement ps) throws Exception {

        String sql = "select c"
                + " from RdbmsLocalAuthorityLookup c";

        Query query = entityManager.createQuery(sql, RdbmsLocalAuthorityLookup.class)
                .setFirstResult(batch * BATCH_SIZE)
                .setMaxResults(BATCH_SIZE);

        List<RdbmsLocalAuthorityLookup> results = query.getResultList();

        for (RdbmsLocalAuthorityLookup lookup: results) {
            String code = lookup.getCode();
            String name = lookup.getName();

            //attempt an update first, and check if it affected any rows
            ps.setString(1, code);
            ps.setString(2, name);
            ps.addBatch();
        }

        ps.executeBatch();
        enterpriseConnection.commit();

        return results.size() == BATCH_SIZE;
    }


    /**
     * copies the ward_lookup table from the main reference database to the subscriber DB specified
     */
    private void copyWards(Connection enterpriseConnection, EntityManager entityManager) throws Exception {
        LOG.info("Starting Ward copying");

        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO ward_lookup (ward_code, ward_name)"
                    + " VALUES (?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " ward_name = VALUES(ward_name)";

            ps = enterpriseConnection.prepareStatement(sql);

            int batch = 0;
            while (copyWardBatch(entityManager, batch, enterpriseConnection, ps)) {
                batch ++;
                LOG.info("Done " + (batch * BATCH_SIZE));
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        LOG.info("Finished Ward copying");
    }

    private static boolean copyWardBatch(EntityManager entityManager, int batch, Connection enterpriseConnection, PreparedStatement ps) throws Exception {

        String sql = "select c"
                + " from RdbmsWardLookup c";

        Query query = entityManager.createQuery(sql, RdbmsWardLookup.class)
                .setFirstResult(batch * BATCH_SIZE)
                .setMaxResults(BATCH_SIZE);

        List<RdbmsWardLookup> results = query.getResultList();

        for (RdbmsWardLookup lookup: results) {
            String code = lookup.getCode();
            String name = lookup.getName();

            //attempt an update first, and check if it affected any rows
            ps.setString(1, code);
            ps.setString(2, name);
            ps.addBatch();
        }

        ps.executeBatch();
        enterpriseConnection.commit();

        return results.size() == BATCH_SIZE;
    }


    /**
     * copies the lsoa_lookup table in the reference DB to an Enterprise/Data Checking DB
     *
     * Usage
     * =================================================================================
     * Then run this utility as:
     *      Main copy_lsoa <enterprise config name>
     */
    private static void copyLsoas(Connection enterpriseConnection, EntityManager entityManager) throws Exception {
        LOG.info("Starting LSOA copying");

        PreparedStatement update = null;
        PreparedStatement insert = null;

        try {
            update = createLsoaUpdatePreparedStatement(enterpriseConnection);
            insert = createLsoaInsertPreparedStatement(enterpriseConnection);

            int batch = 0;
            while (copyLsoaBatch(entityManager, batch, enterpriseConnection, update, insert)) {
                batch ++;
                LOG.info("Done " + (batch * BATCH_SIZE));
            }

        } finally {
            if (update != null) {
                update.close();
            }
            if (insert != null) {
                insert.close();
            }
        }

        LOG.info("Finished LSOA copying");
    }

    private static boolean copyLsoaBatch(EntityManager entityManager, int batch, Connection enterpriseConnection, PreparedStatement update, PreparedStatement insert) throws Exception {

        String sql = "select c"
                + " from RdbmsLsoaLookup c";

        Query query = entityManager.createQuery(sql, RdbmsLsoaLookup.class)
                .setFirstResult(batch * BATCH_SIZE)
                .setMaxResults(BATCH_SIZE);

        List<RdbmsLsoaLookup> results = query.getResultList();

        for (RdbmsLsoaLookup lookup: results) {
            String code = lookup.getLsoaCode();
            String name = lookup.getLsoaName();

            //attempt an update first, and check if it affected any rows
            update.setString(1, name);
            update.setString(2, code);
            update.addBatch();

            int[] rows = update.executeBatch();
            int rowsUpdated = rows[0];
            if (rowsUpdated == 0) {
                //if the update didn't affect any rows, add it to the insert
                insert.setString(1, code);
                insert.setString(2, name);
                insert.addBatch();
            }
        }

        insert.executeBatch();
        enterpriseConnection.commit();

        return results.size() == BATCH_SIZE;
    }

    private static PreparedStatement createLsoaUpdatePreparedStatement(Connection connection) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE lsoa_lookup SET ");
        sb.append("lsoa_name = ? ");
        sb.append("WHERE lsoa_code = ?");

        return connection.prepareStatement(sb.toString());
    }

    private static PreparedStatement createLsoaInsertPreparedStatement(Connection connection) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO lsoa_lookup (lsoa_code, lsoa_name) VALUES (?, ?)");

        return connection.prepareStatement(sb.toString());
    }

    private static void copyMsoas(Connection enterpriseConnection, EntityManager entityManager) throws Exception {
        LOG.info("Starting MSOA copying");

        PreparedStatement update = null;
        PreparedStatement insert = null;

        try {
            update = createMsoaUpdatePreparedStatement(enterpriseConnection);
            insert = createMsoaInsertPreparedStatement(enterpriseConnection);

            int batch = 0;
            while (copyMsoaBatch(entityManager, batch, enterpriseConnection, update, insert)) {
                batch ++;
                LOG.info("Done " + (batch * BATCH_SIZE));
            }

        } finally {
            if (update != null) {
                update.close();
            }
            if (insert != null) {
                insert.close();
            }
        }

        LOG.info("Finished MSOA copying");
    }

    private static boolean copyMsoaBatch(EntityManager entityManager, int batch, Connection enterpriseConnection, PreparedStatement update, PreparedStatement insert) throws Exception {

        String sql = "select c"
                + " from RdbmsMsoaLookup c";

        Query query = entityManager.createQuery(sql, RdbmsMsoaLookup.class)
                .setFirstResult(batch * BATCH_SIZE)
                .setMaxResults(BATCH_SIZE);

        List<RdbmsMsoaLookup> results = query.getResultList();

        for (RdbmsMsoaLookup lookup: results) {
            String code = lookup.getMsoaCode();
            String name = lookup.getMsoaName();

            //attempt an update first, and check if it affected any rows
            update.setString(1, name);
            update.setString(2, code);
            update.addBatch();

            int[] rows = update.executeBatch();
            int rowsUpdated = rows[0];
            if (rowsUpdated == 0) {
                //if the update didn't affect any rows, add it to the insert
                insert.setString(1, code);
                insert.setString(2, name);
                insert.addBatch();
            }
        }

        insert.executeBatch();
        enterpriseConnection.commit();

        return results.size() == BATCH_SIZE;
    }

    private static PreparedStatement createMsoaUpdatePreparedStatement(Connection connection) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE msoa_lookup SET ");
        sb.append("msoa_name = ? ");
        sb.append("WHERE msoa_code = ?");

        return connection.prepareStatement(sb.toString());
    }

    private static PreparedStatement createMsoaInsertPreparedStatement(Connection connection) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO msoa_lookup (msoa_code, msoa_name) VALUES (?, ?)");

        return connection.prepareStatement(sb.toString());
    }


    /**
     * copies the lsoa_lookup table in the reference DB to an Enterprise/Data Checking DB
     *
     * Usage
     * =================================================================================
     * Then run this utility as:
     *      Main copy_deprivation <enterprise config name>
     */
    private static void copyDeprivation(Connection enterpriseConnection, EntityManager entityManager) throws Exception {
        LOG.info("Starting Deprivation copying");

        PreparedStatement update = null;
        try {
            update = createDeprivationUpdatePreparedStatement(enterpriseConnection);

            int batch = 0;
            while (copyDeprivationBatch(entityManager, batch, enterpriseConnection, update)) {
                batch ++;
                LOG.info("Done " + (batch * BATCH_SIZE));
            }

        } finally {
            if (update != null) {
                update.close();
            }
        }

        LOG.info("Finished Deprivation copying");
    }

    private static boolean copyDeprivationBatch(EntityManager entityManager, int batch, Connection enterpriseConnection, PreparedStatement update) throws Exception {

        String sql = "select c"
                + " from RdbmsDeprivationLookup c";

        Query query = entityManager.createQuery(sql, RdbmsDeprivationLookup.class)
                .setFirstResult(batch * BATCH_SIZE)
                .setMaxResults(BATCH_SIZE);

        List<RdbmsDeprivationLookup> results = query.getResultList();
        copyDeprivationInBatch(results, enterpriseConnection, update);

        return results.size() == BATCH_SIZE;
    }

    private static void copyDeprivationInBatch(List<RdbmsDeprivationLookup> lookups, Connection enterpriseConnection, PreparedStatement update) throws Exception {

        for (RdbmsDeprivationLookup lookup: lookups) {
            String code = lookup.getLsoaCode();
            Integer rank = lookup.getImdRank();
            Integer decile = lookup.getImdDecile();

            Integer incomeRank = lookup.getIncomeRank();
            Integer incomeDecile = lookup.getIncomeDecile();
            Integer employmentRank = lookup.getEmploymentRank();
            Integer employmentDecile = lookup.getEmploymentDecile();
            Integer educationRank = lookup.getEducationRank();
            Integer educationDecile = lookup.getEducationDecile();
            Integer healthRank = lookup.getHealthRank();
            Integer healthDecile = lookup.getHealthDecile();
            Integer crimeRank = lookup.getCrimeRank();
            Integer crimeDecile = lookup.getCrimeDecile();
            Integer housingAndServicesBarriersRank = lookup.getHousingAndServicesBarriersRank();
            Integer housingAndServicesBarriersDecile = lookup.getHousingAndServicesBarriersDecile();
            Integer livingEnvironmentRank = lookup.getLivingEnvironmentRank();
            Integer livingEnvironmentDecile = lookup.getLivingEnvironmentDecile();

            update.setInt(1, rank.intValue());
            update.setInt(2, decile.intValue());
            update.setInt(3, incomeRank.intValue());
            update.setInt(4, incomeDecile.intValue());
            update.setInt(5, employmentRank.intValue());
            update.setInt(6, employmentDecile.intValue());
            update.setInt(7, educationRank.intValue());
            update.setInt(8, educationDecile.intValue());
            update.setInt(9, healthRank.intValue());
            update.setInt(10, healthDecile.intValue());
            update.setInt(11, crimeRank.intValue());
            update.setInt(12, crimeDecile.intValue());
            update.setInt(13, housingAndServicesBarriersRank.intValue());
            update.setInt(14, housingAndServicesBarriersDecile.intValue());
            update.setInt(15, livingEnvironmentRank.intValue());
            update.setInt(16, livingEnvironmentDecile.intValue());
            update.setString(17, code);
            update.addBatch();
        }

        int[] rows = update.executeBatch();
        if (rows.length != lookups.size()) {
            throw new Exception("Mismatch in number of batches " + lookups.size() + " and number of results " + rows.length);
        }

        //check the results to see if there were any batches that updated zero rows
        for (int i=0; i<rows.length; i++) {
            if (rows[0] == 0) {
                RdbmsDeprivationLookup lookup = lookups.get(i);
                throw new Exception("Failed to update lsoa_lookup record for lsoa_code " + lookup.getLsoaCode());
            }
        }

        enterpriseConnection.commit();
    }

    private static PreparedStatement createDeprivationUpdatePreparedStatement(Connection connection) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE lsoa_lookup SET ");
        sb.append("imd_rank = ?, ");
        sb.append("imd_decile = ?, ");
        sb.append("income_rank = ?, ");
        sb.append("income_decile = ?, ");
        sb.append("employment_rank = ?, ");
        sb.append("employment_decile = ?, ");
        sb.append("education_rank = ?, ");
        sb.append("education_decile = ?, ");
        sb.append("health_rank = ?, ");
        sb.append("health_decile = ?, ");
        sb.append("crime_rank = ?, ");
        sb.append("crime_decile = ?, ");
        sb.append("housing_and_services_barriers_rank = ?, ");
        sb.append("housing_and_services_barriers_decile = ?, ");
        sb.append("living_environment_rank = ?, ");
        sb.append("living_environment_decile = ? ");
        sb.append("WHERE lsoa_code = ?");

        return connection.prepareStatement(sb.toString());
    }
}
