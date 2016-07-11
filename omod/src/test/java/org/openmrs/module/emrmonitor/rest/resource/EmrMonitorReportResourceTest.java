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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.rest.RestUtil;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.emrmonitor.rest.controller.EmrMonitorRestController;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * Tests the EmrMonitorServerResource
 */
public class EmrMonitorReportResourceTest extends MainResourceControllerTest {

    protected final Log log = LogFactory.getLog(this.getClass());

    private EmrMonitorService emrMonitorService;

    @Before
    public void setup() throws Exception {
        this.emrMonitorService = Context.getService(EmrMonitorService.class);
        executeDataSet("emrmonitorDataSet.xml");
    }

    @Override
    public String getURI() {
        return EmrMonitorRestController.EMRMONITOR_REST_NAMESPACE + "/report";
    }

    @Override
    public String getUuid() {
        return "1379f06d-4463-11e6-be45-e82aea237783";
    }

    @Override
    public long getAllCount() {
        return 1;
    }

    @Override
    @ExpectedException(ResourceDoesNotSupportOperationException.class)
    public void shouldGetAll() throws Exception {
        request(RequestMethod.GET, getURI());
    }

    @Test
    public void shouldCreateAndDeleteReports() throws Exception {

        String reportUuid = "0aaeb4f8-44b6-11e6-be45-e82aea237783";

        EmrMonitorServer server = emrMonitorService.getEmrMonitorServerByUuid("8482c992-4462-11e6-be45-e82aea237783");
        EmrMonitorReport report = new EmrMonitorReport();
        report.setUuid(reportUuid);
        report.setServer(server);
        report.setDateCreated(new Date());
        report.setMetric("java.version", "1.8.0_91");
        report.setMetric("user.language", "en");
        report.setMetric("user.timezone", "America/New_York");

        String jsonReport = RestUtil.convertToJson(report, Representation.DEFAULT, true);
        log.warn(jsonReport);

        // Test that we can post a new report and it is saved
        handle(newPostRequest(getURI(), jsonReport));
        report = emrMonitorService.getEmrMonitorReportByUuid(reportUuid);
        Assert.assertNotNull(report);
        Assert.assertNotNull(report.getId());
        Assert.assertEquals(3, report.getMetrics().size());

        // Test that we can now delete this report
        handle(newDeleteRequest(getURI() + "/" + reportUuid));
        report = emrMonitorService.getEmrMonitorReportByUuid(reportUuid);
        Assert.assertNull(report);
    }
}
