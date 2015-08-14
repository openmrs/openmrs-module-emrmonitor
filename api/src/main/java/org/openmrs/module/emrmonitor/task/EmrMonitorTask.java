package org.openmrs.module.emrmonitor.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.DaemonToken;

import java.util.TimerTask;

/**
 * Generic superclass for an EmrMonitor task
 */

public abstract class EmrMonitorTask extends TimerTask {

    Log log = LogFactory.getLog(getClass());
    private static DaemonToken daemonToken;
    private static boolean enabled = false;


    private Class<? extends EmrMonitorTask> taskClass;

    public static void setDaemonToken(DaemonToken daemonToken) {
        EmrMonitorTask.daemonToken = daemonToken;
    }

    public static void setEnabled(boolean enabled) {
        EmrMonitorTask.enabled = enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    protected abstract Runnable getRunnableTask();

    @Override
    public final void run() {
        if (daemonToken != null && enabled) {
            createAndRunTask();
        } else {
            log.warn("Not running scheduled task. DaemonToken = " + daemonToken + "; enabled = " + enabled);
        }
    }

    /**
     * Construct a new instance of the configured task and execute ot
     */
    public synchronized void createAndRunTask() {
        try {
            log.warn("Running emrMonitor task: " + getClass().getSimpleName());
            Daemon.runInDaemonThread(getRunnableTask(), daemonToken);
        } catch (Exception e) {
            log.error("An error occured while running scheduled emrMonitor task", e);
        }
    }

}
