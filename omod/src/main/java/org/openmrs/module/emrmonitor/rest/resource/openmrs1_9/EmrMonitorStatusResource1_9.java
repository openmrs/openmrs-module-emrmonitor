package org.openmrs.module.emrmonitor.rest.resource.openmrs1_9;

import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.api.EmrMonitor;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.emrmonitor.rest.controller.EmrMonitorRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + EmrMonitorRestController.EMRMONITOR_REST_NAMESPACE
        + "/emrmonitorstatus", supportedClass = EmrMonitor.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class EmrMonitorStatusResource1_9 extends DelegatingCrudResource<EmrMonitor> {


    @Override
    public SimpleObject getAll(RequestContext requestContext) throws ResponseException {

        SimpleObject simpleObject = new SimpleObject().add("systemInformation", new EmrMonitor().getSystemInformation());
        return simpleObject;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addRequiredProperty("systemInformation");
        return description;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
        return getCreatableProperties();
    }

    @Override
    public SimpleObject search(RequestContext requestContext) throws ResponseException {
        return null;
    }

    @Override
    public String getUri(Object o) {
        return RestConstants.URI_PREFIX + EmrMonitorRestController.EMRMONITOR_REST_NAMESPACE + "/emrmonitorstatus"
                + ((EmrMonitor) o);
    }

    @Override
    public EmrMonitor getByUniqueId(String uniqueId) {
        return null;
    }

    @Override
    protected void delete(EmrMonitor delegate, String reason, RequestContext context) throws ResponseException {

    }

    @Override
    public EmrMonitor newDelegate() {
        return new EmrMonitor();
    }

    @Override
    public EmrMonitor save(EmrMonitor delegate) {
        EmrMonitor newEmrMonitor = Context.getService(EmrMonitorService.class).saveSystemInformation(delegate);
        //we are going to save this new class
        return newEmrMonitor;
    }

    @Override
    public void purge(EmrMonitor delegate, RequestContext context) throws ResponseException {

    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        return new DelegatingResourceDescription();
    }
}
