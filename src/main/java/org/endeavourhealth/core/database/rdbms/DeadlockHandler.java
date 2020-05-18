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
    private static final String LOCK_ERR = "Lock wait timeout exceeded; try restarting transaction";

    private int maxAttempts;
    private int attempts;
    private List<Pattern> errorMessages = new ArrayList<>();
    private int retryDelaySeconds;
    private boolean delayBackOff = false; //whether to increase the delay between attempts as the number of attempts goes up

    public DeadlockHandler() {
        this.attempts = 0;
        this.maxAttempts = 5; //allow five attempts by default
        this.retryDelaySeconds = 1; //default to 1s between attempts
        addErrorMessageToHandler(DEADLOCK_ERR);
        addErrorMessageToHandler(LOCK_ERR);
    }

    public void addErrorMessageToHandler(String regex) {
        Pattern p = Pattern.compile(regex);
        this.errorMessages.add(p);
    }

    public int getRetryDelaySeconds() {
        return retryDelaySeconds;
    }

    public void setRetryDelaySeconds(int retryDelaySeconds) {
        this.retryDelaySeconds = retryDelaySeconds;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public boolean isDelayBackOff() {
        return delayBackOff;
    }

    public void setDelayBackOff(boolean delayBackOff) {
        this.delayBackOff = delayBackOff;
    }

    private int getNextDelaySeconds() {

        if (delayBackOff) {
            //if backing off over time, generate the delay from the fibonacci sequence which seems to give
            //a fairly good distribution over time and times by the delay period
            return getFibonacciValue(this.attempts+1) * retryDelaySeconds;

        } else {
            //if not doing the back off thing, then always use the same delay period
            return retryDelaySeconds;
        }
    }

    private int getFibonacciValue(int i) {

        if (i <= 2) {
            return 1;

        } else {
            return getFibonacciValue(i-1) + getFibonacciValue(i-2);
        }
    }

    public boolean canHandleError(Exception exc) {

        //if we're out of lives, then we won't handle it
        if (attempts >= maxAttempts) {
            return false;
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
            return false;
        }

        //if it's a deadlock error, decrease our lives and let it try again
        int delaySec = getNextDelaySeconds();
        LOG.error("Error [" + exc.getMessage() + "] when writing to DB - will try again in " + delaySec + "s (" + (maxAttempts-attempts) + " remaining)");
        attempts++;
        try {
            Thread.sleep((long)delaySec * 1000L);
        } catch (InterruptedException ie) {
            //we should never be interrupted so throw if we get it
            throw new RuntimeException("", ie);
        }

        return true;
    }


    public void handleError(Exception exc) throws Exception {

        boolean throwException = !canHandleError(exc);
        if (throwException) {
            throw exc;
        }
    }
}
