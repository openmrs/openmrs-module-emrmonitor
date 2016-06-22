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

import org.openmrs.util.OpenmrsUtil;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a particular monitoring report for a given server on a given date
 */
public class EmrMonitorReport implements Comparable<EmrMonitorReport>{

    public enum SubmissionStatus {
        WAITING_TO_SEND, SENT, RECEIVED, LOCAL_ONLY
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

    @Override
    public int compareTo(EmrMonitorReport other) {
        int retValue = 0;
        if (other != null && getDateCreated() != null) {
            retValue = OpenmrsUtil.compareWithNullAsLatest(getDateCreated(), other.getDateCreated());
        }
        return retValue;
    }

    public Map<String, Map<String, String>> getMetricsByCategory() {
        Map<String, Map<String, String>> ret = new LinkedHashMap<String, Map<String, String>>();
        for (EmrMonitorReportMetric metric : getMetrics()) {
            Map<String, String> categoryMetrics = ret.get(metric.getCategory());
            if (categoryMetrics == null) {
                categoryMetrics = new LinkedHashMap<String, String>();
                ret.put(metric.getCategory(), categoryMetrics);
            }
            categoryMetrics.put(metric.getMetric(), metric.getValue());
        }
        return ret;
    }

    @Override
    public String toString() {
        return "Monitor report of " + getEmrMonitorServer().getName() + " on " + getDateCreated();
    }
}
