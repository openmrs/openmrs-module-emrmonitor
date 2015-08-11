package org.openmrs.module.emrmonitor;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;

import java.io.Serializable;
import java.util.Date;
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

    @Override
    @JsonIgnore
    public Boolean isVoided() {
        return super.isVoided();
    }
}
