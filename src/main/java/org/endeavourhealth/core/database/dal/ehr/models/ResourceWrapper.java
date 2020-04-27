package org.endeavourhealth.core.database.dal.ehr.models;

import com.google.common.base.Strings;
import org.endeavourhealth.common.fhir.ReferenceHelper;
import org.endeavourhealth.core.fhirStorage.FhirSerializationHelper;
import org.endeavourhealth.core.fhirStorage.exceptions.SerializationException;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ResourceWrapper {

    private UUID serviceId;
    private UUID systemId;
    private UUID resourceId;
    private String resourceType;
    private UUID version;
    private Date createdAt;
    private UUID patientId;
    private String resourceData;
    private Long resourceChecksum;
    private UUID exchangeBatchId;
    private boolean isDeleted;

    public ResourceWrapper() {}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(resourceType);
        sb.append("/");
        sb.append(resourceId.toString());

        if (patientId != null) {
            sb.append(", patient ");
            sb.append(patientId.toString());
        }

        sb.append(", checksum ");
        sb.append(resourceChecksum);

        sb.append(", ");
        sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt));

        if (exchangeBatchId != null) {
            sb.append(", batch ID ");
            sb.append(exchangeBatchId.toString());
        }

        if (isDeleted) {
            sb.append("\r\n");
            sb.append("DELETED");
        }
        if (resourceData != null) {
            sb.append("\r\n");
            sb.append(resourceData);
        }

        return sb.toString();
    }


    public UUID getServiceId() {
        return serviceId;
    }

    public ResourceWrapper setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public UUID getSystemId() {
        return systemId;
    }

    public ResourceWrapper setSystemId(UUID systemId) {
        this.systemId = systemId;
        return this;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public ResourceWrapper setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public String getResourceType() {
        return resourceType;
    }

    public ResourceWrapper setResourceType(String resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public UUID getVersion() {
        return version;
    }

    public ResourceWrapper setVersion(UUID version) {
        this.version = version;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public ResourceWrapper setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public ResourceWrapper setPatientId(UUID patientId) {
        this.patientId = patientId;
        return this;
    }

    public String getResourceData() {
        return resourceData;
    }

    public ResourceWrapper setResourceData(String resourceData) {
        this.resourceData = resourceData;
        return this;
    }

    public Long getResourceChecksum() {
        return resourceChecksum;
    }

    public ResourceWrapper setResourceChecksum(Long resourceChecksum) {
        this.resourceChecksum = resourceChecksum;
        return this;
    }

    public UUID getExchangeBatchId() {
        return exchangeBatchId;
    }

    public ResourceWrapper setExchangeBatchId(UUID exchangeBatchId) {
        this.exchangeBatchId = exchangeBatchId;
        return this;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public ResourceWrapper setDeleted(boolean deleted) {
        isDeleted = deleted;
        return this;
    }

    public String getReferenceString() {
        return ReferenceHelper.createResourceReference(resourceType, resourceId.toString());
    }

    public Reference getReference() {
        return ReferenceHelper.createReference(resourceType, resourceId.toString());
    }

    /**
     * helpers
     */
    public ResourceType getResourceTypeObj() {
        if (Strings.isNullOrEmpty(resourceType)) {
            return null;
        }
        return ResourceType.valueOf(resourceType);
    }

    public ResourceWrapper setResourceTypeObj(ResourceType o) {
        if (o == null) {
            this.resourceType = null;
        } else {
            this.resourceType = o.toString();
        }
        return this;
    }

    public String getResourceIdStr() {
        if (resourceId == null) {
            return null;
        } else {
            return resourceId.toString();
        }
    }

    public ResourceWrapper setResourceIdStr(String s) {
        if (Strings.isNullOrEmpty(s)) {
            this.resourceId = null;
        } else {
            this.resourceId = UUID.fromString(s);
        }
        return this;
    }

    public <T extends Resource> T getResourceAs(T cls) throws SerializationException {
        return (T)getResource();
    }

    public Resource getResource() throws SerializationException {
        if (isDeleted) {
            return null;
        } else {
            return FhirSerializationHelper.deserializeResource(this.getResourceData());
        }
    }
}
