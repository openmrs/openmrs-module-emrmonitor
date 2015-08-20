package org.openmrs.module.emrmonitor;


import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class EmrMonitorServer extends BaseOpenmrsData implements Serializable{

    private Integer id;

    private String name;

    private EmrMonitorServerType serverType;

    private String serverUrl;

    private String serverUserName;

    private String serverUserPassword;

    private String uuid;

    private Date dateCreated;

    private Date dateChanged;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
     * Shallow copy of the EmrMonitorServer
     * @return
     */
    public EmrMonitorServer copy() {
        return copyHelper(new EmrMonitorServer());
    }

    private EmrMonitorServer copyHelper(EmrMonitorServer target) {
        target.setName(this.getName());
        target.setUuid(this.getUuid());
        target.setServerUrl(this.getServerUrl());
        target.setServerUserName(getServerUserName());
        target.setServerUserPassword(getServerUserPassword());
        target.setSystemInformation(this.getSystemInformation());
        return target;
    }

    @Override
    @JsonIgnore
    public Boolean isVoided() {
        return super.isVoided();
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
    public User getVoidedBy() {
        return super.getVoidedBy();
    }
}
