package org.openmrs.module.emrmonitor;


import org.openmrs.util.OpenmrsUtil;

import java.util.Date;

public class EmrMonitorReportMetric implements Comparable<EmrMonitorReportMetric>{

    private Integer id;

    private EmrMonitorReport emrMonitorReport;

    private String category;

    private String metric;

    private String value;

    private Date dateCreated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EmrMonitorReport getEmrMonitorReport() {
        return emrMonitorReport;
    }

    public void setEmrMonitorReport(EmrMonitorReport emrMonitorReport) {
        this.emrMonitorReport = emrMonitorReport;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public int compareTo(EmrMonitorReportMetric other) {
        int retValue = 0;

        retValue =OpenmrsUtil.compareWithNullAsGreatest(getValue(), other.getValue());
        if (retValue == 0){
            retValue =OpenmrsUtil.compareWithNullAsGreatest(getMetric(), other.getMetric());
        }
        if (retValue == 0){
            retValue =OpenmrsUtil.compareWithNullAsGreatest(getCategory(), other.getCategory());
        }
        if (retValue == 0 && getDateCreated() != null) {
            retValue = OpenmrsUtil.compareWithNullAsLatest(getDateCreated(), other.getDateCreated());
        }
        return retValue;
    }
}
