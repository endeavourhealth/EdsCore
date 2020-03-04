package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.datasharingmanager.MasterMappingDalI;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.RdbmsCoreMasterMappingDal;
import org.endeavourhealth.core.database.dal.datasharingmanager.enums.MapType;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonOrganisation;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@NamedQueries({
        @NamedQuery(name="organisation.total",
                query="select 'Total number of organisations', count(o.uuid) " +
                "from OrganisationEntity o " +
                        "where o.isService = 0"),
        @NamedQuery(name="organisation.bulk",
                query="select 'Bulk imported organisations', count(o.uuid) "  +
                        "from OrganisationEntity o " +
                        "where o.isService = 0 " +
                        "and o.bulkImported = 1"),
        @NamedQuery(name="organisation.editedBulk",
                query="select 'Edited bulk imported organisations', count(o.uuid) " +
                        "from OrganisationEntity o " +
                        "where o.isService = 0 " +
                        "and o.bulkImported = 1 " +
                        "and o.bulkItemUpdated = 1"),
        @NamedQuery(name="organisation.manual",
                query="select 'Manually created organisations', count(o.uuid) " +
                        "from OrganisationEntity o " +
                        "where o.isService = 0 " +
                        "and o.bulkImported = 0 "),
        @NamedQuery(name="region.total",
                query="select 'Total number of regions', count(o.uuid) " +
                        "from RegionEntity o"),
        @NamedQuery(name="region.withDSA",
                query="select 'Regions containing a data sharing agreement', count(distinct r.uuid) " +
                        "from RegionEntity r " +
                        "inner join MasterMappingEntity mm on mm.parentUuid = r.uuid and mm.parentMapTypeId = 2 " +
                        "inner join DataSharingAgreementEntity dsa on dsa.uuid = mm.childUuid and mm.childMapTypeId = 3"),
        @NamedQuery(name="region.withOrganisation",
                query="select 'Regions containing an organisation', count(distinct r.uuid) " +
                        "from RegionEntity r " +
                        "inner join MasterMappingEntity mm on mm.parentUuid = r.uuid and mm.parentMapTypeId = 2 " +
                        "inner join OrganisationEntity o on o.uuid = mm.childUuid and mm.childMapTypeId = 1"),
        @NamedQuery(name="region.withRegion",
                query="select 'Regions containing a region', count(distinct r.uuid) " +
                        "from RegionEntity r " +
                        "inner join MasterMappingEntity mm on mm.parentUuid = r.uuid and mm.parentMapTypeId = 2 " +
                        "inner join RegionEntity cr on cr.uuid = mm.childUuid and mm.childMapTypeId = 2"),
        @NamedQuery(name="region.belongingToRegion",
                query="select 'Regions belonging to a region', count(distinct cr.uuid) " +
                        "from RegionEntity r " +
                        "inner join MasterMappingEntity mm on mm.parentUuid = r.uuid and mm.parentMapTypeId = 2 " +
                        "inner join RegionEntity cr on cr.uuid = mm.childUuid and mm.childMapTypeId = 2"),
        @NamedQuery(name="region.orphaned",
                query="select 'Orphaned regions', count(distinct r.uuid) " +
                        "from RegionEntity r " +
                        "left outer join MasterMappingEntity mmp on mmp.parentUuid = r.uuid and mmp.parentMapTypeId = 2 " +
                        "left outer join MasterMappingEntity mmc on mmc.childUuid= r.uuid and mmc.childMapTypeId = 2 " +
                        "where mmp.parentUuid is null " +
                        "and mmc.parentUuid is null"),
        @NamedQuery(name="service.total",
                query="select 'Total number of services', count(distinct s.uuid) " +
                        "from OrganisationEntity s " +
                        "where s.isService = 1"),
        @NamedQuery(name="service.withOrganisation",
                query="select 'Services linked to an organisation', count(distinct s.uuid) " +
                        "from OrganisationEntity s " +
                        "join MasterMappingEntity mm on mm.childUuid = s.uuid and mm.childMapTypeId = 0 " +
                        "where s.isService = 1"),
        @NamedQuery(name="service.orphaned",
                query="select 'Orphaned services', count(distinct s.uuid) " +
                        "from OrganisationEntity s " +
                        "left outer join MasterMappingEntity mm on mm.childUuid = s.uuid and mm.childMapTypeId = 0 " +
                        "where s.isService = 1 " +
                        "and mm.childUuid is null"),
        @NamedQuery(name="cohort.total",
                query="select 'Total number of cohorts', count(distinct c.uuid) " +
                        "from CohortEntity c "),
        @NamedQuery(name="dataFlow.total",
                query="select 'Total number of data flows', count(distinct df.uuid) " +
                        "from DataFlowEntity df "),
        @NamedQuery(name="dataExchange.total",
                query="select 'Total number of data exchanges', count(distinct de.uuid) " +
                        "from DataExchangeEntity de "),
        @NamedQuery(name="dataExchange.totalPubs",
                query="select 'Total number of publisher data exchanges', count(distinct de.uuid) " +
                        "from DataExchangeEntity de " +
                        "where de.publisher = 1  "),
        @NamedQuery(name="dataExchange.totalSubs",
                query="select 'Total number of subscriber data exchanges', count(distinct de.uuid) " +
                        "from DataExchangeEntity de " +
                        "where de.publisher = 0  "),
        @NamedQuery(name="dataExchange.totalVolume",
                query="select 'Total volume for all data flows', coalesce(sum(de.approximateVolume), 0) " +
                        "from DataExchangeEntity de "),
        @NamedQuery(name="dataExchange.averageVolume",
                query="select 'Average volume for data flows', coalesce(avg(de.approximateVolume), 0) " +
                        "from DataExchangeEntity de "),
        @NamedQuery(name="dpa.total",
                query="select 'Total number of data processing agreements', count(distinct dpa.uuid) " +
                        "from DataProcessingAgreementEntity dpa "),
        @NamedQuery(name="dataSet.total",
                query="select 'Total number of data sets', count(distinct ds.uuid) " +
                        "from DataSetEntity ds "),
        @NamedQuery(name="dsa.total",
                query="select 'Total number of data sharing agreements', count(distinct dsa.uuid) " +
                        "from DataSharingAgreementEntity dsa "),
        @NamedQuery(name="dsa.withRegion",
                query="select 'Data sharing agreements belonging to a region', count(distinct dsa.uuid) " +
                        "from DataSharingAgreementEntity dsa " +
                        "inner join MasterMappingEntity mm on mm.childUuid = dsa.uuid and mm.childMapTypeId = 3 " +
                        "inner join RegionEntity r on r.uuid = mm.parentUuid and mm.parentMapTypeId = 2"),
        @NamedQuery(name="dss.total",
                query="select 'Total number of sharing summaries', count(distinct dss.uuid) " +
                        "from DataSharingSummaryEntity dss "),
        @NamedQuery(name="project.total",
                query="select 'Total number of projects', count(distinct p.uuid) " +
                        "from ProjectEntity p "),
})
@Entity
@Table(name = "organisation", schema = "data_sharing_manager")
public class OrganisationEntity {
    private String uuid;
    private String name;
    private String alternativeName;
    private String odsCode;
    private String icoCode;
    private String igToolkitStatus;
    private Date dateOfRegistration;
    private String registrationPerson;
    private String evidenceOfRegistration;
    private byte isService;
    private byte bulkImported;
    private byte bulkItemUpdated;
    private String bulkConflictedWith;
    private byte type;
    private byte active;
    private Short systemSupplierSystemId;
    private String systemSupplierReference;
    private Byte systemSupplierSharingActivated;
    @Transient private List<String> regions = new ArrayList<>();
    @Transient private List<String> parentOrganisations = new ArrayList<>();
    @Transient private List<String> childOrganisations = new ArrayList<>();
    @Transient private List<String> services = new ArrayList<>();
    @Transient private List<String> dpaPublishing = new ArrayList<>();
    @Transient private List<String> dsaPublishing = new ArrayList<>();
    @Transient private List<String> dsaSubscribing = new ArrayList<>();
    @Transient private List<AddressEntity> addresses = new ArrayList<>();

