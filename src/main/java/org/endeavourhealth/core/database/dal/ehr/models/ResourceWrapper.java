package org.endeavourhealth.core.database.dal.ehr.models;

import com.google.common.base.Strings;
import org.endeavourhealth.common.fhir.ReferenceHelper;
import org.endeavourhealth.core.fhirStorage.FhirSerializationHelper;
import org.endeavourhealth.core.fhirStorage.exceptions.SerializationException;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceType;

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

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getSystemId() {
        return systemId;
    }

    public void setSystemId(UUID systemId) {
        this.systemId = systemId;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public UUID getVersion() {
        return version;
    }

    public void setVersion(UUID version) {
        this.version = version;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public String getResourceData() {
        return resourceData;
    }

    public void setResourceData(String resourceData) {
        this.resourceData = resourceData;
    }

    public Long getResourceChecksum() {
        return resourceChecksum;
    }

    public void setResourceChecksum(Long resourceChecksum) {
        this.resourceChecksum = resourceChecksum;
    }

    public UUID getExchangeBatchId() {
        return exchangeBatchId;
    }

    public void setExchangeBatchId(UUID exchangeBatchId) {
        this.exchangeBatchId = exchangeBatchId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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

    public void setResourceTypeObj(ResourceType o) {
        if (o == null) {
            this.resourceType = null;
        } else {
            this.resourceType = o.toString();
        }
    }

    public String getResourceIdStr() {
        if (resourceId == null) {
            return null;
        } else {
            return resourceId.toString();
        }
    }

    public void setResourceIdStr(String s) {
        if (Strings.isNullOrEmpty(s)) {
            this.resourceId = null;
        } else {
            this.resourceId = UUID.fromString(s);
        }
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
