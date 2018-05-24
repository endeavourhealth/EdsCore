package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "pcr_organisation_id_map")
public class RdbmsPcrOrganisationIdMap implements Serializable {

    private String serviceId = null;
    private Long pcrId = null;

    public RdbmsPcrOrganisationIdMap() {}

    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "pcr_id", nullable = false)
    public Long getPcrId() {
        return pcrId;
    }

    public void setPcrId(Long pcrId) {
        this.pcrId = pcrId;
    }
}
