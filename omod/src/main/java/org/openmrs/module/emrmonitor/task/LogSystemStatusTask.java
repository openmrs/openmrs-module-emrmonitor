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

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.emrmonitor.EmrMonitorConfig;
import org.openmrs.module.emrmonitor.UptimeLog;
import org.openmrs.module.emrmonitor.rest.RestUtil;

import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * Log system status information to enable utilizing this information in metrics
 */
public class LogSystemStatusTask extends EmrMonitorTask {

    protected final Log log = LogFactory.getLog(this.getClass());

    @Override
    protected Runnable getRunnableTask() {
        return new RunnableTask();
    }

    private class RunnableTask implements Runnable {

        @Override
        public void run() {
            log.debug("Running the Log System Status task");
            Date date = new Date();
            int connectionStatus = -1;
            if (EmrMonitorConfig.isParentServerConfigured()) {
                try {
                    WebResource checkServerResource = RestUtil.getParentServerResource("server");
                    ClientResponse checkServerResponse = checkServerResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
                    connectionStatus = checkServerResponse.getStatus();
                }
                catch (Exception e) {
                    log.debug("An error occurred trying to connect to parent resource", e);
                    connectionStatus = 500;
                }
            }
            UptimeLog.writeToLog(date, UptimeLog.LOG_ENTRY_TOKEN, Integer.toString(connectionStatus));
        }
    }
}
