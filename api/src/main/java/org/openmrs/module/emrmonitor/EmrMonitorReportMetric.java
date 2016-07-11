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

import java.util.UUID;

/**
 * Represents a particular metric in an EmrMonitorReport
 */
public class EmrMonitorReportMetric extends BaseOpenmrsObject implements Comparable<EmrMonitorReportMetric> {

    private Integer id;
    private String uuid;
    private EmrMonitorReport report;
    private String metric;
    private String value;

    public EmrMonitorReportMetric() {
        uuid = UUID.randomUUID().toString();
    }

    public EmrMonitorReportMetric(EmrMonitorReport report, String metric, String value) {
        this();
        this.report = report;
        this.metric = metric;
        this.value = value;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public EmrMonitorReport getReport() {
        return report;
    }

    public void setReport(EmrMonitorReport report) {
        this.report = report;
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

    @Override
    public int compareTo(EmrMonitorReportMetric o) {
        return OpenmrsUtil.compareWithNullAsGreatest(getMetric(), o.getMetric());
    }

    @Override
    public String toString() {
        return getMetric() + ": " + getValue();
    }
}
