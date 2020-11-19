package org.endeavourhealth.core.database.rdbms.informationmodel;

import org.endeavourhealth.core.database.dal.informationmodel.TppClinicalCodesIMUpdaterDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppClinicalCodeForIMUpdate;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class RdbmsTppClinicalCodesIMUpdaterDal implements TppClinicalCodesIMUpdaterDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppClinicalCodesIMUpdaterDal.class);

    @Override
    public void updateIMForTppClinicalCodes(List<TppClinicalCodeForIMUpdate> codeList) throws Exception {

        Connection connection = ConnectionManager.getInformationModelConnection();
        try {
            String tempTableName = generateTempTableName("tpp_clinical_codes");

            String sql = "CREATE TABLE `" + tempTableName + "` ("
                    + "ctv3_term VARCHAR(255) DEFAULT NULL, "
                    + "ctv3_code VARCHAR(5) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL, "
                    + "snomed_concept_id BIGINT(20) DEFAULT NULL)";

            Statement statement = connection.createStatement(); // one-off SQL due to table name
            statement.executeUpdate(sql);
            connection.commit();
            statement.close();

            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int i = 0;

            for (TppClinicalCodeForIMUpdate code : codeList) {

                String ctv3Term = code.getCtv3Term();
                String ctv3Code = code.getCtv3Code();
                Long snomedConceptId = code.getSnomedConceptId();

                sql = "INSERT INTO `" + tempTableName + "`"
                        + " SELECT "
                        + "'" + ctv3Term.replaceAll("'","''") + "', "
                        + "'" + ctv3Code.replaceAll("'","''") + "', "
                        + snomedConceptId;

                try {
                    i++;
                    statement.addBatch(sql);
                    if(i % 10000 == 0 ) {
                        int[] executed = statement.executeBatch();
                        LOG.info("Executed statements:" + executed.length);
                        connection.commit();
                        System.gc();
                    }
                } catch (SQLException e) {
                    LOG.error("Reason: " + e.getMessage());
                    break;
                }
            }
            int[] executed = statement.executeBatch();
            LOG.info("Executed statements:" + executed.length);
            connection.commit();

            /*
            sql = "CALL tppToConceptUpdate(" + tempTableName + ")";
            statement = connection.createStatement();  // one-off SQL due to table name
            statement.executeUpdate(sql);
            statement.close();


            sql = "DROP TABLE " + tempTableName;
            statement = connection.createStatement(); //one-off SQL due to table name
            statement.executeUpdate(sql);
            statement.close();
            */

        } finally {
            // turn off auto commit
            connection.setAutoCommit(false);
            connection.close();
        }
    }

    private String generateTempTableName(String baseName) {

        return baseName + "_" + UUID.randomUUID().toString();
    }

}
