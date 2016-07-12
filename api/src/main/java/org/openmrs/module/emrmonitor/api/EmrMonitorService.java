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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service interface for EMR monitor API functionality
 */
@Transactional
public interface EmrMonitorService extends OpenmrsService {

    // Server Methods

    EmrMonitorServer ensureLocalServer();
    List<EmrMonitorServer> getChildServers();
    EmrMonitorServer getEmrMonitorServerByUuid(String serverUuid);
    List<EmrMonitorServer> getAllEmrMonitorServers();
    EmrMonitorServer saveEmrMonitorServer(EmrMonitorServer server);
    void purgeEmrMonitorServer(String uuid) throws APIException;

    // Report Methods

    EmrMonitorReport generateEmrMonitorReport();
    EmrMonitorReport getEmrMonitorReportByUuid(String uuid);
    EmrMonitorReport getLatestEmrMonitorReport(EmrMonitorServer server);
    List<EmrMonitorReport> getEmrMonitorReports(EmrMonitorServer server, EmrMonitorReport.SubmissionStatus... status);
    EmrMonitorReport saveEmrMonitorReport(EmrMonitorReport report);
    void purgeEmrMonitorReport(EmrMonitorReport report) throws APIException;

    // Other methods

    Map<String, String> getOpenmrsData();
}