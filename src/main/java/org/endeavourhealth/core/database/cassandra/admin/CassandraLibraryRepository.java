package org.endeavourhealth.core.database.cassandra.admin;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.mapping.Mapper;
import com.google.common.collect.Lists;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.admin.accessors.LibraryAccessor;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraActiveItem;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraAudit;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraItem;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraItemDependency;
import org.endeavourhealth.core.database.dal.admin.LibraryDalI;
import org.endeavourhealth.core.database.dal.admin.models.ActiveItem;
import org.endeavourhealth.core.database.dal.admin.models.Audit;
import org.endeavourhealth.core.database.dal.admin.models.Item;
import org.endeavourhealth.core.database.dal.admin.models.ItemDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CassandraLibraryRepository extends Repository implements LibraryDalI {
    private static final Logger LOG = LoggerFactory.getLogger(CassandraLibraryRepository.class);

    public void save(List<Object> entities) {

        Mapper<CassandraItem> mapperLibraryItem = getMappingManager().mapper(CassandraItem.class);
        Mapper<CassandraAudit> mapperAudit = getMappingManager().mapper(CassandraAudit.class);
        Mapper<CassandraActiveItem> mapperActiveItem = getMappingManager().mapper(CassandraActiveItem.class);
        Mapper<CassandraItemDependency> mapperItemDependency = getMappingManager().mapper(CassandraItemDependency.class);

        BatchStatement batch = new BatchStatement();

        for (Object entity: entities) {

            if (entity instanceof Audit) {
                CassandraAudit dbObj = new CassandraAudit((Audit)entity);
                batch.add(mapperAudit.saveQuery(dbObj));

            } else if (entity instanceof Item) {
                CassandraItem dbObj = new CassandraItem((Item)entity);
                batch.add(mapperLibraryItem.saveQuery(dbObj));

            } else if (entity instanceof ActiveItem) {
                CassandraActiveItem dbObj = new CassandraActiveItem((ActiveItem)entity);
                batch.add(mapperActiveItem.saveQuery(dbObj));

            } else if (entity instanceof ItemDependency) {
                CassandraItemDependency dbObj = new CassandraItemDependency((ItemDependency)entity);
                batch.add(mapperItemDependency.saveQuery(dbObj));

            } else {
                throw new IllegalArgumentException("Unexpected object type " + entity.getClass());
            }
        }

        //LOG.trace("Saving batch of " + batch.size() + " items.");

        getSession().execute(batch);
    }

    public Item getItemByKey(UUID id, UUID auditId) {
        Mapper<CassandraItem> mapperLibraryItem = getMappingManager().mapper(CassandraItem.class);
        CassandraItem result = mapperLibraryItem.get(id, auditId);
        if (result != null) {
            return new Item(result);
        } else {
            return null;
        }
    }

    public Audit getAuditByKey(UUID id) {
        Mapper<CassandraAudit> mapperLibraryItem = getMappingManager().mapper(CassandraAudit.class);
        CassandraAudit result = mapperLibraryItem.get(id);
        if (result != null) {
            return new Audit(result);
        } else {
            return null;
        }
    }

    public ActiveItem getActiveItemByItemId(UUID itemId) {
        LibraryAccessor accessor = getMappingManager().createAccessor(LibraryAccessor.class);
        CassandraActiveItem result = accessor.getActiveItemByItemId(itemId);
        if (result != null) {
            return new ActiveItem(result);
        } else {
            return null;
        }
    }

    public List<ActiveItem> getActiveItemByOrgAndTypeId(UUID organisationId, Integer itemTypeId, Boolean isDeleted) {
        LibraryAccessor accessor = getMappingManager().createAccessor(LibraryAccessor.class);
        List<CassandraActiveItem> results = Lists.newArrayList(accessor.getActiveItemByOrgAndTypeId(organisationId, itemTypeId, isDeleted));
        return results
                .stream()
                .map(T -> new ActiveItem(T))
                .collect(Collectors.toList());
    }

    public List<ActiveItem> getActiveItemByTypeId(Integer itemTypeId, Boolean isDeleted) {
        LibraryAccessor accessor = getMappingManager().createAccessor(LibraryAccessor.class);
        List<CassandraActiveItem> results = Lists.newArrayList(accessor.getActiveItemByTypeId(itemTypeId, isDeleted));
        return results
                .stream()
                .map(T -> new ActiveItem(T))
                .collect(Collectors.toList());
    }

    public List<ActiveItem> getActiveItemByOrg(UUID organisationId) {
        LibraryAccessor accessor = getMappingManager().createAccessor(LibraryAccessor.class);
        List<CassandraActiveItem> results = Lists.newArrayList(accessor.getActiveItemByOrg(organisationId));
        return results
                .stream()
                .map(T -> new ActiveItem(T))
                .collect(Collectors.toList());
    }

    public List<ActiveItem> getActiveItemByAuditId(UUID auditId) {
        LibraryAccessor accessor = getMappingManager().createAccessor(LibraryAccessor.class);
        List<CassandraActiveItem> results = Lists.newArrayList(accessor.getActiveItemByAuditId(auditId));
        return results
                .stream()
                .map(T -> new ActiveItem(T))
                .collect(Collectors.toList());
    }

    public List<ItemDependency> getItemDependencyByItemId(UUID itemId) {
        LibraryAccessor accessor = getMappingManager().createAccessor(LibraryAccessor.class);
        List<CassandraItemDependency> results = Lists.newArrayList(accessor.getItemDependencyByItemId(itemId));
        return results
                .stream()
                .map(T -> new ItemDependency(T))
                .collect(Collectors.toList());
    }

    public List<ItemDependency> getItemDependencyByTypeId(UUID itemId, UUID auditId, Integer dependencyTypeId) {
        LibraryAccessor accessor = getMappingManager().createAccessor(LibraryAccessor.class);
        List<CassandraItemDependency> results = Lists.newArrayList(accessor.getItemDependencyByTypeId(itemId, auditId, dependencyTypeId));
        return results
                .stream()
                .map(T -> new ItemDependency(T))
                .collect(Collectors.toList());
    }

    public List<ItemDependency> getItemDependencyByDependentItemId(UUID dependentItemId, Integer dependencyTypeId) {
        LibraryAccessor accessor = getMappingManager().createAccessor(LibraryAccessor.class);
        List<CassandraItemDependency> results = Lists.newArrayList(accessor.getItemDependencyByDependentItemId(dependentItemId, dependencyTypeId));
        return results
                .stream()
                .map(T -> new ItemDependency(T))
                .collect(Collectors.toList());
    }

    public List<Audit> getAuditByOrgAndDateDesc(UUID organisationId) {
        LibraryAccessor accessor = getMappingManager().createAccessor(LibraryAccessor.class);
        List<CassandraAudit> results = Lists.newArrayList(accessor.getAuditByOrgAndDateDesc(organisationId));
        return results
                .stream()
                .map(T -> new Audit(T))
                .collect(Collectors.toList());
    }



}

