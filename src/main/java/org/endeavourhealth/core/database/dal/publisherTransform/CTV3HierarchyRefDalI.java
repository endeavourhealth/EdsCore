package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.CTV3HierarchyRef;

import java.util.UUID;

public interface CTV3HierarchyRefDalI {

    boolean isChildCodeUnderParentCode (String childReadCode, String ParentReadCode, UUID serviceId) throws Exception;

    void save(CTV3HierarchyRef ref, UUID serviceId) throws Exception;
}
