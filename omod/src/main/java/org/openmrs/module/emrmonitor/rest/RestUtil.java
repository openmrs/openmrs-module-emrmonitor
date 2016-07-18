package org.openmrs.module.emrmonitor.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.openmrs.module.emrmonitor.EmrMonitorConfig;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;

import java.io.IOException;

import static org.openmrs.module.emrmonitor.EmrMonitorConfig.PARENT_URL_PROPERTY;

/**
 * Restful utilities
 */
public class RestUtil {

    private static Log log = LogFactory.getLog(RestUtil.class);

    /**
     * Convert the given object to JSON with the given Representation.  If prettify is true, indent and make more readable
     */
    public static String convertToJson(Object o, Representation rep, boolean prettify) throws IOException {
        SimpleObject representation = (SimpleObject)ConversionUtil.convertToRepresentation(o, rep);
        if (o instanceof EmrMonitorReport) {
            representation.removeProperty("status");
        }
        ObjectMapper mapper = new ObjectMapper();
        if (prettify) {
            mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        }
        return mapper.writeValueAsString(representation);
    }

    /**
     * @return a WebResource to connect to the parent server for the given resource and parameters
     * @param resourcePath should be relative within emrmonitor.  eg. "server/uuid", "report/uuid"
     */
    public static WebResource getParentServerResource(String resourcePath, String... queryParams) {

        if (!EmrMonitorConfig.isParentServerConfigured()) {
            log.debug("No " + PARENT_URL_PROPERTY + " defined in runtime properties. Not able to connect to emrmonitor parent server");
            return null;
        }

        String url = EmrMonitorConfig.getRuntimeProperty(EmrMonitorConfig.PARENT_URL_PROPERTY);
        String username = EmrMonitorConfig.getRuntimeProperty(EmrMonitorConfig.PARENT_USERNAME_PROPERTY);
        String password = EmrMonitorConfig.getRuntimeProperty(EmrMonitorConfig.PARENT_PASSWORD_PROPERTY);

        if (!url.startsWith("https://")) {
            log.debug("non-HTTPS connection to " + url);
        }

        Client restClient = Client.create();
        restClient.setReadTimeout(EmrMonitorConfig.REMOTE_SERVER_TIMEOUT);

        WebResource resource = restClient.resource(url).path("ws/rest/v1/emrmonitor/" + resourcePath);
        if (queryParams != null) {
            for (int i=0; i<queryParams.length; i+=2) {
                resource.queryParam(queryParams[i], queryParams[i+1]);
            }
        }
        resource.addFilter(new HTTPBasicAuthFilter(username, password));
        return resource;
    }
}