    public OrganisationEntity() {    }

    public OrganisationEntity(JsonOrganisation organisation) {  updateFromJson(organisation);  }

    public void updateFromJson(JsonOrganisation organisation) {
        this.uuid = organisation.getUuid();
        this.name = organisation.getName();
        this.alternativeName = organisation.getAlternativeName();
        this.odsCode = organisation.getOdsCode();
        this.icoCode = organisation.getIcoCode();
        this.igToolkitStatus = organisation.getIgToolkitStatus();
        if (organisation.getDateOfRegistration() != null) {
            this.dateOfRegistration = Date.valueOf(organisation.getDateOfRegistration());
        }
        this.registrationPerson = organisation.getRegistrationPerson();
        this.evidenceOfRegistration = organisation.getEvidenceOfRegistration();
        this.isService = (byte) (organisation.getIsService().equals("1") ? 1 : 0);
        this.bulkImported = (byte) (organisation.getBulkImported().equals("1") ? 1 : 0);
        this.bulkItemUpdated = (byte) (organisation.getBulkItemUpdated().equals("1") ? 1 : 0);
        this.bulkConflictedWith = organisation.getBulkConflictedWith();
        this.type = organisation.getType();
        this.active =(byte) (organisation.isActive() ? 1 : 0);
        this.systemSupplierSystemId = organisation.getSystemSupplierSystemId();
        this.systemSupplierReference = organisation.getSystemSupplierReference();
        this.systemSupplierSharingActivated = organisation.getSystemSupplierSharingActivated();

        if (organisation.getRegions() != null) {
            organisation.getRegions().forEach((k, v) -> this.regions.add(k.toString()));
        }
        if (organisation.getParentOrganisations() != null) {
            organisation.getParentOrganisations().forEach((k, v) -> this.parentOrganisations.add(k.toString()));
        }
        if (organisation.getChildOrganisations() != null) {
            organisation.getChildOrganisations().forEach((k, v) -> this.childOrganisations.add(k.toString()));
        }
        if (organisation.getServices() != null) {
            organisation.getServices().forEach((k, v) -> this.services.add(k.toString()));
        }
        if (organisation.getDpaPublishing() != null) {
            organisation.getDpaPublishing().forEach((k, v) -> this.dpaPublishing.add(k.toString()));
        }
        if (organisation.getDsaPublishing() != null) {
            organisation.getDsaPublishing().forEach((k, v) -> this.dsaPublishing.add(k.toString()));
        }
        if (organisation.getDsaSubscribing() != null) {
            organisation.getDsaSubscribing().forEach((k, v) -> this.dsaSubscribing.add(k.toString()));
        }
        if (organisation.getAddresses() != null) {
            organisation.getAddresses().forEach((a) -> this.addresses.add(new AddressEntity(a)));
        }
    }

