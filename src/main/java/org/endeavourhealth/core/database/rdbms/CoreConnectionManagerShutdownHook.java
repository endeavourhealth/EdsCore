package org.endeavourhealth.core.database.rdbms;

import org.endeavourhealth.coreui.framework.ContextShutdownHook;

public class CoreConnectionManagerShutdownHook implements ContextShutdownHook {
    @Override
    public void contextShutdown() {
        ConnectionManager.shutdown();
    }
}
