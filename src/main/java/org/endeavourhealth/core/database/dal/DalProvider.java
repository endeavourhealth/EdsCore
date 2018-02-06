package org.endeavourhealth.core.database.dal;

import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.core.database.cassandra.admin.*;
import org.endeavourhealth.core.database.cassandra.audit.CassandraExchangeRespository;
import org.endeavourhealth.core.database.cassandra.audit.CassandraUserAuditRepository;
import org.endeavourhealth.core.database.cassandra.ehr.CassandraExchangeBatchRepository;
import org.endeavourhealth.core.database.cassandra.ehr.CassandraResourceRepository;
import org.endeavourhealth.core.database.cassandra.transform.CassandraEmisRepository;
import org.endeavourhealth.core.database.cassandra.transform.CassandraResourceIdMapRepository;
import org.endeavourhealth.core.database.cassandra.transform.CassandraVitruCareRepository;
import org.endeavourhealth.core.database.dal.admin.LibraryDalI;
import org.endeavourhealth.core.database.dal.admin.OrganisationDalI;
import org.endeavourhealth.core.database.dal.admin.PatientCohortDalI;
import org.endeavourhealth.core.database.dal.admin.ServiceDalI;
import org.endeavourhealth.core.database.dal.audit.ExchangeBatchDalI;
import org.endeavourhealth.core.database.dal.audit.ExchangeDalI;
import org.endeavourhealth.core.database.dal.audit.QueuedMessageDalI;
import org.endeavourhealth.core.database.dal.audit.UserAuditDalI;
import org.endeavourhealth.core.database.dal.audit.models.IAuditModule;
import org.endeavourhealth.core.database.dal.eds.PatientLinkDalI;
import org.endeavourhealth.core.database.dal.eds.PatientSearchDalI;
import org.endeavourhealth.core.database.dal.ehr.ResourceDalI;
import org.endeavourhealth.core.database.dal.hl7receiver.Hl7ResourceIdDalI;
import org.endeavourhealth.core.database.dal.logback.LogbackDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.EmisTransformDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.InternalIdDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.ResourceIdTransformDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.ResourceMergeDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.SourceFileMappingDalI;
import org.endeavourhealth.core.database.dal.reference.*;
import org.endeavourhealth.core.database.dal.subscriberTransform.*;
import org.endeavourhealth.core.database.rdbms.admin.RdbmsLibraryDal;
import org.endeavourhealth.core.database.rdbms.admin.RdbmsOrganisationDal;
import org.endeavourhealth.core.database.rdbms.admin.RdbmsPatientCohortDal;
import org.endeavourhealth.core.database.rdbms.admin.RdbmsServiceDal;
import org.endeavourhealth.core.database.rdbms.audit.RdbmsExchangeBatchDal;
import org.endeavourhealth.core.database.rdbms.audit.RdbmsExchangeDal;
import org.endeavourhealth.core.database.rdbms.audit.RdbmsQueuedMessageDal;
import org.endeavourhealth.core.database.rdbms.audit.RdbmsUserAuditDal;
import org.endeavourhealth.core.database.rdbms.eds.RdbmsPatientLinkDal;
import org.endeavourhealth.core.database.rdbms.eds.RdbmsPatientSearchDal;
import org.endeavourhealth.core.database.rdbms.ehr.RdbmsResourceDal;
import org.endeavourhealth.core.database.rdbms.hl7receiver.RdbmsHl7ResourceIdDal;
import org.endeavourhealth.core.database.rdbms.jdbcreader.RdbmsJDBCReaderDal;
import org.endeavourhealth.core.database.rdbms.logback.RdbmsLogbackDal;
import org.endeavourhealth.core.database.rdbms.publisherCommon.RdbmsEmisTransformDal;
import org.endeavourhealth.core.database.rdbms.publisherTransform.RdbmsBartsSusResourceMapDal;
import org.endeavourhealth.core.database.rdbms.publisherTransform.RdbmsInternalIdDal;
import org.endeavourhealth.core.database.rdbms.publisherTransform.RdbmsResourceIdDal;
import org.endeavourhealth.core.database.rdbms.publisherTransform.RdbmsResourceMergeDal;
import org.endeavourhealth.core.database.rdbms.publisherTransform.RdbmsSourceFileMappingDal;
import org.endeavourhealth.core.database.rdbms.reference.*;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DalProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DalProvider.class);

    private static Boolean cachedUseCassandra = null;
    private static final Object sync = new Object();

    public static ResourceDalI factoryResourceDal() {
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

    public static Read2ToSnomedMapDalI factoryRead2ToSnomedMapDal() {
        return new RdbmsRead2ToSnomedMapDal();
    }

    public static CTV3ToSnomedMapDalI factoryCTV3ToSnomedMapDal() {
        return new RdmsCTV3ToSnomedMapDal();
    }

    public static PatientCohortDalI factoryPatientCohortDal() {
        if (useCassandra()) {
            return new CassandraPatientCohortRepository();

        } else {
            return new RdbmsPatientCohortDal();
        }
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

    public static Icd10DalI factoryIcd10Dal() {
        return new RdbmsIcd10Dal();
    }

    private static boolean useCassandra() {

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
    }
}
