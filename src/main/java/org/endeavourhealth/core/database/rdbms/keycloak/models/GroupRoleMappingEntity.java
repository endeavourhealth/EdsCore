package org.endeavourhealth.core.database.rdbms.keycloak.models;

import org.endeavourhealth.core.database.rdbms.keycloak.PersistenceManagerKeycloak;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;

/**
 * Created by Leigh Ellwood on 30/05/2017.
 */
@Entity
@Table(name = "GROUP_ROLE_MAPPING", schema = "keycloak")
@IdClass(GroupRoleMappingEntityPK.class)
public class GroupRoleMappingEntity implements Serializable {
    private String roleId;
    private String groupId;

    @Id
    @Column(name = "ROLE_ID", nullable = false, length = 36)
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Id
    @Column(name = "GROUP_ID", nullable = false, length = 36)
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupRoleMappingEntity that = (GroupRoleMappingEntity) o;

        if (roleId != null ? !roleId.equals(that.roleId) : that.roleId != null) return false;
        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = roleId != null ? roleId.hashCode() : 0;
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        return result;
    }

    public static GroupRoleMappingEntity getGroupRoleMappingByRoleId(String roleId) throws Exception {
        EntityManager entityManager = PersistenceManagerKeycloak.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<GroupRoleMappingEntity> cq = cb.createQuery(GroupRoleMappingEntity.class);
        Root<GroupRoleMappingEntity> rootEntry = cq.from(GroupRoleMappingEntity.class);


        Predicate predicate = cb.equal(rootEntry.get("roleId"), roleId);

        cq.where(predicate).orderBy(cb.asc(rootEntry.get("groupId")));
        TypedQuery<GroupRoleMappingEntity> query = entityManager.createQuery(cq);

        GroupRoleMappingEntity ret = null;
        try {
            ret = query.getSingleResult();
        }
        catch (NoResultException e) {
            // handle can not find exception
        }
        catch (NonUniqueResultException e) {
            // handle multiple results
        }
        finally {
            entityManager.close();
        }

        return ret;
    }
}
