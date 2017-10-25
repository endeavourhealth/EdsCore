package org.endeavourhealth.core.database.cassandra.admin.models;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.endeavourhealth.core.database.dal.admin.models.Organisation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Table(keyspace = "admin", name = "organisation")
public class CassandraOrganisation {
    @PartitionKey
    @Column(name = "id")
    private UUID id;
    @Column(name = "name")
    private String name;
    @Column(name = "national_id")
    private String nationalId;
    @Column(name = "services")
    private Map<UUID, String> services = new HashMap<>();

    public CassandraOrganisation() {}

    public CassandraOrganisation(Organisation proxy) {
        this.id = proxy.getId();
        this.name = proxy.getName();
        this.nationalId = proxy.getNationalId();
        this.services = new HashMap<>(proxy.getServices());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }


    public Map<UUID, String> getServices() {
        return services;
    }

    public void setServices(Map<UUID, String> services) {
        if (services == null)
            this.services = new HashMap<>();
        else
            this.services = services;
    }
}