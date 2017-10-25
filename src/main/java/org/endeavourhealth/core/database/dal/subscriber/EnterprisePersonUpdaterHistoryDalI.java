package org.endeavourhealth.core.database.dal.subscriber;

import java.util.Date;

public interface EnterprisePersonUpdaterHistoryDalI {

    Date findDatePersonUpdaterLastRun() throws Exception;
    void updatePersonUpdaterLastRun(Date d) throws Exception;

}
