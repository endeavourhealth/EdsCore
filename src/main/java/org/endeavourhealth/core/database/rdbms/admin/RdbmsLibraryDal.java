package org.endeavourhealth.core.database.rdbms.admin;

import org.endeavourhealth.core.database.dal.admin.LibraryDalI;
import org.endeavourhealth.core.database.dal.admin.models.ActiveItem;
import org.endeavourhealth.core.database.dal.admin.models.Audit;
import org.endeavourhealth.core.database.dal.admin.models.Item;
import org.endeavourhealth.core.database.dal.admin.models.ItemDependency;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsActiveItem;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsAudit;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsItem;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsItemDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RdbmsLibraryDal implements LibraryDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsLibraryDal.class);

    public void save(List<Object> entities) throws Exception {

        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        entityManager.getTransaction().begin();

        for (Object entity: entities) {

            if (entity instanceof Audit) {
                RdbmsAudit dbObj = new RdbmsAudit((Audit)entity);
                entityManager.persist(entity);

            } else if (entity instanceof Item) {
                RdbmsItem dbObj = new RdbmsItem((Item)entity);
                entityManager.persist(entity);

            } else if (entity instanceof ActiveItem) {
                RdbmsActiveItem dbObj = new RdbmsActiveItem((ActiveItem)entity);
                entityManager.persist(entity);

            } else if (entity instanceof ItemDependency) {
                RdbmsItemDependency dbObj = new RdbmsItemDependency((ItemDependency)entity);
                entityManager.persist(entity);

            } else {
                throw new IllegalArgumentException("Unexpected object type " + entity.getClass());
            }
        }

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public Item getItemByKey(UUID id, UUID auditId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsItem c"
                + " where c.id = :id"
                + " and c.auditId = :audit_id";

        Query query = entityManager.createQuery(sql, RdbmsItem.class)
                .setParameter("id", id.toString())
                .setParameter("audit_id", auditId.toString());

        Item ret = null;
        try {
            RdbmsItem result = (RdbmsItem)query.getSingleResult();
            ret = new Item(result);

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
                + " RdbmsAudit c"
                + " where c.id = :id";

        Query query = entityManager.createQuery(sql, RdbmsAudit.class)
                .setParameter("id", id.toString());

        Audit ret = null;
        try {
            RdbmsAudit result = (RdbmsAudit)query.getSingleResult();
            ret = new Audit(result);

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
                + " RdbmsActiveItem c"
                + " where c.itemId = :item_id";

        Query query = entityManager.createQuery(sql, RdbmsActiveItem.class)
                .setParameter("item_id", itemId.toString());

        ActiveItem ret = null;
        try {
            RdbmsActiveItem result = (RdbmsActiveItem)query.getSingleResult();
            ret = new ActiveItem(result);

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
                + " RdbmsActiveItem c"
                + " where c.organisationId = :organisation_id"
                + " and c.itemTypeId = :item_type_id"
                + " and c.isDeleted = :is_deleted";

        Query query = entityManager.createQuery(sql, RdbmsActiveItem.class)
                .setParameter("organisation_id", organisationId)
                .setParameter("item_type_id", itemTypeId)
                .setParameter("is_deleted", isDeleted);

        List<RdbmsActiveItem> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ActiveItem(T))
                .collect(Collectors.toList());
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
                + " RdbmsActiveItem c"
                + " where c.auditOd = :audit_id";

        Query query = entityManager.createQuery(sql, RdbmsActiveItem.class)
                .setParameter("audit_id", auditId.toString());

        List<RdbmsActiveItem> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ActiveItem(T))
                .collect(Collectors.toList());
    }

    public List<ActiveItem> getActiveItemByTypeId(Integer itemTypeId, Boolean isDeleted) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsActiveItem c"
                + " where c.itemTypeId = :item_type_id"
                + " and c.isDeleted = :is_deleted";

        Query query = entityManager.createQuery(sql, RdbmsActiveItem.class)
                .setParameter("item_type_id", itemTypeId)
                .setParameter("is_deleted", isDeleted);

        List<RdbmsActiveItem> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ActiveItem(T))
                .collect(Collectors.toList());
    }

    public List<ItemDependency> getItemDependencyByItemId(UUID itemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsItemDependency c"
                + " where c.itemId = :item_id";

        Query query = entityManager.createQuery(sql, RdbmsItemDependency.class)
                .setParameter("item_id", itemId.toString());

        List<RdbmsItemDependency> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ItemDependency(T))
                .collect(Collectors.toList());
    }

    public List<ItemDependency> getItemDependencyByTypeId(UUID itemId, UUID auditId, Integer dependencyTypeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsItemDependency c"
                + " where c.itemId = :item_id"
                + " and c.auditId = :audit_id"
                + " and c.dependencyTypeId = :dependency_type_id";

        Query query = entityManager.createQuery(sql, RdbmsItemDependency.class)
                .setParameter("item_id", itemId.toString())
                .setParameter("audit_id", auditId.toString())
                .setParameter("dependency_type_id", dependencyTypeId);

        List<RdbmsItemDependency> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ItemDependency(T))
                .collect(Collectors.toList());
    }

    public List<ItemDependency> getItemDependencyByDependentItemId(UUID dependentItemId, Integer dependencyTypeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsItemDependency c"
                + " where c.dependentItemId = :dependent_item_id"
                + " and c.dependencyTypeId = :dependency_type_id";

        Query query = entityManager.createQuery(sql, RdbmsItemDependency.class)
                .setParameter("dependent_item_id", dependentItemId.toString())
                .setParameter("dependency_type_id", dependencyTypeId);

        List<RdbmsItemDependency> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ItemDependency(T))
                .collect(Collectors.toList());
    }

    public List<Audit> getAuditByOrgAndDateDesc(UUID organisationId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsAudit c"
                + " where c.organisationId = :organisation_id"
                + " order by c.timestamp DESC";

        Query query = entityManager.createQuery(sql, RdbmsAudit.class)
                .setParameter("organisation_id", organisationId)
                .setMaxResults(5);

        List<RdbmsAudit> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new Audit(T))
                .collect(Collectors.toList());
    }
}
