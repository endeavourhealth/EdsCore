package org.endeavourhealth.core.database.rdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeadlockHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DeadlockHandler.class);

    private static final String DEADLOCK_ERR = "Deadlock found when trying to get lock; try restarting transaction";
    private static final int NUM_ATTEMPTS = 5;

    private int attemptsRemaining = NUM_ATTEMPTS;
    private List<Pattern> errorMessages = new ArrayList<>();

    public DeadlockHandler() {
        addOtherErrorMessageToHandler(DEADLOCK_ERR);
    }

    public void addOtherErrorMessageToHandler(String regex) {
        Pattern p = Pattern.compile(regex);
        this.errorMessages.add(p);
    }

    public void handleError(Exception exc) throws Exception {

        //if we're out of lives, then throw the exception
        if (attemptsRemaining <= 0) {
            throw exc;
        }

        boolean throwException = true;

        //check the messages against the ones we're filtering out, making sure to check
        //nested exceptions too
        Throwable t = exc;
        while (t != null && throwException) {
            String msg = t.getMessage();
            if (msg != null) {
                for (Pattern p : errorMessages) {
                    Matcher m = p.matcher(msg);
                    if (m.matches()) {
                        throwException = false;
                        break;
                    }
                }
            }

            t = t.getCause();
        }

        if (throwException) {
            throw exc;
        }

        //if it's a deadlock error, decrease our lives and let it try again
        LOG.error("Error [" + exc.getMessage() + "] when writing to DB - will try again (" + attemptsRemaining + " remaining)");
        Thread.sleep(1000);
        attemptsRemaining--;
    }
}
