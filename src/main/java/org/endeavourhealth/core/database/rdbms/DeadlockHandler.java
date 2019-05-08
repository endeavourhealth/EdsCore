package org.endeavourhealth.core.database.rdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeadlockHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DeadlockHandler.class);

    private static final String DEADLOCK_ERR = "Deadlock found when trying to get lock; try restarting transaction";
    private static final int NUM_ATTEMPTS = 5;

    private int attemptsRemaining = NUM_ATTEMPTS;

    public DeadlockHandler() {

    }

    public void handleError(Exception ex) throws Exception {

        //if we're out of lives, then throw the exception
        if (attemptsRemaining <= 0) {
            throw ex;
        }

        //if it's not a deadlock error, throw the exception
        String msg = ex.getMessage();
        if (msg == null
            || !msg.equalsIgnoreCase(DEADLOCK_ERR)) {
            throw ex;
        }

        //if it's a deadlock error, decrease our lives and let it try again
        LOG.error("Deadlock when writing to DB - will try again (" + attemptsRemaining + " remaining)");
        Thread.sleep(1000);
        attemptsRemaining--;

    }
}
