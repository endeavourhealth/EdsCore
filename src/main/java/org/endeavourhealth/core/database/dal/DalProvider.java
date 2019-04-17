package org.endeavourhealth.core.database.dal;

import org.endeavourhealth.core.database.dal.admin.*;
import org.endeavourhealth.core.database.dal.audit.*;
import org.endeavourhealth.core.database.dal.audit.models.IAuditModule;
import org.endeavourhealth.core.database.dal.eds.PatientLinkDalI;
import org.endeavourhealth.core.database.dal.eds.PatientSearchDalI;
import org.endeavourhealth.core.database.dal.ehr.ResourceDalI;
import org.endeavourhealth.core.database.dal.hl7receiver.Hl7ResourceIdDalI;
import org.endeavourhealth.core.database.dal.jdbcreader.JDBCReaderDalI;
import org.endeavourhealth.core.database.dal.logback.LogbackDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.*;
import org.endeavourhealth.core.database.dal.publisherStaging.StagingCdsDalI;
import org.endeavourhealth.core.database.dal.publisherStaging.StagingCdsTailDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.*;
import org.endeavourhealth.core.database.dal.reference.*;
import org.endeavourhealth.core.database.dal.subscriberTransform.*;
import org.endeavourhealth.core.database.rdbms.admin.*;
import org.endeavourhealth.core.database.rdbms.audit.*;
import org.endeavourhealth.core.database.rdbms.eds.RdbmsPatientLinkDal;
import org.endeavourhealth.core.database.rdbms.eds.RdbmsPatientSearchDal;
import org.endeavourhealth.core.database.rdbms.ehr.RdbmsResourceDal;
import org.endeavourhealth.core.database.rdbms.hl7receiver.RdbmsHl7ResourceIdDal;
import org.endeavourhealth.core.database.rdbms.jdbcreader.RdbmsJDBCReaderDal;
import org.endeavourhealth.core.database.rdbms.logback.RdbmsLogbackDal;
import org.endeavourhealth.core.database.rdbms.publisherCommon.*;
import org.endeavourhealth.core.database.rdbms.publisherStaging.RdbmsStagingCdsDal;
import org.endeavourhealth.core.database.rdbms.publisherStaging.RdbmsStagingCdsTailDal;
import org.endeavourhealth.core.database.rdbms.publisherTransform.*;
import org.endeavourhealth.core.database.rdbms.reference.*;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DalProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DalProvider.class);

    /*private static Boolean cachedUseCassandra = null;
    private static final Object sync = new Object();*/

    public static ResourceDalI factoryResourceDal() {
        return new RdbmsResourceDal();
    }

    public static VitruCareTransformDalI factoryVitruCareTransformDal(String subscriberConfigName) {
        return new RdbmsVitruCareTransformDal(subscriberConfigName);
    }

    public static EmisTransformDalI factoryEmisTransformDal() {
        return new RdbmsEmisTransformDal();
    }

    public static TppCtv3LookupDalI factoryTppCtv3LookupDal() {
        return new RdbmsTppCtv3LookupDal();
    }

    public static ResourceIdTransformDalI factoryResourceIdTransformDal() {
        return new RdbmsResourceIdDal();
    }

    public static QueuedMessageDalI factoryQueuedMessageDal() {
        return new RdbmsQueuedMessageDal();
    }

    public static ExchangeBatchDalI factoryExchangeBatchDal() {
        return new RdbmsExchangeBatchDal();
    }

    public static ExchangeGeneralErrorDalI factoryExchangeGeneralErrorDal() {
        return new RdbmsExchangeGeneralErrorDal();
    }

    public static ExchangeProtocolErrorDalI factoryExchangeProtocolErrorDal() {
        return new RdbmsExchangeProtocolErrorDal();
    }


    public static ExchangeDalI factoryExchangeDal() {
        return new RdbmsExchangeDal();
    }

    public static UserAuditDalI factoryUserAuditDal(IAuditModule auditModule) {
        return new RdbmsUserAuditDal(auditModule);
    }

    public static SusResourceMapDalI factoryBartsSusResourceMapDal() {
        return new RdbmsSusResourceMapDal();
    }

    public static CernerCodeValueRefDalI factoryCernerCodeValueRefDal() {
        return new RdbmsCernerCodeValueRefDal();
    }

    public static TppMappingRefDalI factoryTppMappingRefDal() {
        return new RdbmsTppMappingRefDal();
    }

    public static TppImmunisationContentDalI factoryTppImmunisationContentDal() {
        return new RdbmsTppImmunisationContentDal();
    }

    public static TppConfigListOptionDalI factoryTppConfigListOptionDal() {
        return new RdbmsTppConfigListOptionDal();
    }

    public static JDBCReaderDalI factoryJDBCReaderDal() {
        return new RdbmsJDBCReaderDal();
    }

    public static ServiceDalI factoryServiceDal() {
        return new RdbmsServiceDal();
    }

    public static OrganisationDalI factoryOrganisationDal() {
        return new RdbmsOrganisationDal();
    }

    public static LibraryDalI factoryLibraryDal() {
        return new RdbmsLibraryDal();
    }

    public static SnomedDalI factorySnomedDal() {
        return new RdbmsSnomedDal();
    }

    public static PatientCohortDalI factoryPatientCohortDal() {
        return new RdbmsPatientCohortDal();
    }

    public static LinkDistributorTaskListDalI factoryLinkDistributorTaskListDal() {
        return new RdbmsLinkDistributorTaskListDal();
    }

    public static LinkDistributorPopulatorDalI factoryLinkDistributorPopulatorDal() {
        return new RdbmsLinkDistributorPopulatorDal();
    }

    public static TransformWarningDalI factoryTransformWarningDal() {
        return new RdbmsTransformWarningDal();
    }

    public static TppMultiLexToCtv3MapDalI factoryTppMultiLexToCtv3MapDal() {
        return new RdbmsTppMultiLexToCtv3MapDal();
    }

    public static TppCtv3HierarchyRefDalI factoryTppCtv3HierarchyRefDal() {
        return new RdbmsTppCtv3HierarchyRefDal();
    }

    public static CernerClinicalEventMappingDalI factoryCernerClinicalEventMappingDal() {
        return new RdbmsCernerClinicalEventMappingDal();
    }

    public static SubscriberApiAuditDalI factorySubscriberAuditApiDal() {
        return new RdbmsSubscriberApiAuditDal();
    }


    /*public static ResourceDalI factoryResourceDal() {
        if (useCassandra()) {
            return new CassandraResourceRepository();

        } else {
            return new RdbmsResourceDal();
        }
    }

    public static VitruCareTransformDalI factoryVitruCareTransformDal(String subscriberConfigName) {
        if (useCassandra()) {
            return new CassandraVitruCareRepository();

        } else {
            return new RdbmsVitruCareTransformDal(subscriberConfigName);
        }
    }

    public static EmisTransformDalI factoryEmisTransformDal() {
        if (useCassandra()) {
            return new CassandraEmisRepository();

        } else {
            return new RdbmsEmisTransformDal();
        }
    }

    public static ResourceIdTransformDalI factoryResourceIdTransformDal() {
        if (useCassandra()) {
            return new CassandraResourceIdMapRepository();

        } else {
            return new RdbmsResourceIdDal();
        }
    }

    public static QueuedMessageDalI factoryQueuedMessageDal() {
        if (useCassandra()) {
            return new CassandraQueuedMessageRepository();

        } else {
            return new RdbmsQueuedMessageDal();
        }
    }

    public static ExchangeBatchDalI factoryExchangeBatchDal() {
        if (useCassandra()) {
            return new CassandraExchangeBatchRepository();

        } else {
            return new RdbmsExchangeBatchDal();
        }
    }

    public static ExchangeDalI factoryExchangeDal() {
        if (useCassandra()) {
            return new CassandraExchangeRespository();

        } else {
            return new RdbmsExchangeDal();
        }
    }

    public static UserAuditDalI factoryUserAuditDal(IAuditModule auditModule) {
        if (useCassandra()) {
            return new CassandraUserAuditRepository(auditModule);

        } else {
            return new RdbmsUserAuditDal(auditModule);
        }
    }

    public static RdbmsBartsSusResourceMapDal factoryBartsSusResourceMapDal() {
        if (useCassandra()) {
            return null;
        } else {
            return new RdbmsBartsSusResourceMapDal();
        }
    }

    public static RdbmsCernerCodeValueRefDal factoryCernerCodeValueRefDal() {
        if (useCassandra()) {
            return null;
        } else {
            return new RdbmsCernerCodeValueRefDal();
        }
    }

    public static RdbmsJDBCReaderDal factoryJDBCReaderDal() {
        if (useCassandra()) {
            return null;
        } else {
            return new RdbmsJDBCReaderDal();
        }
    }

    public static ServiceDalI factoryServiceDal() {
        if (useCassandra()) {
            return new CassandraServiceRepository();

        } else {
            return new RdbmsServiceDal();
        }
    }

    public static OrganisationDalI factoryOrganisationDal() {
        if (useCassandra()) {
            return new CassandraOrganisationRepository();

        } else {
            return new RdbmsOrganisationDal();
        }
    }

    public static LibraryDalI factoryLibraryDal() {
        if (useCassandra()) {
            return new CassandraLibraryRepository();

        } else {
            return new RdbmsLibraryDal();
        }
    }

    public static SnomedDalI factorySnomedDal() {
        if (useCassandra()) {
            return new CassandraSnomedRepository();

        } else {
            return new RdbmsSnomedDal();
        }
    }

    public static PatientCohortDalI factoryPatientCohortDal() {
        if (useCassandra()) {
            return new CassandraPatientCohortRepository();

        } else {
            return new RdbmsPatientCohortDal();
        }
    }*/


    public static Read2ToSnomedMapDalI factoryRead2ToSnomedMapDal() {
        return new RdbmsRead2ToSnomedMapDal();
    }

    public static CTV3ToSnomedMapDalI factoryCTV3ToSnomedMapDal() {
        return new RdbmsCTV3ToSnomedMapDal();
    }

    public static PatientLinkDalI factoryPatientLinkDal() {
        return new RdbmsPatientLinkDal();
    }

    public static PatientSearchDalI factoryPatientSearchDal() {
        return new RdbmsPatientSearchDal();
    }

    public static EncounterCodeDalI factoryEncounterCodeDal() {
        return new RdbmsEncounterCodeDal();
    }

    public static PseudoIdDalI factoryPseudoIdDal(String subscriberConfigName) {
        return new RdbmsPseudoIdDal(subscriberConfigName);
    }

    public static HouseholdIdDalI factoryHouseholdIdDal(String subscriberConfigName) {
        return new RdbmsHouseholdIdDal(subscriberConfigName);
    }

    public static EnterprisePersonUpdaterHistoryDalI factoryEnterprisePersonUpdateHistoryDal(String subscriberConfigName) {
        return new RdbmsEnterprisePersonUpdaterHistoryDal(subscriberConfigName);
    }

    public static EnterpriseIdDalI factoryEnterpriseIdDal(String subscriberConfigName) {
        return new RdbmsEnterpriseIdDal(subscriberConfigName);
    }

    public static EnterpriseAgeUpdaterlDalI factoryEnterpriseAgeUpdaterlDal(String subscriberConfigName) {
        return new RdbmsEnterpriseAgeUpdaterDal(subscriberConfigName);
    }

    public static PcrPersonUpdaterHistoryDalI factoryPcrPersonUpdateHistoryDal(String subscriberConfigName) {
        return new RdbmsPcrPersonUpdaterHistoryDal(subscriberConfigName);
    }

    public static PcrIdDalI factoryPcrIdDal(String subscriberConfigName) {
        return new RdbmsPcrIdDal(subscriberConfigName);
    }


    public static PcrAgeUpdaterlDalI factoryPcrAgeUpdaterlDal(String subscriberConfigName) {
        return new RdbmsPcrAgeUpdaterDal(subscriberConfigName);
    }

    public static BartsStagingDataDalI factoryBartsStagingDataDalI() {
        return new RdbmsBartsStagingDataDal();
    }

    public static PostcodeDalI factoryPostcodeDal() {
        return new RdbmsPostcodeDal();
    }

    public static ReferenceUpdaterDalI factoryReferenceUpdaterDal() {
        return new RdbmsReferenceUpdaterDal();
    }

    public static ReferenceCopierDalI factoryReferenceCopierDal() {
        return new RdbmsReferenceCopierDal();
    }

    public static LogbackDalI factoryLogbackDal() {
        return new RdbmsLogbackDal();
    }

    public static CodingDalI factoryCodingDal() {
        return new RdbmsCodingDal();
    }

    public static ExchangeBatchExtraResourceDalI factoryExchangeBatchExtraResourceDal(String subscriberConfigName) {
        return new RdbmsExchangeBatchExtraResourcesDal(subscriberConfigName);
    }

    public static ResourceMergeDalI factoryResourceMergeDal() {
        return new RdbmsResourceMergeDal();
    }

    public static InternalIdDalI factoryInternalIdDal() {
        return new RdbmsInternalIdDal();
    }

    public static Hl7ResourceIdDalI factoryHL7ResourceDal() {
        return new RdbmsHl7ResourceIdDal();
    }

    public static SourceFileMappingDalI factorySourceFileMappingDal() {
        return new RdbmsSourceFileMappingDal();
    }

    public static Opcs4DalI factoryOpcs4Dal() {
        return new RdbmsOpcs4Dal();
    }

    public static SnomedToBnfChapterDalI factorySnomedToBnfChapter (){
        return new RdbmsSnomedToBnfChapterDal();
    }

    public static Icd10DalI factoryIcd10Dal() {
        return new RdbmsIcd10Dal();
    }

    public static PublishedFileDalI factoryPublishedFileDal() {
        return new RdbmsPublishedFileDal();
    }

    public static StagingCdsDalI factoryStagingCdsDalI () { return new RdbmsStagingCdsDal(); }

    public static StagingCdsTailDalI factoryStagingCdsTailDalI () { return new RdbmsStagingCdsTailDal(); }

    /*private static boolean useCassandra() {

        if (cachedUseCassandra == null) {
            synchronized (sync) {
                boolean b;
                try {
                    JsonNode json = ConfigManager.getConfigurationAsJson("core_db");
                    b = json.get("cassandra").asBoolean();
                } catch (Exception ex) {
                    //if the config record isn't there use Cassandra
                    b = true;
                }

                cachedUseCassandra = new Boolean(b);
                LOG.info("Using Cassandra = " + cachedUseCassandra);
            }
        }

        return cachedUseCassandra.booleanValue();
    }*/
}
