/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.emrmonitor.rest.resource.openmrs;

import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorReportMetric;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;

/**
 * Provides the web service resource for a report metric
 */
@SubResource(parent=EmrMonitorReportResource.class, path="metric", supportedClass = EmrMonitorReportMetric.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class EmrMonitorReportMetricResource extends DelegatingSubResource<EmrMonitorReportMetric, EmrMonitorReport, EmrMonitorReportResource> {

    @Override
    public EmrMonitorReport getParent(EmrMonitorReportMetric metric) {
        return metric.getReport();
    }

    @Override
    public void setParent(EmrMonitorReportMetric metric, EmrMonitorReport report) {
        metric.setReport(report);
    }

    @Override
    public PageableResult doGetAll(EmrMonitorReport report, RequestContext context) throws ResponseException {
        return new NeedsPaging<EmrMonitorReportMetric>(new ArrayList<EmrMonitorReportMetric>(report.getMetrics()), context);
    }

    @Override
    public EmrMonitorReportMetric getByUniqueId(String uniqueId) {
        return Context.getService(RestHelperService.class).getObjectByUuid(EmrMonitorReportMetric.class, uniqueId);
    }

    @Override
    protected void delete(EmrMonitorReportMetric metric, String reason, RequestContext context) throws ResponseException {
        purge(metric, context);
    }

    @Override
    public void purge(EmrMonitorReportMetric metric, RequestContext context) throws ResponseException {
        EmrMonitorReport report = metric.getReport();
        report.getMetrics().remove(metric);
        getEmrMonitorService().saveEmrMonitorReport(report);
    }

    @Override
    public EmrMonitorReportMetric newDelegate() {
        return new EmrMonitorReportMetric();
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addProperty("metric");
            description.addProperty("value");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addProperty("metric");
            description.addProperty("value");
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    public EmrMonitorReportMetric save(EmrMonitorReportMetric metric) {
        EmrMonitorReport report = metric.getReport();
        if (!report.getMetrics().contains(metric)) {
            report.getMetrics().add(metric);
        }
        getEmrMonitorService().saveEmrMonitorReport(report);
        return metric;
    }

    @PropertyGetter("display")
    public String getDisplayString(EmrMonitorReportMetric metric) {
        return metric.toString();
    }

    private EmrMonitorService getEmrMonitorService() {
        return Context.getService(EmrMonitorService.class);
    }
}
