package org.openmrs.module.emrmonitor.rest.resource.openmrs;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.emrmonitor.rest.controller.EmrMonitorRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + EmrMonitorRestController.EMRMONITOR_REST_NAMESPACE
        + "/server", supportedClass = EmrMonitorServer.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class EmrMonitorServerResource extends DelegatingCrudResource<EmrMonitorServer> {

    /**
     * @param context
     * @see org.openmrs.module.webservices.rest.web.resource.api.Listable#getAll(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public SimpleObject getAll(RequestContext context) throws ResponseException {
        SimpleObject simpleObject = new SimpleObject().add("servers", Context.getService(EmrMonitorService.class).getAllEmrMonitorServers());
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
        if (delegate.isVoided()){
            return;
        }
        EmrMonitorServer emrMonitorServer = Context.getService(EmrMonitorService.class).voidEmrMonitorServer(delegate, "delete via ws");
        return;
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
        if (delegate.getId() != null){
            // existing EmrMonitorServer record gets updated
            log.warn("updating current EmrMonitorServer record: " + delegate.toString());
        } else {
            log.warn("creating new EmrMonitorServer record: " + delegate.toString());
        }
        if (delegate.getSystemInformation() != null) {
            return Context.getService(EmrMonitorService.class)
                    .saveEmrMonitorServer(delegate, delegate.getSystemInformation(), EmrMonitorReport.SubmissionStatus.RECEIVED);
        }
        return Context.getService(EmrMonitorService.class).saveEmrMonitorServer(delegate);
    }

    /**
     * Gets a description of resource's properties which can be set on creation.
     *
     * @return the description
     * @throws org.openmrs.module.webservices.rest.web.response.ResponseException
     */
    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("name");
        description.addProperty("serverType");
        description.addProperty("serverUrl");
        description.addProperty("serverUserName");
        description.addProperty("serverUserPassword");
        description.addProperty("uuid");
        description.addProperty("systemInformation");

        return description;
    }

    /**
     * Gets a description of resource's properties which can be edited.
     * <p/>
     * By default delegates to {@link #getCreatableProperties()} and removes sub-resources returned
     * by {@link #getPropertiesToExposeAsSubResources()}.
     *
     * @return the description
     * @throws org.openmrs.module.webservices.rest.web.response.ResponseException
     */
    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
        return getCreatableProperties();
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
        Context.getService(EmrMonitorService.class).purgeEmrMonitorServer(delegate);
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
        description.addProperty("name");
        description.addProperty("serverType");
        description.addProperty("serverUrl");
        description.addProperty("serverUserName");
        description.addProperty("uuid");
        description.addProperty("systemInformation");
        description.addProperty("dateCreated");
        description.addProperty("dateChanged");
        description.addSelfLink();
        return description;
    }

    /**
     * Implementations should override this method if they are actually searchable.
     *
     * @param context
     */
    @Override
    protected PageableResult doSearch(RequestContext context) {
        List<EmrMonitorServer> servers= new ArrayList<EmrMonitorServer>();
        String type = context.getParameter("type");
        if (StringUtils.isNotBlank(type)) {
            List<EmrMonitorServer> emrMonitorServers = Context.getService(EmrMonitorService.class)
                    .getEmrMonitorServerByType(EmrMonitorServerType.valueOf(type));
            if (emrMonitorServers != null && emrMonitorServers.size() > 0) {
                servers = emrMonitorServers;
            }
        }
        return new NeedsPaging<EmrMonitorServer>(servers, context);
    }
}
