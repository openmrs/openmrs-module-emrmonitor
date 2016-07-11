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
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.emrmonitor.rest.controller.EmrMonitorRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Arrays;
import java.util.List;

/**
 * Provides the web service resource for a report
 */
@Resource(name = RestConstants.VERSION_1 + EmrMonitorRestController.EMRMONITOR_REST_NAMESPACE + "/report",
        supportedClass = EmrMonitorReport.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class EmrMonitorReportResource extends DelegatingCrudResource<EmrMonitorReport> {

    @Override
    public EmrMonitorReport getByUniqueId(String uniqueId) {
        return getEmrMonitorService().getEmrMonitorReportByUuid(uniqueId);
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        String serverUuid = context.getRequest().getParameter("server");
        if (serverUuid != null) {
            EmrMonitorServer server = getEmrMonitorService().getEmrMonitorServerByUuid(serverUuid);
            if (server == null) {
                throw new ObjectNotFoundException();
            }
            List<EmrMonitorReport> reports = getEmrMonitorService().getEmrMonitorReports(server); // TODO: Support for status and date ranges
            return new NeedsPaging<EmrMonitorReport>(reports, context);
        }
        //currently this is not supported since the superclass throws an exception
        return super.doSearch(context);
    }

    @Override
    protected void delete(EmrMonitorReport delegate, String s, RequestContext requestContext) throws ResponseException {
        purge(delegate, requestContext);
    }

    @Override
    public void purge(EmrMonitorReport delegate, RequestContext requestContext) throws ResponseException {
        getEmrMonitorService().purgeEmrMonitorReport(delegate);
    }

    @Override
    public EmrMonitorReport newDelegate() {
        return new EmrMonitorReport();
    }

    @Override
    public EmrMonitorReport save(EmrMonitorReport delegate) throws ResponseException {
        return getEmrMonitorService().saveEmrMonitorReport(delegate);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription d = null;
        if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation || rep instanceof RefRepresentation) {
            d = new DelegatingResourceDescription();
            d.addProperty("uuid");
            d.addProperty("display");
            d.addSelfLink();
            if (rep instanceof DefaultRepresentation) {
                d.addProperty("server", Representation.REF);
                d.addProperty("dateCreated");
                d.addProperty("metrics", Representation.DEFAULT);
                d.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            }
            else if (rep instanceof FullRepresentation) {
                d.addProperty("server");
                d.addProperty("dateCreated");
                d.addProperty("metrics");
                d.addProperty("status");
                d.addSelfLink();
            }
        }
        return d;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription d = new DelegatingResourceDescription();
        d.addRequiredProperty("uuid");
        d.addRequiredProperty("server");
        d.addRequiredProperty("dateCreated");
        d.addProperty("metrics");
        return d;
    }

    @Override
    public List<String> getPropertiesToExposeAsSubResources() {
        return Arrays.asList("metrics");
    }

    // TODO: Figure out why I need to do this.
    @PropertySetter("server")
    public void setServer(EmrMonitorReport report, EmrMonitorServer server) {
        server = getEmrMonitorService().getEmrMonitorServerByUuid(server.getUuid());
        report.setServer(server);
    }

    @PropertyGetter("display")
    public String getDisplayString(EmrMonitorReport emrMonitorReport) {
        return emrMonitorReport.toString();
    }

    private EmrMonitorService getEmrMonitorService() {
        return Context.getService(EmrMonitorService.class);
    }
}
