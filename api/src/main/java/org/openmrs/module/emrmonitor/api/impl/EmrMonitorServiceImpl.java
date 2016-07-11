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

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emrmonitor.EmrMonitorConstants;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.emrmonitor.api.db.EmrMonitorDAO;
import org.openmrs.module.emrmonitor.metric.MetricProducer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Override
    public EmrMonitorServer ensureLocalServer() {
        EmrMonitorServer s = dao.getLocalServer();
        if (s == null) {
            s = new EmrMonitorServer();
            s.setUuid(UUID.randomUUID().toString());
            try {
                s.setName(InetAddress.getLocalHost().getHostName());
            }
            catch (UnknownHostException e) {
                s.setName("Local Server");
            }
            s.setServerType(EmrMonitorServerType.LOCAL);
            saveEmrMonitorServer(s);
        }
        return s;
    }

    @Override
    public List<EmrMonitorServer> getChildServers() {
        return dao.getChildServers();
    }

    @Override
    public EmrMonitorServer getEmrMonitorServerByUuid(String serverUuid) {
        return dao.getEmrMonitorServerByUuid(serverUuid);
    }

    @Override
    public List<EmrMonitorServer> getAllEmrMonitorServers() {
        return dao.getAllEmrMonitorServers();
    }

    @Override
    public EmrMonitorServer saveEmrMonitorServer(EmrMonitorServer server) {
        return dao.saveEmrMonitorServer(server);
    }

    @Override
    public void purgeEmrMonitorServer(String uuid) throws APIException {
        dao.deleteEmrMonitorServer(uuid);
    }

    @Override
    public EmrMonitorReport generateEmrMonitorReport() {
        log.debug("Generating EMR monitor report");

        EmrMonitorServer localServer = ensureLocalServer();
        EmrMonitorReport report = new EmrMonitorReport();
        report.setServer(localServer);
        report.setDateCreated(new Date());
        report.setStatus(EmrMonitorReport.SubmissionStatus.WAITING_TO_SEND);

        StopWatch sw = new StopWatch();
        for (MetricProducer metricProducer : Context.getRegisteredComponents(MetricProducer.class)) {
            if (metricProducer.isEnabled()) {
                String namespace = metricProducer.getNamespace();
                log.debug("Generating metrics for " + namespace + " (" + metricProducer.getClass().getSimpleName() + ")");
                sw.start();
                Map<String, String> metrics = metricProducer.produceMetrics();
                if (metrics != null) {
                    for (String metricName : metrics.keySet()) {
                        String metricValue = metrics.get(metricName);
                        report.setMetric(namespace + "." + metricName, metricValue);
                        log.debug(metricName + ": " + metricValue);
                    }
                }
                sw.stop();
                log.debug(namespace + " metrics generated in: " + sw.toString());
                sw.reset();
            }
        }

        return saveEmrMonitorReport(report);
    }

    @Override
    public EmrMonitorReport getEmrMonitorReportByUuid(String uuid) {
        return dao.getEmrMonitorReportByUuid(uuid);
    }

    @Override
    public EmrMonitorReport saveEmrMonitorReport(EmrMonitorReport report) {
        // TODO: Not sure this is right or what we want.  Maybe move this submission status to a separate queue table?
        if (report.getStatus() == null) {
            if (report.getServer().getServerType() == EmrMonitorServerType.LOCAL) {
                if (EmrMonitorConstants.isParentServerConfigured()) {
                    report.setStatus(EmrMonitorReport.SubmissionStatus.LOCAL_ONLY);
                }
                else {
                    report.setStatus(EmrMonitorReport.SubmissionStatus.WAITING_TO_SEND);
                }
            }
            else {
                report.setStatus(EmrMonitorReport.SubmissionStatus.RECEIVED);
            }
        }
        return dao.saveEmrMonitorReport(report);
    }

    @Override
    public void purgeEmrMonitorReport(EmrMonitorReport report) throws APIException {
        dao.deleteEmrMonitorReport(report);
    }

    @Override
    public List<EmrMonitorReport> getEmrMonitorReports(EmrMonitorServer server, EmrMonitorReport.SubmissionStatus... status) {
        return dao.getEmrMonitorReports(server, status);
    }

    @Override
    public Map<String, String> getOpenmrsData() {
        return dao.getOpenmrsData();
    }
}