package org.endeavourhealth.core.database.rdbms.publisherStaging;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherStaging.StagingTargetDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingTarget;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RdbmsStagingTargetDal implements StagingTargetDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingTargetDal.class);

    @Override
    public void processStagingForTarget(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        CallableStatement stmt = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "{call process_procedure_staging_exchange(?)}";
            stmt = connection.prepareCall(sql);

            entityManager.getTransaction().begin();

            stmt.setString(1, exchangeId.toString());

            stmt.execute();

            entityManager.getTransaction().commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            entityManager.close();
        }
    }

    @Override
    public List<StagingTarget> getTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "select  unique_id, is_delete, person_id, encounter_id, performer_personnel_id, " +
                    " dt_performed, dt_ended, " +
                    " free_text, recorded_by_personnel_id, dt_recorded, procedure_type, procedure_term, procedure_code, "+
                    " sequence_number, parent_procedure_unique_id, qualifier, location, specialty, audit_json "+
                    " from "+
                    " procedure_target "+
                    " where exchange_id = ?";

            ps = connection.prepareStatement(sql);
            ps.setString(1, exchangeId.toString());

            ResultSet rs = ps.executeQuery();
            List<StagingTarget> resultList = new ArrayList<>();
            while (rs.next()) {

                StagingTarget stagingTarget = new StagingTarget();
                stagingTarget.setUniqueId(rs.getString(1));
                stagingTarget.setIsDeleted(rs.getBoolean(2));
                stagingTarget.setPersonId(rs.getInt(3));
                stagingTarget.setEncounterId(rs.getInt(4));
                stagingTarget.setPerformerPersonnelId(rs.getInt(5));

                java.sql.Timestamp ts = rs.getTimestamp(6);
                if (ts != null) {
                    stagingTarget.setDtPerformed(new Date(ts.getTime()));
                }
                ts = rs.getTimestamp(7);
                if (ts != null) {
                    stagingTarget.setDtEnded(new Date(ts.getTime()));
                }
                stagingTarget.setFreeText(rs.getString(8));
                stagingTarget.setRecordByPersonnelId(rs.getInt(9));

                ts = rs.getTimestamp(10);
                if (ts != null) {
                    stagingTarget.setDtRecorded(new Date(ts.getTime()));
                }
                stagingTarget.setProcedureType(rs.getString(11));
                stagingTarget.setProcedureTerm(rs.getString(12));
                stagingTarget.setProcedureCode(rs.getString(13));
                stagingTarget.setProcedureSeqNbr(rs.getInt(14));
                stagingTarget.setParentProcedureUniqueId(rs.getString(15));
                stagingTarget.setQualifier(rs.getString(16));
                stagingTarget.setLocation(rs.getString(17));
                stagingTarget.setSpecialty(rs.getString(18));

                String auditJson = rs.getString(18);
                if (!Strings.isNullOrEmpty(auditJson)) {
                    stagingTarget.setAudit(ResourceFieldMappingAudit.readFromJson(auditJson));
                }

                resultList.add(stagingTarget);
            }

            return resultList;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }




//
//        try {
//            String sql = "select c"
//                    + " from "
//                    + " RdbmsStagingTarget c"
//                    + " where c.exchangeId = :exchange_id";
//
//            Query query = entityManager.createQuery(sql, RdbmsStagingTarget.class)
//                    .setParameter("exchange_id", exchangeId.toString());
//
//            List<RdbmsStagingTarget> resultList = query.getResultList();
//
//            return resultList
//                    .stream()
//                    .map(T -> {
//                        try {
//                            return new StagingTarget(T);
//                        } catch (Exception e) {
//                            return null;
//                        }
//                    })
//                    .collect(Collectors.toList());
//
////            if (resultList.size() > 0) {
////
////                LOG.debug("Target Procedures: "+resultList.size()+" from resultList for exchangeId: "+exchangeId);
////                List<StagingTarget> list = new ArrayList<>();
////                for (RdbmsStagingTarget rdbmsStagingTarget : resultList) {
////                    StagingTarget stagingTarget = new StagingTarget(rdbmsStagingTarget);
////                    list.add(stagingTarget);
////                    LOG.debug("EdsCore:  Added uniqueId:"+stagingTarget.getUniqueId()+" as hashCode: "+stagingTarget.hashCode()+" from rdbms uniqueId: "+rdbmsStagingTarget.getUniqueId());
////                }
////                return list;
////            } else {
////                return null;
////            }
//        }
//        finally {
//            if (entityManager.isOpen()) {
//                entityManager.close();
//            }
//        }
    }
}
