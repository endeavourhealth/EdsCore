package org.endeavourhealth.core.database.dal.audit.models;

public abstract class HeaderKeys {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // General
    public static final String ContentType = "content-type";

    // Message source
    public static final String MessageId = "MessageId";
    public static final String SenderLocalIdentifier = "SenderLocalIdentifier"; //the ODS code
    public static final String SourceSystem = "SourceSystem";
    public static final String SystemVersion = "SystemVersion";
    public static final String MessageEvent = "MessageEvent";
    public static final String ResponseUri = "ResponseUri";
    public static final String MessageFormat = "MessageFormat";

    // Derived from the SenderLocalIdentifier
    public static final String SenderServiceUuid = "SenderServiceUuid";
    public static final String SenderOrganisationUuid = "SenderOrganisationUuid";
    public static final String SenderSystemUuid = "SenderSystemUuid";

    //derived from the body
    public static final String DataDate = "DataDate"; //date of the date being published

    // PublisherTransform
    public static final String BatchIdsJson = "BatchIds";
    public static final String DestinationAddress = "DestinationAddress";
    public static final String TransformBatch = "TransformBatch";
    public static final String ProtocolIds = "Protocols";
    public static final String SubscriberBatch = "SubscriberBatch";
}

