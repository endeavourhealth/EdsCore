package org.endeavourhealth.core.database.dal.admin;

import java.util.List;

public interface LinkDistributorPopulatorDalI {

    void populate() throws Exception;
    long countDone() throws Exception;
    long countToDo() throws Exception;
    void clearDown() throws Exception;
    void updateDoneFlag(List<String> patients) throws Exception;

}
