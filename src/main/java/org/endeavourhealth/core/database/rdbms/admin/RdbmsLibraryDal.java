package org.endeavourhealth.core.database.rdbms.admin;

import com.google.common.base.Strings;
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
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RdbmsLibraryDal implements LibraryDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsLibraryDal.class);

    public void save(List<Object> entities) throws Exception {

        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        PreparedStatement psAudit = null;
        PreparedStatement psItem = null;
        PreparedStatement psActiveItem = null;
        PreparedStatement psItemDependency = null;
        try {
            entityManager.getTransaction().begin();

            for (Object entity : entities) {

                if (entity instanceof Audit) {
                    RdbmsAudit dbObj = new RdbmsAudit((Audit) entity);
                    if (psAudit == null) {
                        psAudit = createSaveAuditPreparedStatement(entityManager);
                    }
                    populateSaveAuditPreparedStatement(psAudit, dbObj);
                    //LOG.info(psAudit.toString());
                    psAudit.executeUpdate();

                } else if (entity instanceof Item) {
                    RdbmsItem dbObj = new RdbmsItem((Item) entity);
                    if (psItem == null) {
                        psItem = createSaveItemPreparedStatement(entityManager);
                    }
                    populateSaveItemPreparedStatement(psItem, dbObj);
                    //LOG.info(psItem.toString());
                    psItem.executeUpdate();

                } else if (entity instanceof ActiveItem) {
                    RdbmsActiveItem dbObj = new RdbmsActiveItem((ActiveItem) entity);
                    if (psActiveItem == null) {
                        psActiveItem = createSaveActiveItemPreparedStatement(entityManager);
                    }
                    populateSaveActiveItemPreparedStatement(psActiveItem, dbObj);
                    //LOG.info(psActiveItem.toString());
                    psActiveItem.executeUpdate();

                } else if (entity instanceof ItemDependency) {
                    RdbmsItemDependency dbObj = new RdbmsItemDependency((ItemDependency) entity);
                    if (psItemDependency == null) {
                        psItemDependency = createSaveItemDependencyPreparedStatement(entityManager);
                    }
                    populateSaveItemDependencyPreparedStatement(psItemDependency, dbObj);
                    //LOG.info(psItemDependency.toString());
                    psItemDependency.executeUpdate();

                } else {
                    throw new IllegalArgumentException("Unexpected object type " + entity.getClass());
                }
            }

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (psAudit != null) {
                psAudit.close();
            }
            if (psItem != null) {
                psItem.close();
            }
            if (psActiveItem != null) {
                psActiveItem.close();
            }
            if (psItemDependency != null) {
                psItemDependency.close();
            }
            entityManager.close();
        }
    }

    public static PreparedStatement createSaveItemDependencyPreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "INSERT INTO item_dependency"
                + " (item_id, audit_id, dependent_item_id, dependency_type_id)"
                + " VALUES (?, ?, ?, ?)";
        //note there's no handler for errors due to duplicate keys, as item dependency should NEVER be updated

        return connection.prepareStatement(sql);
    }

    public static void populateSaveItemDependencyPreparedStatement(PreparedStatement ps, RdbmsItemDependency itemDependency) throws Exception {

        ps.setString(1, itemDependency.getItemId());
        ps.setString(2, itemDependency.getAuditId());
        ps.setString(3, itemDependency.getDependentItemId());
        ps.setInt(4, itemDependency.getDependencyTypeId());
    }

    public static PreparedStatement createSaveActiveItemPreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "INSERT INTO active_item"
                + " (item_id, audit_id, item_type_id, is_deleted, organisation_id)"
                + " VALUES (?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " audit_id = VALUES(audit_id),"
                + " item_type_id = VALUES(item_type_id),"
                + " is_deleted = VALUES(is_deleted),"
                + " organisation_id = VALUES(organisation_id)";

        return connection.prepareStatement(sql);
    }

    public static void populateSaveActiveItemPreparedStatement(PreparedStatement ps, RdbmsActiveItem activeItem) throws Exception {

        ps.setString(1, activeItem.getItemId());
        ps.setString(2, activeItem.getAuditId());
        ps.setInt(3, activeItem.getItemTypeId());
        ps.setBoolean(4, activeItem.getIsDeleted());
        ps.setString(5, activeItem.getOrganisationId());
    }

    public static PreparedStatement createSaveItemPreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "INSERT INTO item"
                + " (id, audit_id, xml_content, title, description, is_deleted)"
                + " VALUES (?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " audit_id = VALUES(audit_id),"
                + " xml_content = VALUES(xml_content),"
                + " title = VALUES(title),"
                + " description = VALUES(description),"
                + " is_deleted = VALUES(is_deleted)";


        return connection.prepareStatement(sql);
    }

    public static void populateSaveItemPreparedStatement(PreparedStatement ps, RdbmsItem item) throws Exception {

        ps.setString(1, item.getId());
        ps.setString(2, item.getAuditId());
        if (!Strings.isNullOrEmpty(item.getXmlContent())) {
            ps.setString(3, item.getXmlContent());
        } else {
            ps.setNull(3, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(item.getTitle())) {
            ps.setString(4, item.getTitle());
        } else {
            ps.setNull(4, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(item.getDescription())) {
            ps.setString(5, item.getDescription());
        } else {
            ps.setNull(5, Types.VARCHAR);
        }
        ps.setBoolean(6, item.isDeleted());
    }

    public static PreparedStatement createSaveAuditPreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "INSERT INTO audit"
                + " (id, organisation_id, timestamp, end_user_id)"
                + " VALUES (?, ?, ?, ?)";
        //note there's no handler for errors due to duplicate keys, as audit should NEVER be updated

        return connection.prepareStatement(sql);
    }

    public static void populateSaveAuditPreparedStatement(PreparedStatement ps, RdbmsAudit audit) throws Exception {

        ps.setString(1, audit.getId());
        ps.setString(2, audit.getOrganisationId());
        ps.setTimestamp(3, new java.sql.Timestamp(audit.getTimestamp().getTime()));
        ps.setString(4, audit.getEndUserId());
    }

    public Item getItemByKey(UUID id, UUID auditId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsItem c"
                    + " where c.id = :id"
                    + " and c.auditId = :audit_id";

            Query query = entityManager.createQuery(sql, RdbmsItem.class)
                    .setParameter("id", id.toString())
                    .setParameter("audit_id", auditId.toString());

            RdbmsItem result = (RdbmsItem)query.getSingleResult();
            return new Item(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public Audit getAuditByKey(UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsAudit c"
                    + " where c.id = :id";

            Query query = entityManager.createQuery(sql, RdbmsAudit.class)
                    .setParameter("id", id.toString());

            RdbmsAudit result = (RdbmsAudit)query.getSingleResult();
            return new Audit(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public ActiveItem getActiveItemByItemId(UUID itemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsActiveItem c"
                    + " where c.itemId = :item_id";

            Query query = entityManager.createQuery(sql, RdbmsActiveItem.class)
                    .setParameter("item_id", itemId.toString());

            RdbmsActiveItem result = (RdbmsActiveItem)query.getSingleResult();
            return new ActiveItem(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public List<ActiveItem> getActiveItemByOrgAndTypeId(UUID organisationId, Integer itemTypeId, Boolean isDeleted) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsActiveItem c"
                    + " where c.organisationId = :organisation_id"
                    + " and c.itemTypeId = :item_type_id"
                    + " and c.isDeleted = :is_deleted";

            Query query = entityManager.createQuery(sql, RdbmsActiveItem.class)
                    .setParameter("organisation_id", organisationId.toString())
                    .setParameter("item_type_id", itemTypeId)
                    .setParameter("is_deleted", isDeleted);

            List<RdbmsActiveItem> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ActiveItem(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }


    public List<ActiveItem> getActiveItemByAuditId(UUID auditId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsActiveItem c"
                    + " where c.auditId = :audit_id";

            Query query = entityManager.createQuery(sql, RdbmsActiveItem.class)
                    .setParameter("audit_id", auditId.toString());

            List<RdbmsActiveItem> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ActiveItem(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<ActiveItem> getActiveItemByTypeId(Integer itemTypeId, Boolean isDeleted) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsActiveItem c"
                    + " where c.itemTypeId = :item_type_id"
                    + " and c.isDeleted = :is_deleted";

            Query query = entityManager.createQuery(sql, RdbmsActiveItem.class)
                    .setParameter("item_type_id", itemTypeId)
                    .setParameter("is_deleted", isDeleted);

            List<RdbmsActiveItem> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ActiveItem(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<ItemDependency> getItemDependencyByItemId(UUID itemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsItemDependency c"
                    + " where c.itemId = :item_id";

            Query query = entityManager.createQuery(sql, RdbmsItemDependency.class)
                    .setParameter("item_id", itemId.toString());

            List<RdbmsItemDependency> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ItemDependency(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<ItemDependency> getItemDependencyByTypeId(UUID itemId, UUID auditId, Integer dependencyTypeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
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

            return ret
                    .stream()
                    .map(T -> new ItemDependency(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<ItemDependency> getItemDependencyByDependentItemId(UUID dependentItemId, Integer dependencyTypeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsItemDependency c"
                    + " where c.dependentItemId = :dependent_item_id"
                    + " and c.dependencyTypeId = :dependency_type_id";

            Query query = entityManager.createQuery(sql, RdbmsItemDependency.class)
                    .setParameter("dependent_item_id", dependentItemId.toString())
                    .setParameter("dependency_type_id", dependencyTypeId);

            List<RdbmsItemDependency> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ItemDependency(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<Audit> getAuditByOrgAndDateDesc(UUID organisationId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsAudit c"
                    + " where c.organisationId = :organisation_id"
                    + " order by c.timestamp DESC";

            Query query = entityManager.createQuery(sql, RdbmsAudit.class)
                    .setParameter("organisation_id", organisationId.toString())
                    .setMaxResults(5);

            List<RdbmsAudit> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new Audit(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }
}
