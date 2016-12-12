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
import org.openmrs.module.emrmonitor.EmrMonitorConfig;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.rest.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;

import javax.ws.rs.core.MediaType;
import java.net.SocketTimeoutException;
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
            if (EmrMonitorConfig.isParentServerConfigured()) {

                // Get the local server here
                EmrMonitorServer localServer = getEmrMonitorService().ensureLocalServer();

                int serverStatus = 504; // By default, set this to 504 which means "Gateway Timeout"
                WebResource checkServerResource = RestUtil.getParentServerResource("server/" + localServer.getUuid());

                // Next, query the parent server to see if this local server is already registered there
                try {
                    ClientResponse checkServerResponse = checkServerResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
                    serverStatus = checkServerResponse.getStatus();
                }
                catch (Exception e) {
                    log.debug("An error occurred while trying to get the parent server resource", e);
                }

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
                        List<EmrMonitorReport> reports = getEmrMonitorService().getEmrMonitorReports(localServer, EmrMonitorReport.SubmissionStatus.WAITING_TO_SEND);
                        log.info("Attempting to transmit " + reports.size() + " reports that are waiting to send.");

                        for (EmrMonitorReport report : reports) {
                            try {
                                log.debug("Sending report to parent: " + report.toString());
                                String jsonToSend = RestUtil.convertToJson(report, Representation.FULL, true);
                                ClientResponse response = resource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, jsonToSend);
                                log.debug("Report transmission response.getStatus = " + response.getStatus());
                                log.debug("Report transmission response = " + response.toString());
                                if (response.getStatus() == 201) {
                                    report.setStatus(EmrMonitorReport.SubmissionStatus.SENT);
                                    getEmrMonitorService().saveEmrMonitorReport(report);
                                    log.warn("Successfully sent emr monitor report from " + report.getDateCreated());
                                }
                                else {
                                    log.warn("Non-success reponse code of " + response.getStatus() + " when sending report");
                                }
                            }
                            catch (SocketTimeoutException ste) {
                                log.warn("Socket timed out while attempting to submit emrmonitor report from " + report.getDateCreated());
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
    }
}
