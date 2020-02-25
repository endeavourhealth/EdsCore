package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonProjectSchedule;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ProjectScheduleEntity;

public interface ProjectScheduleDalI {

    public ProjectScheduleEntity get(String uuid) throws Exception;
    public void save(JsonProjectSchedule schedule) throws Exception;
    public void update(JsonProjectSchedule schedule) throws Exception;
    public void delete(String uuid) throws Exception;
}
