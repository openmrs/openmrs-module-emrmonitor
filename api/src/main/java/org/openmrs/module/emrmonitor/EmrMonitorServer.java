package org.openmrs.module.emrmonitor;


import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;

import java.util.Date;
import java.util.Map;

public class EmrMonitorServer {

    private Integer serverId;

    private String serverName;

    private EmrMonitorServerType serverType;

    private String serverUrl;

    private String serverUserName;

    private String serverUserPassword;

    private String uuid;

    private Date dateCreated;

    private Date dateChanged;

    private Map<String, Map<String, String>> systemInformation = null;

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(Date dateChanged) {
        this.dateChanged = dateChanged;
    }

    public Map<String, Map<String, String>> getSystemInformation() {
        if (systemInformation == null ) {
            EmrMonitorServer server = Context.getService(EmrMonitorService.class).getSystemInformation(this.getUuid());
            if (server != null && (server.getServerId().compareTo(this.serverId) == 0) ) {
                systemInformation = server.getSystemInformation();
            }
        }
        return systemInformation;
    }

    public void setSystemInformation(Map<String, Map<String, String>> systemInformation) {
        this.systemInformation = systemInformation;
    }
}
