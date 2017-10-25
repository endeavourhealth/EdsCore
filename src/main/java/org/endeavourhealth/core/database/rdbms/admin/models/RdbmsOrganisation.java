package org.endeavourhealth.core.database.rdbms.admin.models;

import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.dal.admin.models.Organisation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "organisation", schema = "public")
public class RdbmsOrganisation implements Serializable {

    private String id = null;
    private String name = null;
    private String nationalId = null;
    private String services = null; //json containing a map of linked service UUIDs and names

    public RdbmsOrganisation() {}

    public RdbmsOrganisation(Organisation proxy) throws Exception {
        this.id = proxy.getId().toString();
        this.name = proxy.getName();
        this.nationalId = proxy.getNationalId();

        Map<UUID, String> map = proxy.getServices();
        this.services = ObjectMapperPool.getInstance().writeValueAsString(map);
    }

    @Id
    @Column(name = "id", nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "national_id", nullable = false)
    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    @Column(name = "services", nullable = false)
    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

}
