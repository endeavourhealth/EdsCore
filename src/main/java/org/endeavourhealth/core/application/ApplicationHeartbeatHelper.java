package org.endeavourhealth.core.application;

import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.common.utility.MetricsHelper;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.audit.ApplicationHeartbeatDalI;
import org.endeavourhealth.core.database.dal.audit.models.ApplicationHeartbeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class ApplicationHeartbeatHelper implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationHeartbeatHelper.class);

    private static ApplicationHeartbeatHelper instance;

    private ApplicationHeartbeatCallbackI callback = null;
    private AtomicInteger stop = new AtomicInteger();

    public static void start() {
        start(null);
    }

    public static void start(ApplicationHeartbeatCallbackI callback) {
        if (instance != null) {
            throw new RuntimeException("ApplicationHeartbeat already started");
        }

        instance = new ApplicationHeartbeatHelper(callback);
        Thread t = new Thread(instance);
        t.setName("ApplicationHeartbeat");
        t.start();
    }

    private ApplicationHeartbeatHelper(ApplicationHeartbeatCallbackI callback) {
        this.callback = callback;
    }

    @Override
    public void run() {

        try {
            while (!isStopped()) {

                Runtime r = Runtime.getRuntime();
                int maxHeapMb = (int)(r.maxMemory() / (1024L * 1024L));
                int currentHeapMb = (int)(r.totalMemory() / (1024L * 1024L));

                ApplicationHeartbeat h = new ApplicationHeartbeat();
                h.setApplicationName(ConfigManager.getAppId()); //config manager is inited with app ID, so just use that
                h.setApplicationInstanceName(ConfigManager.getAppSubId());
                h.setTimestmp(new Date());
                h.setHostName(MetricsHelper.getHostName());
                h.setMaxHeapMb(new Integer(maxHeapMb));
                h.setCurrentHeapMb(new Integer(currentHeapMb));

                //and if we have a callback, then use it to work out if our app is "busy"
                if (callback != null) {
                    callback.populateIsBusy(h);
                }

                ApplicationHeartbeatDalI dal = DalProvider.factoryApplicationHeartbeatDal();
                dal.saveHeartbeat(h);

                //sleep a minute
                Thread.sleep(1000 * 60);
            }
        } catch (Exception ex) {
            LOG.error("", ex);
        }
    }

    public static void stop() {
        if (instance == null) {
            return;
        }
        instance.stop.incrementAndGet();
    }

    public static boolean isStopped() {
        if (instance == null) {
            return true;
        }
        return instance.stop.get() > 0;
    }
}