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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.emrmonitor.EmrMonitorConfig;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorServer;

/**
 * Generate system metrics report for the Local Server on a periodic basis
 */
public class GenerateLocalReportTask extends EmrMonitorTask {

    protected final Log log = LogFactory.getLog(this.getClass());

    @Override
    protected Runnable getRunnableTask() {
        return new RunnableTask();
    }

    private class RunnableTask implements Runnable {

        @Override
        public void run() {
            log.debug("Running the Generate Local Report task");

            EmrMonitorServer localServer = getEmrMonitorService().ensureLocalServer();
            if (localServer == null) {
                log.warn("No local emrmonitor server defined.  Not generating a report.");
                return;
            }

            EmrMonitorReport latestReport = getEmrMonitorService().getLatestEmrMonitorReport(localServer);
            if (latestReport != null) {
                long msSinceLastReport = System.currentTimeMillis() - latestReport.getDateCreated().getTime();
                long msMinimum = EmrMonitorConfig.getMinutesBetweenReports() * 1000 * 60;
                if (msSinceLastReport <=  msMinimum) {
                    log.debug("Not generating report: " + msSinceLastReport + " <= " + msMinimum);
                    return;
                }
            }

            log.info("Generating the daily emrmonitor report");
            getEmrMonitorService().generateEmrMonitorReport();
            log.info("Successfully generated the daily emrmonitor report");
        }
    }
}
