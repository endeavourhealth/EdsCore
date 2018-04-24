package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppCtv3HierarchyRef;

public interface TppCtv3HierarchyRefDalI {

    boolean isChildCodeUnderParentCode(String childReadCode, String ParentReadCode) throws Exception;

    void save(TppCtv3HierarchyRef ref) throws Exception;
}
