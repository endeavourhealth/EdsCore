package org.endeavourhealth.core.database.rdbms.informationmodel;

import org.endeavourhealth.core.database.dal.informationmodel.TppClinicalCodesIMUpdaterDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.TppClinicalCodeForIMUpdate;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class RdbmsTppClinicalCodesIMUpdaterDal implements TppClinicalCodesIMUpdaterDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsTppClinicalCodesIMUpdaterDal.class);

    @Override
    public void updateIMForTppClinicalCodes(List<TppClinicalCodeForIMUpdate> codeList) throws Exception {

        Connection connection = ConnectionManager.getInformationModelConnection();
        try {
            //turn on auto commit
            connection.setAutoCommit(true);

            String tempTableName = generateTempTableName("tpp_clinical_codes");

            String sql = "CREATE TABLE " + tempTableName + " ("
                    + "ctv3_term VARCHAR(255) DEFAULT NULL, "
                    + "ctv3_code VARCHAR(5) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL, "
                    + "snomed_concept_id BIGINT(20) DEFAULT NULL)";

            Statement statement = connection.createStatement(); // one-off SQL due to table name
            statement.executeUpdate(sql);
            statement.close();

            for (TppClinicalCodeForIMUpdate code : codeList) {

                String ctv3Term = code.getCtv3Term();
                String ctv3Code = code.getCtv3Code();
                Long snomedConceptId = code.getSnomedConceptId();

                sql = "INSERT INTO " + tempTableName
                        + " SELECT "
                        + ctv3Term + ", "
                        + ctv3Code + ", "
                        + snomedConceptId;

                statement = connection.createStatement(); // one-off SQL due to table name
                statement.executeUpdate(sql);
                statement.close();

            }

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
