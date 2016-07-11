/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.emrmonitor.rest.resource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.emrmonitor.EmrMonitorConstants;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import javax.ws.rs.core.MediaType;

/**
 * Tests the EmrMonitorServerResource
 */
@Ignore
public class RestTest extends BaseModuleWebContextSensitiveTest {

    protected final Log log = LogFactory.getLog(this.getClass());

    private EmrMonitorService emrMonitorService;

    @Test
    public void setup() throws Exception {

        Client restClient = Client.create();
        restClient.setReadTimeout(EmrMonitorConstants.REMOTE_SERVER_TIMEOUT);

        WebResource resource = restClient.resource("http://localhost:8081/openmrs-standalone").path("ws/rest/v1/emrmonitor/server/1f30fc47-c650-4b5d-9037-eb4c0c2132ba");
        resource.addFilter(new HTTPBasicAuthFilter("admin", "test"));
        ClientResponse serverResponse = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        System.out.println("Status: " + serverResponse.getStatus());
        System.out.println("Status: " + serverResponse.toString());
    }
}
