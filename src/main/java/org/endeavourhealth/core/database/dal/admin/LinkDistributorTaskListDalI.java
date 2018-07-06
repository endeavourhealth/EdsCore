package org.endeavourhealth.core.database.dal.admin;

import org.endeavourhealth.core.database.dal.admin.models.LinkDistributorTaskList;

import java.util.List;

public interface LinkDistributorTaskListDalI {

    void insertTask(String configName) throws Exception;
    boolean safeToRun() throws Exception;
    List<LinkDistributorTaskList> getTaskList() throws Exception;
    LinkDistributorTaskList getNextTaskToProcess() throws Exception;
    void updateTaskStatus(String configName, byte status) throws Exception;

}
