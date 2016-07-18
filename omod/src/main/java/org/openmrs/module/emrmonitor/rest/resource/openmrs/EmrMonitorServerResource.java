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
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

/**
 * Test the EmrMonitorServerResource
 */
@Resource(name = RestConstants.VERSION_1 + EmrMonitorRestController.EMRMONITOR_REST_NAMESPACE + "/server",
        supportedClass = EmrMonitorServer.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class EmrMonitorServerResource extends DelegatingCrudResource<EmrMonitorServer> {

    public EmrMonitorServerResource() {
        super();
        propertiesIgnoredWhenUpdating.add("latestReport");  // TODO: Figure out why this is necessary
    }

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        List<EmrMonitorServer> servers = getEmrMonitorService().getAllEmrMonitorServers();
        return new NeedsPaging<EmrMonitorServer>(servers, context);
    }

    @Override
    public EmrMonitorServer getByUniqueId(String uniqueId) {
        return getEmrMonitorService().getEmrMonitorServerByUuid(uniqueId);
    }

    @Override
    protected void delete(EmrMonitorServer delegate, String reason, RequestContext context) throws ResponseException {
        purge(delegate.getUuid(), context);
    }

    @Override
    public void purge(String uuid, RequestContext context) throws ResponseException {
        getEmrMonitorService().purgeEmrMonitorServer(uuid);
    }

    @Override
    public void purge(EmrMonitorServer delegate, RequestContext context) throws ResponseException {
        purge(delegate.getUuid(), context);
    }

    @Override
    public EmrMonitorServer newDelegate() {
        return new EmrMonitorServer();
    }

    @Override
    public EmrMonitorServer save(EmrMonitorServer delegate) {
        return getEmrMonitorService().saveEmrMonitorServer(delegate);
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("uuid");
        description.addProperty("name");
        description.addProperty("serverType");
        return description;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("name");
        return description;
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = null;
        if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation || rep instanceof RefRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display", "name");
            description.addSelfLink();
            description.addProperty("uuid");
            if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
                description.addProperty("name");
                description.addProperty("serverType");
                description.addProperty("latestReport");
            }
        }
        return description;
    }

    @PropertyGetter("latestReport")
    public EmrMonitorReport getLatestReport(EmrMonitorServer server) {
        return getEmrMonitorService().getLatestEmrMonitorReport(server);
    }

    private EmrMonitorService getEmrMonitorService() {
        return Context.getService(EmrMonitorService.class);
    }
}
