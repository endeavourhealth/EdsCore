package org.endeavourhealth.core.database.rdbms.datasharingmanager;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.DataProcessingAgreementDalI;
import org.endeavourhealth.core.database.dal.datasharingmanager.MasterMappingDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.DataProcessingAgreementEntity;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.RegionEntity;
import org.endeavourhealth.core.database.dal.datasharingmanager.enums.MapType;
import org.endeavourhealth.core.database.dal.usermanager.caching.DataProcessingAgreementCache;
import org.endeavourhealth.core.database.dal.usermanager.caching.RegionCache;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class RdbmsCoreDataProcessingAgreementDal implements DataProcessingAgreementDalI {

    private static MasterMappingDalI masterMappingRepository = DalProvider.factoryDSMMasterMappingDal();

    public DataProcessingAgreementEntity getDPA(String uuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            DataProcessingAgreementEntity ret = entityManager.find(DataProcessingAgreementEntity.class, uuid);

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public List<DataProcessingAgreementEntity> getDPAsFromList(List<String> dpas) throws Exception {
        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<DataProcessingAgreementEntity> cq = cb.createQuery(DataProcessingAgreementEntity.class);
            Root<DataProcessingAgreementEntity> rootEntry = cq.from(DataProcessingAgreementEntity.class);

            Predicate predicate = rootEntry.get("uuid").in(dpas);

            cq.where(predicate);
            TypedQuery<DataProcessingAgreementEntity> query = entityManager.createQuery(cq);

            List<DataProcessingAgreementEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public List<DataProcessingAgreementEntity> getDataProcessingAgreementsForOrganisation(String odsCode) throws Exception {

        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            Query query = entityManager.createQuery(
                    "select dpa from DataProcessingAgreementEntity dpa " +
                            "inner join MasterMappingEntity mm on mm.parentUuid = dpa.uuid and mm.parentMapTypeId = :dpaType " +
                            "inner join OrganisationEntity o on o.uuid = mm.childUuid " +
                            "where o.odsCode = :ods " +
                            "and mm.childMapTypeId = :publisherType " +
                            "and (dpa.startDate is null or (dpa.startDate is not null and dpa.startDate <= current_date)) " +
                            "and (dpa.endDate is null or dpa.endDate >= current_date) " +
                            "and dpa.dsaStatusId = 0 ");
            query.setParameter("dpaType", MapType.DATAPROCESSINGAGREEMENT.getMapType());
            query.setParameter("ods", odsCode);
            query.setParameter("publisherType", MapType.PUBLISHER.getMapType());

            List<DataProcessingAgreementEntity> result = query.getResultList();

            return result;
        } finally {
            entityManager.close();
        }
    }

    /*public List<DataProcessingAgreementEntity> getDataProcessingAgreementsForOrganisationAndSystemType(String odsCode, String systemName) throws Exception {

        EntityManager entityManager = ConnectionManager.getDsmEntityManager();

        try {
            Query query = entityManager.createQuery(
                    "select dpa from DataProcessingAgreementEntity dpa " +
                            "inner join MasterMappingEntity mm on mm.parentUuid = dpa.uuid and mm.parentMapTypeId = :dpaType " +
                            "inner join OrganisationEntity o on o.uuid = mm.childUuid and mm.childMapTypeId = :publisherType " +
                            "inner join MasterMappingEntity mdf on mdf.parentUuid = dpa.uuid and mdf.parentMapTypeId = :dpaType " +
                            "inner join DataFlowEntity df on df.uuid = mdf.childUuid and mm.childMapTypeId = :dataFlowType " +
                            "inner join MasterMappingEntity mde on mde.parentUuid = df.uuid and mde.parentMapTypeId = :dataFlowType " +
                            "inner join DataExchangeEntity de on de.uuid = mde.childUuid and mde.childMapTypeId = :exchangeType " +
                            "where o.odsCode = :ods " +
                            " and de.systemName = :sysName " +
                            "and (dpa.startDate is not null and dpa.startDate <= current_date) " +
                            "and (dpa.endDate is null or dpa.endDate >= current_date) " +
                            "and dpa.dsaStatusId = 0 ");
            query.setParameter("dpaType", MapType.DATAPROCESSINGAGREEMENT.getMapType());
            query.setParameter("ods", odsCode);
            query.setParameter("sysName", systemName);
            query.setParameter("publisherType", MapType.PUBLISHER.getMapType());
            query.setParameter("dataFlowType", MapType.DATAFLOW.getMapType());
            query.setParameter("exchangeType", MapType.DATAEXCHANGE.getMapType());

            List<DataProcessingAgreementEntity> result = query.getResultList();

            return result;
        } finally {
            entityManager.close();
        }
    }*/

    public List<DataProcessingAgreementEntity> getAllDPAsForAllChildRegions(String regionUUID) throws Exception {
        List<String> dpaUUIDs = new ArrayList<>();

        List<RegionEntity> allRegions = RegionCache.getAllChildRegionsForRegion(regionUUID);

        for (RegionEntity region : allRegions) {
            dpaUUIDs.addAll(masterMappingRepository.getChildMappings(region.getUuid(), MapType.REGION.getMapType(), MapType.DATAPROCESSINGAGREEMENT.getMapType()));
        }

        List<DataProcessingAgreementEntity> ret = new ArrayList<>();

        if (!dpaUUIDs.isEmpty())
            ret = DataProcessingAgreementCache.getDPADetails(dpaUUIDs);

        return ret;

    }
}
