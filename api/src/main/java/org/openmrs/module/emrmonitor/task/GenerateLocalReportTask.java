package org.openmrs.module.emrmonitor.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Generate system metrics report for the Local Server on a periodic basis
 */
public class GenerateLocalReportTask extends EmrMonitorTask{

    @Override
    protected Runnable getRunnableTask() {
        return new RunnableTask();
    }

    private class RunnableTask implements Runnable {
        @Override
        public void run() {
            Context.getService(EmrMonitorService.class).refreshLocalServerReport();
        }
    }
}
