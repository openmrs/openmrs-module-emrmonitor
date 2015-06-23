package org.openmrs.module.emrmonitor.rest.resource.openmrs1_9;

import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
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
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + EmrMonitorRestController.EMRMONITOR_REST_NAMESPACE
        + "/emrmonitorserver", supportedClass = EmrMonitorServer.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class EmrMonitorServerResource1_9 extends DelegatingCrudResource<EmrMonitorServer> {

    /**
     * @param context
     * @see org.openmrs.module.webservices.rest.web.resource.api.Listable#getAll(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public SimpleObject getAll(RequestContext context) throws ResponseException {
        SimpleObject simpleObject = new SimpleObject().add("servers", Context.getService(EmrMonitorService.class).getEmrMonitorServers());
        return simpleObject;
    }

    /**
     * Gets the delegate object with the given unique id. Implementations may decide whether
     * "unique id" means a uuid, or if they also want to retrieve delegates based on a unique
     * human-readable property.
     *
     * @param uniqueId
     * @return the delegate for the given uniqueId
     */
    @Override
    public EmrMonitorServer getByUniqueId(String uniqueId) {
        return null;
    }

    /**
     * Void or retire delegate, whichever action is appropriate for the resource type. Subclasses
     * need to override this method, which is called internally by
     * {@link #delete(String, String, org.openmrs.module.webservices.rest.web.RequestContext)}.
     *
     * @param delegate
     * @param reason
     * @param context
     * @throws org.openmrs.module.webservices.rest.web.response.ResponseException
     */
    @Override
    protected void delete(EmrMonitorServer delegate, String reason, RequestContext context) throws ResponseException {

    }

    /**
     * Instantiates a new instance of the handled delegate
     *
     * @return
     */
    @Override
    public EmrMonitorServer newDelegate() {
        return null;
    }

    /**
     * Writes the delegate to the database
     *
     * @param delegate
     * @return the saved instance
     */
    @Override
    public EmrMonitorServer save(EmrMonitorServer delegate) {
        return null;
    }

    /**
     * Purge delegate from persistent storage. Subclasses need to override this method, which is
     * called internally by {@link #purge(String, org.openmrs.module.webservices.rest.web.RequestContext)}.
     *
     * @param delegate
     * @param context
     * @throws org.openmrs.module.webservices.rest.web.response.ResponseException
     */
    @Override
    public void purge(EmrMonitorServer delegate, RequestContext context) throws ResponseException {

    }

    /**
     * Gets the {@link org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription} for the given representation for this
     * resource, if it exists
     *
     * @param rep
     * @return
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        return null;
    }
}
