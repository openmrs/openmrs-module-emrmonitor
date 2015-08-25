package org.openmrs.module.emrmonitor.task;


import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;

import java.util.List;

public class TransmitLocalReportTask extends EmrMonitorTask{

    @Override
    protected Runnable getRunnableTask() {
        return new RunnableTask();
    }

    private class RunnableTask implements Runnable {
        @Override
        public void run() {
            try{
                //transmit local report to PARENT server
                List<EmrMonitorServer> parents = Context.getService(EmrMonitorService.class).getEmrMonitorServerByType(EmrMonitorServerType.PARENT);
                if (parents != null && parents.size() > 0) { //we are already registered with PARENT servers
                    //get LocalReport
                    EmrMonitorServer localServer = Context.getService(EmrMonitorService.class).getLocalServer();
                    if ( localServer != null ) {
                        List<EmrMonitorReport> reports = Context.getService(EmrMonitorService.class)
                                .getEmrMonitorReportByServerAndStatus(localServer, EmrMonitorReport.SubmissionStatus.WAITING_TO_SEND);
                        if (reports != null && reports.size() > 0) {
                            log.warn("transmiting " + reports.size() + " reports");
                            for (EmrMonitorServer parent : parents) {
                                try {
                                    Context.getService(EmrMonitorService.class).sendEmrMonitorReports(parent, reports);
                                } catch (Exception transmissionException){
                                    log.error("error transmitting local report to parent: " + parent.getName() , transmissionException);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("error transmitting local report ", e);
            }
        }
    }
}
