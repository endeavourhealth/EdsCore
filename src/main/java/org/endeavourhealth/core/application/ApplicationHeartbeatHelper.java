package org.endeavourhealth.core.application;

import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.common.utility.MetricsHelper;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.audit.ApplicationHeartbeatDalI;
import org.endeavourhealth.core.database.dal.audit.models.ApplicationHeartbeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URL;
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

        Date dtStarted = new Date();

        try {
            while (!isStopped()) {

                Runtime r = Runtime.getRuntime();
                Integer maxHeapMb = new Integer((int)(r.maxMemory() / (1024L * 1024L)));
                Integer currentHeapMb = new Integer((int)(r.totalMemory() / (1024L * 1024L)));
                Integer serverMemoryMb = null;
                Integer serverCpuUsagePercent = null;

                //get Memory and CPU via JMX
                try {
                    com.sun.management.OperatingSystemMXBean b = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                    serverMemoryMb = new Integer((int)(b.getTotalPhysicalMemorySize() / (1024L * 1024L)));
                    serverCpuUsagePercent = new Integer((int)(b.getSystemCpuLoad() * 100D));

                } catch (Throwable t) {
                    //the above will fail if run on a non-Oracle JVM, since the stuff we want is only available on their
                    //implmentation of the SystemMX bean
                }

                //get this fresh each time so we know if a jar has been deployed but a QR not restarted
                Date dtJar = findJarDateTime();

                ApplicationHeartbeat h = new ApplicationHeartbeat();
                h.setApplicationName(ConfigManager.getAppId()); //config manager is inited with app ID, so just use that
                h.setApplicationInstanceName(ConfigManager.getAppSubId());
                h.setTimestmp(new Date());
                h.setHostName(MetricsHelper.getHostName());
                h.setMaxHeapMb(maxHeapMb);
                h.setCurrentHeapMb(currentHeapMb);
                h.setServerMemoryMb(serverMemoryMb);
                h.setServerCpuUsagePercent(serverCpuUsagePercent);
                h.setDtStarted(dtStarted);
                h.setDtJar(dtJar);

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

    /**
     * finds the datetime the jar was built. Also supports non-jar environments for dev purposes
     */
    private Date findJarDateTime() {

        try {
            //if we have a callback object then use the class for that since it will give us one of the top-level
            //jars, which is really what we want to know. In the absense of that, use this class, which will only
            //tell us the build date of the Core Jar, but is better than nothing
            Class cls = null;
            if (callback != null) {
                cls = callback.getClass();
            } else {
                cls = ApplicationHeartbeatHelper.class;
            }

            String clsName = cls.getSimpleName() + ".class";
            URL clsLoc = cls.getResource(clsName);
            String protocol = clsLoc.getProtocol();

            if (protocol.equals("file")) {
                //if the protocol is a file then we're running outside of a jar, such as on a dev laptop
                //e.g. file:/C:/Users/.m2/repository/org/endeavourhealth/core/application/ApplicationHeartbeatHelper.class
                URI uri = clsLoc.toURI();
                File f = new File(uri);
                return new Date(f.lastModified());

            } else if (protocol.equals("jar")) {
                //if the protocol is a jar, then we're running from a jar, on a server
                String fullPath = clsLoc.getPath(); //e.g. file:/C:/Users/drewl/.m2/repository/org/endeavourhealth/common/core/1.644-SNAPSHOT/core-1.644-SNAPSHOT.jar!/org/endeavourhealth/core/application/ApplicationHeartbeatHelper.class
                String jarPath = fullPath.substring(5, fullPath.indexOf("!")); //e.g. C:/Users/drewl/.m2/repository/org/endeavourhealth/common/core/1.644-SNAPSHOT/core-1.644-SNAPSHOT.jar
                File f = new File(jarPath);
                return new Date(f.lastModified());

            } else {
                LOG.error("Unsupported protocol [" + protocol + "] when trying to find bulid date");
                return null;
            }

        } catch (Exception ex) {
            LOG.error("Failed to find Jar datetime for application heartbeat (will carry on)");
            return null;
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
