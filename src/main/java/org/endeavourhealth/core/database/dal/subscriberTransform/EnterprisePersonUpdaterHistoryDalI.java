package org.endeavourhealth.core.database.dal.subscriberTransform;

import java.util.Date;

public interface EnterprisePersonUpdaterHistoryDalI {

    Date findDatePersonUpdaterLastRun() throws Exception;
    void updatePersonUpdaterLastRun(Date d) throws Exception;

}
