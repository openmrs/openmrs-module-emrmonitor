/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.emrmonitor.api;

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service interface for EMR monitor API functionality
 */
@Transactional
public interface EmrMonitorService extends OpenmrsService {

    EmrMonitorServer getEmrMonitorServerByUuid(String serverUuid);

    List<EmrMonitorServer> getAllEmrMonitorServers();

    List<EmrMonitorServer> getEmrMonitorServerByType(EmrMonitorServerType serverType);

    EmrMonitorServer getLocalServer();

    EmrMonitorServer refreshLocalServerReport();

    EmrMonitorServer getRemoteParentServer(EmrMonitorServer remoteServer) throws IOException;

    EmrMonitorServer saveEmrMonitorServer(EmrMonitorServer server);

    Map<String, Map<String, String>> getExtraSystemInfo();

	Map<String, String> getOpenmrsData();

    EmrMonitorServer saveEmrMonitorServer(EmrMonitorServer server, Map<String, Map<String,String>> systemInformation, EmrMonitorReport.SubmissionStatus reportStatus);

    EmrMonitorServer testConnection(EmrMonitorServer server) throws IOException;

    EmrMonitorServer registerServer(EmrMonitorServer server) throws IOException ;

    EmrMonitorServer retireEmrMonitorServer(EmrMonitorServer server, String reason) throws APIException;

    void purgeEmrMonitorServer(EmrMonitorServer server) throws APIException;

    Map<String, Map<String, String>> getSystemInfoFromReport(EmrMonitorReport report) throws IOException;

    EmrMonitorReport saveEmrMonitorReport(EmrMonitorReport report);

    List<EmrMonitorReport> getEmrMonitorReportByServerAndStatus(EmrMonitorServer server, EmrMonitorReport.SubmissionStatus status);

    boolean sendEmrMonitorReports(EmrMonitorServer parent, List<EmrMonitorReport> reports) throws IOException;
}