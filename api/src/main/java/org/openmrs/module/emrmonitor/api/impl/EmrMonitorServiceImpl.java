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
package org.openmrs.module.emrmonitor.api.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.emrmonitor.api.EmrMonitor;
import org.openmrs.module.emrmonitor.api.EmrMonitorProperties;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.emrmonitor.api.db.EmrMonitorDAO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * It is a default implementation of {@link EmrMonitorService}.
 */
public class EmrMonitorServiceImpl extends BaseOpenmrsService implements EmrMonitorService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private EmrMonitorDAO dao;
	
	/**
     * @param dao the dao to set
     */
    public void setDao(EmrMonitorDAO dao) {
	    this.dao = dao;
    }
    
    /**
     * @return the dao
     */
    public EmrMonitorDAO getDao() {
	    return dao;
    }

    @Override
    public EmrMonitor saveSystemInformation(EmrMonitor emrMonitor) {
        File systemStatusInfoFile = new File(String.format("%s/%s", EmrMonitorProperties.getEmrMonitorDirectory().getAbsolutePath(), emrMonitor.getUuid()));
        try {
            FileWriter file = new FileWriter(systemStatusInfoFile.getAbsolutePath());
            file.write(new ObjectMapper().writeValueAsString(emrMonitor.getSystemInformation()));
            file.flush();
            file.close();
        } catch (IOException e) {
            log.error("failed to open file for for writing", e);
        }
        return emrMonitor;
    }

    @Override
    public EmrMonitor getSystemInformation(String systemId) {
        return null;
    }
}