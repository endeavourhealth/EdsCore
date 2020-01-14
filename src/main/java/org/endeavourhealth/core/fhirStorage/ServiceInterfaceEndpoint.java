package org.endeavourhealth.core.fhirStorage;

import java.util.UUID;

public final class ServiceInterfaceEndpoint {

    public static final String STATUS_NORMAL = "Publisher_Normal"; //published data accepted and processed as normal
    public static final String STATUS_DRAFT = "Publisher_Draft"; //published data will be rejected by Messaging API
    public static final String STATUS_AUTO_FAIL = "Publisher_Auto_Fail"; //published data will automatically fail the inbound transform (way to clear a queue)
    public static final String STATUS_BULK_PROCESSING = "Publisher_Bulk"; //published data accepted but routed to separate queued for processing

    private UUID systemUuid = null;
    private UUID technicalInterfaceUuid = null;

    //used for subscriber interfaces to give the config record name containing the technical details
    //and used for publisher interfaces to give the publisher state, one of the above constants
    private String endpoint = null;

    public ServiceInterfaceEndpoint() {
    }

    public UUID getSystemUuid() {
        return systemUuid;
    }

    public void setSystemUuid(UUID systemUuid) {
        this.systemUuid = systemUuid;
    }

    public UUID getTechnicalInterfaceUuid() {
        return technicalInterfaceUuid;
    }

    public void setTechnicalInterfaceUuid(UUID technicalInterfaceUuid) {
        this.technicalInterfaceUuid = technicalInterfaceUuid;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
