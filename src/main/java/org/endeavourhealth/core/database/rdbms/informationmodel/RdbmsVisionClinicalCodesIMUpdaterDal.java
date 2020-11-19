package org.endeavourhealth.core.database.rdbms.informationmodel;

import org.endeavourhealth.core.database.dal.informationmodel.VisionClinicalCodesIMUpdaterDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.VisionClinicalCodeForIMUpdate;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class RdbmsVisionClinicalCodesIMUpdaterDal implements VisionClinicalCodesIMUpdaterDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsVisionClinicalCodesIMUpdaterDal.class);

    @Override
    public void updateIMForVisionClinicalCodes(List<VisionClinicalCodeForIMUpdate> codeList) throws Exception {

        Connection connection = ConnectionManager.getInformationModelConnection();
        try {
            //turn on auto commit
            connection.setAutoCommit(true);

            String tempTableName = generateTempTableName("vision_clinical_codes");

            String sql = "CREATE TABLE `" + tempTableName + "` ("
                    + "read_term VARCHAR(500) DEFAULT NULL, "
                    + "read_code VARCHAR(250) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL, "
                    + "snomed_concept_id BIGINT(20) DEFAULT NULL, "
                    + "is_vision_code TINYINT (1) NOT NULL)";

            Statement statement = connection.createStatement(); // one-off SQL due to table name
            statement.executeUpdate(sql);
            statement.close();

            for (VisionClinicalCodeForIMUpdate code : codeList) {

                String readTerm = code.getReadTerm();
                String readCode = code.getReadCode();
                Long snomedConceptId = code.getSnomedConceptId();
                boolean isVisionCode = code.getIsVisionCode();

                sql = "INSERT INTO `" + tempTableName + "`"
                        + " SELECT "
                        + "'" + readTerm.replaceAll("'","''") + "', "
                        + "'" + readCode + "', "
                        + snomedConceptId + ","
                        + isVisionCode;

                statement = connection.createStatement(); // one-off SQL due to table name
                LOG.info(sql);
                statement.executeUpdate(sql);
                statement.close();

            }

            /*
            sql = "CALL visionToConceptUpdate(" + tempTableName + ")";
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
