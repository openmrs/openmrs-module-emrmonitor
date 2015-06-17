package org.openmrs.module.emrmonitor.rest.resource.openmrs1_9;

import org.openmrs.module.emrmonitor.EmrMonitor;
import org.openmrs.module.emrmonitor.rest.controller.EmrMonitorRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + EmrMonitorRestController.EMRMONITOR_REST_NAMESPACE
        + "/emrmonitorstatus", supportedClass = EmrMonitor.EmrMonitorStatus.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class EmrMonitorStatusResource1_9 implements Listable, Searchable {


    @Override
    public SimpleObject getAll(RequestContext requestContext) throws ResponseException {

        EmrMonitor.EmrMonitorStatus[] statuses = EmrMonitor.EmrMonitorStatus.values();
        SimpleObject simpleObject = new SimpleObject().add("results", statuses);
        return simpleObject;
    }

    @Override
    public SimpleObject search(RequestContext requestContext) throws ResponseException {
        return null;
    }

    @Override
    public String getUri(Object o) {
        return RestConstants.URI_PREFIX + EmrMonitorRestController.EMRMONITOR_REST_NAMESPACE + "/emrmonitorstatus"
                + ((EmrMonitor.EmrMonitorStatus) o);
    }
}
