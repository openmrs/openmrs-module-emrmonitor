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

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(EmrMonitorService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface EmrMonitorService extends OpenmrsService {
     
	/*
	 * Add service methods here
	 * 
	 */

    public EmrMonitorServer saveSystemInformation(EmrMonitorServer emrMonitorServer);

    public EmrMonitorServer getSystemInformation(String serverUuid);

    public EmrMonitorServer getEmrMonitorServerByUuid(String serverUuid);

    public List<EmrMonitorServer> getEmrMonitorServers();

    public EmrMonitorServer getEmrMonitorServerByType(EmrMonitorServerType serverType);

    public EmrMonitorServer saveEmrMonitorServer(EmrMonitorServer server);

    public Map<String, Map<String, String>> getExtraSystemInfo();
}