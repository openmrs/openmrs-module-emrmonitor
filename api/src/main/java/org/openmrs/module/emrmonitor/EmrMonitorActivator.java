/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
	 * @see ModuleActivator#started()
	 */
	public void started() {
        EmrMonitorTask.setEnabled(true);
		log.info("EmrMonitor Module started");
	}
	
	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("EmrMonitor Module stopped");
	}

    @Override
    public void setDaemonToken(DaemonToken daemonToken) {
        EmrMonitorTask.setDaemonToken(daemonToken);
    }
}
