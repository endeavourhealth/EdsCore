package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.BartsStagingDataDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.BartsStagingDataProcedure;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsBartsStagingData;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.Enumerations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

public class RdbmsBartsStagingDataDal implements BartsStagingDataDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsBartsStagingDataDal.class);



    @Override
    public List<UUID> getSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType) throws Exception {
        return null;
    }

    @Override
    public void saveBartsStagingDataProcedure(BartsStagingDataProcedure bartsStagingDataProcedure) throws Exception {

        if (bartsStagingDataProcedure == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        RdbmsBartsStagingData dbObj = new RdbmsBartsStagingData(bartsStagingDataProcedure);
        UUID serviceId = bartsStagingDataProcedure.getServiceId();

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisMapping);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            //primary key (service_id, nomenclature_id)
            String sql = "INSERT INTO staging_procedure "
                    + "(exchange_id,encntr_id, person_id, consultant, "
                    + "proc_dt_tm, updt_by,create_dt_tm, proc_cd_type, proc_cd)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_id = VALUES(exchange_id), "
                    + " person_id = VALUES(person_id),"
                    + " consultant = VALUES(consultant),"
                    + " proc_dt_tm = VALUES(proc_dt_tm),"
                    + " updt_by = VALUES (updt_by),"
                    + " create_dt_tm = VALUES(create_dt_tm),"
                    + " proc_cd_type = VALUES(proc_cd_type),"
                    + " proc_cd = VALUES(proc_cd)";

            int col = 1;

            ps = connection.prepareStatement(sql);

            ps.setString(1, dbObj.getExchangeId());
            ps.setInt(2, dbObj.getEncounterId());
           ps.setInt(3,dbObj.getPersonId());
           ps.setString(4, dbObj.getConsultant());
            java.sql.Date procSqlDate = new java.sql.Date(dbObj.getProc_dt_tm().getTime());
           ps.setDate(5,procSqlDate);
           ps.setInt(6,dbObj.getUpdatedBy());
            java.sql.Date createSqlDate = new java.sql.Date(dbObj.getCreate_dt_tm().getTime());
            ps.setDate(7,createSqlDate);
            ps.setString(8,dbObj.getProcedureCode());
            ps.setString(9,dbObj.getProcedureCodeType());



//            if (dbObj.getAuditJson() == null) {
//                ps.setNull(11, Types.VARCHAR);
//            } else {
//                ps.setString(11, dbObj.getAuditJson());
//            }

            ps.executeUpdate();

            //transaction.commit();
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

}
