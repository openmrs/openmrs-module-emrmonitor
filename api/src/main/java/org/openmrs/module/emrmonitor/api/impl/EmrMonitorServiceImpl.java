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

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.SessionFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.api.EmrMonitor;
import org.openmrs.module.emrmonitor.api.EmrMonitorProperties;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.emrmonitor.api.db.EmrMonitorDAO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
            file.write(new ObjectMapper().writeValueAsString(emrMonitor));
            file.flush();
            file.close();
        } catch (IOException e) {
            log.error("failed to open file for for writing", e);
        }
        return emrMonitor;
    }

    @Override
    public EmrMonitorServer saveSystemInformation(EmrMonitorServer emrMonitorServer) {

        File systemStatusInfoFile = new File(String.format("%s/%s", EmrMonitorProperties.getEmrMonitorDirectory().getAbsolutePath(), emrMonitorServer.getUuid()));
        try {
            if (systemStatusInfoFile.exists()) {
                boolean delete = systemStatusInfoFile.delete();
                if (!delete) {
                    log.error("failed to delete previous version of system status info");
                    return null;
                }
            }
            FileWriter file = new FileWriter(systemStatusInfoFile.getAbsolutePath());
            file.write(new ObjectMapper().writeValueAsString(emrMonitorServer));
            file.flush();
            file.close();
        } catch (IOException e) {
            log.error("failed to open file for for writing", e);
        }
        return emrMonitorServer;
    }

    @Override
    public List<EmrMonitorServer> getEmrMonitorServers() {
        return dao.getEmrMonitorServers();
    }

    @Override
    public EmrMonitorServer saveEmrMonitorServer(EmrMonitorServer server) {
        server =  dao.saveEmrMonitorServer(server);
        server.setSystemInformation(Context.getAdministrationService().getSystemInformation());
        server = saveSystemInformation(server);
        return server;

    }

    @Override
    public EmrMonitorServer getEmrMonitorServerByType(EmrMonitorServerType serverType) {
        return dao.getEmrMonitorServerByType(serverType);
    }

    @Override
    public EmrMonitorServer getSystemInformation(String serverUuid) {
        EmrMonitorServer emrMonitorServer = null;
        if (StringUtils.isNotBlank(serverUuid)) {
            File systemStatusInfoFile = new File(String.format("%s/%s", EmrMonitorProperties.getEmrMonitorDirectory().getAbsolutePath(), serverUuid));
            if (systemStatusInfoFile.exists()) {
                try {
                    emrMonitorServer = new ObjectMapper().readValue(systemStatusInfoFile, EmrMonitorServer.class);

                } catch (IOException e) {
                    log.error("failed to read and parse system status info file: " + systemStatusInfoFile.getAbsolutePath(), e);
                }
            }
        }
        return emrMonitorServer;
    }
}