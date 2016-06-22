/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.emrmonitor.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;

import java.util.List;

/**
 * Transmit local server reports to parent server on a periodic basis
 */
public class TransmitLocalReportTask extends EmrMonitorTask {

    @Override
    protected Runnable getRunnableTask() {
        return new RunnableTask();
    }

    private class RunnableTask implements Runnable {
        @Override
        public void run() {
            try{
                //transmit local report to PARENT server
                List<EmrMonitorServer> parents = getService().getEmrMonitorServerByType(EmrMonitorServerType.PARENT);
                if (parents != null && parents.size() > 0) { //we are already registered with PARENT servers
                    //get LocalReport
                    EmrMonitorServer localServer = getService().getLocalServer();
                    if ( localServer != null ) {
                        List<EmrMonitorReport> reports = getService().getEmrMonitorReportByServerAndStatus(localServer, EmrMonitorReport.SubmissionStatus.WAITING_TO_SEND);
                        if (reports != null && reports.size() > 0) {
                            log.warn("transmiting " + reports.size() + " reports");
                            for (EmrMonitorServer parent : parents) {
                                try {
                                    getService().sendEmrMonitorReports(parent, reports);
                                }
                                catch (Exception e){
                                    log.error("error transmitting local report to parent: " + parent.getName() , e);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("error transmitting local report ", e);
            }
        }

        private EmrMonitorService getService() {
            return Context.getService(EmrMonitorService.class);
        }
    }
}
