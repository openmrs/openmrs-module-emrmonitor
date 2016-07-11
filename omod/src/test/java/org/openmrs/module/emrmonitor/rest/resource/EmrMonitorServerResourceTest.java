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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.emrmonitor.rest.controller.EmrMonitorRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

import java.util.UUID;

/**
 * Tests the EmrMonitorServerResource
 */
public class EmrMonitorServerResourceTest extends MainResourceControllerTest {

    private EmrMonitorService emrMonitorService;

    @Before
    public void setup() throws Exception {
        this.emrMonitorService = Context.getService(EmrMonitorService.class);
        executeDataSet("emrmonitorDataSet.xml");
    }

    @Test
    public void shouldCreateAndDeleteServers() throws Exception {

        // First create a new server
        String newServerUuid = UUID.randomUUID().toString();
        String newServerName = "New Child Server";
        EmrMonitorServer server = new EmrMonitorServer();
        server.setUuid(newServerUuid);
        server.setName(newServerName);
        server.setServerType(EmrMonitorServerType.CHILD);
        handle(newPostRequest(getURI(), server));

        // Now get this server
        SimpleObject savedServer = deserialize(handle(newGetRequest(getURI() + "/" + newServerUuid)));
        Assert.assertEquals(newServerUuid, savedServer.get("uuid"));
        Assert.assertEquals(newServerName, savedServer.get("name"));
        Assert.assertEquals(EmrMonitorServerType.CHILD.toString(), savedServer.get("serverType"));

        server = emrMonitorService.getEmrMonitorServerByUuid(newServerUuid);
        Assert.assertNotNull(server.getId());
        Assert.assertEquals(newServerName, server.getName());

        // Now delete this server
        handle(newDeleteRequest(getURI() + "/" + newServerUuid, new Parameter("purge", "true")));

        server = emrMonitorService.getEmrMonitorServerByUuid(newServerUuid);
        Assert.assertNull(server);
    }

    @Override
    public String getURI() {
        return EmrMonitorRestController.EMRMONITOR_REST_NAMESPACE + "/server";
    }

    @Override
    public String getUuid() {
        return "e6e492e5-4321-11e6-be45-e82aea237783";
    }

    @Override
    public long getAllCount() {
        return 2;
    }

}
