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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.emrmonitor.task.EmrMonitorTask;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class EmrMonitorActivator extends BaseModuleActivator implements DaemonTokenAware{
	
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
        EmrMonitorTask.setEnabled(true);
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

    @Override
    public void setDaemonToken(DaemonToken daemonToken) {
        EmrMonitorTask.setDaemonToken(daemonToken);
    }
}
