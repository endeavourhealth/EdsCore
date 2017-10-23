package org.endeavourhealth.core.rdbms.admin;

import org.endeavourhealth.core.rdbms.ConnectionManager;
import org.endeavourhealth.core.rdbms.admin.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

public class LibraryHelper {
    private static final Logger LOG = LoggerFactory.getLogger(LibraryHelper.class);

    public void save(List<Object> entities) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        entityManager.getTransaction().begin();

        for (Object entity: entities) {

            if (entity instanceof Audit
                    || entity instanceof Item
                    || entity instanceof ActiveItem
                    || entity instanceof ItemDependency) {
                entityManager.persist(entity);

            } else {
                throw new RuntimeException("Unexpected object type " + entity.getClass());
            }
        }

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public Item getItemByKey(UUID id, UUID auditId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " Item c"
                + " where c.id = :id"
                + " and c.auditId = :audit_id";

        Query query = entityManager.createQuery(sql, Item.class)
                .setParameter("id", id.toString())
                .setParameter("audit_id", auditId.toString());

        Item ret = null;
        try {
            ret = (Item)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();
        return ret;
    }

    public Audit getAuditByKey(UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " Audit c"
                + " where c.id = :id";

        Query query = entityManager.createQuery(sql, Audit.class)
                .setParameter("id", id.toString());

        Audit ret = null;
        try {
            ret = (Audit)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();
        return ret;
    }

    public ActiveItem getActiveItemByItemId(UUID itemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " ActiveItem c"
                + " where c.itemId = :item_id";

        Query query = entityManager.createQuery(sql, ActiveItem.class)
                .setParameter("item_id", itemId.toString());

        ActiveItem ret = null;
        try {
            ret = (ActiveItem)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();
        return ret;
    }

    public List<ActiveItem> getActiveItemByOrgAndTypeId(UUID organisationId, Integer itemTypeId, Boolean isDeleted) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " ActiveItem c"
                + " where c.organisationId = :organisation_id"
                + " and c.itemTypeId = :item_type_id"
                + " and c.isDeleted = :is_deleted";

        Query query = entityManager.createQuery(sql, ActiveItem.class)
                .setParameter("organisation_id", organisationId)
                .setParameter("item_type_id", itemTypeId)
                .setParameter("is_deleted", isDeleted);

        List<ActiveItem> ret = query.getResultList();
        entityManager.close();

        return ret;
    }

    /*public List<ActiveItem> getActiveItemByTypeId(Integer itemTypeId, Boolean isDeleted) throws Exception {
        LibraryAccessor accessor = getMappingManager().createAccessor(LibraryAccessor.class);
        return accessor.getActiveItemByTypeId(itemTypeId, isDeleted);
    }*/

    /*public List<ActiveItem> getActiveItemByOrg(UUID organisationId) throws Exception {
        LibraryAccessor accessor = getMappingManager().createAccessor(LibraryAccessor.class);
        return accessor.getActiveItemByOrg(organisationId);
    }*/

    public List<ActiveItem> getActiveItemByAuditId(UUID auditId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " ActiveItem c"
                + " where c.auditOd = :audit_id";

        Query query = entityManager.createQuery(sql, ActiveItem.class)
                .setParameter("audit_id", auditId.toString());

        List<ActiveItem> ret = query.getResultList();
        entityManager.close();

        return ret;
    }

    public List<ItemDependency> getItemDependencyByItemId(UUID itemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " ItemDependency c"
                + " where c.itemId = :item_id";

        Query query = entityManager.createQuery(sql, ItemDependency.class)
                .setParameter("item_id", itemId.toString());

        List<ItemDependency> ret = query.getResultList();
        entityManager.close();

        return ret;
    }

    public List<ItemDependency> getItemDependencyByTypeId(UUID itemId, UUID auditId, Integer dependencyTypeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " ItemDependency c"
                + " where c.itemId = :item_id"
                + " and c.auditId = :audit_id"
                + " and c.dependencyTypeId = :dependency_type_id";

        Query query = entityManager.createQuery(sql, ItemDependency.class)
                .setParameter("item_id", itemId.toString())
                .setParameter("audit_id", auditId.toString())
                .setParameter("dependency_type_id", dependencyTypeId);

        List<ItemDependency> ret = query.getResultList();
        entityManager.close();

        return ret;
    }

    public List<ItemDependency> getItemDependencyByDependentItemId(UUID dependentItemId, Integer dependencyTypeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " ItemDependency c"
                + " where c.dependentItemId = :dependent_item_id"
                + " and c.dependencyTypeId = :dependency_type_id";

        Query query = entityManager.createQuery(sql, ItemDependency.class)
                .setParameter("dependent_item_id", dependentItemId.toString())
                .setParameter("dependency_type_id", dependencyTypeId);

        List<ItemDependency> ret = query.getResultList();
        entityManager.close();

        return ret;
    }

    public List<Audit> getAuditByOrgAndDateDesc(UUID organisationId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " Audit c"
                + " where c.organisationId = :organisation_id"
                + " order by c.timestamp DESC";

        Query query = entityManager.createQuery(sql, Audit.class)
                .setParameter("organisation_id", organisationId)
                .setMaxResults(5);

        List<Audit> ret = query.getResultList();
        entityManager.close();

        return ret;
    }
}
