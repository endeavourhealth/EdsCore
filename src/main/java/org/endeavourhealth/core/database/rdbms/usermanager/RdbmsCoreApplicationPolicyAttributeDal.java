package org.endeavourhealth.core.database.rdbms.usermanager;

import org.endeavourhealth.core.database.dal.usermanager.ApplicationPolicyAttributeDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationPolicyAttributeEntity;
import org.endeavourhealth.core.database.dal.usermanager.models.JsonApplicationPolicyAttribute;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class RdbmsCoreApplicationPolicyAttributeDal implements ApplicationPolicyAttributeDalI {

    public List<JsonApplicationPolicyAttribute> getApplicationPolicyAttributes(String roleTypeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            String sql = "select " +
                    " rp.id," +
                    " rp.applicationPolicyId," +
                    " rt.name as roleName," +
                    " a.name as applicationName," +
                    " a.id as applicationId," +
                    " rp.applicationAccessProfileId," +
                    " aap.name as profileName," +
                    " aap.description as profileDescription," +
                    " aap.superUser, " +
                    " rp.isDeleted" +
                    " from ApplicationPolicyAttributeEntity rp" +
                    " join ApplicationPolicyEntity rt on rp.applicationPolicyId = rt.id" +
                    " join ApplicationAccessProfileEntity aap on aap.id = rp.applicationAccessProfileId" +
                    " join ApplicationEntity a on a.id = aap.applicationId" +
                    " where rp.applicationPolicyId = :roleTypeId" +
                    " and rp.isDeleted = 0";

            Query query = entityManager.createQuery(sql);

            query.setParameter("roleTypeId", roleTypeId);

            List<Object[]> results = query.getResultList();

            return convertRoleProfilesToJson(results);
        } finally {
            entityManager.close();
        }
    }

    private List<JsonApplicationPolicyAttribute> convertRoleProfilesToJson(List<Object[]> results) throws Exception {
        List<JsonApplicationPolicyAttribute> profiles = new ArrayList<>();

        for (Object[] obj : results) {
            JsonApplicationPolicyAttribute profile = new JsonApplicationPolicyAttribute();
            profile.setId(obj[0].toString());
            profile.setApplicationPolicyId(obj[1].toString());
            profile.setName(obj[2].toString());
            profile.setApplication(obj[3].toString());
            profile.setApplicationId(obj[4].toString());
            profile.setApplicationAccessProfileId(obj[5].toString());
            profile.setApplicationAccessProfileName(obj[6].toString());
            profile.setApplicationAccessProfileDescription(obj[7].toString());
            profile.setApplicationAccessProfileSuperUser(obj[8].toString().equals("1"));
            profile.setDeleted(obj[9].toString().equals("1"));

            profiles.add(profile);
        }

        return profiles;
    }

    public ApplicationPolicyAttributeEntity getRoleTypeAccessProfile(String profileId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            ApplicationPolicyAttributeEntity ret = entityManager.find(ApplicationPolicyAttributeEntity.class, profileId);

            return ret;
        } finally {
            entityManager.close();
        }
    }
}
