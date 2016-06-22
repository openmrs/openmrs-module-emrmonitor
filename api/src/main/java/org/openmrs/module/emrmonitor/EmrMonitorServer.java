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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.User;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a particular server that is registered with a given name
 * All systems are expected to have at least one server registered as serverType of LOCAL, representing itself
 * If a system wishes to report it's data up to a central server, it may register that server as serverType of PARENT
 * If a system is a parent that other servers communicate monitoring data to, those other servers may be registered as serverType of CHILD
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class EmrMonitorServer extends BaseOpenmrsMetadata implements Serializable {

    private Integer id;
    private EmrMonitorServerType serverType;
    private String serverUrl;
    private String serverUserName;
    private String serverUserPassword;
    private Map<String, Map<String, String>> systemInformation = null;
    private Set<EmrMonitorReport> emrMonitorReports;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public EmrMonitorServerType getServerType() {
        return serverType;
    }

    public void setServerType(EmrMonitorServerType serverType) {
        this.serverType = serverType;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerUserName() {
        return serverUserName;
    }

    public void setServerUserName(String serverUserName) {
        this.serverUserName = serverUserName;
    }

    public String getServerUserPassword() {
        return serverUserPassword;
    }

    public void setServerUserPassword(String serverUserPassword) {
        this.serverUserPassword = serverUserPassword;
    }

    public Map<String, Map<String, String>> getSystemInformation() {
        if (systemInformation == null) {
            Set<EmrMonitorReport> reports = getEmrMonitorReports();
            if (reports != null && reports.size()>0) {
                EmrMonitorReport lastReport = reports.iterator().next();
                if (lastReport !=null) {
                    systemInformation = new LinkedHashMap<String, Map<String, String>>();
                    Set<EmrMonitorReportMetric> metrics = lastReport.getMetrics();
                    for (EmrMonitorReportMetric metric : metrics) {
                        String category = metric.getCategory();
                        String metricName = metric.getMetric();
                        String metricValue = metric.getValue();
                        Map<String, String> categoryMetrics = systemInformation.get(category);
                        if (categoryMetrics == null) {
                            categoryMetrics = new LinkedHashMap<String, String>();
                        }
                        categoryMetrics.put(metricName, metricValue);
                        systemInformation.put(category, categoryMetrics);
                    }
                }
            }
        }
        return systemInformation;
    }

    public void setSystemInformation(Map<String, Map<String, String>> systemInformation) {
        this.systemInformation = systemInformation;
    }

    @JsonIgnore
    public Set<EmrMonitorReport> getEmrMonitorReports() {
        return emrMonitorReports;
    }

    public void setEmrMonitorReports(Set<EmrMonitorReport> emrMonitorReports) {
        this.emrMonitorReports = emrMonitorReports;
    }

    /**
     * @return a shallow copy of the EmrMonitorServer
     */
    public EmrMonitorServer copy() {
        EmrMonitorServer target = new EmrMonitorServer();
        target.setUuid(this.getUuid());
        target.setName(this.getName());
        target.setServerType(this.getServerType());
        target.setServerUrl(this.getServerUrl());
        target.setServerUserName(getServerUserName());
        target.setServerUserPassword(getServerUserPassword());
        target.setSystemInformation(this.getSystemInformation());
        return target;
    }

    @Override
    @JsonIgnore
    public Boolean isRetired() {
        return super.isRetired();
    }

    @Override
    @JsonIgnore
    public User getChangedBy() {
        return super.getChangedBy();
    }

    @Override
    @JsonIgnore
    public User getCreator() {
        return super.getCreator();
    }

    @Override
    @JsonIgnore
    public User getRetiredBy() {
        return super.getRetiredBy();
    }
}
