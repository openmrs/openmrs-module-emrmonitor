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
            try {
                // refresh Local Server EmrMonitorServer record
                EmrMonitorServer localServer = Context.getService(EmrMonitorService.class).getLocalServer();
                if (localServer == null) {
                    //create new Local Server record
                    localServer = new EmrMonitorServer();
                    localServer.setName(InetAddress.getLocalHost().getHostName());
                    localServer.setServerType(EmrMonitorServerType.LOCAL);
                    localServer.setDateCreated(new Date());
                    localServer.setUuid(UUID.randomUUID().toString());
                }

                Map<String, Map<String, String>> systemInformation = Context.getAdministrationService().getSystemInformation();
                Map<String, Map<String, String>> extraSystemInfo = null; //Context.getService(EmrMonitorService.class).getExtraSystemInfo();
                if (extraSystemInfo != null && extraSystemInfo.size() > 0) {
                    systemInformation.putAll(extraSystemInfo);
                }
                localServer.setSystemInformation(systemInformation);

                localServer = Context.getService(EmrMonitorService.class).saveEmrMonitorServer(localServer, systemInformation);
                if (localServer == null) {
                    log.error("failed to generate new local server system information");
                }
            } catch (Exception e) {
                log.error("error generating local server system information", e);
            }

        }
    }
}