    public void setMappingsFromDAL () throws Exception {
        MasterMappingDalI masterMappingRepository = DalProvider.factoryDSMMasterMappingDal();
        Short thisMapType = MapType.ORGANISATION.getMapType();

        this.setRegions(masterMappingRepository.getParentMappings(this.uuid, thisMapType, MapType.REGION.getMapType()));
        this.setParentOrganisations(masterMappingRepository.getParentMappings(this.uuid, thisMapType, thisMapType));
        this.setChildOrganisations(masterMappingRepository.getChildMappings(this.uuid, thisMapType, thisMapType));
        this.setServices(masterMappingRepository.getChildMappings(this.uuid, thisMapType, MapType.SERVICE.getMapType()));
        this.setDpaPublishing(masterMappingRepository.getParentMappings(this.uuid, MapType.PUBLISHER.getMapType(), MapType.DATAPROCESSINGAGREEMENT.getMapType()));
        this.setDsaPublishing(masterMappingRepository.getParentMappings(this.uuid, MapType.PUBLISHER.getMapType(), MapType.DATASHARINGAGREEMENT.getMapType()));
        this.setDsaSubscribing(masterMappingRepository.getParentMappings(this.uuid, MapType.SUBSCRIBER.getMapType(), MapType.DATASHARINGAGREEMENT.getMapType()));
    }

