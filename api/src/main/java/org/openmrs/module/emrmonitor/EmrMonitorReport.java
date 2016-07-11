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

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.util.OpenmrsUtil;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a particular monitoring report for a given server on a given date
 */
public class EmrMonitorReport extends BaseOpenmrsObject implements Comparable<EmrMonitorReport>{

    public enum SubmissionStatus {
        WAITING_TO_SEND, SENT, RECEIVED, LOCAL_ONLY
    }

    private Integer id;
    private EmrMonitorServer server;
    private Set<EmrMonitorReportMetric> metrics;
    private Date dateCreated;
    private SubmissionStatus status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EmrMonitorServer getServer() {
        return server;
    }

    public void setServer(EmrMonitorServer server) {
        this.server = server;
    }

    public Set<EmrMonitorReportMetric> getMetrics() {
        if (metrics == null) {
            metrics = new TreeSet<EmrMonitorReportMetric>();
        }
        return metrics;
    }

    public void setMetrics(Set<EmrMonitorReportMetric> metrics) {
        this.metrics = metrics;
    }

    public EmrMonitorReportMetric getMetric(String metric) {
        for (EmrMonitorReportMetric m : getMetrics()) {
            if (m.getMetric().equals(metric)) {
                return m;
            }
        }
        return null;
    }

    public void setMetric(String name, String value) {
        EmrMonitorReportMetric m = getMetric(name);
        if (m == null) {
            m = new EmrMonitorReportMetric(this, name, value);
            getMetrics().add(m);
        }
        m.setValue(value);
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

    @Override
    public String toString() {
        return "Monitor report of " + getServer().getName() + " on " + getDateCreated();
    }
}
