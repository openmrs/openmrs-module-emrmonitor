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
package org.openmrs.module.emrmonitor;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class EmrMonitorActivator implements ModuleActivator {
	
	protected Log log = LogFactory.getLog(getClass());
		
	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	public void willRefreshContext() {
		log.info("Refreshing OpenMRS EmrMonitor Module");
	}
	
	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		log.info("OpenMRS EmrMonitor Module refreshed");
	}
	
	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("Starting OpenMRS EmrMonitor Module");
	}
	
	/**
	 * @see ModuleActivator#started()
	 */
	public void started() {

        try {
            // refresh Local Server EmrMonitorServer record
            EmrMonitorServer localServer = Context.getService(EmrMonitorService.class).getEmrMonitorServerByType(EmrMonitorServerType.LOCAL);
            if (localServer == null) {
                //create new Local Server record
                localServer = new EmrMonitorServer();
                localServer.setServerName(InetAddress.getLocalHost().getHostName());
                localServer.setServerType(EmrMonitorServerType.LOCAL);
                localServer.setDateCreated(new Date());
                localServer.setUuid(UUID.randomUUID().toString());
            }

            Map<String, Map<String, String>> systemInformation = Context.getAdministrationService().getSystemInformation();
            Map<String, Map<String, String>> extraSystemInfo = Context.getService(EmrMonitorService.class).getExtraSystemInfo();
            if (extraSystemInfo != null && extraSystemInfo.size() > 0) {
                systemInformation.putAll(extraSystemInfo);
            }
            localServer.setSystemInformation(systemInformation);

            localServer = Context.getService(EmrMonitorService.class).saveEmrMonitorServer(localServer);
            if (localServer == null) {
                log.error("failed to generate new local server system information");
            }
        } catch (Exception e) {
            log.error("error generating local server system information");
        }

		log.info("OpenMRS EmrMonitor Module started");

	}
	
	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("Stopping OpenMRS EmrMonitor Module");
	}
	
	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("OpenMRS EmrMonitor Module stopped");
	}
		
}
