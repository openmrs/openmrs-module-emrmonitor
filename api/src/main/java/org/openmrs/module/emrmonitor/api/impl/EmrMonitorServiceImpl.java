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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorReportMetric;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.api.EmrMonitorProperties;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.emrmonitor.api.ExtraSystemInformation;
import org.openmrs.module.emrmonitor.api.db.EmrMonitorDAO;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * It is a default implementation of {@link EmrMonitorService}.
 */
public class EmrMonitorServiceImpl extends BaseOpenmrsService implements EmrMonitorService {
	
	protected final Log log = LogFactory.getLog(this.getClass());

	private EmrMonitorDAO dao;

    private Client restClient = null;

    /**
     * @param dao the dao to set
     */
    public void setDao(EmrMonitorDAO dao) {
	    this.dao = dao;
    }

    @PostConstruct
    public void init() {
        restClient = Client.create();
    }
    
    /**
     * @return the dao
     */
    public EmrMonitorDAO getDao() {
	    return dao;
    }


    @Override
    public List<EmrMonitorServer> getAllEmrMonitorServers() {
        return dao.getAllEmrMonitorServers();
    }

    @Override
    public EmrMonitorServer saveEmrMonitorServer(EmrMonitorServer server) {
        if (server != null ){
            Date dateCreated = new Date();
            if (server.getDateCreated() == null ) {
                server.setDateCreated(dateCreated);
            }
            server.setDateChanged(dateCreated);
            server = dao.saveEmrMonitorServer(server);
        }
        return server;
    }

    @Override
    public EmrMonitorReport saveEmrMonitorReport(EmrMonitorReport report) {
        if (report != null ){
            Date dateCreated = new Date();
            if (report.getDateCreated() == null ) {
                report.setDateCreated(dateCreated);
            }
            report = dao.saveEmrMonitorReport(report);
        }
        return report;
    }

    @Override
    public EmrMonitorServer saveEmrMonitorServer(EmrMonitorServer server, Map<String, Map<String, String>> systemInformation) {
        EmrMonitorServer emrMonitorServer = null;
        if (server != null ) {
            emrMonitorServer = saveEmrMonitorServer(server);
            if (systemInformation != null) {
                Date dateCreated = new Date();
                EmrMonitorReport emrMonitorReport = new EmrMonitorReport();
                emrMonitorReport.setEmrMonitorServer(emrMonitorServer);
                emrMonitorReport.setDateCreated(dateCreated);
                Set<EmrMonitorReportMetric> reportMetrics = new TreeSet<EmrMonitorReportMetric>();

                for (String category : systemInformation.keySet()) {
                    Map<String, String> metrics = systemInformation.get(category);
                    for (String metric : metrics.keySet()) {
                        String value = metrics.get(metric);
                        EmrMonitorReportMetric emrMonitorReportMetric = new EmrMonitorReportMetric();
                        emrMonitorReportMetric.setCategory(category);
                        emrMonitorReportMetric.setEmrMonitorReport(emrMonitorReport);
                        emrMonitorReportMetric.setMetric(metric);
                        emrMonitorReportMetric.setValue(value);
                        emrMonitorReportMetric.setDateCreated(dateCreated);
                        reportMetrics.add(emrMonitorReportMetric);
                    }
                }
                emrMonitorReport.setMetrics(reportMetrics);
                emrMonitorReport.setStatus(EmrMonitorReport.SubmissionStatus.WAITING_TO_SEND);
                saveEmrMonitorReport(emrMonitorReport);
                emrMonitorServer.setSystemInformation(systemInformation);
            }
        }
        return emrMonitorServer;
    }

    @Override
    public EmrMonitorServer testConnection(EmrMonitorServer server)  throws IOException {
        EmrMonitorServer remoteServer = null;
        if (server != null) {
            WebResource resource = setUpWebResource(server, EmrMonitorProperties.REMOTE_SERVER_TIMEOUT);
            String json = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
            JsonNode results = new ObjectMapper().readValue(json, JsonNode.class).get("servers");
            if (results !=null && results.size() > 0) {
                remoteServer= server;
            }
        }
        return remoteServer;
    }

