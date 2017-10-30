package org.endeavourhealth.core.database.rdbms.reference;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.reference.PostcodeDalI;
import org.endeavourhealth.core.database.dal.reference.models.PostcodeLookup;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.reference.models.RdbmsPostcodeLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RdbmsPostcodeDal implements PostcodeDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsPostcodeDal.class);

    public PostcodeLookup getPostcodeReference(String postcode) throws Exception {

        EntityManager entityManager = ConnectionManager.getReferenceEntityManager();
        try {
            return getPostcodeReference(postcode, entityManager);

        } finally {
            entityManager.close();
        }
    }

    public PostcodeLookup getPostcodeReference(String postcode, EntityManager entityManager) throws Exception {

        //if called with an empty postcode, just return null
        if (Strings.isNullOrEmpty(postcode)) {
            return null;
        }

        //we force everything to upper case when creating the table, so do that now
        postcode = postcode.toUpperCase();

        //because we've got no guarantee how/where the raw postcodes are spaced, we use the string without spaces as our primary key
        postcode = postcode.replaceAll(" ", "");

        String sql = "select r"
                   + " from RdbmsPostcodeLookup r"
                   + " where r.postcodeNoSpace = :postcodeNoSpace";

        Query query = entityManager
                .createQuery(sql, RdbmsPostcodeLookup.class)
                .setParameter("postcodeNoSpace", postcode);

        try {
            RdbmsPostcodeLookup result = (RdbmsPostcodeLookup)query.getSingleResult();
            return new PostcodeLookup(result);

        } catch (NoResultException e) {
            return null;

        }
    }


}
