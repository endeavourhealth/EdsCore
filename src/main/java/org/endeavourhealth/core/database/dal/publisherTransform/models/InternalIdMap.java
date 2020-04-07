package org.endeavourhealth.core.database.dal.publisherTransform.models;

import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsInternalIdMap;

import java.util.Date;
import java.util.UUID;

public class InternalIdMap {

    public static final String KEY_SPLIT_CHAR = "|";
    public static final String TYPE_MRN_TO_MILLENNIUM_PERSON_ID = "MRNtoMILLPERSID";
    public static final String TYPE_MILLENNIUM_PERSON_ID_TO_MRN = "MILLPERSIDtoMRN";
    public static final String TYPE_ALTKEY_LOCATION = "ALTKEY-LOCATION";
    public static final String TYPE_ENCOUNTER_ID_TO_VISIT_ID = "ENCOUNTERIDtoVISITID";
    public static final String TYPE_VISIT_ID_TO_ENCOUNTER_ID = "VISITIDtoENCOUNTERID";
    public static final String TYPE_FIN_NO_TO_EPISODE_UUID = "FINNOtoEPISODEUUID";
    public static final String TYPE_ENCOUNTER_ID_TO_EPISODE_UUID = "ENCOUNTERIDtoEPISODEUUID";
    public static final String TYPE_AE_ARRIVAL_DT_TM_TO_EPISODE_UUID = "AEARRIVALDTTMtoEPISODEUUID";
    //public static final String TYPE_TPP_STAFF_PROFILE_ID_TO_STAFF_MEMBER_ID = "STAFFPROFILEIDtoSTAFFMEMBERID"; -- replaced with staging table
    public static final String TYPE_CERNER_ODS_CODE_TO_ORG_ID = "CERNER_ODS_CODE_TO_ORD_ID";

    private UUID serviceId = null;
    private String idType = null;
    private String sourceId = null;
    private String destinationId = null;
    private Date updatedAt = null;

    public InternalIdMap() {}

    public InternalIdMap(RdbmsInternalIdMap r) {
        this.serviceId = UUID.fromString(r.getServiceId());
        this.idType = r.getIdType();
        this.sourceId = r.getSourceId();
        this.destinationId = r.getDestinationId();
        this.updatedAt = r.getUpdatedAt();
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
