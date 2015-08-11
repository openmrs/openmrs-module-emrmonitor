package org.openmrs.module.emrmonitor;


import java.util.Date;

public class EmrMonitorReport {

    public enum SubmissionStatus {
        WAITING_TO_SEND, SENT, LOCAL_ONLY
    }

    private Integer id;

    private EmrMonitorServer emrMonitorServer;

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
