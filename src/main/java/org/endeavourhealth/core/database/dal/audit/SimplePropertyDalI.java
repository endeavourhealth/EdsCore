package org.endeavourhealth.core.database.dal.audit;

import java.util.Date;

public interface SimplePropertyDalI {

    void savePropertyString(String propertyName, String propertyValue) throws Exception;
    void savePropertyBoolean(String propertyName, Boolean propertyValue) throws Exception;
    void savePropertyDate(String propertyName, Date propertyValue) throws Exception;
    void savePropertyDouble(String propertyName, Double propertyValue) throws Exception;
    void savePropertyLong(String propertyName, Long propertyValue) throws Exception;

    String getPropertyString(String propertyName) throws Exception;
    Boolean getPropertyBoolean(String propertyName) throws Exception;
    Date getPropertyDate(String propertyName) throws Exception;
    Double getPropertyDouble(String propertyName) throws Exception;
    Long getPropertyLong(String propertyName) throws Exception;

}
