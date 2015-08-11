package org.openmrs.module.emrmonitor;


import org.openmrs.util.OpenmrsUtil;

import java.util.Date;
import java.util.Set;

public class EmrMonitorReport implements Comparable<EmrMonitorReport>{

    @Override
    public int compareTo(EmrMonitorReport other) {
        int retValue = 0;
        if (other != null && getDateCreated() != null) {
            retValue = OpenmrsUtil.compareWithNullAsLatest(getDateCreated(), other.getDateCreated());
        }
        return retValue;
    }

    public enum SubmissionStatus {
        WAITING_TO_SEND, SENT, LOCAL_ONLY
    }

    private Integer id;

    private EmrMonitorServer emrMonitorServer;

    private Set<EmrMonitorReportMetric> metrics;

    private Date dateCreated;

    private SubmissionStatus status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EmrMonitorServer getEmrMonitorServer() {
        return emrMonitorServer;
    }

    public void setEmrMonitorServer(EmrMonitorServer emrMonitorServer) {
        this.emrMonitorServer = emrMonitorServer;
    }

    public Set<EmrMonitorReportMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(Set<EmrMonitorReportMetric> metrics) {
        this.metrics = metrics;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }
}