    @Override
    public EmrMonitorServer registerServer(EmrMonitorServer server) throws IOException {
        EmrMonitorServer parentServer = null;
        if (server != null) {
            ObjectMapper mapper = new ObjectMapper();

            EmrMonitorServer localServer = getLocalServer();
            if (localServer != null) {
                EmrMonitorServer copy = localServer.copy();
                copy.setServerType(EmrMonitorServerType.CHILD);
                String localServerJson = mapper.writeValueAsString(copy);

                WebResource resource = restClient.resource(server.getServerUrl()).path("ws/rest/v1/emrmonitor/server");
                restClient.setReadTimeout(EmrMonitorProperties.REMOTE_SERVER_TIMEOUT);
                resource.addFilter(new HTTPBasicAuthFilter(server.getServerUserName(), server.getServerUserPassword()));
                ClientResponse response = resource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, localServerJson);

                log.warn("response.getStatus = " + response.getStatus());
                log.warn("response = " + response.toString());
                if (response.getStatus() == 201) {
                    // entity has been created
                    EmrMonitorServer emrMonitorServer = null;
                    try {
                        String responseEntity = response.getEntity(String.class);
                        emrMonitorServer = new ObjectMapper().readValue(responseEntity, EmrMonitorServer.class);
                    } catch(Exception e) {
                        throw new IOException("failed to de-serialize server node received from the parent: " + response.toString());
                    }
                    emrMonitorServer.setServerType(EmrMonitorServerType.PARENT);
                    //add remote server as the PARENT server in the emrmonitor_server table
                    saveEmrMonitorServer(emrMonitorServer, emrMonitorServer.getSystemInformation());
                    return emrMonitorServer;
                } else {
                    throw new IOException("failed to register with parent server: " + response.toString());
                }
            }
        }
        return server;
    }

    @Override
    public EmrMonitorServer voidEmrMonitorServer(EmrMonitorServer server, String reason) throws APIException {
        if (server == null) {
            return null;
        }
        return dao.saveEmrMonitorServer(server);
    }

    @Override
    public void purgeEmrMonitorServer(EmrMonitorServer server) throws APIException {
       dao.deleteEmrMonitorServer(server);
    }

    @Override
    public Map<String, Map<String, String>> getExtraSystemInfo() {
    	
    	ExtraSystemInformation extinfo=new ExtraSystemInformation();
    	return extinfo.getExtraSystemInformation();
    }

    @Override
    public EmrMonitorServer getLocalServer() {
        List<EmrMonitorServer> servers = getEmrMonitorServerByType(EmrMonitorServerType.LOCAL);
        if (servers != null && servers.size() > 0) {
            return servers.get(0);
        }
        return null;
    }

    @Override
    public List<EmrMonitorServer> getEmrMonitorServerByType(EmrMonitorServerType serverType) {
        return dao.getEmrMonitorServerByType(serverType);
    }

    @Override
    public EmrMonitorServer getEmrMonitorServerByUuid(String serverUuid) {
        return (EmrMonitorServer) dao.getEmrMonitorServerByUuid(serverUuid);
    }

	@Override
	public Map<String, String> getOpenmrsData() {
		return dao.getOpenmrsData();
	}


    private WebResource setUpWebResource(EmrMonitorServer remoteServerConfiguration, Integer timeout) {
        if (remoteServerConfiguration == null) {
            throw new IllegalArgumentException("Unknown remote server");
        }
        if (!remoteServerConfiguration.getServerUrl().startsWith("https://")) {
            log.warn("non-HTTPS connection to " + remoteServerConfiguration.getName());
        }

        WebResource resource = restClient.resource(remoteServerConfiguration.getServerUrl()).path("ws/rest/v1/emrmonitor/server").queryParam("v", "default");
        if(timeout!=null){
            restClient.setReadTimeout(timeout);
        }
        resource.addFilter(new HTTPBasicAuthFilter(remoteServerConfiguration.getServerUserName(), remoteServerConfiguration.getServerUserPassword()));
        return resource;
    }
}