    @Id
    @Column(name = "uuid", nullable = false, length = 36)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "alternative_name", nullable = true, length = 100)
    public String getAlternativeName() {
        return alternativeName;
    }

    public void setAlternativeName(String alternativeName) {
        this.alternativeName = alternativeName;
    }

    @Basic
    @Column(name = "ods_code", nullable = true, length = 10)
    public String getOdsCode() {
        return odsCode;
    }

    public void setOdsCode(String odsCode) {
        this.odsCode = odsCode;
    }

    @Basic
    @Column(name = "ico_code", nullable = true, length = 10)
    public String getIcoCode() {
        return icoCode;
    }

    public void setIcoCode(String icoCode) {
        this.icoCode = icoCode;
    }

    @Basic
    @Column(name = "ig_toolkit_status", nullable = true, length = 10)
    public String getIgToolkitStatus() {
        return igToolkitStatus;
    }

    public void setIgToolkitStatus(String igToolkitStatus) {
        this.igToolkitStatus = igToolkitStatus;
    }

    @Basic
    @Column(name = "date_of_registration", nullable = true)
    public Date getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(Date dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    @Basic
    @Column(name = "registration_person", nullable = true, length = 36)
    public String getRegistrationPerson() {
        return registrationPerson;
    }

    public void setRegistrationPerson(String registrationPerson) {
        this.registrationPerson = registrationPerson;
    }

    @Basic
    @Column(name = "evidence_of_registration", nullable = true, length = 500)
    public String getEvidenceOfRegistration() {
        return evidenceOfRegistration;
    }

    public void setEvidenceOfRegistration(String evidenceOfRegistration) {
        this.evidenceOfRegistration = evidenceOfRegistration;
    }

    @Basic
    @Column(name = "is_service", nullable = false)
    public byte getIsService() {
        return isService;
    }

    public void setIsService(byte isService) {
        this.isService = isService;
    }

    @Basic
    @Column(name = "type", nullable = false, length = 40)
    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    @Basic
    @Column(name = "bulk_imported", nullable = false)
    public byte getBulkImported() {
        return bulkImported;
    }

    public void setBulkImported(byte bulkImported) {
        this.bulkImported = bulkImported;
    }

    @Basic
    @Column(name = "bulk_item_updated", nullable = false)
    public byte getBulkItemUpdated() {
        return bulkItemUpdated;
    }

    public void setBulkItemUpdated(byte bulkItemUpdated) {
        this.bulkItemUpdated = bulkItemUpdated;
    }

    @Basic
    @Column(name = "bulk_conflicted_with", nullable = true, length = 36)
    public String getBulkConflictedWith() {
        return bulkConflictedWith;
    }

    public void setBulkConflictedWith(String bulkConflictedWith) {
        this.bulkConflictedWith = bulkConflictedWith;
    }

    @Basic
    @Column(name = "system_supplier_system_id", nullable = true)
    public Short getSystemSupplierSystemId() { return systemSupplierSystemId; }

    public void setSystemSupplierSystemId(Short systemSupplierSystemId) { this.systemSupplierSystemId = systemSupplierSystemId; }

    @Basic
    @Column(name = "system_supplier_reference", nullable = true, length = 50)
    public String getSystemSupplierReference() { return systemSupplierReference; }

    public void setSystemSupplierReference(String systemSupplierReference) { this.systemSupplierReference = systemSupplierReference; }

    @Basic
    @Column(name = "system_supplier_sharing_activated", nullable = true)
    public Byte getSystemSupplierSharingActivated() { return systemSupplierSharingActivated; }

    public void setSystemSupplierSharingActivated(Byte activated) {this.systemSupplierSharingActivated = activated; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganisationEntity that = (OrganisationEntity) o;

        if (isService != that.isService) return false;
        if (bulkImported != that.bulkImported) return false;
        if (bulkItemUpdated != that.bulkItemUpdated) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (alternativeName != null ? !alternativeName.equals(that.alternativeName) : that.alternativeName != null)
            return false;
        if (odsCode != null ? !odsCode.equals(that.odsCode) : that.odsCode != null) return false;
        if (icoCode != null ? !icoCode.equals(that.icoCode) : that.icoCode != null) return false;
        if (igToolkitStatus != null ? !igToolkitStatus.equals(that.igToolkitStatus) : that.igToolkitStatus != null)
            return false;
        if (dateOfRegistration != null ? !dateOfRegistration.equals(that.dateOfRegistration) : that.dateOfRegistration != null)
            return false;
        if (registrationPerson != null ? !registrationPerson.equals(that.registrationPerson) : that.registrationPerson != null)
            return false;
        if (evidenceOfRegistration != null ? !evidenceOfRegistration.equals(that.evidenceOfRegistration) : that.evidenceOfRegistration != null)
            return false;
        if (bulkConflictedWith != null ? !bulkConflictedWith.equals(that.bulkConflictedWith) : that.bulkConflictedWith != null)
            return false;
        if (systemSupplierSystemId != null ? !systemSupplierSystemId.equals(that.systemSupplierSystemId) : that.systemSupplierSystemId != null)
            return false;
        if (systemSupplierReference != null ? !systemSupplierReference.equals(that.systemSupplierReference) : that.systemSupplierReference != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (alternativeName != null ? alternativeName.hashCode() : 0);
        result = 31 * result + (odsCode != null ? odsCode.hashCode() : 0);
        result = 31 * result + (icoCode != null ? icoCode.hashCode() : 0);
        result = 31 * result + (igToolkitStatus != null ? igToolkitStatus.hashCode() : 0);
        result = 31 * result + (dateOfRegistration != null ? dateOfRegistration.hashCode() : 0);
        result = 31 * result + (registrationPerson != null ? registrationPerson.hashCode() : 0);
        result = 31 * result + (evidenceOfRegistration != null ? evidenceOfRegistration.hashCode() : 0);
        result = 31 * result + (int) isService;
        result = 31 * result + (int) bulkImported;
        result = 31 * result + (int) bulkItemUpdated;
        result = 31 * result + (bulkConflictedWith != null ? bulkConflictedWith.hashCode() : 0);
        result = 31 * result + (systemSupplierSystemId != null ? systemSupplierSystemId.hashCode() : 0);
        result = 31 * result + (systemSupplierReference != null ? systemSupplierReference.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "active", nullable = false)
    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    @Transient
    public List<String> getRegions() {
        return regions;
    }

    @Transient
    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    @Transient
    public List<String> getParentOrganisations() {
        return parentOrganisations;
    }

    @Transient
    public void setParentOrganisations(List<String> parentOrganisations) {
        this.parentOrganisations = parentOrganisations;
    }

    @Transient
    public List<String> getChildOrganisations() {
        return childOrganisations;
    }

    @Transient
    public void setChildOrganisations(List<String> childOrganisations) {
        this.childOrganisations = childOrganisations;
    }

    @Transient
    public List<String> getServices() {
        return services;
    }

    @Transient
    public void setServices(List<String> services) {
        this.services = services;
    }

    @Transient
    public List<String> getDpaPublishing() {
        return dpaPublishing;
    }

    @Transient
    public void setDpaPublishing(List<String> dpaPublishing) {
        this.dpaPublishing = dpaPublishing;
    }

    @Transient
    public List<String> getDsaPublishing() {
        return dsaPublishing;
    }

    @Transient
    public void setDsaPublishing(List<String> dsaPublishing) {
        this.dsaPublishing = dsaPublishing;
    }

    @Transient
    public List<String> getDsaSubscribing() {
        return dsaSubscribing;
    }

    @Transient
    public void setDsaSubscribing(List<String> dsaSubscribing) {
        this.dsaSubscribing = dsaSubscribing;
    }

    @Transient
    public List<AddressEntity> getAddresses() {
        return addresses;
    }

    @Transient
    public void setAddresses(List<AddressEntity> addresses) {
        this.addresses = addresses;
    }

    @Transient
    public String organisationOrService() {
        return (isService == 1 ? "Service" : "Organisation");
    }
}
