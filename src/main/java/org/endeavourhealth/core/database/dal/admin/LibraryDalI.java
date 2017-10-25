package org.endeavourhealth.core.database.dal.admin;

import org.endeavourhealth.core.database.dal.admin.models.ActiveItem;
import org.endeavourhealth.core.database.dal.admin.models.Audit;
import org.endeavourhealth.core.database.dal.admin.models.Item;
import org.endeavourhealth.core.database.dal.admin.models.ItemDependency;

import java.util.List;
import java.util.UUID;

public interface LibraryDalI {

    void save(List<Object> entities) throws Exception;
    Item getItemByKey(UUID id, UUID auditId) throws Exception;
    Audit getAuditByKey(UUID id) throws Exception;
    ActiveItem getActiveItemByItemId(UUID itemId) throws Exception;
    List<ActiveItem> getActiveItemByAuditId(UUID auditId) throws Exception;
    List<ActiveItem> getActiveItemByTypeId(Integer itemTypeId, Boolean isDeleted) throws Exception;
    List<ActiveItem> getActiveItemByOrgAndTypeId(UUID organisationId, Integer itemTypeId, Boolean isDeleted) throws Exception;
    List<ItemDependency> getItemDependencyByItemId(UUID itemId) throws Exception;
    List<ItemDependency> getItemDependencyByTypeId(UUID itemId, UUID auditId, Integer dependencyTypeId) throws Exception;
    List<ItemDependency> getItemDependencyByDependentItemId(UUID dependentItemId, Integer dependencyTypeId) throws Exception;
    List<Audit> getAuditByOrgAndDateDesc(UUID organisationId) throws Exception;

}
