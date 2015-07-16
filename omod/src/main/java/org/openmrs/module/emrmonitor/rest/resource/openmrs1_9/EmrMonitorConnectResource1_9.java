package org.openmrs.module.emrmonitor.rest.resource.openmrs1_9;


import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.emrmonitor.rest.controller.EmrMonitorRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.io.IOException;

@Resource(name = RestConstants.VERSION_1 + EmrMonitorRestController.EMRMONITOR_REST_NAMESPACE
        + "/connect", supportedClass = EmrMonitorServer.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class EmrMonitorConnectResource1_9 extends DelegatingCrudResource<EmrMonitorServer> {


    /**
     * Gets a description of resource's properties which can be set on creation.
     *
     * @return the description
     * @throws org.openmrs.module.webservices.rest.web.response.ResponseException
     */
    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("serverName");
        description.addProperty("serverType");
        description.addProperty("serverUrl");
        description.addProperty("serverUserName");
        description.addProperty("serverUserPassword");
        return description;
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
        return Context.getService(EmrMonitorService.class).getEmrMonitorServerByUuid(uniqueId);
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
        return new EmrMonitorServer();
    }

    /**
     * Writes the delegate to the database
     *
     * @param delegate
     * @return the saved instance
     */
    @Override
    public EmrMonitorServer save(EmrMonitorServer delegate) {
        EmrMonitorServer remoteServer = null;
        try {
            remoteServer = Context.getService(EmrMonitorService.class).testConnection(delegate);
        } catch (IOException e) {
            log.error("error connecting to remote server", e);
        }
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
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("serverName");
        description.addProperty("serverType");
        description.addProperty("serverUrl");
        description.addProperty("serverUserName");
        description.addProperty("uuid");
        description.addProperty("systemInformation");
        description.addProperty("dateCreated");
        description.addProperty("dateChanged");
        // description.addProperty("display", findMethod("getDisplayString"));
        description.addSelfLink();
        return description;
    }
}
