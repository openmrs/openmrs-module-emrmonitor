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
import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.EmrMonitorConstants;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.emrmonitor.rest.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;

import javax.ws.rs.core.MediaType;
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
            log.debug("Running transmit reports task.");
            if (EmrMonitorConstants.isParentServerConfigured()) {

                // Get the local server here
                EmrMonitorServer localServer = getService().ensureLocalServer();

                // Next, query the parent server to see if this local server is already registered there
                WebResource checkServerResource = RestUtil.getParentServerResource("server/" + localServer.getUuid());
                ClientResponse checkServerResponse = checkServerResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
                int serverStatus = checkServerResponse.getStatus();

                boolean isRegistered = (serverStatus == 200);

                if (serverStatus == 200 || serverStatus == 404) {

                    // Server has not yet been registered, so register it
                    if (!isRegistered) {
                        try {
                            EmrMonitorServer childServerToRegister = new EmrMonitorServer();
                            childServerToRegister.setUuid(localServer.getUuid());
                            childServerToRegister.setName(localServer.getName());
                            childServerToRegister.setServerType(EmrMonitorServerType.CHILD);
                            String jsonToSend = RestUtil.convertToJson(childServerToRegister, Representation.FULL, true);

                            log.debug("Preparing to register server with parent:");
                            log.debug(jsonToSend);
                            WebResource resource = RestUtil.getParentServerResource("server");
                            ClientResponse response = resource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, jsonToSend);
                            log.debug("Server registration response.getStatus = " + response.getStatus());
                            log.debug("Server registration response = " + response.toString());
                            isRegistered = (response.getStatus() == 201);
                        }
                        catch (Exception e) {
                            log.warn("Error registering server with parent", e);
                        }
                    }

                    if (isRegistered) {
                        // If server is registered, then transmit reports
                        WebResource resource = RestUtil.getParentServerResource("report");
                        List<EmrMonitorReport> reports = getService().getEmrMonitorReports(localServer, EmrMonitorReport.SubmissionStatus.WAITING_TO_SEND);
                        log.info("Attempting to transmit " + reports.size() + " reports that are waiting to send.");

                        // TODO: Do we want to batch these?

                        for (EmrMonitorReport report : reports) {
                            try {
                                log.debug("Sending report to parent: " + report.toString());
                                String jsonToSend = RestUtil.convertToJson(report, Representation.FULL, true);
                                ClientResponse response = resource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, jsonToSend);
                                log.debug("Report transmission response.getStatus = " + response.getStatus());
                                log.debug("Report transmission response = " + response.toString());
                                if (response.getStatus() == 201) {
                                    report.setStatus(EmrMonitorReport.SubmissionStatus.SENT);
                                    getService().saveEmrMonitorReport(report);
                                }
                                else {
                                    log.warn("Non-success reponse code of " + response.getStatus() + " when sending report");
                                }
                            }
                            catch (Exception e) {
                                log.warn("An error occurred while submitting emrmonitor report", e);
                            }
                        }
                    }
                }
                else {
                    log.warn("Unable to connect to " + checkServerResource.toString() + ". Got status: " + serverStatus);
                }
            }
            else {
                log.debug("No parent server is configured.");
            }
            log.debug("Finished transmit reports task");
        }

        private EmrMonitorService getService() {
            return Context.getService(EmrMonitorService.class);
        }
    }
}
